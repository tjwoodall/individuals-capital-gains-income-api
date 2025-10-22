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

package v3.otherCgt.createAmend.def2.model.request

import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import shared.models.utils.JsonErrorValidators
import support.UnitSpec
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.{cryptoassetsModel, cryptoassetsMtdJson}

class CryptoassetsSpec extends UnitSpec with JsonErrorValidators {

  private val model: Cryptoassets = cryptoassetsModel.copy(amountOfNetLoss = Some(99999999999.99))

  private val mtdJson: JsValue = cryptoassetsMtdJson.update("/amountOfNetLoss", JsNumber(99999999999.99))

  private val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "description string",
      |  "tokenName": "Name of token",
      |  "acquisitionDate": "2025-08-04",
      |  "disposalDate": "2025-09-04",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  "Cryptoassets" when {
    "read from valid JSON" should {
      "produce the expected model" in {
        mtdJson.as[Cryptoassets] shouldBe model
      }
    }

    "read from empty JSON" should {
      "produce an error" in {
        JsObject.empty.validate[Cryptoassets].isError shouldBe true
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(model) shouldBe downstreamJson
      }
    }
  }

}
