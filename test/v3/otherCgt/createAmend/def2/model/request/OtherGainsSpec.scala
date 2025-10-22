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
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.{otherGainsModel, otherGainsMtdJson}

class OtherGainsSpec extends UnitSpec with JsonErrorValidators {

  private val model: OtherGains = otherGainsModel.copy(amountOfNetLoss = Some(99999999999.99))

  private val mtdJson: JsValue = otherGainsMtdJson.update("/amountOfNetLoss", JsNumber(99999999999.99))

  private val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "assetType": "listedShares",
      |  "numberOfDisposals": 1,
      |  "assetDescription": "example of this asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2025-04-07",
      |  "disposalDate": "2025-07-10",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsWithINV": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["INV"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  "OtherGains" when {
    "read from valid JSON" should {
      "produce the expected model" in {
        mtdJson.as[OtherGains] shouldBe model
      }
    }

    "read from empty JSON" should {
      "produce an error" in {
        JsObject.empty.validate[OtherGains].isError shouldBe true
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(model) shouldBe downstreamJson
      }
    }
  }

}
