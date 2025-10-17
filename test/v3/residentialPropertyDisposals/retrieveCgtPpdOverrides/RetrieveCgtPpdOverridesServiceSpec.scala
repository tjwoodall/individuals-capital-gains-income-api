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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides

import common.errors.SourceFormatError
import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.model.response.Def1_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.request.Def2_RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response.Def2_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum

import scala.concurrent.Future

class RetrieveCgtPpdOverridesServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = "2025-26"

  val request: Def2_RetrieveCgtPpdOverridesRequestData = Def2_RetrieveCgtPpdOverridesRequestData(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear),
    source = MtdSourceEnum.latest
  )

  trait Test extends MockRetrieveCgtPpdOverridesConnector {

    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "RetrieveCgtPpdOverrides")

    val service: RetrieveCgtPpdOverridesService = new RetrieveCgtPpdOverridesService(
      connector = mockRetrieveCgtPpdOverridesConnector
    )

  }

  "RetrieveCgtPpdOverridesService" should {
    "return correct result for a success" when {
      "using schema Def1" in new Test {
        val response: Def1_RetrieveCgtPpdOverridesResponse = Def1_RetrieveCgtPpdOverridesResponse(Some(2000.99), None, None)
        val outcome                                        = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtPpdOverridesConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }

      "using schema Def2" in new Test {
        val response: Def2_RetrieveCgtPpdOverridesResponse = Def2_RetrieveCgtPpdOverridesResponse(Some(2000.99), None, None)
        val outcome                                        = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtPpdOverridesConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }
    }

    "return not found when no ppd data contained in response" when {
      "using schema Def1" in new Test {
        val response: Def1_RetrieveCgtPpdOverridesResponse = Def1_RetrieveCgtPpdOverridesResponse(None, None, None)
        val outcome                                        = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtPpdOverridesConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
      }

      "using schema Def2" in new Test {
        val response: Def2_RetrieveCgtPpdOverridesResponse = Def2_RetrieveCgtPpdOverridesResponse(None, None, None)
        val outcome                                        = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtPpdOverridesConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
      }
    }

    "map errors according to spec" when {

      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockRetrieveCgtPpdOverridesConnector
            .retrieve(request)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_VIEW", SourceFormatError),
        ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
        ("INVALID_CORRELATIONID", InternalError),
        ("INVALID_CORRELATION_ID", InternalError),
        ("NO_DATA_FOUND", NotFoundError),
        ("SERVER_ERROR", InternalError),
        ("SERVICE_UNAVAILABLE", InternalError)
      )

      errors.foreach(args => serviceError.tupled(args))
    }
  }

}
