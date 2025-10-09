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

case class OtherGains(assetType: String,
                      numberOfDisposals: BigInt,
                      assetDescription: String,
                      companyName: Option[String],
                      companyRegistrationNumber: Option[String],
                      acquisitionDate: String,
                      disposalDate: String,
                      disposalProceeds: BigDecimal,
                      allowableCosts: BigDecimal,
                      gainsWithBadr: Option[BigDecimal],
                      gainsWithInv: Option[BigDecimal],
                      gainsBeforeLosses: BigDecimal,
                      losses: Option[BigDecimal],
                      claimOrElectionCodes: Option[Seq[OtherGainsClaimOrElectionCodes]],
                      amountOfNetGain: Option[BigDecimal],
                      amountOfNetLoss: Option[BigDecimal],
                      rttTaxPaid: Option[BigDecimal])

object OtherGains {

  implicit val reads: Reads[OtherGains] = (
    (JsPath \ "assetType").read[DownstreamAssetType].map(_.toMtd) and
      (JsPath \ "numberOfDisposals").read[BigInt] and
      (JsPath \ "assetDescription").read[String] and
      (JsPath \ "companyName").readNullable[String] and
      (JsPath \ "companyRegistrationNumber").readNullable[String] and
      (JsPath \ "acquisitionDate").read[String] and
      (JsPath \ "disposalDate").read[String] and
      (JsPath \ "disposalProceeds").read[BigDecimal] and
      (JsPath \ "allowableCosts").read[BigDecimal] and
      (JsPath \ "gainsWithBADR").readNullable[BigDecimal] and
      (JsPath \ "gainsWithINV").readNullable[BigDecimal] and
      (JsPath \ "gainsBeforeLosses").read[BigDecimal] and
      (JsPath \ "losses").readNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").readNullable[Seq[OtherGainsClaimOrElectionCodes]] and
      (JsPath \ "amountOfNetGain").readNullable[BigDecimal] and
      (JsPath \ "amountOfNetLoss").readNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").readNullable[BigDecimal]
  )(OtherGains.apply)

  implicit val writes: OWrites[OtherGains] = Json.writes[OtherGains]
}
