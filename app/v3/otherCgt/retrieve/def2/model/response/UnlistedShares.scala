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

case class UnlistedShares(numberOfDisposals: Int,
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
                          claimOrElectionCodes: Option[Seq[UnlistedSharesClaimOrElectionCodes]],
                          gainsReportedOnRtt: Option[BigDecimal],
                          gainsExceedingLifetimeLimit: Option[BigDecimal],
                          gainsUnderSeis: Option[BigDecimal],
                          lossUsedAgainstGeneralIncome: Option[BigDecimal],
                          eisOrSeisReliefDueCurrentYear: Option[BigDecimal],
                          lossesUsedAgainstGeneralIncomePreviousYear: Option[BigDecimal],
                          eisOrSeisReliefDuePreviousYear: Option[BigDecimal],
                          rttTaxPaid: Option[BigDecimal])

object UnlistedShares {

  implicit val reads: Reads[UnlistedShares] = (
    (JsPath \ "numberOfDisposals").read[Int] and
      (JsPath \ "assetDescription").read[String] and
      (JsPath \ "companyName").read[String] and
      (JsPath \ "companyRegistrationNumber").readNullable[String] and
      (JsPath \ "acquisitionDate").read[String] and
      (JsPath \ "disposalDate").read[String] and
      (JsPath \ "disposalProceeds").read[BigDecimal] and
      (JsPath \ "allowableCosts").read[BigDecimal] and
      (JsPath \ "gainsWithBADR").readNullable[BigDecimal] and
      (JsPath \ "gainsWithINV").readNullable[BigDecimal] and
      (JsPath \ "gainsBeforeLosses").read[BigDecimal] and
      (JsPath \ "losses").readNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").readNullable[Seq[UnlistedSharesClaimOrElectionCodes]] and
      (JsPath \ "gainsReportedOnRtt").readNullable[BigDecimal] and
      (JsPath \ "gainsExceedingLifetimeLimit").readNullable[BigDecimal] and
      (JsPath \ "gainsUnderSEIS").readNullable[BigDecimal] and
      (JsPath \ "lossUsedAgainstGeneralIncome").readNullable[BigDecimal] and
      (JsPath \ "eisOrSeisReliefDueCurrentYear").readNullable[BigDecimal] and
      (JsPath \ "lossesUsedAgainstGeneralIncomePreviousYear").readNullable[BigDecimal] and
      (JsPath \ "eisOrSeisReliefDuePreviousYear").readNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").readNullable[BigDecimal]
  )(UnlistedShares.apply)

  implicit val writes: OWrites[UnlistedShares] = Json.writes[UnlistedShares]
}
