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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.model.request

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import support.UnitSpec
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.model.request

class Def2_CreateAmendCgtPpdOverridesRequestBodySpec extends UnitSpec {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78,
      |            "gainsWithBadr": 4367.33,
      |            "gainsWithInv": 3244.22
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99,
      |            "gainsWithBadr": 2212.44
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89,
      |             "gainsWithBadr": 3000.33,
      |             "gainsWithInv": 2000.22
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  val multiplePropertyDisposalsModels: Seq[MultiplePropertyDisposals] =
    Seq(
      MultiplePropertyDisposals(
        "AB0000000092",
        Some(1234.78),
        None,
        Some(4367.33),
        Some(3244.22)
      ),
      MultiplePropertyDisposals(
        "AB0000000098",
        None,
        Some(134.99),
        Some(2212.44),
        None
      )
    )

  val singlePropertyDisposalsModels: Seq[SinglePropertyDisposals] =
    Seq(
      SinglePropertyDisposals(
        "AB0000000098",
        "2020-02-28",
        454.24,
        "2020-03-29",
        3434.45,
        233.45,
        423.34,
        2324.67,
        3434.23,
        Some(436.23),
        Some(234.23),
        Some(4567.89),
        None,
        None,
        None
      ),
      SinglePropertyDisposals(
        "AB0000000091",
        "2020-02-28",
        454.24,
        "2020-03-29",
        3434.45,
        233.45,
        423.34,
        2324.67,
        3434.23,
        Some(436.23),
        Some(234.23),
        None,
        Some(4567.89),
        Some(3000.33),
        Some(2000.22)
      )
    )

  val mtdRequestBody: Def2_CreateAmendCgtPpdOverridesRequestBody =
    request.Def2_CreateAmendCgtPpdOverridesRequestBody(
      Some(multiplePropertyDisposalsModels),
      Some(singlePropertyDisposalsModels)
    )

  val hipJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78,
      |            "gainsWithBADR": 4367.33,
      |            "gainsWithINV": 3244.22
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfLoss": 134.99,
      |            "gainsWithBADR": 2212.44
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfLoss": 4567.89,
      |             "gainsWithBADR": 3000.33,
      |             "gainsWithINV": 2000.22
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  val emptyJson: JsValue = JsObject.empty

  val invalidJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": "notANumber"
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  "CreateAmendPpdOverridesRequestBody" when {
    "read from a valid JSON" should {
      "produce the expected object" in {
        mtdJson.as[Def2_CreateAmendCgtPpdOverridesRequestBody] shouldBe mtdRequestBody
      }
    }

    "read from invalid Json" should {
      "provide a JsError" in {
        invalidJson.validate[Def2_CreateAmendCgtPpdOverridesRequestBody] shouldBe a[JsError]
      }
    }

    "read from an empty JSON" should {
      "produce an empty object" in {
        emptyJson.as[Def2_CreateAmendCgtPpdOverridesRequestBody] shouldBe Def2_CreateAmendCgtPpdOverridesRequestBody.empty
      }
    }

    "written JSON" should {
      "produce the expected JsObject" in {
        Json.toJson(mtdRequestBody) shouldBe hipJson
      }
    }

    "written from an empty object" should {
      "produce an empty JSON" in {
        Json.toJson(Def2_CreateAmendCgtPpdOverridesRequestBody.empty) shouldBe emptyJson
      }
    }
  }

}
