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

package v1.otherCgt.delete

import common.connectors.CgtConnectorSpec
import shared.models.domain.{Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v1.otherCgt.delete.def1.model.request.Def1_DeleteOtherCgtRequestData
import v1.otherCgt.delete.model.request.DeleteOtherCgtRequestData

import scala.concurrent.Future
class DeleteOtherCgtConnectorSpec extends CgtConnectorSpec {

  "DeleteOtherCgtConnector" should {
    "return the expected response for a non-TYS request" when {
      "a valid request is made" in new Api1661Test with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2019-20")
        val outcome          = Right(ResponseWrapper(correlationId, ()))

        willDelete(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/$nino/2019-20"
        ).returns(Future.successful(outcome))

        await(connector.deleteOtherCgt(request)) shouldBe outcome
      }
    }
    "return the expected response for a TYS request" when {
      "a valid request is made" in new TysIfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")
        val outcome          = Right(ResponseWrapper(correlationId, ()))

        willDelete(
          url = url"$baseUrl/income-tax/income/disposals/other-gains/23-24/$nino"
        ).returns(Future.successful(outcome))

        await(connector.deleteOtherCgt(request)) shouldBe outcome
      }
    }
  }

  trait Test {
    _: ConnectorTest =>

    def taxYear: TaxYear

    protected val nino: String = "AA111111A"

    protected val request: DeleteOtherCgtRequestData =
      Def1_DeleteOtherCgtRequestData(
        nino = Nino(nino),
        taxYear = taxYear
      )

    val connector: DeleteOtherCgtConnector = new DeleteOtherCgtConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

}
