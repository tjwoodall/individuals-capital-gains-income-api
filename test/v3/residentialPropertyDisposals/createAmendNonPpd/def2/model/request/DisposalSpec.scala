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

class DisposalSpec extends UnitSpec {

  private val mtdJson = Json.parse(
    """
      |{
      |  "numberOfDisposals" : 2,
      |  "customerReference" : "ABC-2345",
      |  "disposalDate" : "2021-01-29",
      |  "completionDate" : "2021-04-25",
      |  "disposalProceeds" : 2345.67,
      |  "acquisitionDate" : "2021-03-22",
      |  "acquisitionAmount" : 2341.45,
      |  "improvementCosts" : 345.34,
      |  "additionalCosts" : 234.89,
      |  "prfAmount" : 67.9,
      |  "otherReliefAmount" : 123.89,
      |  "gainsWithBadr" : 566.9,
      |  "gainsBeforeLosses" : 567.9,
      |  "lossesFromThisYear" : 456.89,
      |  "claimOrElectionCodes": ["PRR"],
      |  "amountOfNetGain" : 566.9
      |}
      |""".stripMargin
  )

  private val downstreamJson = Json.parse(
    """
      |{
      |  "numberOfDisposals" : 2,
      |  "customerRef" : "ABC-2345",
      |  "disposalDate" : "2021-01-29",
      |  "completionDate" : "2021-04-25",
      |  "disposalProceeds" : 2345.67,
      |  "acquisitionDate" : "2021-03-22",
      |  "acquisitionAmount" : 2341.45,
      |  "improvementCosts" : 345.34,
      |  "additionalCosts" : 234.89,
      |  "prfAmount" : 67.9,
      |  "otherReliefAmount" : 123.89,
      |  "gainsWithBADR" : 566.9,
      |  "gainsBeforeLosses" : 567.9,
      |  "lossesFromThisYear" : 456.89,
      |  "claimOrElectionCodes": ["PRR"],
      |  "amountOfNetGain": 566.9
      |}
      |""".stripMargin
  )

  private val model = Disposal(
    numberOfDisposals = 2,
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
    gainsWithBadr = Some(566.9),
    gainsBeforeLosses = 567.9,
    lossesFromThisYear = Some(456.89),
    claimOrElectionCodes = Some(Seq("PRR")),
    amountOfNetGain = Some(566.9),
    amountOfNetLoss = None
  )

  "reads" should {
    "read to a case class" when {
      "provided valid JSON" in {
        mtdJson.as[Disposal] shouldBe model
      }
    }
  }

  "writes" should {
    "write to JSON" when {
      "provided a case class" in {
        Json.toJson(model) shouldBe downstreamJson
      }
    }
  }

}
