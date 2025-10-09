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

package v3.otherCgt.retrieve.def2.model.response

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Cryptoassets(numberOfDisposals: BigInt,
                        assetDescription: String,
                        tokenName: String,
                        acquisitionDate: String,
                        disposalDate: String,
                        disposalProceeds: BigDecimal,
                        allowableCosts: BigDecimal,
                        gainsWithBadr: Option[BigDecimal],
                        gainsBeforeLosses: BigDecimal,
                        losses: Option[BigDecimal],
                        claimOrElectionCodes: Option[Seq[CryptoassetsClaimOrElectionCodes]],
                        amountOfNetGain: Option[BigDecimal],
                        amountOfNetLoss: Option[BigDecimal],
                        rttTaxPaid: Option[BigDecimal])

object Cryptoassets {

  implicit val reads: Reads[Cryptoassets] = (
    (JsPath \ "numberOfDisposals").read[BigInt] and
      (JsPath \ "assetDescription").read[String] and
      (JsPath \ "tokenName").read[String] and
      (JsPath \ "acquisitionDate").read[String] and
      (JsPath \ "disposalDate").read[String] and
      (JsPath \ "disposalProceeds").read[BigDecimal] and
      (JsPath \ "allowableCosts").read[BigDecimal] and
      (JsPath \ "gainsWithBADR").readNullable[BigDecimal] and
      (JsPath \ "gainsBeforeLosses").read[BigDecimal] and
      (JsPath \ "losses").readNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").readNullable[Seq[CryptoassetsClaimOrElectionCodes]] and
      (JsPath \ "amountOfNetGain").readNullable[BigDecimal] and
      (JsPath \ "amountOfNetLoss").readNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").readNullable[BigDecimal]
  )(Cryptoassets.apply)

  implicit val writes: OWrites[Cryptoassets] = Json.writes[Cryptoassets]
}
