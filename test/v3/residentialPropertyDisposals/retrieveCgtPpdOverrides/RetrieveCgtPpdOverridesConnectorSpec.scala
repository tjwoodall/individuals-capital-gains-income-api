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

import org.scalamock.handlers.CallHandler
import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.request.Def2_RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response.Def2_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.response.RetrieveCgtPpdOverridesResponse

import scala.concurrent.Future

class RetrieveCgtPpdOverridesConnectorSpec extends ConnectorSpec {

  val nino: String          = "AA111111A"
  val source: MtdSourceEnum = MtdSourceEnum.latest

  val queryParams: Seq[(String, String)] = Seq(("view", source.toDownstreamViewString))

  val response: RetrieveCgtPpdOverridesResponse = Def2_RetrieveCgtPpdOverridesResponse(None, None, None)

  trait Test {
    self: ConnectorTest =>

    def taxYear: TaxYear

    val request: Def2_RetrieveCgtPpdOverridesRequestData = Def2_RetrieveCgtPpdOverridesRequestData(Nino(nino), taxYear, source)

    val connector: RetrieveCgtPpdOverridesConnector = new RetrieveCgtPpdOverridesConnector(mockHttpClient, mockSharedAppConfig)

    protected def stubHttpResponse(outcome: DownstreamOutcome[RetrieveCgtPpdOverridesResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtPpdOverridesResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/$nino/${taxYear.asMtd}",
        queryParams
      ).returns(Future.successful(outcome))
    }

    protected def stubTysIfsHttpResponse(outcome: DownstreamOutcome[RetrieveCgtPpdOverridesResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtPpdOverridesResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/$nino",
        queryParams
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHipHttpResponse(outcome: DownstreamOutcome[RetrieveCgtPpdOverridesResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtPpdOverridesResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/$nino",
        queryParams
      ).returns(Future.successful(outcome))
    }

  }

  "RetrieveCgtPpdOverridesConnector" should {

    "return a 200 status for a success" when {
      "the request is for Non-TYS tax years" in new DesTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2018-19")
        val outcome          = Right(ResponseWrapper(correlationId, response))

        stubHttpResponse(outcome)

        val result = await(connector.retrieve(request))
        result shouldBe outcome
      }

      "the request is for TYS tax years and the downstream is IFS" in new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val outcome          = Right(ResponseWrapper(correlationId, response))

        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1881.enabled" -> false))
        stubTysIfsHttpResponse(outcome)

        val result = await(connector.retrieve(request))
        result shouldBe outcome
      }

      "the request is for TYS tax years and the downstream is HIP" in new HipTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val outcome          = Right(ResponseWrapper(correlationId, response))

        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1881.enabled" -> true))
        stubTysHipHttpResponse(outcome)

        val result = await(connector.retrieve(request))
        result shouldBe outcome
      }
    }
  }

}
