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

package v3.residentialPropertyDisposals.createAmendNonPpd.def2.model.request

import play.api.libs.json.Json
import support.UnitSpec

class Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBodySpec extends UnitSpec {

  private val validMtdJson = Json.parse(
    """
      |{
      |  "disposals" : [
      |    {
      |      "numberOfDisposals": 3,
      |      "customerReference" : "ABC-2345",
      |      "disposalDate" : "2021-01-29",
      |      "completionDate" : "2021-04-25",
      |      "disposalProceeds" : 2345.67,
      |      "acquisitionDate" : "2021-03-22",
      |      "acquisitionAmount" : 2341.45,
      |      "improvementCosts" : 345.34,
      |      "additionalCosts" : 234.89,
      |      "prfAmount" : 67.9,
      |      "otherReliefAmount" : 123.89,
      |      "gainsWithBadr": 123.89,
      |      "gainsBeforeLosses": 123.89,
      |      "lossesFromThisYear" : 456.89,
      |      "claimOrElectionCodes": ["PRR"],
      |      "amountOfNetGain" : 567.9,
      |      "amountOfNetLoss" : 567.9
      |    },
      |    { 
      |      "numberOfDisposals": 3,
      |      "customerReference" : "AB3456789012",
      |      "disposalDate" : "2021-01-21",
      |      "completionDate" : "2021-03-20",
      |      "disposalProceeds" : 234.32,
      |      "acquisitionDate" : "2021-02-21",
      |      "acquisitionAmount" : 345.23,
      |      "improvementCosts" : 456.23,
      |      "additionalCosts" : 234.34,
      |      "prfAmount" : 238.34,
      |      "otherReliefAmount" : 267.12,
      |      "gainsWithBadr": 123.89,
      |      "gainsBeforeLosses": 123.89,
      |      "lossesFromThisYear" : 456.89,   
      |      "claimOrElectionCodes": ["PRR"],
      |      "amountOfNetGain" : 567.9,
      |      "amountOfNetLoss" : 567.9
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  private val validDesJson = Json.parse(
    """
      |{
      |  "disposals" : [
      |    {
      |      "numberOfDisposals": 3,
      |      "customerRef" : "ABC-2345",
      |      "disposalDate" : "2021-01-29",
      |      "completionDate" : "2021-04-25",
      |      "disposalProceeds" : 2345.67,
      |      "acquisitionDate" : "2021-03-22",
      |      "acquisitionAmount" : 2341.45,
      |      "improvementCosts" : 345.34,
      |      "additionalCosts" : 234.89,
      |      "prfAmount" : 67.9,
      |      "otherReliefAmount" : 123.89,
      |      "gainsWithBADR": 123.89,
      |      "gainsBeforeLosses": 123.89,
      |      "lossesFromThisYear" : 456.89,
      |      "claimOrElectionCodes": ["PRR"],
      |      "amountOfNetGain" : 567.9,
      |      "amountOfNetLoss" : 567.9
      |    },
      |    {
      |      "numberOfDisposals": 3,
      |      "customerRef" : "AB3456789012",
      |      "disposalDate" : "2021-01-21",
      |      "completionDate" : "2021-03-20",
      |      "disposalProceeds" : 234.32,
      |      "acquisitionDate" : "2021-02-21",
      |      "acquisitionAmount" : 345.23,
      |      "improvementCosts" : 456.23,
      |      "additionalCosts" : 234.34,
      |      "prfAmount" : 238.34,
      |      "otherReliefAmount" : 267.12,
      |      "gainsWithBADR": 123.89,
      |      "gainsBeforeLosses": 123.89,
      |      "lossesFromThisYear" : 456.89,
      |      "claimOrElectionCodes": ["PRR"],
      |      "amountOfNetGain" : 567.9,
      |      "amountOfNetLoss" : 567.9
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  private val validModel = Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody(disposals = Seq(
    Disposal(
      numberOfDisposals = 3,
      customerReference = Some("ABC-2345"),
      disposalDate = "2021-01-29",
      completionDate = "2021-04-25",
      disposalProceeds = 2345.67,
      acquisitionDate = "2021-03-22",
      acquisitionAmount = 2341.45,
      improvementCosts = Some(345.34),
      additionalCosts = Some(234.89),
      prfAmount = Some(67.9),
      otherReliefAmount = Some(123.89),
      gainsWithBadr = Some(123.89),
      gainsBeforeLosses = 123.89,
      lossesFromThisYear = Some(456.89),
      claimOrElectionCodes = Some(Seq("PRR")),
      amountOfNetGain = Some(567.9),
      amountOfNetLoss = Some(567.9)
    ),
    Disposal(
      numberOfDisposals = 3,
      customerReference = Some("AB3456789012"),
      disposalDate = "2021-01-21",
      completionDate = "2021-03-20",
      disposalProceeds = 234.32,
      acquisitionDate = "2021-02-21",
      acquisitionAmount = 345.23,
      improvementCosts = Some(456.23),
      additionalCosts = Some(234.34),
      prfAmount = Some(238.34),
      otherReliefAmount = Some(267.12),
      gainsWithBadr = Some(123.89),
      gainsBeforeLosses = 123.89,
      lossesFromThisYear = Some(456.89),
      claimOrElectionCodes = Some(Seq("PRR")),
      amountOfNetGain = Some(567.9),
      amountOfNetLoss = Some(567.9)
    )
  ))

  private val emptyModel = Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody(disposals = Seq.empty)

  "reads" should {
    "read to a case class" when {
      "provided valid JSON" in {
        validMtdJson.as[Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody] shouldBe validModel
      }
    }
  }

  "writes" should {
    "write to JSON" when {
      "provided a case class" in {
        Json.toJson(validModel) shouldBe validDesJson
      }
    }
  }

  "isEmpty" should {
    "return true" when {
      "an empty disposals Seq is provided" in {
        emptyModel.isEmpty shouldBe true
      }
    }

    "return false" when {
      "a non-empty disposals Seq is provided" in {
        validModel.isEmpty shouldBe false
      }
    }
  }

}
