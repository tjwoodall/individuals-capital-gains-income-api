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

import org.scalamock.handlers.CallHandler
import play.api.Configuration
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.request.Def1_RetrieveCgtResidentialPropertyRequestData
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response.Def1_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.model.response.RetrieveCgtResidentialPropertyResponse

import scala.concurrent.Future

class RetrieveCgtResidentialPropertyConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"

  val response: RetrieveCgtResidentialPropertyResponse = Def1_RetrieveCgtResidentialPropertyResponse(
    customerAddedDisposals = None
  )

  trait Test {
    self: ConnectorTest =>

    def taxYear: TaxYear = TaxYear.fromMtd("2018-19")

    val request: Def1_RetrieveCgtResidentialPropertyRequestData =
      Def1_RetrieveCgtResidentialPropertyRequestData(Nino(nino), taxYear)

    val connector: RetrieveCgtResidentialPropertyConnector =
      new RetrieveCgtResidentialPropertyConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    protected def stubHttpResponse(outcome: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtResidentialPropertyResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/$nino/${taxYear.asMtd}"
      ).returns(Future.successful(outcome))
    }

    protected def stubTysIfsHttpResponse(outcome: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtResidentialPropertyResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/$nino"
      ).returns(Future.successful(outcome))
    }

    protected def stubTysHipHttpResponse(outcome: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse])
        : CallHandler[Future[DownstreamOutcome[RetrieveCgtResidentialPropertyResponse]]]#Derived = {
      willGet(
        url = url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/$nino"
      ).returns(Future.successful(outcome))
    }

  }

  "RetrieveCgtResidentialPropertyConnector" must {

    "return a 200 status for a success scenario for Non-TYS tax years" in new DesTest with Test {

      val outcome = Right(ResponseWrapper(correlationId, response))

      stubHttpResponse(outcome)

      val result: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse] = await(connector.retrieve(request))
      result shouldBe outcome
    }
  }

  "retrieve for Tax Year Specific (TYS)" must {
    "return a 200 status for a success scenario for TYS tax Years" when {
      "the downstream is IFS" in new IfsTest with Test {
        override def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val outcome                   = Right(ResponseWrapper(correlationId, response))

        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1881.enabled" -> false))
        stubTysIfsHttpResponse(outcome)

        val result: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }
      "the downstream is HIP" in new HipTest with Test {
        override def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val outcome                   = Right(ResponseWrapper(correlationId, response))
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1881.enabled" -> true))
        stubTysHipHttpResponse(outcome)

        val result: DownstreamOutcome[RetrieveCgtResidentialPropertyResponse] = await(connector.retrieve(request))
        result shouldBe outcome
      }
    }
  }

}
