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

package v2.residentialPropertyDisposals.deleteCgtPpdOverrides

import common.errors.RuleOutsideAmendmentWindowError
import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{
  DownstreamErrorCode,
  DownstreamErrors,
  ErrorWrapper,
  InternalError,
  MtdError,
  NinoFormatError,
  NotFoundError,
  RuleTaxYearNotSupportedError,
  TaxYearFormatError
}
import shared.models.outcomes.ResponseWrapper
import support.UnitSpec
import uk.gov.hmrc.http.HeaderCarrier
import v2.residentialPropertyDisposals.deleteCgtPpdOverrides.def1.model.request.Def1_DeleteCgtPpdOverridesRequestData
import v2.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteCgtPpdOverridesServiceSpec extends UnitSpec {

  private val nino: String           = "AA123456A"
  private val taxYear: String        = "2019-20"
  implicit val correlationId: String = "a1e8057e-fbbc-47a8-a8b4-78d9f015c253"

  private val requestData: DeleteCgtPpdOverridesRequestData = Def1_DeleteCgtPpdOverridesRequestData(Nino(nino), TaxYear.fromMtd(taxYear))

  trait Test extends MockDeleteCgtPpdOverridesConnector {
    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new DeleteCgtPpdOverridesService(
      connector = mockDeleteCgtPpdOverridesConnector
    )

  }

  "Delete CGT PPD Overrides service" when {
    "a service call is successful" should {
      "return a mapped result" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))
        MockDeleteCgtPpdOverridesConnector
          .deleteCgtPpdOverrides(requestData)
          .returns(Future.successful(outcome))

        await(service.delete(requestData)) shouldBe outcome
      }
    }
    "a service call is unsuccessful" should {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"return ${error.code} error when $downstreamErrorCode error is returned from the connector" in new Test {
          val outcome = Left(ErrorWrapper(correlationId, error))
          MockDeleteCgtPpdOverridesConnector
            .deleteCgtPpdOverrides(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.delete(requestData)) shouldBe outcome
        }

      val errors = List(
        "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
        "INVALID_TAX_YEAR"          -> TaxYearFormatError,
        "INVALID_VIEW"              -> InternalError,
        "INVALID_CORRELATIONID"     -> InternalError,
        "NO_DATA_FOUND"             -> NotFoundError,
        "SERVER_ERROR"              -> InternalError
      )

      val extraTysErrors = List(
        "TAX_YEAR_NOT_SUPPORTED"   -> RuleTaxYearNotSupportedError,
        "OUTSIDE_AMENDMENT_WINDOW" -> RuleOutsideAmendmentWindowError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError).tupled(args))
    }
  }

}
