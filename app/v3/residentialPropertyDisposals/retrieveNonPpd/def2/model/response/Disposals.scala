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

package v3.residentialPropertyDisposals.retrieveNonPpd.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class Disposals(numberOfDisposals: Int,
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
                     claimOrElectionCodes: Option[Seq[ClaimOrElectionCodes]],
                     amountOfNetGain: Option[BigDecimal],
                     amountOfNetLoss: Option[BigDecimal])

object Disposals {

  implicit val reads: Reads[Disposals] = (
    (JsPath \ "numberOfDisposals").read[Int] and
      (JsPath \ "customerRef").readNullable[String] and
      (JsPath \ "disposalDate").read[String] and
      (JsPath \ "completionDate").read[String] and
      (JsPath \ "disposalProceeds").read[BigDecimal] and
      (JsPath \ "acquisitionDate").read[String] and
      (JsPath \ "acquisitionAmount").read[BigDecimal] and
      (JsPath \ "improvementCosts").readNullable[BigDecimal] and
      (JsPath \ "additionalCosts").readNullable[BigDecimal] and
      (JsPath \ "prfAmount").readNullable[BigDecimal] and
      (JsPath \ "otherReliefAmount").readNullable[BigDecimal] and
      (JsPath \ "gainsWithBADR").readNullable[BigDecimal] and
      (JsPath \ "gainsBeforeLosses").read[BigDecimal] and
      (JsPath \ "lossesFromThisYear").readNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").readNullable[Seq[ClaimOrElectionCodes]] and
      (JsPath \ "amountOfNetGain").readNullable[BigDecimal] and
      (JsPath \ "amountOfNetLoss").readNullable[BigDecimal]
  )(Disposals.apply)

  implicit val writes: OWrites[Disposals] = Json.writes[Disposals]
}
