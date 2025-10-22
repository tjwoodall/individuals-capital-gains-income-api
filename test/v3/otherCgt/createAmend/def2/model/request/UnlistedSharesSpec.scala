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

import play.api.libs.json.{JsObject, JsValue, Json}
import support.UnitSpec
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.{unlistedSharesModel, unlistedSharesMtdJson}

class UnlistedSharesSpec extends UnitSpec {

  private val downstreamJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "My asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2025-04-10",
      |  "disposalDate": "2025-04-12",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsWithINV": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO"],
      |  "gainsReportedOnRtt": 99999999999.99,
      |  "gainsExceedingLifetimeLimit": 99999999999.99,
      |  "gainsUnderSEIS": 99999999999.99,
      |  "lossUsedAgainstGeneralIncome": 99999999999.99,
      |  "eisOrSeisReliefDueCurrentYear": 99999999999.99,
      |  "lossesUsedAgainstGeneralIncomePreviousYear": 99999999999.99,
      |  "eisOrSeisReliefDuePreviousYear": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  "UnlistedShares" when {
    "read from valid JSON" should {
      "produce the expected model" in {
        unlistedSharesMtdJson.as[UnlistedShares] shouldBe unlistedSharesModel
      }
    }

    "read from empty JSON" should {
      "produce an error" in {
        JsObject.empty.validate[UnlistedShares].isError shouldBe true
      }
    }

    "written to JSON" should {
      "produce the expected JSON" in {
        Json.toJson(unlistedSharesModel) shouldBe downstreamJson
      }
    }
  }

}
