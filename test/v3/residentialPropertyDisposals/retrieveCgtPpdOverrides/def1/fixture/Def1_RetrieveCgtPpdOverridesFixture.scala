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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.fixture

import play.api.libs.json.{JsValue, Json}
import shared.models.domain.Timestamp
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.model.response.{
  Def1_RetrieveCgtPpdOverridesResponse,
  MultiplePropertyDisposals,
  SinglePropertyDisposals
}
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum.`hmrc-held`

object Def1_RetrieveCgtPpdOverridesFixture {

  val singlePropertyDisposals: SinglePropertyDisposals =
    SinglePropertyDisposals(
      `hmrc-held`,
      Some(Timestamp("2020-07-07T10:59:47.544Z")),
      "Da2467289109",
      Some(Timestamp("2020-07-06T09:37:17.123Z")),
      Some("2022-02-04"),
      "2022-03-08",
      1999.99,
      Some("2018-04-06"),
      1999.99,
      Some(1999.99),
      Some(5000.99),
      Some(1999.99),
      Some(1999.99),
      Some(1999.99),
      Some(1999.99),
      Some(1999.99),
      Some(1999.99)
    )

  val multiplePropertyDisposals: MultiplePropertyDisposals =
    MultiplePropertyDisposals(
      `hmrc-held`,
      Some(Timestamp("2020-07-07T10:59:47.544Z")),
      "Da2467289108",
      Some(Timestamp("2020-07-06T09:37:17.123Z")),
      Some(3),
      Some(2022),
      Some("2022-03-08"),
      Some(1999.99),
      Some(1999.99)
    )

  val responseModel: Def1_RetrieveCgtPpdOverridesResponse =
    Def1_RetrieveCgtPpdOverridesResponse(
      Some(1000.00),
      Some(Seq(multiplePropertyDisposals)),
      Some(Seq(singlePropertyDisposals))
    )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |    "ppdYearToDate": 1000.00,
      |    "multiplePropertyDisposals": [
      |        {
      |            "source": "hmrc-held",
      |            "submittedOn": "2020-07-07T10:59:47.544Z",
      |            "ppdSubmissionId": "Da2467289108",
      |            "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |            "numberOfDisposals": 3,
      |            "disposalTaxYear": 2022,
      |            "completionDate": "2022-03-08",
      |            "amountOfNetGain": 1999.99,
      |            "amountOfNetLoss": 1999.99
      |        }
      |    ],
      |    "singlePropertyDisposals": [
      |        {
      |            "source": "hmrc-held",
      |            "submittedOn": "2020-07-07T10:59:47.544Z",
      |            "ppdSubmissionId": "Da2467289109",
      |            "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |            "disposalDate": "2022-02-04",
      |            "completionDate": "2022-03-08",
      |            "disposalProceeds": 1999.99,
      |            "acquisitionDate": "2018-04-06",
      |            "acquisitionAmount": 1999.99,
      |            "improvementCosts": 1999.99,
      |            "additionalCosts": 5000.99,
      |            "prfAmount": 1999.99,
      |            "otherReliefAmount": 1999.99,
      |            "lossesFromThisYear": 1999.99,
      |            "lossesFromPreviousYear": 1999.99,
      |            "amountOfNetGain": 1999.99,
      |            "amountOfNetLoss": 1999.99
      |        }
      |    ]
      |}
      |""".stripMargin
  )

  val downstreamJson: JsValue = Json.parse("""
      |{
      |  "ppdService": {
      |    "ppdYearToDate": 1000.00,
      |    "multiplePropertyDisposals": [
      |      {
      |        "source": "HMRC HELD",
      |        "submittedOn": "2020-07-07T10:59:47.544Z",
      |        "ppdSubmissionId": "Da2467289108",
      |        "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |        "numberOfDisposals": 3,
      |        "disposalTaxYear": "2022",
      |        "completionDate": "2022-03-08",
      |        "amountOfNetGain": 1999.99,
      |        "amountOfLoss": 1999.99
      |      }
      |    ],
      |    "singlePropertyDisposals": [
      |      {
      |        "source": "HMRC HELD",
      |        "submittedOn": "2020-07-07T10:59:47.544Z",
      |        "ppdSubmissionId": "Da2467289109",
      |        "ppdSubmissionDate": "2020-07-06T09:37:17.123Z",
      |        "disposalDate": "2022-02-04",
      |        "completionDate": "2022-03-08",
      |        "disposalProceeds": 1999.99,
      |        "acquisitionDate": "2018-04-06",
      |        "acquisitionAmount": 1999.99,
      |        "improvementCosts": 1999.99,
      |        "additionalCosts": 5000.99,
      |        "prfAmount": 1999.99,
      |        "otherReliefAmount": 1999.99,
      |        "lossesFromThisYear": 1999.99,
      |        "lossesFromPreviousYear": 1999.99,
      |        "amountOfNetGain": 1999.99,
      |        "amountOfLoss": 1999.99
      |      }
      |    ]
      |  },
      |  "customerAddedDisposals": {
      |    "submittedOn": "2020-07-07T10:59:47.544Z",
      |    "disposals": [
      |      {
      |        "customerReference": "CGTDISPOSAL01",
      |        "disposalDate": "2022-02-04",
      |        "completionDate": "2022-03-08",
      |        "disposalProceeds": 1999.99,
      |        "acquisitionDate": "2018-04-06",
      |        "acquisitionAmount": 1999.99,
      |        "improvementCosts": 1999.99,
      |        "additionalCosts": 5000.99,
      |        "prfAmount": 1999.99,
      |        "otherReliefAmount": 1999.99,
      |        "lossesFromThisYear": 1999.99,
      |        "lossesFromPreviousYear": 1999.99,
      |        "amountOfNetGain": 1999.99
      |      }
      |    ]
      |  }
      |}
      |""".stripMargin)

}
