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

package v3.otherCgt.createAmend.def2

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.*
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveBigInteger, ResolveIsoDate, ResolveParsedNumber, ResolveStringPattern, ResolverSupport}
import shared.models.errors.{DateFormatError, MtdError}
import v3.otherCgt.createAmend.def2.model.request.*

object Def2_CreateAmendOtherCgtRulesValidator extends RulesValidator[Def2_CreateAmendOtherCgtRequestData] with ResolverSupport {

  private val assetDescriptionAndTokenNameRegex = "^[0-9a-zA-Z{À-˿'}\\- _&`():.'^]{1,90}$".r
  private val companyNameRegex                  = "^.{0,160}$".r
  private val companyRegistrationNumberRegex    = "^(?:\\d{8}|[A-Za-z]{2}\\d{6})$".r
  private val resolveParsedNumber               = ResolveParsedNumber()
  private val resolveBigInteger                 = ResolveBigInteger(1, 99999999999L)

  private def resolveEnum[A](parser: PartialFunction[String, A], error: => MtdError): Resolver[String, A] =
    resolvePartialFunction(error)(parser)

  def validateBusinessRules(parsed: Def2_CreateAmendOtherCgtRequestData): Validated[Seq[MtdError], Def2_CreateAmendOtherCgtRequestData] = {
    import parsed.body.*

    combine(
      validateCryptoassets(cryptoassets),
      validateOtherGains(otherGains),
      validateUnlistedShares(unlistedShares),
      validateGainExcludedIndexedSecurities(gainExcludedIndexedSecurities),
      validateQualifyingAssetHoldingCompany(qualifyingAssetHoldingCompany),
      validateNonStandardGains(nonStandardGains),
      validateLosses(losses),
      validateAdjustments(adjustments),
      validateLifetimeAllowance(lifetimeAllowance)
    ).onSuccess(parsed)
  }

  private def validateClaimOrElectionCodes[A](claimOrElectionCodes: Option[Seq[String]],
                                              parser: PartialFunction[String, A],
                                              basePath: String): Validated[Seq[MtdError], Unit] = {
    claimOrElectionCodes.fold(valid) { codes =>
      codes.zipWithIndex.traverse_ { case (code, subIndex) =>
        resolveEnum(parser, ClaimOrElectionCodesFormatError.withPath(s"$basePath/claimOrElectionCodes/$subIndex"))(code)
      }
    }
  }

