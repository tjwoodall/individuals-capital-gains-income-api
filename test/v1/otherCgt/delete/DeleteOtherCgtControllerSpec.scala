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

package v1.otherCgt.delete

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import utils.MockIdGenerator
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import config.MockAppConfig
import play.api.libs.json.JsValue
import play.api.mvc.Result
import play.api.Configuration
import v1.otherCgt.delete.def1.model.request.Def1_DeleteOtherCgtRequestData
import v1.otherCgt.delete.model.request.DeleteOtherCgtRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteOtherCgtControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockDeleteOtherCgtService
    with MockAuditService
    with MockDeleteOtherCgtValidatorFactory
    with MockIdGenerator
    with MockAppConfig {

  val taxYear: String = "2019-20"

  val requestData: DeleteOtherCgtRequestData = Def1_DeleteOtherCgtRequestData(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  "DeleteOtherCgtController" should {
    "return a successful response with status 204 (No Content)" when {
      "a valid request is supplied" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteOtherCgtService
          .delete(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(expectedStatus = NO_CONTENT)
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTestWithAudit(NinoFormatError)
      }

      "service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockDeleteOtherCgtService
          .delete(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTestWithAudit(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new DeleteOtherCgtController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockDeleteOtherCgtValidatorFactory,
      service = mockDeleteOtherCgtService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedAppConfig.featureSwitches.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.deleteOtherCgt(validNino, taxYear)(fakeRequest)

    def event(auditResponse: AuditResponse, maybeRequestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "DeleteOtherCgtDisposalsAndGains",
        transactionName = "Delete-Other-Cgt-Disposals-And-Gains",
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

  }

}
