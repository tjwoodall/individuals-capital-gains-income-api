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

package v2.residentialPropertyDisposals.retrieveAll

import common.errors.SourceFormatError
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
import shared.services.ServiceSpec
import uk.gov.hmrc.http.HeaderCarrier
import v2.residentialPropertyDisposals.retrieveAll.def1.model.MtdSourceEnum
import v2.residentialPropertyDisposals.retrieveAll.def1.model.request.Def1_RetrieveAllResidentialPropertyRequestData
import v2.residentialPropertyDisposals.retrieveAll.def1.model.response.{Def1_RetrieveAllResidentialPropertyCgtResponse, PpdService}
import v2.residentialPropertyDisposals.retrieveAll.model.request.RetrieveAllResidentialPropertyCgtRequestData

import scala.concurrent.Future

class RetrieveAllResidentialPropertyCgtServiceSpec extends ServiceSpec {

  private val nino    = "AA112233A"
  private val taxYear = "2019-20"

  val request: RetrieveAllResidentialPropertyCgtRequestData = Def1_RetrieveAllResidentialPropertyRequestData(
    nino = Nino(nino),
    taxYear = TaxYear.fromMtd(taxYear),
    source = MtdSourceEnum.latest
  )

  val response: Def1_RetrieveAllResidentialPropertyCgtResponse = Def1_RetrieveAllResidentialPropertyCgtResponse(
    ppdService = Some(
      PpdService(
        ppdYearToDate = Some(2000.99),
        multiplePropertyDisposals = None,
        singlePropertyDisposals = None
      )
    ),
    customerAddedDisposals = None
  )

  trait Test extends MockRetrieveAllResidentialPropertyCgtConnector {

    implicit val hc: HeaderCarrier              = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("controller", "RetrieveAllResidentialPropertyCgt")

    val service: RetrieveAllResidentialPropertyCgtService = new RetrieveAllResidentialPropertyCgtService(
      connector = mockRetrieveAllResidentialPropertyCgtConnector
    )

  }

  "RetrieveAllResidentialPropertyCgtService" when {

    "retrieve" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, response))

        MockRetrieveAllResidentialPropertyCgtConnector
          .retrieve(request)
          .returns(Future.successful(outcome))

        await(service.retrieve(request)) shouldBe outcome
      }

      "map errors according to spec" when {

        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockRetrieveAllResidentialPropertyCgtConnector
              .retrieve(request)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.retrieve(request)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_CORRELATIONID", InternalError),
          ("NO_DATA_FOUND", NotFoundError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError),
          ("INVALID_VIEW", SourceFormatError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError)
        )

        errors.foreach(args => (serviceError).tupled(args))
      }
    }
  }

}
