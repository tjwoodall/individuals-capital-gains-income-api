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

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class Disposal(numberOfDisposals: Int,
                    customerReference: Option[String],
                    disposalDate: String,
                    completionDate: String,
                    disposalProceeds: BigDecimal,
                    acquisitionDate: String,
                    acquisitionAmount: BigDecimal,
                    improvementCosts: Option[BigDecimal],
                    additionalCosts: Option[BigDecimal],
                    prfAmount: Option[BigDecimal],
                    otherReliefAmount: Option[BigDecimal],
                    gainsWithBadr: Option[BigDecimal],
                    gainsBeforeLosses: BigDecimal,
                    lossesFromThisYear: Option[BigDecimal],
                    claimOrElectionCodes: Option[Seq[String]],
                    amountOfNetGain: Option[BigDecimal],
                    amountOfNetLoss: Option[BigDecimal]) {

  def gainAndLossAreBothSupplied: Boolean = amountOfNetLoss.isDefined && amountOfNetGain.isDefined

  def isNetAmountEmpty: Boolean = amountOfNetLoss.isEmpty && amountOfNetGain.isEmpty
}

object Disposal {
  implicit val reads: Reads[Disposal] = Json.reads[Disposal]

  implicit val writes: OWrites[Disposal] = (
    (__ \ "numberOfDisposals").write[Int] and
      (__ \ "customerRef").writeNullable[String] and
      (__ \ "disposalDate").write[String] and
      (__ \ "completionDate").write[String] and
      (__ \ "disposalProceeds").write[BigDecimal] and
      (__ \ "acquisitionDate").write[String] and
      (__ \ "acquisitionAmount").write[BigDecimal] and
      (__ \ "improvementCosts").writeNullable[BigDecimal] and
      (__ \ "additionalCosts").writeNullable[BigDecimal] and
      (__ \ "prfAmount").writeNullable[BigDecimal] and
      (__ \ "otherReliefAmount").writeNullable[BigDecimal] and
      (__ \ "gainsWithBADR").writeNullable[BigDecimal] and
      (__ \ "gainsBeforeLosses").write[BigDecimal] and
      (__ \ "lossesFromThisYear").writeNullable[BigDecimal] and
      (__ \ "claimOrElectionCodes").writeNullable[Seq[String]] and
      (__ \ "amountOfNetGain").writeNullable[BigDecimal] and
      (__ \ "amountOfNetLoss").writeNullable[BigDecimal]
  )(o => Tuple.fromProductTyped(o))

}
