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

package v1.controllers.validators

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{
  ResolveAssetDescription,
  ResolveAssetType,
  ResolveClaimOrElectionCodes,
  ResolveIsoDate,
  ResolveParsedNumber
}
import api.models.errors.{
  AssetDescriptionFormatError,
  AssetTypeFormatError,
  ClaimOrElectionCodesFormatError,
  DateFormatError,
  MtdError,
  RuleGainAfterReliefLossAfterReliefError,
  RuleGainLossError
}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import v1.models.request.createAmendOtherCgt.{CreateAmendOtherCgtRequestBody, CreateAmendOtherCgtRequestData, Disposal}

object CreateAmendeOtherCgtRulesValidator extends RulesValidator[CreateAmendOtherCgtRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()
  private val regex                          = "^[0-9a-zA-Z{À-˿'}\\- _&`():.'^]{1,90}$".r

  def validateBusinessRules(parsed: CreateAmendOtherCgtRequestData): Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] = {

    import parsed._

    combine(
      validateDisposalSequence(body)
    ).onSuccess(parsed)
  }

  private def validateDisposalSequence(requestBody: CreateAmendOtherCgtRequestBody): Validated[Seq[MtdError], Unit] = {
    requestBody.disposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (disposal, index) =>
        validateDisposal(disposal, index)
      }
    }
  }

  private def validateDisposal(disposal: Disposal, index: Int): Validated[Seq[MtdError], Unit] = {
    import disposal._

    val validatedMandatoryDecimalNumbers = List(
      (disposalProceeds, s"/disposals/$index/disposalProceeds"),
      (allowableCosts, s"/disposals/$index/allowableCosts")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedOptionalDecimalNumbers = List(
      (gain, s"/disposals/$index/gain"),
      (loss, s"/disposals/$index/Loss"),
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

    val validatedAssetDescription: Validated[Seq[MtdError], String] = assetDescription match {
      case value: String =>
        ResolveAssetDescription(value, regex, AssetDescriptionFormatError.withPath(s"/disposals/$index/assetDescription"))
      case _ => Invalid(List(AssetDescriptionFormatError.withPath(s"/disposals/$index/assetDescription")))
    }

    val validatedAssetType = assetType match {
      case value: String =>
        ResolveAssetType(value, AssetTypeFormatError.withPath(s"/disposals/$index/assetType"))
      case _ => Invalid(List(AssetDescriptionFormatError.withPath(s"/disposals/$index/assetDescription")))
    }

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

}
