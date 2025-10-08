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

package v3.residentialPropertyDisposals.createAmendNonPpd.def2

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.*
import common.errors.*
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveIsoDate, ResolveParsedNumber, ResolveStringPattern, ResolveInteger}
import shared.models.errors.{DateFormatError, MtdError}
import v3.residentialPropertyDisposals.createAmendNonPpd.def2.model.request.{Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData, Disposal}

object Def2_CreateAmendCgtResidentialPropertyDisposalsRulesValidator
    extends RulesValidator[Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()
  private val resolveInteger                 = ResolveInteger(1, 9999)
  private val regex                          = "^[0-9a-zA-Z{À-˿'}\\- _&`():.'^]{1,90}$".r

  def validateBusinessRules(parsed: Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData)
      : Validated[Seq[MtdError], Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData] = {

    import parsed.body.*

    combine(
      disposals.zipWithIndex.traverse_ { case (disposal, index) =>
        validateDisposal(disposal, index)
      }
    ).onSuccess(parsed)
  }

  private def validateDisposal(disposal: Disposal, index: Int): Validated[Seq[MtdError], Unit] = {
    import disposal.*

    val validatedMandatoryDecimalNumbers = List(
      (disposalProceeds, s"/disposals/$index/disposalProceeds"),
      (acquisitionAmount, s"/disposals/$index/acquisitionAmount"),
      (gainsBeforeLosses, s"/disposals/$index/gainsBeforeLosses")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedOptionalDecimalNumbers = List(
      (improvementCosts, s"/disposals/$index/improvementCosts"),
      (additionalCosts, s"/disposals/$index/additionalCosts"),
      (prfAmount, s"/disposals/$index/prfAmount"),
      (otherReliefAmount, s"/disposals/$index/otherReliefAmount"),
      (gainsWithBadr, s"/disposals/$index/gainsWithBadr"),
      (lossesFromThisYear, s"/disposals/$index/lossesFromThisYear"),
      (amountOfNetGain, s"/disposals/$index/amountOfNetGain"),
      (amountOfNetLoss, s"/disposals/$index/amountOfNetLoss")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedDates = List(
      (disposalDate, s"/disposals/$index/disposalDate"),
      (completionDate, s"/disposals/$index/completionDate"),
      (acquisitionDate, s"/disposals/$index/acquisitionDate")
    ).traverse_ { case (value, path) =>
      val resolveDate = ResolveIsoDate(DateFormatError.withPath(path))
      resolveDate(value)
    }

    val validatedCustomerRef = customerReference match {
      case Some(value) =>
        val resolveCustomerRef = new ResolveStringPattern(regex, CustomerRefFormatError.withPath(s"/disposals/$index/customerReference"))
        resolveCustomerRef(value)
      case None => valid
    }

    val validatedLossOrGains = if (disposal.gainAndLossAreBothSupplied) {
      Invalid(List(RuleGainLossError.copy(paths = Some(Seq(s"/disposals/$index")))))
    } else {
      valid
    }

    val validatedNumberOfDisposals = resolveInteger(numberOfDisposals, s"/disposals/$index/numberOfDisposals")

    val validatedClaimOrElectionCodes = claimOrElectionCodes match {
      case Some(values: Seq[String]) =>
        combine(
          values.zipWithIndex.traverse_ { case (value, subIndex) =>
            ResolveClaimOrElectionCodes.resolver(ClaimOrElectionCodesFormatError.withPath(s"/disposals/$index/claimOrElectionCodes/$subIndex"))(value)
          }
        )
      case None => valid
    }

    val validatedLossesFromThisYearRule = if (numberOfDisposals <= 1 && lossesFromThisYear.isDefined) {
      Invalid(List(RuleIncorrectLossesSubmittedError.copy(paths = Some(Seq(s"/disposals/$index")))))
    } else {
      valid
    }

    combine(
      validatedNumberOfDisposals,
      validatedMandatoryDecimalNumbers,
      validatedOptionalDecimalNumbers,
      validatedDates,
      validatedCustomerRef,
      validatedClaimOrElectionCodes,
      validatedLossOrGains,
      validatedLossesFromThisYearRule
    )
  }

}
