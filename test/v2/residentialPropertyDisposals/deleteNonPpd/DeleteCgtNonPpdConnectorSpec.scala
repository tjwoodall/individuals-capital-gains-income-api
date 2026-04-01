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

package v2.residentialPropertyDisposals.deleteNonPpd

import common.connectors.CgtConnectorSpec
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.NinoFormatError
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v2.residentialPropertyDisposals.deleteNonPpd.def1.model.request.Def1_DeleteCgtNonPpdRequestData
import v2.residentialPropertyDisposals.deleteNonPpd.model.request.DeleteCgtNonPpdRequestData

import scala.concurrent.Future

class DeleteCgtNonPpdConnectorSpec extends CgtConnectorSpec {

  val nino: String = "AA111111A"

  "deleteCgtNonPpd" when {
    "given a valid request (NON-TYS)" must {
      "return a success response" in new Api1661Test with Test {
        override def taxYear: TaxYear = TaxYear.fromMtd("2018-19")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willDelete(url"$baseUrl/income-tax/income/disposals/residential-property/$nino/2018-19")
          .returns(Future.successful(outcome))

        await(connector.deleteCgtNonPpd(request)) shouldBe outcome

      }
    }

    "given a valid request (TYS) to HIP" must {

      "return a success response" in new HipTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willDelete(url"$baseUrl/itsa/income-tax/v1/23-24/income/disposals/residential-property/$nino")
          .returns(Future.successful(outcome))

        await(connector.deleteCgtNonPpd(request)) shouldBe outcome
      }
    }

    "given a request returning an error" must {
      "return an unsuccessful response with the correct correlationId and a single error" in new HipTest with Test {

        val outcome: Left[ResponseWrapper[NinoFormatError.type], Nothing] = Left(ResponseWrapper(correlationId, NinoFormatError))

        willDelete(url"$baseUrl/itsa/income-tax/v1/23-24/income/disposals/residential-property/$nino").returns(Future.successful(outcome))

        await(connector.deleteCgtNonPpd(request)) shouldBe outcome
      }
    }

  }

  trait Test { self: ConnectorTest =>
    def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

    protected val connector: DeleteCgtNonPpdConnector = new DeleteCgtNonPpdConnector(http = mockHttpClient, appConfig = mockSharedAppConfig)

    protected val request: DeleteCgtNonPpdRequestData = Def1_DeleteCgtNonPpdRequestData(Nino("AA111111A"), taxYear = taxYear)

  }

}
