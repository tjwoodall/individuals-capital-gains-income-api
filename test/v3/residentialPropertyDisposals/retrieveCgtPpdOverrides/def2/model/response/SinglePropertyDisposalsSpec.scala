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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.fixture.Def2_RetrieveCgtPpdOverridesFixture.singlePropertyDisposals

class SinglePropertyDisposalsSpec extends UnitSpec {

  val downstreamJson: JsValue = Json.parse(
    """
      |{
      |   "source": "HMRC HELD",
      |   "submittedOn": "2020-07-07T10:59:47.544Z",
      |   "ppdSubmissionId": "Da2467289109",
      |   "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |   "disposalDate": "2022-02-04",
      |   "completionDate": "2022-03-08",
      |   "disposalProceeds": 1999.99,
      |   "acquisitionDate": "2018-04-06",
      |   "acquisitionAmount": 1999.99,
      |   "improvementCosts": 1999.99,
      |   "additionalCosts": 5000.99,
      |   "prfAmount": 1999.99,
      |   "otherReliefAmount": 1999.99,
      |   "gainsWithBADR": 1999.99,
      |   "gainsWithINV": 1999.99,
      |   "lossesFromThisYear": 1999.99,
      |   "lossesFromPreviousYear": 1999.99,
      |   "amountOfNetGain": 1999.99,
      |   "amountOfNetLoss": 1999.99
      |}
      |""".stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "source": "hmrc-held",
      |   "submittedOn": "2020-07-07T10:59:47.544Z",
      |   "ppdSubmissionId": "Da2467289109",
      |   "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |   "disposalDate": "2022-02-04",
      |   "completionDate": "2022-03-08",
      |   "disposalProceeds": 1999.99,
      |   "acquisitionDate": "2018-04-06",
      |   "acquisitionAmount": 1999.99,
      |   "improvementCosts": 1999.99,
      |   "additionalCosts": 5000.99,
      |   "prfAmount": 1999.99,
      |   "otherReliefAmount": 1999.99,
      |   "gainsWithBadr": 1999.99,
      |   "gainsWithInv": 1999.99,
      |   "lossesFromThisYear": 1999.99,
      |   "lossesFromPreviousYear": 1999.99,
      |   "amountOfNetGain": 1999.99,
      |   "amountOfNetLoss": 1999.99
      |}
      |""".stripMargin
  )

  "SinglePropertyDisposals" should {
    "use reads to return a valid object" when {
      "valid json is supplied" in {
        downstreamJson.as[SinglePropertyDisposals] shouldBe singlePropertyDisposals
      }
    }

    "use writes to produce the expected json" in {
      Json.toJson(singlePropertyDisposals) shouldBe mtdJson
    }
  }

}