  private def validateCryptoassets(cryptoassets: Option[Seq[Cryptoassets]]): Validated[Seq[MtdError], Unit] = {
    cryptoassets.fold(valid) { cryptoassets =>
      cryptoassets.zipWithIndex.traverse_ { case (cryptoassets, index) =>
        val basePath = s"/cryptoassets/$index"

        val validatedNumberOfDisposals = resolveBigInteger(cryptoassets.numberOfDisposals, s"$basePath/numberOfDisposals")

        val validatedAssetDescription = ResolveStringPattern(
          cryptoassets.assetDescription,
          assetDescriptionAndTokenNameRegex,
          AssetDescriptionFormatError.withPath(s"$basePath/assetDescription")
        )

        val validatedTokenName = ResolveStringPattern(
          cryptoassets.tokenName,
          assetDescriptionAndTokenNameRegex,
          TokenNameFormatError.withPath(s"$basePath/tokenName")
        )

        val validatedDates = List(
          (cryptoassets.acquisitionDate, s"$basePath/acquisitionDate"),
          (cryptoassets.disposalDate, s"$basePath/disposalDate")
        ).traverse_ { case (value, path) =>
          ResolveIsoDate(value, DateFormatError.withPath(path))
        }

        val validatedMandatoryDecimalNumbers = List(
          (cryptoassets.disposalProceeds, s"$basePath/disposalProceeds"),
          (cryptoassets.allowableCosts, s"$basePath/allowableCosts"),
          (cryptoassets.gainsBeforeLosses, s"$basePath/gainsBeforeLosses")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedOptionalDecimalNumbers = List(
          (cryptoassets.gainsWithBadr, s"$basePath/gainsWithBadr"),
          (cryptoassets.losses, s"$basePath/losses"),
          (cryptoassets.amountOfNetGain, s"$basePath/amountOfNetGain"),
          (cryptoassets.amountOfNetLoss, s"$basePath/amountOfNetLoss"),
          (cryptoassets.rttTaxPaid, s"$basePath/rttTaxPaid")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedClaimOrElectionCodes = validateClaimOrElectionCodes(
          cryptoassets.claimOrElectionCodes,
          CryptoassetsClaimOrElectionCodes.parser,
          basePath
        )

        val validatedLossGainsRule = if (cryptoassets.hasNetAmountViolation) {
          Invalid(List(RuleAmountGainLossError.withPath(basePath)))
        } else {
          valid
        }

        combine(
          validatedNumberOfDisposals,
          validatedAssetDescription,
          validatedTokenName,
          validatedDates,
          validatedMandatoryDecimalNumbers,
          validatedOptionalDecimalNumbers,
          validatedClaimOrElectionCodes,
          validatedLossGainsRule
        )
      }
    }
  }

  private def validateOtherGains(otherGains: Option[Seq[OtherGains]]): Validated[Seq[MtdError], Unit] = {
    otherGains.fold(valid) { otherGains =>
      otherGains.zipWithIndex.traverse_ { case (otherGains, index) =>
        val basePath = s"/otherGains/$index"

        val validatedAssetType = resolveEnum(
          AssetType.parser,
          AssetTypeFormatError.withPath(s"$basePath/assetType")
        )(otherGains.assetType)

        val validatedNumberOfDisposals = resolveBigInteger(otherGains.numberOfDisposals, s"$basePath/numberOfDisposals")

        val validatedAssetDescription = ResolveStringPattern(
          otherGains.assetDescription,
          assetDescriptionAndTokenNameRegex,
          AssetDescriptionFormatError.withPath(s"$basePath/assetDescription")
        )

        val validatedCompanyName = ResolveStringPattern(
          otherGains.companyName,
          companyNameRegex,
          CompanyNameFormatError.withPath(s"$basePath/companyName")
        )

        val validatedCompanyRegistrationNumber = ResolveStringPattern(
          otherGains.companyRegistrationNumber,
          companyRegistrationNumberRegex,
          CompanyRegistrationNumberFormatError.withPath(s"$basePath/companyRegistrationNumber")
        )

        val validatedDates = List(
          (otherGains.acquisitionDate, s"$basePath/acquisitionDate"),
          (otherGains.disposalDate, s"$basePath/disposalDate")
        ).traverse_ { case (value, path) =>
          ResolveIsoDate(value, DateFormatError.withPath(path))
        }

        val validatedMandatoryDecimalNumbers = List(
          (otherGains.disposalProceeds, s"$basePath/disposalProceeds"),
          (otherGains.allowableCosts, s"$basePath/allowableCosts"),
          (otherGains.gainsBeforeLosses, s"$basePath/gainsBeforeLosses")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedOptionalDecimalNumbers = List(
          (otherGains.gainsWithBadr, s"$basePath/gainsWithBadr"),
          (otherGains.gainsWithInv, s"$basePath/gainsWithInv"),
          (otherGains.losses, s"$basePath/losses"),
          (otherGains.amountOfNetGain, s"$basePath/amountOfNetGain"),
          (otherGains.amountOfNetLoss, s"$basePath/amountOfNetLoss"),
          (otherGains.rttTaxPaid, s"$basePath/rttTaxPaid")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedClaimOrElectionCodes = validateClaimOrElectionCodes(
          otherGains.claimOrElectionCodes,
          OtherGainsClaimOrElectionCodes.parser,
          basePath
        )

        val validatedCompanyNameListedSharesRule = if (otherGains.isMissingCompanyNameForListedShares) {
          Invalid(List(RuleMissingCompanyNameError.withPath(basePath)))
        } else {
          valid
        }

        val validatedClaimOrElectionCodesRule = if (otherGains.hasInvalidListedSharesCodes) {
          Invalid(List(RuleInvalidClaimOrElectionCodesError.forListedShares.withPath(basePath)))
        } else if (otherGains.hasInvalidNonUkCode) {
          Invalid(List(RuleInvalidClaimOrElectionCodesError.withPath(basePath)))
        } else {
          valid
        }

        val validatedBadInvRule = if (otherGains.bothBadAndInvSupplied) {
          Invalid(List(RuleInvalidClaimDisposalsError.withPath(s"$basePath/claimOrElectionCodes")))
        } else {
          valid
        }

        val validatedLossGainsRule = if (otherGains.hasNetAmountViolation) {
          Invalid(List(RuleAmountGainLossError.withPath(basePath)))
        } else {
          valid
        }

        combine(
          validatedAssetType,
          validatedNumberOfDisposals,
          validatedAssetDescription,
          validatedCompanyName,
          validatedCompanyRegistrationNumber,
          validatedDates,
          validatedMandatoryDecimalNumbers,
          validatedOptionalDecimalNumbers,
          validatedClaimOrElectionCodes,
          validatedCompanyNameListedSharesRule,
          validatedClaimOrElectionCodesRule,
          validatedBadInvRule,
          validatedLossGainsRule
        )
      }
    }
  }

  private def validateUnlistedShares(unlistedShares: Option[Seq[UnlistedShares]]): Validated[Seq[MtdError], Unit] = {
    unlistedShares.fold(valid) { unlistedShares =>
      unlistedShares.zipWithIndex.traverse_ { case (unlistedShares, index) =>
        val basePath = s"/unlistedShares/$index"

        val validatedNumberOfDisposals = resolveBigInteger(unlistedShares.numberOfDisposals, s"$basePath/numberOfDisposals")

        val validatedAssetDescription = ResolveStringPattern(
          unlistedShares.assetDescription,
          assetDescriptionAndTokenNameRegex,
          AssetDescriptionFormatError.withPath(s"$basePath/assetDescription")
        )

        val validatedCompanyName = ResolveStringPattern(
          unlistedShares.companyName,
          companyNameRegex,
          CompanyNameFormatError.withPath(s"$basePath/companyName")
        )

        val validatedCompanyRegistrationNumber = ResolveStringPattern(
          unlistedShares.companyRegistrationNumber,
          companyRegistrationNumberRegex,
          CompanyRegistrationNumberFormatError.withPath(s"$basePath/companyRegistrationNumber")
        )

        val validatedDates = List(
          (unlistedShares.acquisitionDate, s"$basePath/acquisitionDate"),
          (unlistedShares.disposalDate, s"$basePath/disposalDate")
        ).traverse_ { case (value, path) =>
          ResolveIsoDate(value, DateFormatError.withPath(path))
        }

        val validatedMandatoryDecimalNumbers = List(
          (unlistedShares.disposalProceeds, s"$basePath/disposalProceeds"),
          (unlistedShares.allowableCosts, s"$basePath/allowableCosts"),
          (unlistedShares.gainsBeforeLosses, s"$basePath/gainsBeforeLosses")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedOptionalDecimalNumbers = List(
          (unlistedShares.gainsWithBadr, s"$basePath/gainsWithBadr"),
          (unlistedShares.gainsWithInv, s"$basePath/gainsWithInv"),
          (unlistedShares.losses, s"$basePath/losses"),
          (unlistedShares.gainsReportedOnRtt, s"$basePath/gainsReportedOnRtt"),
          (unlistedShares.gainsExceedingLifetimeLimit, s"$basePath/gainsExceedingLifetimeLimit"),
          (unlistedShares.gainsUnderSeis, s"$basePath/gainsUnderSeis"),
          (unlistedShares.lossUsedAgainstGeneralIncome, s"$basePath/lossUsedAgainstGeneralIncome"),
          (unlistedShares.eisOrSeisReliefDueCurrentYear, s"$basePath/eisOrSeisReliefDueCurrentYear"),
          (unlistedShares.lossesUsedAgainstGeneralIncomePreviousYear, s"$basePath/lossesUsedAgainstGeneralIncomePreviousYear"),
          (unlistedShares.eisOrSeisReliefDuePreviousYear, s"$basePath/eisOrSeisReliefDuePreviousYear"),
          (unlistedShares.rttTaxPaid, s"$basePath/rttTaxPaid")
        ).traverse_ { case (value, path) =>
          resolveParsedNumber(value, path)
        }

        val validatedClaimOrElectionCodes = validateClaimOrElectionCodes(
          unlistedShares.claimOrElectionCodes,
          UnlistedSharesClaimOrElectionCodes.parser,
          basePath
        )

        val validatedBadInvRule = if (unlistedShares.bothBadAndInvSupplied) {
          Invalid(List(RuleInvalidClaimDisposalsError.withPath(s"$basePath/claimOrElectionCodes")))
        } else {
          valid
        }

        combine(
          validatedNumberOfDisposals,
          validatedAssetDescription,
          validatedCompanyName,
          validatedCompanyRegistrationNumber,
          validatedDates,
          validatedMandatoryDecimalNumbers,
          validatedOptionalDecimalNumbers,
          validatedClaimOrElectionCodes,
          validatedBadInvRule
        )
      }
    }
  }

  private def validateGainExcludedIndexedSecurities(
      gainExcludedIndexedSecurities: Option[GainExcludedIndexedSecurities]
  ): Validated[Seq[MtdError], Unit] = {
    gainExcludedIndexedSecurities.fold(valid) { gainExcludedIndexedSecurities =>
      resolveParsedNumber(
        gainExcludedIndexedSecurities.gainsFromExcludedSecurities,
        "/gainExcludedIndexedSecurities/gainsFromExcludedSecurities"
      ).toUnit
    }
  }

  private def validateQualifyingAssetHoldingCompany(
      qualifyingAssetHoldingCompany: Option[QualifyingAssetHoldingCompany]
  ): Validated[Seq[MtdError], Unit] = {
    qualifyingAssetHoldingCompany.fold(valid) { qualifyingAssetHoldingCompany =>
      List(
        (qualifyingAssetHoldingCompany.gainsFromQahcBeforeLosses, "/qualifyingAssetHoldingCompany/gainsFromQahcBeforeLosses"),
        (qualifyingAssetHoldingCompany.lossesFromQahc, "/qualifyingAssetHoldingCompany/lossesFromQahc")
      ).traverse_ { case (value, path) =>
        resolveParsedNumber(value, path)
      }
    }
  }

  private def validateNonStandardGains(nonStandardGains: Option[NonStandardGains]): Validated[Seq[MtdError], Unit] = {
    nonStandardGains.fold(valid) { nonStandardGains =>
      List(
        (nonStandardGains.attributedGains, "/nonStandardGains/attributedGains"),
        (nonStandardGains.attributedGainsRttTaxPaid, "/nonStandardGains/attributedGainsRttTaxPaid"),
        (nonStandardGains.otherGains, "/nonStandardGains/otherGains"),
        (nonStandardGains.otherGainsRttTaxPaid, "/nonStandardGains/otherGainsRttTaxPaid")
      ).traverse_ { case (value, path) =>
        resolveParsedNumber(value, path)
      }
    }
  }

  private def validateLosses(losses: Option[Losses]): Validated[Seq[MtdError], Unit] = {
    losses.fold(valid) { losses =>
      List(
        (losses.broughtForwardLossesUsedInCurrentYear, "/losses/broughtForwardLossesUsedInCurrentYear"),
        (losses.setAgainstInYearGains, "/losses/setAgainstInYearGains"),
        (losses.setAgainstEarlierYear, "/losses/setAgainstEarlierYear"),
        (losses.lossesToCarryForward, "/losses/lossesToCarryForward")
      ).traverse_ { case (value, path) =>
        resolveParsedNumber(value, path)
      }
    }
  }

  private def validateAdjustments(adjustments: Option[Adjustments]): Validated[Seq[MtdError], Unit] = {
    adjustments.fold(valid) { adjustments =>
      resolveParsedNumber(adjustments.adjustmentAmount, "/adjustments/adjustmentAmount").toUnit
    }
  }

  private def validateLifetimeAllowance(lifetimeAllowance: Option[LifetimeAllowance]): Validated[Seq[MtdError], Unit] = {
    lifetimeAllowance.fold(valid) { lifetimeAllowance =>
      List(
        (lifetimeAllowance.lifetimeAllowanceBadr, "/lifetimeAllowance/lifetimeAllowanceBadr"),
        (lifetimeAllowance.lifetimeAllowanceInv, "/lifetimeAllowance/lifetimeAllowanceInv")
      ).traverse_ { case (value, path) =>
        resolveParsedNumber(value, path)
      }
    }
  }

}
