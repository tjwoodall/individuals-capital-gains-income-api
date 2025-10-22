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
                      claimOrElectionCodes: Option[Seq[String]],
                      amountOfNetGain: Option[BigDecimal],
                      amountOfNetLoss: Option[BigDecimal],
                      rttTaxPaid: Option[BigDecimal]) {

  def isMissingCompanyNameForListedShares: Boolean = assetType == "listed-shares" && companyName.isEmpty

  def hasInvalidListedSharesCodes: Boolean = assetType == "listed-shares" && claimOrElectionCodes.exists(_.exists(Set("PRR", "LET").contains))

  def hasInvalidNonUkCode: Boolean = assetType == "non-uk-residential-property" && claimOrElectionCodes.exists(_.contains("INV"))

  def bothBadAndInvSupplied: Boolean = claimOrElectionCodes.exists { codes =>
    codes.contains("BAD") && codes.contains("INV")
  }

  def hasNetAmountViolation: Boolean = {
    val bothDefined: Boolean = amountOfNetGain.isDefined && amountOfNetLoss.isDefined
    val bothEmpty: Boolean   = amountOfNetGain.isEmpty && amountOfNetLoss.isEmpty
    bothDefined || bothEmpty
  }

}

object OtherGains {
  implicit val reads: Reads[OtherGains] = Json.reads[OtherGains]

  implicit val writes: OWrites[OtherGains] = (
    (JsPath \ "assetType").write[String].contramap[String](AssetType.parser(_).toDownstream) and
      (JsPath \ "numberOfDisposals").write[BigInt] and
      (JsPath \ "assetDescription").write[String] and
      (JsPath \ "companyName").writeNullable[String] and
      (JsPath \ "companyRegistrationNumber").writeNullable[String] and
      (JsPath \ "acquisitionDate").write[String] and
      (JsPath \ "disposalDate").write[String] and
      (JsPath \ "disposalProceeds").write[BigDecimal] and
      (JsPath \ "allowableCosts").write[BigDecimal] and
      (JsPath \ "gainsWithBADR").writeNullable[BigDecimal] and
      (JsPath \ "gainsWithINV").writeNullable[BigDecimal] and
      (JsPath \ "gainsBeforeLosses").write[BigDecimal] and
      (JsPath \ "losses").writeNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").writeNullable[Seq[String]] and
      (JsPath \ "amountOfNetGain").writeNullable[BigDecimal] and
      (JsPath \ "amountOfNetLoss").writeNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").writeNullable[BigDecimal]
  )(o => Tuple.fromProductTyped(o))

}
