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

package v3.otherCgt.createAmend.def2.fixture

import play.api.libs.json.{JsValue, Json}
import v3.otherCgt.createAmend.def2.model.request.*

object Def2_CreateAmendOtherCgtFixture {

  val cryptoassetsModel: Cryptoassets = Cryptoassets(
    numberOfDisposals = 1,
    assetDescription = "description string",
    tokenName = "Name of token",
    acquisitionDate = "2025-08-04",
    disposalDate = "2025-09-04",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq("GHO")),
    amountOfNetGain = Some(99999999999.99),
    amountOfNetLoss = None,
    rttTaxPaid = Some(99999999999.99)
  )

  val otherGainsModel: OtherGains = OtherGains(
    assetType = "listed-shares",
    numberOfDisposals = 1,
    assetDescription = "example of this asset",
    companyName = Some("Bob the Builder"),
    companyRegistrationNumber = Some("11111111"),
    acquisitionDate = "2025-04-07",
    disposalDate = "2025-07-10",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsWithInv = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq("INV")),
    amountOfNetGain = Some(99999999999.99),
    amountOfNetLoss = None,
    rttTaxPaid = Some(99999999999.99)
  )

  val unlistedSharesModel: UnlistedShares = UnlistedShares(
    numberOfDisposals = 1,
    assetDescription = "My asset",
    companyName = "Bob the Builder",
    companyRegistrationNumber = Some("11111111"),
    acquisitionDate = "2025-04-10",
    disposalDate = "2025-04-12",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsWithInv = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq("GHO")),
    gainsReportedOnRtt = Some(99999999999.99),
    gainsExceedingLifetimeLimit = Some(99999999999.99),
    gainsUnderSeis = Some(99999999999.99),
    lossUsedAgainstGeneralIncome = Some(99999999999.99),
    eisOrSeisReliefDueCurrentYear = Some(99999999999.99),
    lossesUsedAgainstGeneralIncomePreviousYear = Some(99999999999.99),
    eisOrSeisReliefDuePreviousYear = Some(99999999999.99),
    rttTaxPaid = Some(99999999999.99)
  )

  val gainExcludedIndexedSecuritiesModel: GainExcludedIndexedSecurities = GainExcludedIndexedSecurities(
    gainsFromExcludedSecurities = Some(99999999999.99)
  )

  val qualifyingAssetHoldingCompanyModel: QualifyingAssetHoldingCompany = QualifyingAssetHoldingCompany(
    gainsFromQahcBeforeLosses = Some(99999999999.99),
    lossesFromQahc = Some(99999999999.99)
  )

  val nonStandardGainsModel: NonStandardGains = NonStandardGains(
    attributedGains = Some(99999999999.99),
    attributedGainsRttTaxPaid = Some(99999999999.99),
    otherGains = Some(99999999999.99),
    otherGainsRttTaxPaid = Some(99999999999.99)
  )

  val lossesModel: Losses = Losses(
    broughtForwardLossesUsedInCurrentYear = Some(99999999999.99),
    setAgainstInYearGains = Some(99999999999.99),
    setAgainstEarlierYear = Some(99999999999.99),
    lossesToCarryForward = Some(99999999999.99)
  )

  val adjustmentsModel: Adjustments = Adjustments(
    adjustmentAmount = Some(99999999999.99)
  )

  val lifetimeAllowanceModel: LifetimeAllowance = LifetimeAllowance(
    lifetimeAllowanceBadr = Some(99999999999.99),
    lifetimeAllowanceInv = Some(99999999999.99)
  )

  val fullRequestBodyModel: Def2_CreateAmendOtherCgtRequestBody = Def2_CreateAmendOtherCgtRequestBody(
    cryptoassets = Some(Seq(cryptoassetsModel)),
    otherGains = Some(Seq(otherGainsModel)),
    unlistedShares = Some(Seq(unlistedSharesModel)),
    gainExcludedIndexedSecurities = Some(gainExcludedIndexedSecuritiesModel),
    qualifyingAssetHoldingCompany = Some(qualifyingAssetHoldingCompanyModel),
    nonStandardGains = Some(nonStandardGainsModel),
    losses = Some(lossesModel),
    adjustments = Some(adjustmentsModel),
    lifetimeAllowance = Some(lifetimeAllowanceModel)
  )

  val cryptoassetsMtdJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "description string",
      |  "tokenName": "Name of token",
      |  "acquisitionDate": "2025-08-04",
      |  "disposalDate": "2025-09-04",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO"],
      |  "amountOfNetGain": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val otherGainsMtdJson: JsValue = Json.parse(
    """
      |{
      |  "assetType": "listed-shares",
      |  "numberOfDisposals": 1,
      |  "assetDescription": "example of this asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2025-04-07",
      |  "disposalDate": "2025-07-10",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsWithInv": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["INV"],
      |  "amountOfNetGain": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val unlistedSharesMtdJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "My asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2025-04-10",
      |  "disposalDate": "2025-04-12",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsWithInv": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO"],
      |  "gainsReportedOnRtt": 99999999999.99,
      |  "gainsExceedingLifetimeLimit": 99999999999.99,
      |  "gainsUnderSeis": 99999999999.99,
      |  "lossUsedAgainstGeneralIncome": 99999999999.99,
      |  "eisOrSeisReliefDueCurrentYear": 99999999999.99,
      |  "lossesUsedAgainstGeneralIncomePreviousYear": 99999999999.99,
      |  "eisOrSeisReliefDuePreviousYear": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val gainExcludedIndexedSecuritiesJson: JsValue = Json.parse(
    """
      |{
      |  "gainsFromExcludedSecurities": 99999999999.99
      |}
    """.stripMargin
  )

  val qualifyingAssetHoldingCompanyMtdJson: JsValue = Json.parse(
    """
      |{
      |  "gainsFromQahcBeforeLosses": 99999999999.99,
      |  "lossesFromQahc": 99999999999.99
      |}
    """.stripMargin
  )

  val nonStandardGainsJson: JsValue = Json.parse(
    """
      |{
      |  "attributedGains": 99999999999.99,
      |  "attributedGainsRttTaxPaid": 99999999999.99,
      |  "otherGains": 99999999999.99,
      |  "otherGainsRttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val lossesJson: JsValue = Json.parse(
    """
      |{
      |  "broughtForwardLossesUsedInCurrentYear": 99999999999.99,
      |  "setAgainstInYearGains": 99999999999.99,
      |  "setAgainstEarlierYear": 99999999999.99,
      |  "lossesToCarryForward": 99999999999.99
      |}
    """.stripMargin
  )

  val adjustmentsJson: JsValue = Json.parse(
    """
      |{
      |  "adjustmentAmount": 99999999999.99
      |}
    """.stripMargin
  )

  val lifetimeAllowanceMtdJson: JsValue = Json.parse(
    """
      |{
      |  "lifetimeAllowanceBadr": 99999999999.99,
      |  "lifetimeAllowanceInv": 99999999999.99
      |}
    """.stripMargin
  )

  val fullRequestBodyMtdJson: JsValue = Json.parse(
    s"""
      |{
      |  "cryptoassets": [$cryptoassetsMtdJson],
      |  "otherGains": [$otherGainsMtdJson],
      |  "unlistedShares": [$unlistedSharesMtdJson],
      |  "gainExcludedIndexedSecurities": $gainExcludedIndexedSecuritiesJson,
      |  "qualifyingAssetHoldingCompany": $qualifyingAssetHoldingCompanyMtdJson,
      |  "nonStandardGains": $nonStandardGainsJson,
      |  "losses": $lossesJson,
      |  "adjustments": $adjustmentsJson,
      |  "lifetimeAllowance": $lifetimeAllowanceMtdJson
      |}
    """.stripMargin
  )

  val emptyObjectsAndArraysMtdJson: JsValue = Json.parse(
    """
      |{
      |  "cryptoassets": [],
      |  "otherGains": [],
      |  "unlistedShares": [],
      |  "gainExcludedIndexedSecurities": {},
      |  "qualifyingAssetHoldingCompany": {},
      |  "nonStandardGains": {},
      |  "losses": {},
      |  "adjustments": {},
      |  "lifetimeAllowance": {}
      |}
    """.stripMargin
  )

  val missingMandatoryFieldsMtdJson: JsValue = Json.parse(
    """
      |{
      |  "cryptoassets": [{}],
      |  "otherGains": [{}],
      |  "unlistedShares": [{}]
      |}
    """.stripMargin
  )

}
