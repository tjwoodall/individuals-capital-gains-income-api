/*
 * Copyright 2023 HM Revenue & Customs
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

package v2.otherCgt.createAmend.def1

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.*
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveIsoDate, ResolveParsedNumber}
import shared.models.errors.{DateFormatError, MtdError}
import v2.otherCgt.createAmend.def1.model.request.*

object Def1_CreateAmendOtherCgtRulesValidator extends RulesValidator[Def1_CreateAmendOtherCgtRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()
  private val resolveParsedNumber            = ResolveParsedNumber(min = -99999999999.99)
  private val regex                          = "^[0-9a-zA-Z{À-˿'}\\- _&`():.'^]{1,90}$".r

  def validateBusinessRules(parsed: Def1_CreateAmendOtherCgtRequestData): Validated[Seq[MtdError], Def1_CreateAmendOtherCgtRequestData] = {

    import parsed.*

    combine(
      validateDisposalSequence(body),
      validateNonStandardGains(body),
      validateLosses(body),
      validateAdjustments(body)
    ).onSuccess(parsed)
  }

  private def validateDisposalSequence(requestBody: Def1_CreateAmendOtherCgtRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.disposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (disposal, index) =>
        validateDisposal(disposal, index)
      }
    }
  }

  private def validateDisposal(disposal: Disposal, index: Int): Validated[Seq[MtdError], Unit] = {
    import disposal.*

    val validatedMandatoryDecimalNumbers = List(
      (disposalProceeds, s"/disposals/$index/disposalProceeds"),
      (allowableCosts, s"/disposals/$index/allowableCosts")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedOptionalDecimalNumbers = List(
      (gain, s"/disposals/$index/gain"),
      (loss, s"/disposals/$index/loss"),
      (gainAfterRelief, s"/disposals/$index/gainAfterRelief"),
      (lossAfterRelief, s"/disposals/$index/lossAfterRelief"),
      (rttTaxPaid, s"/disposals/$index/rttTaxPaid")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedDates = List(
      (disposalDate, s"/disposals/$index/disposalDate"),
      (acquisitionDate, s"/disposals/$index/acquisitionDate")
    ).traverse_ { case (value, path) =>
      val resolveDate = ResolveIsoDate(DateFormatError.withPath(path))
      resolveDate(value)
    }

    val validatedAssetDescription: Validated[Seq[MtdError], String] =
      ResolveAssetDescription(assetDescription, regex, AssetDescriptionFormatError.withPath(s"/disposals/$index/assetDescription"))

    val validatedAssetType = ResolveAssetType(assetType, AssetTypeFormatError.withPath(s"/disposals/$index/assetType"))

    val validatedClaimOrElectionCodes = claimOrElectionCodes match {
      case Some(values: Seq[String]) =>
        combine(
          values.zipWithIndex.traverse_ { case (value, subIndex) =>
            ResolveClaimOrElectionCodes(value, ClaimOrElectionCodesFormatError.withPath(s"/disposals/$index/claimOrElectionCodes/$subIndex"))
          }
        )
      case None => valid
    }

    val validatedLossOrGains = if (disposal.gainAndLossBothSupplied) {
      Invalid(List(RuleGainLossError.copy(paths = Some(Seq(s"/disposals/$index")))))
    } else {
      valid
    }

    val validatedLossAfterReliefOrGainAfterRelief = if (disposal.gainAfterReliefAndLossAfterReliefAreBothSupplied) {
      Invalid(List(RuleGainAfterReliefLossAfterReliefError.copy(paths = Some(Seq(s"/disposals/$index")))))
    } else {
      valid
    }

    combine(
      validatedMandatoryDecimalNumbers,
      validatedOptionalDecimalNumbers,
      validatedDates,
      validatedLossOrGains,
      validatedLossAfterReliefOrGainAfterRelief,
      validatedAssetDescription,
      validatedAssetType,
      validatedClaimOrElectionCodes
    )
  }

  private def validateNonStandardGains(requestBody: Def1_CreateAmendOtherCgtRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.nonStandardGains.map(validateNonStandardGains).getOrElse(valid)
  }

  private def validateNonStandardGains(nonStandardGains: NonStandardGains): Validated[Seq[MtdError], Unit] = {
    import nonStandardGains.*

    List(
      (carriedInterestGain, "/nonStandardGains/carriedInterestGain"),
      (carriedInterestRttTaxPaid, "/nonStandardGains/carriedInterestRttTaxPaid"),
      (attributedGains, "/nonStandardGains/attributedGains"),
      (attributedGainsRttTaxPaid, "/nonStandardGains/attributedGainsRttTaxPaid"),
      (otherGains, "/nonStandardGains/otherGains"),
      (otherGainsRttTaxPaid, "/nonStandardGains/otherGainsRttTaxPaid")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }
  }

  private def validateLosses(requestBody: Def1_CreateAmendOtherCgtRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.losses.map(validateLosses).getOrElse(valid)
  }

  private def validateLosses(losses: Losses): Validated[Seq[MtdError], Unit] = {
    import losses.*

    List(
      (broughtForwardLossesUsedInCurrentYear, "/losses/broughtForwardLossesUsedInCurrentYear"),
      (setAgainstInYearGains, "/losses/setAgainstInYearGains"),
      (setAgainstInYearGeneralIncome, "/losses/setAgainstInYearGeneralIncome"),
      (setAgainstEarlierYear, "/losses/setAgainstEarlierYear")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }
  }

  private def validateAdjustments(requestBody: Def1_CreateAmendOtherCgtRequestBody): Validated[Seq[MtdError], Unit] = {
    import requestBody.*

    List(
      (adjustments, "/adjustments")
    ).traverse_ { case (value, path) =>
      resolveParsedNumber(value, path)
    }
  }

}
