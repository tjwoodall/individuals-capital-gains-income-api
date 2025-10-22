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

case class UnlistedShares(numberOfDisposals: BigInt,
                          assetDescription: String,
                          companyName: String,
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
                          gainsReportedOnRtt: Option[BigDecimal],
                          gainsExceedingLifetimeLimit: Option[BigDecimal],
                          gainsUnderSeis: Option[BigDecimal],
                          lossUsedAgainstGeneralIncome: Option[BigDecimal],
                          eisOrSeisReliefDueCurrentYear: Option[BigDecimal],
                          lossesUsedAgainstGeneralIncomePreviousYear: Option[BigDecimal],
                          eisOrSeisReliefDuePreviousYear: Option[BigDecimal],
                          rttTaxPaid: Option[BigDecimal]) {

  def bothBadAndInvSupplied: Boolean = claimOrElectionCodes.exists { codes =>
    codes.contains("BAD") && codes.contains("INV")
  }

}

object UnlistedShares {
  implicit val reads: Reads[UnlistedShares] = Json.reads[UnlistedShares]

  implicit val writes: OWrites[UnlistedShares] = (
    (JsPath \ "numberOfDisposals").write[BigInt] and
      (JsPath \ "assetDescription").write[String] and
      (JsPath \ "companyName").write[String] and
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
      (JsPath \ "gainsReportedOnRtt").writeNullable[BigDecimal] and
      (JsPath \ "gainsExceedingLifetimeLimit").writeNullable[BigDecimal] and
      (JsPath \ "gainsUnderSEIS").writeNullable[BigDecimal] and
      (JsPath \ "lossUsedAgainstGeneralIncome").writeNullable[BigDecimal] and
      (JsPath \ "eisOrSeisReliefDueCurrentYear").writeNullable[BigDecimal] and
      (JsPath \ "lossesUsedAgainstGeneralIncomePreviousYear").writeNullable[BigDecimal] and
      (JsPath \ "eisOrSeisReliefDuePreviousYear").writeNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").writeNullable[BigDecimal]
  )(o => Tuple.fromProductTyped(o))

}
