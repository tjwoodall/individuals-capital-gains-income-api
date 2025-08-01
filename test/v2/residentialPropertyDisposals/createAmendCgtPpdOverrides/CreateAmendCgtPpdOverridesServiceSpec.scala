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

package v2.residentialPropertyDisposals.createAmendCgtPpdOverrides

import common.errors.{
  PpdSubmissionIdNotFoundError,
  RuleDuplicatedPpdSubmissionIdError,
  RuleIncorrectDisposalTypeError,
  RuleOutsideAmendmentWindowError
}
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
  RuleTaxYearNotEndedError,
  RuleTaxYearNotSupportedError,
  TaxYearFormatError
}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.fixture.Def1_CreateAmendCgtPpdOverridesServiceConnectorFixture.requestBodyModel
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.model.request.Def1_CreateAmendCgtPpdOverridesRequestData
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

import scala.concurrent.Future

class CreateAmendCgtPpdOverridesServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = "2019-20"

  val createAmendCgtPpdOverridesRequest: CreateAmendCgtPpdOverridesRequestData = Def1_CreateAmendCgtPpdOverridesRequestData(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestBodyModel
  )

  trait Test extends MockCreateAmendCgtPpdOverridesConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("Other", "amend")

    val service: CreateAmendCgtPpdOverridesService = new CreateAmendCgtPpdOverridesService(
      connector = mockCreateAmendCgtPpdOverridesConnector
    )

  }

  "CreateAmendCgtPpdOverridesService" when {
    "createAndAmend" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockCreateAmendCgtPpdOverridesConnector
          .createAmend(createAmendCgtPpdOverridesRequest)
          .returns(Future.successful(outcome))

        await(service.createAmend(createAmendCgtPpdOverridesRequest)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the connector" in new Test {

            MockCreateAmendCgtPpdOverridesConnector
              .createAmend(createAmendCgtPpdOverridesRequest)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.createAmend(createAmendCgtPpdOverridesRequest)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        def failuresArrayError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the connector in a failures array" in new Test {

            MockCreateAmendCgtPpdOverridesConnector
              .createAmend(createAmendCgtPpdOverridesRequest)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode(downstreamErrorCode)))))))

            await(service.createAmend(createAmendCgtPpdOverridesRequest)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATIONID", InternalError),
          ("INVALID_PAYLOAD", InternalError),
          ("PPD_SUBMISSIONID_NOT_FOUND", PpdSubmissionIdNotFoundError),
          ("NO_PPD_SUBMISSIONS_FOUND", NotFoundError),
          ("DUPLICATE_SUBMISSION", RuleDuplicatedPpdSubmissionIdError),
          ("INVALID_REQUEST_BEFORE_TAX_YEAR", RuleTaxYearNotEndedError),
          ("INVALID_DISPOSAL_TYPE", RuleIncorrectDisposalTypeError),
          ("OUTSIDE_AMENDMENT_WINDOW", RuleOutsideAmendmentWindowError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError)
        )

        val extraTysErrors = List(
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError).tupled(args))
        (errors ++ extraTysErrors).foreach(args => (failuresArrayError).tupled(args))
      }
    }
  }

}
