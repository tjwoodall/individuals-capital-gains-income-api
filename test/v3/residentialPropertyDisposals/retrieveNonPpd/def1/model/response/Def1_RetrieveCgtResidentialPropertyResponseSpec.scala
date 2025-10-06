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

package v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.fixture.Def1_RetrieveCgtResidentialPropertyControllerFixture.*

class Def1_RetrieveCgtResidentialPropertyResponseSpec extends UnitSpec {

  "Def1_RetrieveCgtResidentialPropertyResponse" when {
    "Reads" should {
      "return a valid object" when {
        "a valid json is supplied" in {
          downstreamJson.as[Def1_RetrieveCgtResidentialPropertyResponse] shouldBe responseModel
        }
      }
    }

    "writes" should {
      "produce the expected json" in {
        Json.toJson(responseModel) shouldBe mtdJson
      }
    }
  }

}
