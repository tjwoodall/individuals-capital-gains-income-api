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

package v1.residentialPropertyDisposals.createAmendNonPpd

import common.connectors.CgtConnectorSpec
import play.api.Configuration
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v1.residentialPropertyDisposals.createAmendNonPpd.def1.fixture.Def1_CreateAmendCgtResidentialPropertyDisposalsServiceConnectorFixture.requestBody
import v1.residentialPropertyDisposals.createAmendNonPpd.def1.model.request.Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData

import scala.concurrent.Future

class CreateAmendCgtResidentialPropertyDisposalsConnectorSpec extends CgtConnectorSpec {

  private val nino: String = "AA111111A"

  trait Test { self: ConnectorTest =>

    val connector: CreateAmendCgtResidentialPropertyDisposalsConnector = new CreateAmendCgtResidentialPropertyDisposalsConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    def taxYear: TaxYear

    val createAmendCgtResidentialPropertyDisposalsRequest: Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData =
      Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData(
        nino = Nino(nino),
        taxYear = taxYear,
        body = requestBody
      )

  }

  "createAndAmend" should {
    "return a 204 status" when {
      "a valid request is made" in new Api1661Test with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/disposals/residential-property/$nino/2019-20",
          body = requestBody
        )
          .returns(Future.successful(outcome))

        await(connector.createAndAmend(createAmendCgtResidentialPropertyDisposalsRequest)) shouldBe outcome
      }

      "a valid request is made for a TYS tax year on IFS" in new IfsTest with Test {
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1952.enabled" -> false))
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/disposals/residential-property/23-24/$nino",
          body = requestBody
        )
          .returns(Future.successful(outcome))

        await(connector.createAndAmend(createAmendCgtResidentialPropertyDisposalsRequest)) shouldBe outcome
      }

      "a valid request is made for a TYS tax year on HIP" in new HipTest with Test {
        MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("ifs_hip_migration_1952.enabled" -> true))

        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/itsa/income-tax/v1/23-24/income/disposals/residential-property/$nino",
          body = requestBody
        )
          .returns(Future.successful(outcome))

        await(connector.createAndAmend(createAmendCgtResidentialPropertyDisposalsRequest)) shouldBe outcome
      }
    }
  }

}
