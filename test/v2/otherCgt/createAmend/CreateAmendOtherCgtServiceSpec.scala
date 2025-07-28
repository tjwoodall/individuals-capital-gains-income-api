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

package v2.otherCgt.createAmend

import common.errors.{RuleAcquisitionDateError, RuleDisposalDateNotFutureError, RuleOutsideAmendmentWindowError}
import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{
  DownstreamErrorCode,
  DownstreamErrors,
  ErrorWrapper,
  InternalError,
  MtdError,
  NinoFormatError,
  RuleTaxYearNotSupportedError,
  TaxYearFormatError
}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v2.otherCgt.createAmend.def1.fixture.Def1_CreateAmendOtherCgtConnectorServiceFixture.mtdRequestBody
import v2.otherCgt.createAmend.def1.model.request.Def1_CreateAmendOtherCgtRequestData
import v2.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

import scala.concurrent.Future

class CreateAmendOtherCgtServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = TaxYear.fromMtd("2019-20")

  val createAmendOtherCgtRequestData: CreateAmendOtherCgtRequestData = Def1_CreateAmendOtherCgtRequestData(
    nino = Nino(nino),
    taxYear = taxYear,
    body = mtdRequestBody
  )

  trait Test extends MockCreateAmendOtherCgtConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("Other", "amend")

    val service: CreateAmendOtherCgtService = new CreateAmendOtherCgtService(
      connector = mockCreateAmendOtherCgtDisposalsAndGainsConnector
    )

  }

  "createAndAmend" should {
    "return a success response" when {
      "a valid request is made" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        MockCreateAmendOtherCgtConnector
          .createAndAmend(createAmendOtherCgtRequestData)
          .returns(Future.successful(outcome))

        await(service.createAmend(createAmendOtherCgtRequestData)) shouldBe outcome
      }
    }

    "map errors according to spec" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the connector" in new Test {

          MockCreateAmendOtherCgtConnector
            .createAndAmend(createAmendOtherCgtRequestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.createAmend(createAmendOtherCgtRequestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_CORRELATIONID", InternalError),
        ("INVALID_PAYLOAD", InternalError),
        ("INVALID_DISPOSAL_DATE", RuleDisposalDateNotFutureError),
        ("INVALID_ACQUISITION_DATE", RuleAcquisitionDateError),
        ("OUTSIDE_AMENDMENT_WINDOW", RuleOutsideAmendmentWindowError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      val extraTysErrors = List(
        ("INVALID_CORRELATION_ID" -> InternalError),
        ("TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError).tupled(args))
    }
  }

}
