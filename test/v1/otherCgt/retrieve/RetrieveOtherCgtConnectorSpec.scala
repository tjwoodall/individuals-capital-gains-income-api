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

package v1.otherCgt.retrieve

import common.connectors.CgtConnectorSpec
import play.api.Configuration
import shared.models.domain.{Nino, TaxYear, Timestamp}
import shared.models.errors.NinoFormatError
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v1.otherCgt.retrieve.def1.model.request.Def1_RetrieveOtherCgtRequestData
import v1.otherCgt.retrieve.def1.model.response.Def1_RetrieveOtherCgtResponse
import v1.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData
import v1.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

import scala.concurrent.Future

class RetrieveOtherCgtConnectorSpec extends CgtConnectorSpec {

  "RetrieveOtherCgtConnector" should {
    "return the expected response for a non-TYS request" when {
      "a valid request is made" in new Api1661Test with Test {
        override def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

        val outcome: Right[Nothing, ResponseWrapper[RetrieveOtherCgtResponse]] = Right(ResponseWrapper(correlationId, response))

        willGet(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/$nino/2019-20"
        ).returns(Future.successful(outcome))

        await(connector.retrieveOtherCgt(request)) shouldBe outcome
      }
    }

    "return the expected response for a TYS request" when {
      "a valid request is made" in new IfsTest with Test {
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1951.enabled" -> false))
        val outcome: Right[Nothing, ResponseWrapper[RetrieveOtherCgtResponse]] = Right(ResponseWrapper(correlationId, response))

        willGet(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/23-24/$nino"
        ).returns(Future.successful(outcome))

        await(connector.retrieveOtherCgt(request)) shouldBe outcome
      }
    }

    "return a success response when feature switch is enabled (HIP enabled)" in new HipTest with Test {
      MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1951.enabled" -> true))
      val outcome: Right[Nothing, ResponseWrapper[RetrieveOtherCgtResponse]] = Right(ResponseWrapper(correlationId, response))

      willGet(
        url = url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/other-gains/$nino"
      ).returns(Future.successful(outcome))

      await(connector.retrieveOtherCgt(request)) shouldBe outcome
    }

    "given a request returning an error" must {
      "return an unsuccessful response with the correct correlationId and a single error" in new HipTest with Test {
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1951.enabled" -> true))

        val outcome: Left[ResponseWrapper[NinoFormatError.type], Nothing] = Left(ResponseWrapper(correlationId, NinoFormatError))

        willGet(
          url = url"$baseUrl/itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/other-gains/$nino"
        ).returns(Future.successful(outcome))

        await(connector.retrieveOtherCgt(request)) shouldBe outcome
      }
    }
  }

  trait Test {
    self: ConnectorTest =>

    def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

    protected val nino: String = "AA111111A"

    protected val request: RetrieveOtherCgtRequestData =
      Def1_RetrieveOtherCgtRequestData(
        nino = Nino(nino),
        taxYear = taxYear
      )

    val response: RetrieveOtherCgtResponse = Def1_RetrieveOtherCgtResponse(
      submittedOn = Timestamp("2021-05-07T16:18:44.403Z"),
      disposals = None,
      nonStandardGains = None,
      losses = None,
      adjustments = None
    )

    val connector: RetrieveOtherCgtConnector = new RetrieveOtherCgtConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

}
