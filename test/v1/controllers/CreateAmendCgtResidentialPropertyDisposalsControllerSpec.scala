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
import api.mocks.MockIdGenerator
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetailOld}
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockNrsProxyService}
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Result}
import v1.mocks.requestParsers.MockCreateAmendCgtResidentialPropertyDisposalsRequestParser
import v1.mocks.services._
import v1.models.request.createAmendCgtResidentialPropertyDisposals._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendCgtResidentialPropertyDisposalsControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAppConfig
    with MockCreateAmendCgtResidentialPropertyDisposalsService
    with MockAuditService
    with MockNrsProxyService
    with MockCreateAmendCgtResidentialPropertyDisposalsRequestParser
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

  val rawData: CreateAmendCgtResidentialPropertyDisposalsRawData = CreateAmendCgtResidentialPropertyDisposalsRawData(
    nino = validNino,
    taxYear = taxYear,
    body = AnyContentAsJson.apply(validRequestJson)
  )

  val requestModel: CreateAmendCgtResidentialPropertyDisposalsRequestBody = CreateAmendCgtResidentialPropertyDisposalsRequestBody(
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

  val requestData: CreateAmendCgtResidentialPropertyDisposalsRequest = CreateAmendCgtResidentialPropertyDisposalsRequest(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestModel
  )

  val auditData: JsValue = Json.parse(s"""
                                         |{
                                         |  "nino":"$validNino",
                                         |  "taxYear": "$taxYear"
                                         |  }""".stripMargin)

  "CreateAmendCgtResidentialPropertyDisposalsController" should {
    "return a successful response with status OK" when {
      "happy path" in new Test {
        MockedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()

        MockCreateAmendCgtResidentialPropertyDisposalsRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockNrsProxyService
          .submitAsync(validNino, "itsa-cgt-disposal", validRequestJson)
          .returns(())

        MockCreateAmendCgtResidentialPropertyDisposalsService
          .createAndAmend(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(expectedStatus = OK, None, Some(validRequestJson), Some(auditData))
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockCreateAmendCgtResidentialPropertyDisposalsRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTestWithAudit(NinoFormatError, maybeAuditRequestBody = Some(validRequestJson))
      }

      "service returns an error" in new Test {
        MockCreateAmendCgtResidentialPropertyDisposalsRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockNrsProxyService.submitAsync(validNino, "itsa-cgt-disposal", validRequestJson)

        MockCreateAmendCgtResidentialPropertyDisposalsService
          .createAndAmend(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError, maybeAuditRequestBody = Some(validRequestJson))
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetailOld] {

    val controller = new CreateAmendCgtResidentialPropertyDisposalsController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockCreateAmendCgtResidentialPropertyDisposalsRequestParser,
      service = mockCreateAmendCgtResidentialPropertyDisposalsService,
      auditService = mockAuditService,
      nrsProxyService = mockNrsProxyService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] =
      controller.createAmendCgtResidentialPropertyDisposals(validNino, taxYear)(fakePostRequest(validRequestJson))

    def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetailOld] =
      AuditEvent(
        auditType = "CreateAmendCgtResidentialPropertyDisposals",
        transactionName = "Create-Amend-Cgt-Residential-Property-Disposals",
        detail = GenericAuditDetailOld(
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "taxYear" -> taxYear),
          request = requestBody,
          `X-CorrelationId` = correlationId,
          response = auditResponse
        )
      )

    MockedAppConfig.featureSwitches.returns(Configuration("allowTemporalValidationSuspension.enabled" -> true)).anyNumberOfTimes()
  }

}
