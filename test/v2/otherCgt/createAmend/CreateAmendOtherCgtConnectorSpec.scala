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

package v2.otherCgt.createAmend

import config.MockAppConfig
import shared.connectors.ConnectorSpec
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.otherCgt.createAmend.def1.fixture.Def1_CreateAmendOtherCgtConnectorServiceFixture.mtdRequestBody
import v2.otherCgt.createAmend.def1.model.request.Def1_CreateAmendOtherCgtRequestData
import v2.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

import scala.concurrent.Future

class CreateAmendOtherCgtConnectorSpec extends ConnectorSpec with MockAppConfig {

  private val nino: String = "AA111111A"

  val allowedIfsHeaders: Seq[String] = List(
    "Accept",
    "Gov-Test-Scenario",
    "Content-Type",
    "Location",
    "X-Request-Timestamp",
    "X-Session-Id"
  )

  trait Test { _: ConnectorTest =>
    def taxYear: TaxYear

    val connector: CreateAmendOtherCgtConnector = new CreateAmendOtherCgtConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

  "createAndAmend" should {
    "return a 204 status" when {
      "a valid request is made" in new IfsTest with Test {

        override val taxYear: TaxYear = TaxYear.fromMtd("2019-20")

        val createAmendOtherCgtRequestData: CreateAmendOtherCgtRequestData = Def1_CreateAmendOtherCgtRequestData(
          nino = Nino(nino),
          taxYear = taxYear,
          body = mtdRequestBody
        )

        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/$nino/2019-20",
          body = mtdRequestBody
        ).returns(Future.successful(outcome))

        await(connector.createAndAmend(createAmendOtherCgtRequestData)) shouldBe outcome
      }

      "a valid request is made with Tax Year Specific tax year" in new IfsTest with Test {

        override val taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val createAmendOtherCgtRequestData: CreateAmendOtherCgtRequestData = Def1_CreateAmendOtherCgtRequestData(
          nino = Nino(nino),
          taxYear = taxYear,
          body = mtdRequestBody
        )
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        willPut(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/23-24/$nino",
          body = mtdRequestBody
        ).returns(Future.successful(outcome))

        await(connector.createAndAmend(createAmendOtherCgtRequestData)) shouldBe outcome
      }
    }
  }

}
