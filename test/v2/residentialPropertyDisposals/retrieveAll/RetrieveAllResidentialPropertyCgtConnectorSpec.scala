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

package v2.residentialPropertyDisposals.retrieveAll

import org.scalamock.handlers.CallHandler
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v2.residentialPropertyDisposals.retrieveAll.def1.model.MtdSourceEnum
import v2.residentialPropertyDisposals.retrieveAll.def1.model.request.Def1_RetrieveAllResidentialPropertyRequestData
import v2.residentialPropertyDisposals.retrieveAll.def1.model.response.Def1_RetrieveAllResidentialPropertyCgtResponse
import v2.residentialPropertyDisposals.retrieveAll.model.response.RetrieveAllResidentialPropertyCgtResponse
import uk.gov.hmrc.http.StringContextOps

import scala.concurrent.Future

class RetrieveAllResidentialPropertyCgtConnectorSpec extends ConnectorSpec {

  val nino: String          = "AA111111A"
  val source: MtdSourceEnum = MtdSourceEnum.latest

  val queryParams: Seq[(String, String)] = Seq(("view", source.toDesViewString))

  val response: RetrieveAllResidentialPropertyCgtResponse = Def1_RetrieveAllResidentialPropertyCgtResponse(
    ppdService = None,
    customerAddedDisposals = None
  )

  trait Test {
    _: ConnectorTest =>

    def taxYear: TaxYear = TaxYear.fromMtd("2018-19")

    val request: Def1_RetrieveAllResidentialPropertyRequestData =
      Def1_RetrieveAllResidentialPropertyRequestData(Nino(nino), taxYear, source)

    val connector: RetrieveAllResidentialPropertyCgtConnector =
      new RetrieveAllResidentialPropertyCgtConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    protected def stubHttpResponse(outcome: DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/$nino/${taxYear.asMtd}",
        queryParams
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHttpResponse(outcome: DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/$nino",
        queryParams
      ).returns(Future.successful(outcome))
    }

  }

  "RetrieveAllResidentialPropertyCgtConnector" when {

    "retrieveAllResidentialPropertyCgt" must {
      "return a 200 status for a success scenario" in new DesTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, response))

        stubHttpResponse(outcome)

        val result: DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }
    }

    "retrieveAllResidentialPropertyCgt for Tax Year Specific (TYS)" must {
      "return a 200 status for a success scenario" in new TysIfsTest with Test {
        override def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome = Right(ResponseWrapper(correlationId, response))

        stubTysHttpResponse(outcome)

        val result: DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }
    }
  }

}
