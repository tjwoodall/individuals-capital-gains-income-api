/*
 * Copyright 2025 HM Revenue & Customs
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

package v3.residentialPropertyDisposals.createAmendNonPpd

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError, RuleTaxYearNotSupportedError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import shared.utils.MockIdGenerator
import v3.residentialPropertyDisposals.MockNrsProxyService
import v3.residentialPropertyDisposals.createAmendNonPpd.def1.model.request.{
  Def1_CreateAmendCgtResidentialPropertyDisposalsRequestBody,
  Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData,
  Disposal
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendCgtResidentialPropertyDisposalsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockSharedAppConfig
    with MockCreateAmendCgtResidentialPropertyDisposalsService
    with MockAuditService
    with MockNrsProxyService
    with MockCreateAmendCgtResidentialPropertyDisposalsValidatorFactory
    with MockIdGenerator {

  val taxYear: String = "2020-21"

  val validRequestJson: JsValue = Json.parse(
    """
      |{
      |   "disposals":[
      |      {
      |         "customerReference": "CGTDISPOSAL01",
      |         "disposalDate": "2021-03-24",
      |         "completionDate": "2021-03-26",
      |         "disposalProceeds": 1999.99,
      |         "acquisitionDate": "2021-03-22",
      |         "acquisitionAmount": 1999.99,
      |         "improvementCosts": 1999.99,
      |         "additionalCosts": 1999.99,
      |         "prfAmount": 1999.99,
      |         "otherReliefAmount": 1999.99,
      |         "lossesFromThisYear": 1999.99,
      |         "lossesFromPreviousYear": 1999.99,
      |         "amountOfNetGain": 1999.99,
      |         "amountOfNetLoss": 1999.99
      |      }
      |   ]
      |}
     """.stripMargin
  )

  val requestModel: Def1_CreateAmendCgtResidentialPropertyDisposalsRequestBody = Def1_CreateAmendCgtResidentialPropertyDisposalsRequestBody(
    disposals = List(
      Disposal(
        customerReference = Some("CGTDISPOSAL01"),
        disposalDate = "2021-03-24",
        completionDate = "2021-03-26",
        disposalProceeds = 1999.99,
        acquisitionDate = "2021-03-22",
        acquisitionAmount = 1999.99,
        improvementCosts = Some(1999.99),
        additionalCosts = Some(1999.99),
        prfAmount = Some(1999.99),
        otherReliefAmount = Some(1999.99),
        lossesFromThisYear = Some(1999.99),
        lossesFromPreviousYear = Some(1999.99),
        amountOfNetGain = Some(1999.99),
        amountOfNetLoss = Some(1999.99)
      )
    )
  )

  val requestData: Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData = Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestModel
  )

  "CreateAmendCgtResidentialPropertyDisposalsController" should {
    "return a successful response with status NO_CONTENT" when {
      "happy path" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockNrsProxyService
          .submitAsync(validNino, "itsa-cgt-disposal", validRequestJson)
          .returns(())

        MockCreateAmendCgtResidentialPropertyDisposalsService
          .createAndAmend(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(expectedStatus = NO_CONTENT, None, Some(validRequestJson))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(validRequestJson))
      }

      "service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockNrsProxyService.submitAsync(validNino, "itsa-cgt-disposal", validRequestJson)

        MockCreateAmendCgtResidentialPropertyDisposalsService
          .createAndAmend(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(validRequestJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller: CreateAmendCgtResidentialPropertyDisposalsController = new CreateAmendCgtResidentialPropertyDisposalsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreateAmendCgtResidentialPropertyDisposalsValidatorFactory,
      service = mockCreateAmendCgtResidentialPropertyDisposalsService,
      auditService = mockAuditService,
      nrsProxyService = mockNrsProxyService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] =
      controller.createAmendCgtResidentialPropertyDisposals(validNino, taxYear)(fakePostRequest(validRequestJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreateAmendCgtResidentialPropertyDisposals",
        transactionName = "Create-Amend-Cgt-Residential-Property-Disposals",
        detail = GenericAuditDetail(
          userType = "Individual",
          versionNumber = "3.0",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).anyNumberOfTimes()
  }

}
