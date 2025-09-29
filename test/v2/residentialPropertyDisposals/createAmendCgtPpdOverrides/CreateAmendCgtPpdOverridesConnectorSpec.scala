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

package v2.residentialPropertyDisposals.createAmendCgtPpdOverrides

import common.connectors.CgtConnectorSpec
import play.api.Configuration
import shared.connectors.DownstreamOutcome
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.fixture.Def1_CreateAmendCgtPpdOverridesServiceConnectorFixture.requestBodyModel
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.model.request.Def1_CreateAmendCgtPpdOverridesRequestData

import scala.concurrent.Future

class CreateAmendCgtPpdOverridesConnectorSpec extends CgtConnectorSpec {

  trait Test {
    self: ConnectorTest =>

    def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
    val nino: String     = "AA111111A"

    val request = Def1_CreateAmendCgtPpdOverridesRequestData(
      nino = Nino(nino),
      taxYear = taxYear,
      body = requestBodyModel
    )

    protected val connector: CreateAmendCgtPpdOverridesConnector = new CreateAmendCgtPpdOverridesConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

  "CreateAmendCgtPpdOverridesConnector" when {
    "createAndAmend" must {
      "return a 204 status for a success scenario" in new Api1661Test with Test {

        override def taxYear = TaxYear.fromMtd("2019-20")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/disposals/residential-property/ppd/$nino/${taxYear.asMtd}",
          body = requestBodyModel
        )
          .returns(Future.successful(outcome))

        await(connector.createAmend(request)) shouldBe outcome
      }
    }

    "createAndAmend called for a Tax Year Specific tax year" must {
      "return a 200 status for a success scenario" when {
        "the downstream request to IFS is successful" in new IfsTest with Test {

          MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1946.enabled" -> false))
          implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))

          val outcome = Right(ResponseWrapper(correlationId, ()))

          willPut(
            url"$baseUrl/income-tax/income/disposals/residential-property/ppd/${taxYear.asTysDownstream}/${nino}",
            requestBodyModel) returns Future
            .successful(outcome)

          val result = await(connector.createAmend(request))
          result shouldBe outcome
        }

        "the downstream request to HIP is successful" in new HipTest with Test {
          MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1946.enabled" -> true))
          val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

          willPut(url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/ppd/$nino", requestBodyModel)
            .returns(Future.successful(outcome))

          val result: DownstreamOutcome[Unit] = await(connector.createAmend(request))
          result shouldBe outcome
        }
      }
    }

  }

}
