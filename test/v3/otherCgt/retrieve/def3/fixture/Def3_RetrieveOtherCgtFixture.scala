/*
 * Copyright 2026 HM Revenue & Customs
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

package v3.otherCgt.retrieve.def3.fixture

import api.models.domain.Timestamp
import play.api.libs.json.{JsValue, Json}
import v3.otherCgt.retrieve.def3.model.response.*
import v3.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

object Def3_RetrieveOtherCgtFixture {

  val cryptoassetsResponseModel: Cryptoassets = Cryptoassets(
    numberOfDisposals = 1,
    assetDescription = "description string",
    tokenName = "Name of token",
    acquisitionDate = "2026-08-04",
    disposalDate = "2026-09-04",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq(CryptoassetsClaimOrElectionCodes.GHO, CryptoassetsClaimOrElectionCodes.INC)),
    amountOfNetGain = Some(99999999999.99),
    amountOfNetLoss = Some(99999999999.99),
    rttTaxPaid = Some(99999999999.99)
  )

  val otherGainsResponseModel: OtherGains = OtherGains(
    assetType = "other-property",
    numberOfDisposals = 1,
    assetDescription = "example of this asset",
    companyName = Some("Bob the Builder"),
    companyRegistrationNumber = Some("11111111"),
    acquisitionDate = "2026-04-07",
    disposalDate = "2026-07-10",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsWithInv = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq(OtherGainsClaimOrElectionCodes.GHO, OtherGainsClaimOrElectionCodes.INC)),
    amountOfNetGain = Some(99999999999.99),
    amountOfNetLoss = Some(99999999999.99),
    rttTaxPaid = Some(99999999999.99)
  )

  val unlistedSharesResponseModel: UnlistedShares = UnlistedShares(
    numberOfDisposals = 1,
    assetDescription = "My asset",
    companyName = "Bob the Builder",
    companyRegistrationNumber = Some("11111111"),
    acquisitionDate = "2026-04-10",
    disposalDate = "2026-04-12",
    disposalProceeds = 99999999999.99,
    allowableCosts = 99999999999.99,
    gainsWithBadr = Some(99999999999.99),
    gainsWithInv = Some(99999999999.99),
    gainsBeforeLosses = 99999999999.99,
    losses = Some(99999999999.99),
    claimOrElectionCodes = Some(Seq(UnlistedSharesClaimOrElectionCodes.GHO, UnlistedSharesClaimOrElectionCodes.INC)),
    gainsReportedOnRtt = Some(99999999999.99),
    gainsExceedingLifetimeLimit = Some(99999999999.99),
    gainsUnderSeis = Some(99999999999.99),
    lossUsedAgainstGeneralIncome = Some(99999999999.99),
    eisOrSeisReliefDueCurrentYear = Some(99999999999.99),
    lossesUsedAgainstGeneralIncomePreviousYear = Some(99999999999.99),
    eisOrSeisReliefDuePreviousYear = Some(99999999999.99),
    rttTaxPaid = Some(99999999999.99)
  )

  val gainExcludedIndexedSecuritiesResponseModel: GainExcludedIndexedSecurities = GainExcludedIndexedSecurities(
    gainsFromExcludedSecurities = Some(99999999999.99)
  )

  val qualifyingAssetHoldingCompanyResponseModel: QualifyingAssetHoldingCompany = QualifyingAssetHoldingCompany(
    gainsFromQahcBeforeLosses = Some(99999999999.99),
    lossesFromQahc = Some(99999999999.99)
  )

  val lossesResponseModel: Losses = Losses(
    broughtForwardLossesUsedInCurrentYear = Some(99999999999.99),
    setAgainstInYearGains = Some(99999999999.99),
    setAgainstEarlierYear = Some(99999999999.99),
    lossesToCarryForward = Some(99999999999.99)
  )

  val adjustmentsResponseModel: Adjustments = Adjustments(
    adjustmentAmount = Some(-99999999999.99)
  )

  val lifetimeAllowanceResponseModel: LifetimeAllowance = LifetimeAllowance(
    lifetimeAllowanceBadr = Some(99999999999.99),
    lifetimeAllowanceInv = Some(99999999999.99)
  )

  val fullResponseModel: RetrieveOtherCgtResponse = Def3_RetrieveOtherCgtResponse(
    submittedOn = Timestamp("2027-02-07T16:18:44.403Z"),
    cryptoassets = Some(Seq(cryptoassetsResponseModel)),
    otherGains = Some(Seq(otherGainsResponseModel)),
    unlistedShares = Some(Seq(unlistedSharesResponseModel)),
    gainExcludedIndexedSecurities = Some(gainExcludedIndexedSecuritiesResponseModel),
    qualifyingAssetHoldingCompany = Some(qualifyingAssetHoldingCompanyResponseModel),
    losses = Some(lossesResponseModel),
    adjustments = Some(adjustmentsResponseModel),
    lifetimeAllowance = Some(lifetimeAllowanceResponseModel)
  )

  val cryptoassetsValidMtdResponseJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "description string",
      |  "tokenName": "Name of token",
      |  "acquisitionDate": "2026-08-04",
      |  "disposalDate": "2026-09-04",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val otherGainsValidMtdResponseJson: JsValue = Json.parse(
    """
      |{
      |  "assetType": "other-property",
      |  "numberOfDisposals": 1,
      |  "assetDescription": "example of this asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2026-04-07",
      |  "disposalDate": "2026-07-10",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsWithInv": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val unlistedSharesValidMtdResponseJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "My asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2026-04-10",
      |  "disposalDate": "2026-04-12",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBadr": 99999999999.99,
      |  "gainsWithInv": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
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

  val gainExcludedIndexedSecuritiesValidResponseJson: JsValue = Json.parse(
    """
      |{
      |   "gainsFromExcludedSecurities": 99999999999.99
      |}
    """.stripMargin
  )

  val qualifyingAssetHoldingCompanyValidMtdResponseJson: JsValue = Json.parse(
    """
      |{
      |     "gainsFromQahcBeforeLosses": 99999999999.99,
      |     "lossesFromQahc": 99999999999.99
      |}
    """.stripMargin
  )

  val lossesValidResponseJson: JsValue = Json.parse(
    """
      |{
      |  "broughtForwardLossesUsedInCurrentYear": 99999999999.99,
      |  "setAgainstInYearGains": 99999999999.99,
      |  "setAgainstEarlierYear": 99999999999.99,
      |  "lossesToCarryForward": 99999999999.99
      |}
    """.stripMargin
  )

  val adjustmentsValidResponseJson: JsValue = Json.parse(
    """
      |{
      |  "adjustmentAmount": -99999999999.99
      |}
    """.stripMargin
  )

  val lifetimeAllowanceValidMtdResponseJson: JsValue = Json.parse(
    """
      |{
      |  "lifetimeAllowanceBadr": 99999999999.99,
      |  "lifetimeAllowanceInv": 99999999999.99
      |}
    """.stripMargin
  )

  val fullValidMtdResponseJson: JsValue = Json.parse(
    s"""
      |{
      |  "submittedOn": "2027-02-07T16:18:44.403Z",
      |  "cryptoassets": [$cryptoassetsValidMtdResponseJson],
      |  "otherGains": [$otherGainsValidMtdResponseJson],
      |  "unlistedShares": [$unlistedSharesValidMtdResponseJson],
      |  "gainExcludedIndexedSecurities": $gainExcludedIndexedSecuritiesValidResponseJson,
      |  "qualifyingAssetHoldingCompany": $qualifyingAssetHoldingCompanyValidMtdResponseJson,
      |  "losses": $lossesValidResponseJson,
      |  "adjustments": $adjustmentsValidResponseJson,
      |  "lifetimeAllowance": $lifetimeAllowanceValidMtdResponseJson
      |}
    """.stripMargin
  )

  val cryptoassetsValidDownstreamResponseJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "description string",
      |  "tokenName": "Name of token",
      |  "acquisitionDate": "2026-08-04",
      |  "disposalDate": "2026-09-04",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val otherGainsValidDownstreamResponseJson: JsValue = Json.parse(
    """
      |{
      |  "assetType": "otherProperty",
      |  "numberOfDisposals": 1,
      |  "assetDescription": "example of this asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2026-04-07",
      |  "disposalDate": "2026-07-10",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsWithINV": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
      |  "amountOfNetGain": 99999999999.99,
      |  "amountOfNetLoss": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val unlistedSharesValidDownstreamResponseJson: JsValue = Json.parse(
    """
      |{
      |  "numberOfDisposals": 1,
      |  "assetDescription": "My asset",
      |  "companyName": "Bob the Builder",
      |  "companyRegistrationNumber": "11111111",
      |  "acquisitionDate": "2026-04-10",
      |  "disposalDate": "2026-04-12",
      |  "disposalProceeds": 99999999999.99,
      |  "allowableCosts": 99999999999.99,
      |  "gainsWithBADR": 99999999999.99,
      |  "gainsWithINV": 99999999999.99,
      |  "gainsBeforeLosses": 99999999999.99,
      |  "losses": 99999999999.99,
      |  "claimOrElectionCodes": ["GHO", "INC"],
      |  "gainsReportedOnRtt": 99999999999.99,
      |  "gainsExceedingLifetimeLimit": 99999999999.99,
      |  "gainsUnderSEIS": 99999999999.99,
      |  "lossUsedAgainstGeneralIncome": 99999999999.99,
      |  "eisOrSeisReliefDueCurrentYear": 99999999999.99,
      |  "lossesUsedAgainstGeneralIncomePreviousYear": 99999999999.99,
      |  "eisOrSeisReliefDuePreviousYear": 99999999999.99,
      |  "rttTaxPaid": 99999999999.99
      |}
    """.stripMargin
  )

  val qualifyingAssetHoldingCompanyValidDownstreamResponseJson: JsValue = Json.parse(
    """
      |{
      |  "gainsFromQAHCBeforeLosses": 99999999999.99,
      |  "lossesFromQAHC": 99999999999.99
      |}
    """.stripMargin
  )

  val lifetimeAllowanceValidDownstreamResponseJson: JsValue = Json.parse(
    """
      |{
      |  "lifetimeAllowanceBADR": 99999999999.99,
      |  "lifetimeAllowanceINV": 99999999999.99
      |}
    """.stripMargin
  )

  val fullValidDownstreamResponseJson: JsValue = Json.parse(
    s"""
      |{
      |  "submittedOn": "2027-02-07T16:18:44.403Z",
      |  "cryptoassets": [$cryptoassetsValidDownstreamResponseJson],
      |  "otherGains": [$otherGainsValidDownstreamResponseJson],
      |  "unlistedShares": [$unlistedSharesValidDownstreamResponseJson],
      |  "gainExcludedIndexedSecurities": $gainExcludedIndexedSecuritiesValidResponseJson,
      |  "qualifyingAssetHoldingCompany": $qualifyingAssetHoldingCompanyValidDownstreamResponseJson,
      |  "losses": $lossesValidResponseJson,
      |  "adjustments": $adjustmentsValidResponseJson,
      |  "lifeTimeAllowance": $lifetimeAllowanceValidDownstreamResponseJson
      |}
    """.stripMargin
  )

  val minimumResponseModel: Def3_RetrieveOtherCgtResponse = Def3_RetrieveOtherCgtResponse(
    submittedOn = Timestamp("2027-02-07T16:18:44.403Z"),
    cryptoassets = None,
    otherGains = None,
    unlistedShares = None,
    gainExcludedIndexedSecurities = None,
    qualifyingAssetHoldingCompany = None,
    losses = None,
    adjustments = None,
    lifetimeAllowance = None
  )

  val minimumValidResponseJson: JsValue = Json.parse(
    """
      |{
      |  "submittedOn": "2027-02-07T16:18:44.403Z"
      |}
    """.stripMargin
  )

}
