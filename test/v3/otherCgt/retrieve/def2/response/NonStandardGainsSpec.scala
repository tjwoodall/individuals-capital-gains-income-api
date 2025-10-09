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
import v3.otherCgt.retrieve.def2.fixture.Def2_RetrieveOtherCgtFixture.{nonStandardGainsResponseModel, nonStandardGainsValidResponseJson}
import v3.otherCgt.retrieve.def2.model.response.{Def2_RetrieveOtherCgtResponse, NonStandardGains}

class NonStandardGainsSpec extends UnitSpec {

  val minimumValidResponseJson: JsValue = JsObject.empty

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |     "attributedGains":true
      |}
     """.stripMargin
  )

  val minimumResponseModel: NonStandardGains = NonStandardGains(
    attributedGains = None,
    attributedGainsRttTaxPaid = None,
    otherGains = None,
    otherGainsRttTaxPaid = None
  )

  "NonStandardGains" when {
    "read from valid JSON" should {
      "produce the expected response model" in {
        nonStandardGainsValidResponseJson.as[NonStandardGains] shouldBe nonStandardGainsResponseModel
      }
    }

    "read from the minimum valid JSON" should {
      "produce the expected response model" in {
        minimumValidResponseJson.as[NonStandardGains] shouldBe minimumResponseModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        invalidJson.validate[Def2_RetrieveOtherCgtResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(nonStandardGainsValidResponseJson) shouldBe nonStandardGainsValidResponseJson
      }
    }
  }

}
