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

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import support.UnitSpec
import v3.otherCgt.retrieve.def2.fixture.Def2_RetrieveOtherCgtFixture.{
  otherGainsResponseModel,
  otherGainsValidDownstreamResponseJson,
  otherGainsValidMtdResponseJson
}
import v3.otherCgt.retrieve.def2.model.response.OtherGains

class OtherGainsSpec extends UnitSpec {

  val minimumValidResponseJson: JsValue = Json.parse(
    """
      |{
      |   "assetType": "otherProperty",
      |   "numberOfDisposals": 1,
      |   "assetDescription": "example of this asset",
      |   "acquisitionDate": "2025-04-07",
      |   "disposalDate": "2025-07-10",
      |   "disposalProceeds": 99999999999.99,
      |   "allowableCosts": 99999999999.99,
      |   "gainsBeforeLosses": 99999999999.99
      |}
     """.stripMargin
  )

  val minimumResponseModel: OtherGains = OtherGains(
    assetType = "other-property",
    numberOfDisposals = 1,
    assetDescription = "example of this asset",
    companyName = None,
    companyRegistrationNumber = None,
    acquisitionDate = "2025-04-07",
    disposalDate = "2025-07-10",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = None,
    gainsWithInv = None,
    gainsBeforeLosses = 99999999999.99,
    losses = None,
    claimOrElectionCodes = None,
    amountOfNetGain = None,
    amountOfNetLoss = None,
    rttTaxPaid = None
  )

  "OtherGains" when {
    "read from valid JSON" should {
      "produce the expected response model" in {
        otherGainsValidDownstreamResponseJson.as[OtherGains] shouldBe otherGainsResponseModel
      }
    }

    "read from the minimum valid JSON" should {
      "produce the expected response model" in {
        minimumValidResponseJson.as[OtherGains] shouldBe minimumResponseModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        JsObject.empty.validate[OtherGains] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(otherGainsResponseModel) shouldBe otherGainsValidMtdResponseJson
      }
    }
  }

}
