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
import v3.otherCgt.retrieve.def2.fixture.Def2_RetrieveOtherCgtFixture.*
import v3.otherCgt.retrieve.def2.model.response.*

class Def2_RetrieveOtherCgtResponseSpec extends UnitSpec {

  "Def2_RetrieveOtherCgtResponse" when {
    "read from valid JSON" should {
      "produce the expected response model" in {
        fullValidDownstreamResponseJson.as[Def2_RetrieveOtherCgtResponse] shouldBe fullResponseModel
      }
    }

    "read from the minimum valid JSON" should {
      "produce the expected response model" in {
        minimumValidResponseJson.as[Def2_RetrieveOtherCgtResponse] shouldBe minimumResponseModel
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        JsObject.empty.validate[Def2_RetrieveOtherCgtResponse] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(fullResponseModel) shouldBe fullValidMtdResponseJson
      }
    }
  }

}
