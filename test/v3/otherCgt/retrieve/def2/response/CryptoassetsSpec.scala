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

package v3.otherCgt.retrieve.def2.response

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v3.otherCgt.retrieve.def2.fixture.Def2_RetrieveOtherCgtFixture.{
  cryptoassetsResponseModel,
  cryptoassetsValidDownstreamResponseJson,
  cryptoassetsValidMtdResponseJson
}
import v3.otherCgt.retrieve.def2.model.response.{Cryptoassets, Def2_RetrieveOtherCgtResponse}

class CryptoassetsSpec extends UnitSpec {

  val minimumValidResponseJson: JsValue = Json.parse(
    """
      |{
      |     "numberOfDisposals": 1,
      |     "assetDescription": "description string",
      |     "tokenName": "Name of token",
      |     "acquisitionDate": "2025-08-04",
      |     "disposalDate": "2025-09-04",
      |     "disposalProceeds": 99999999999.99,
      |     "allowableCosts": 99999999999.99,
      |     "gainsBeforeLosses": 99999999999.99
      |}
       """.stripMargin
  )

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |   "numberOfDisposals":true
      |}
       """.stripMargin
  )

  val minimumResponseModel: Cryptoassets = Cryptoassets(
    numberOfDisposals = 1,
    assetDescription = "description string",
    tokenName = "Name of token",
    acquisitionDate = "2025-08-04",
    disposalDate = "2025-09-04",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = None,
    gainsBeforeLosses = 99999999999.99,
    losses = None,
    claimOrElectionCodes = None,
    amountOfNetGain = None,
    amountOfNetLoss = None,
    rttTaxPaid = None
  )

  "Cryptoassets" when {
    "read from valid JSON" should {
      "produce the expected response model" in {
        cryptoassetsValidDownstreamResponseJson.as[Cryptoassets] shouldBe cryptoassetsResponseModel
      }
    }

    "read from the minimum valid JSON" should {
      "produce the expected response model" in {
        minimumValidResponseJson.as[Cryptoassets] shouldBe minimumResponseModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        invalidJson.validate[Def2_RetrieveOtherCgtResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(cryptoassetsResponseModel) shouldBe cryptoassetsValidMtdResponseJson
      }
    }
  }

}
