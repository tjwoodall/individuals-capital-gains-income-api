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

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.{qualifyingAssetHoldingCompanyModel, qualifyingAssetHoldingCompanyMtdJson}

class QualifyingAssetHoldingCompanySpec extends UnitSpec {

  private val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "gainsFromQAHCBeforeLosses": 99999999999.99,
      |  "lossesFromQAHC": 99999999999.99
      |}
    """.stripMargin
  )

  "QualifyingAssetHoldingCompany" when {
    "read from valid JSON" should {
      "produce the expected model" in {
        qualifyingAssetHoldingCompanyMtdJson.as[QualifyingAssetHoldingCompany] shouldBe qualifyingAssetHoldingCompanyModel
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(qualifyingAssetHoldingCompanyModel) shouldBe downstreamJson
      }
    }
  }

}
