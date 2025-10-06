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

package v3.residentialPropertyDisposals.retrieveNonPpd

import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.fixture.Def1_RetrieveCgtResidentialPropertyControllerFixture
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.request.Def1_RetrieveCgtResidentialPropertyRequestData
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response.Def1_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.def2.fixture.Def2_RetrieveCgtResidentialPropertyControllerFixture
import v3.residentialPropertyDisposals.retrieveNonPpd.def2.model.response.Def2_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.model.request.RetrieveCgtResidentialPropertyRequestData

import scala.concurrent.Future

class RetrieveCgtResidentialPropertyServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = "2019-20"

  val request: RetrieveCgtResidentialPropertyRequestData = Def1_RetrieveCgtResidentialPropertyRequestData(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  trait Test extends MockRetrieveCgtResidentialPropertyConnector {

    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "RetrieveCgtResidentialProperty")

    val service: RetrieveCgtResidentialPropertyService = new RetrieveCgtResidentialPropertyService(
      connector = mockRetrieveCgtResidentialPropertyConnector
    )

  }

  "RetrieveCgtResidentialPropertyService" when {

    "retrieve" must {
      "return correct result for a success using schema Def1" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, Def1_RetrieveCgtResidentialPropertyControllerFixture.responseModel))

        MockRetrieveCgtResidentialPropertyConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }

      "return correct result for a success using schema Def2" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, Def2_RetrieveCgtResidentialPropertyControllerFixture.responseModel))

        MockRetrieveCgtResidentialPropertyConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }

      "return NotFoundError when no customerAddedDisposals are found using schema Def1" in new Test {
        val response = Def1_RetrieveCgtResidentialPropertyResponse(customerAddedDisposals = None)

        val outcome = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtResidentialPropertyConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
      }

      "return NotFoundError when no customerAddedDisposals are found using schema Def2" in new Test {
        val response = Def2_RetrieveCgtResidentialPropertyResponse(customerAddedDisposals = None)

        val outcome = Right(ResponseWrapper(correlationId, response))

        MockRetrieveCgtResidentialPropertyConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, NotFoundError))
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockRetrieveCgtResidentialPropertyConnector
              .retrieve(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATIONID", InternalError),
          ("INVALID_CORRELATION_ID", InternalError),
          ("NO_DATA_FOUND", NotFoundError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError),
          ("INVALID_VIEW", InternalError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        errors.foreach(args => serviceError.tupled(args))
      }
    }
  }

}
