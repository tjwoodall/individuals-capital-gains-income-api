/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import utils.MockIdGenerator
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockNrsProxyService}
import config.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import v1.controllers.validators.MockCreateAmendCgtPpdOverridesValidatorFactory
import v1.mocks.services.MockCreateAmendCgtPpdOverridesService
import v1.models.request.createAmendCgtPpdOverrides._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendCgtPpdOverridesControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAppConfig
    with MockCreateAmendCgtPpdOverridesService
    with MockAuditService
    with MockNrsProxyService
    with MockCreateAmendCgtPpdOverridesValidatorFactory
    with MockIdGenerator {

  val taxYear: String = "2019-20"

  val validRequestJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  val rawData: CreateAmendCgtPpdOverridesRawData = CreateAmendCgtPpdOverridesRawData(
    nino = validNino,
    taxYear = taxYear,
    body = AnyContentAsJson.apply(validRequestJson)
  )

  //@formatter:off
  val requestModel: CreateAmendCgtPpdOverridesRequestBody = CreateAmendCgtPpdOverridesRequestBody(
    multiplePropertyDisposals = Some(
      List(
        MultiplePropertyDisposals("AB0000000092", Some(1234.78), None),
        MultiplePropertyDisposals("AB0000000098", None, Some(134.99))
      )),
    singlePropertyDisposals = Some(
      List(
        SinglePropertyDisposals("AB0000000098", "2020-02-28", 454.24, Some("2020-03-29"), 3434.45, 233.45,
          423.34, 2324.67, 3434.23, Some(436.23), Some(234.23), Some(4567.89), None),
        SinglePropertyDisposals("AB0000000091", "2020-02-28", 454.24, Some("2020-03-29"), 3434.45, 233.45,
          423.34, 2324.67, 3434.23, Some(436.23), Some(234.23), None, Some(4567.89)
        )
      ))
  )
  //@formatter:on

  val requestData: CreateAmendCgtPpdOverridesRequestData = CreateAmendCgtPpdOverridesRequestData(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestModel
  )

  val auditData: JsValue = Json.parse(s"""
       |{
       |  "nino":"$validNino",
       |  "taxYear": "$taxYear"
       |  }""".stripMargin)

  "CreateAmendCgtPpdOverridesController" should {
    "return a successful response with status OK" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))
        MockedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()

        MockNrsProxyService
          .submitAsync(validNino, "itsa-cgt-disposal-ppd", validRequestJson)
          .returns(())

        MockCreateAmendCgtPpdOverridesService
          .createAmend(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(expectedStatus = OK, None, Some(validRequestJson), Some(auditData))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(validRequestJson))
      }

      "service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockNrsProxyService
          .submitAsync(validNino, "itsa-cgt-disposal-ppd", validRequestJson)
          .returns(())

        MockCreateAmendCgtPpdOverridesService
          .createAmend(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(validRequestJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new CreateAmendCgtPpdOverridesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreateAmendCgtPpdOverridesValidatorFactory,
      service = mockCreateAmendCgtPpdOverridesService,
      auditService = mockAuditService,
      nrsProxyService = mockNrsProxyService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitches.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.createAmendCgtPpdOverrides(validNino, taxYear)(fakePostRequest(validRequestJson))

    def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreateAmendCgtPpdOverrides",
        transactionName = "Create-Amend-Cgt-Ppd-Overrides",
        detail = GenericAuditDetail(
          userType = "Individual",
          versionNumber = "1.0",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = maybeRequestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedAppConfig.featureSwitches.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).anyNumberOfTimes()
  }

}
