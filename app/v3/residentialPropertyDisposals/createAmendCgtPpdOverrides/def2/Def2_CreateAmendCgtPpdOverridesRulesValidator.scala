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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.toFoldableOps
import common.errors.{PpdSubmissionIdFormatError, RuleAmountGainLossError}
import shared.controllers.validators.resolvers.{ResolveIsoDate, ResolveParsedNumber}
import shared.models.errors.{DateFormatError, RuleDateRangeInvalidError}
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.model.request.{
  Def2_CreateAmendCgtPpdOverridesRequestBody,
  MultiplePropertyDisposals,
  SinglePropertyDisposals
}
import shared.controllers.validators.RulesValidator
import shared.models.errors.MtdError
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.model.request.Def2_CreateAmendCgtPpdOverridesRequestData

object Def2_CreateAmendCgtPpdOverridesRulesValidator extends RulesValidator[Def2_CreateAmendCgtPpdOverridesRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()
  private val ppdSubmissionIdRegex           = "^[A-Za-z0-9]{12}$"

  def validateBusinessRules(
      parsed: Def2_CreateAmendCgtPpdOverridesRequestData): Validated[Seq[MtdError], Def2_CreateAmendCgtPpdOverridesRequestData] = {
    import parsed.*
    combine(
      validateBothSuppliedDisposals(body),
      validateSuppliedDisposals(body)
    ).onSuccess(parsed)
  }

  private def validateBothSuppliedDisposals(requestBody: Def2_CreateAmendCgtPpdOverridesRequestBody): Validated[Seq[MtdError], Unit] = {
    val BothSuppliedMultiplePropertyValidation = requestBody.multiplePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (multiplePropertyDisposals, index) =>
        validateBothSuppliedMultipleDisposals(multiplePropertyDisposals, index)
      }
    }

    val BothSuppliedSinglePropertyValidation = requestBody.singlePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (singlePropertyDisposals, index) =>
        validateBothSuppliedSingleDisposals(singlePropertyDisposals, index)
      }
    }

    combine(BothSuppliedMultiplePropertyValidation, BothSuppliedSinglePropertyValidation)
  }

  private def validateBothSuppliedMultipleDisposals(multiplePropertyDisposals: MultiplePropertyDisposals,
                                                    arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    if (multiplePropertyDisposals.isBothSupplied) {
      Invalid(List(RuleAmountGainLossError.withPath(s"/multiplePropertyDisposals/$arrayIndex")))
    } else if (multiplePropertyDisposals.isNetAmountEmpty) {
      Invalid(List(RuleAmountGainLossError.withPath(s"/multiplePropertyDisposals/$arrayIndex")))
    } else {
      Valid(())
    }
  }

  private def validateBothSuppliedSingleDisposals(singlePropertyDisposals: SinglePropertyDisposals,
                                                  arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    if (singlePropertyDisposals.isBothSupplied) {
      Invalid(List(RuleAmountGainLossError.withPath(s"/singlePropertyDisposals/$arrayIndex")))
    } else if (singlePropertyDisposals.isBothEmpty) {
      Invalid(List(RuleAmountGainLossError.withPath(s"/singlePropertyDisposals/$arrayIndex")))
    } else {
      Valid(())
    }
  }

  private def validatePpdSubmissionId(ppdSubmissionId: String, error: MtdError): Validated[Seq[MtdError], Unit] = {
    if (ppdSubmissionId.matches(ppdSubmissionIdRegex)) {
      valid
    } else {
      Invalid(List(error))
    }
  }

  private def validateSuppliedDisposals(requestBody: Def2_CreateAmendCgtPpdOverridesRequestBody): Validated[Seq[MtdError], Unit] = {
    val multiplePropertyPpdValidation = requestBody.multiplePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (multiplePropertyDisposals, index) =>
        validateMultiplePropertyDisposalsPpdId(multiplePropertyDisposals, index)
      }
    }
    val multiplePropertyValuesValidation = requestBody.multiplePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (multiplePropertyDisposals, index) =>
        validateMultiplePropertyDisposalsValues(multiplePropertyDisposals, index)
      }
    }
    val singlePropertyPpdValidation = requestBody.singlePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (singlePropertyDisposals, index) =>
        validateSinglePropertyDisposalsPpdId(singlePropertyDisposals, index)
      }
    }
    val singlePropertyValuesValidation = requestBody.singlePropertyDisposals.fold[Validated[Seq[MtdError], Unit]](Valid(())) { disposals =>
      disposals.zipWithIndex.traverse_ { case (singlePropertyDisposals, index) =>
        validateSinglePropertyDisposalsValues(singlePropertyDisposals, index)
      }
    }

    combine(multiplePropertyPpdValidation, multiplePropertyValuesValidation, singlePropertyPpdValidation, singlePropertyValuesValidation)
  }

  private def validateMultiplePropertyDisposalsPpdId(multiplePropertyDisposals: MultiplePropertyDisposals,
                                                     arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    validatePpdSubmissionId(
      multiplePropertyDisposals.ppdSubmissionId,
      PpdSubmissionIdFormatError.withPath(s"/multiplePropertyDisposals/$arrayIndex/ppdSubmissionId"))

  }

  private def validateMultiplePropertyDisposalsValues(multiplePropertyDisposals: MultiplePropertyDisposals,
                                                      arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    import multiplePropertyDisposals.*
    val validatedNonNegatives = List(
      (amountOfNetGain, s"/multiplePropertyDisposals/$arrayIndex/amountOfNetGain"),
      (amountOfNetLoss, s"/multiplePropertyDisposals/$arrayIndex/amountOfNetLoss"),
      (gainsWithBadr, s"/multiplePropertyDisposals/$arrayIndex/gainsWithBadr"),
      (gainsWithInv, s"/multiplePropertyDisposals/$arrayIndex/gainsWithInv")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }
    validatedNonNegatives
  }

  private def validateSinglePropertyDisposalsPpdId(singlePropertyDisposals: SinglePropertyDisposals,
                                                   arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    validatePpdSubmissionId(
      singlePropertyDisposals.ppdSubmissionId,
      PpdSubmissionIdFormatError.withPath(s"/singlePropertyDisposals/$arrayIndex/ppdSubmissionId"))
  }

  private def validateSinglePropertyDisposalsValues(singlePropertyDisposals: SinglePropertyDisposals,
                                                    arrayIndex: Int): Validated[Seq[MtdError], Unit] = {
    import singlePropertyDisposals.*
    val validatedNonNegatives = List(
      (disposalProceeds, s"/singlePropertyDisposals/$arrayIndex/disposalProceeds"),
      (acquisitionAmount, s"/singlePropertyDisposals/$arrayIndex/acquisitionAmount"),
      (improvementCosts, s"/singlePropertyDisposals/$arrayIndex/improvementCosts"),
      (additionalCosts, s"/singlePropertyDisposals/$arrayIndex/additionalCosts"),
      (prfAmount, s"/singlePropertyDisposals/$arrayIndex/prfAmount"),
      (otherReliefAmount, s"/singlePropertyDisposals/$arrayIndex/otherReliefAmount")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedOptionalNonNegatives = List(
      (lossesFromThisYear, s"/singlePropertyDisposals/$arrayIndex/lossesFromThisYear"),
      (lossesFromPreviousYear, s"/singlePropertyDisposals/$arrayIndex/lossesFromPreviousYear"),
      (amountOfNetGain, s"/singlePropertyDisposals/$arrayIndex/amountOfNetGain"),
      (amountOfNetLoss, s"/singlePropertyDisposals/$arrayIndex/amountOfNetLoss"),
      (gainsWithBadr, s"/singlePropertyDisposals/$arrayIndex/gainsWithBadr"),
      (gainsWithInv, s"/singlePropertyDisposals/$arrayIndex/gainsWithInv")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedCompletionDate = {
      ResolveIsoDate.withMinMaxCheck(
        completionDate,
        DateFormatError.withPath(s"/singlePropertyDisposals/$arrayIndex/completionDate"),
        RuleDateRangeInvalidError.withPath(s"/singlePropertyDisposals/$arrayIndex/completionDate")
      )
    }

    val validatedAcquisitionDate = {
      ResolveIsoDate.withMinMaxCheck(
        acquisitionDate,
        DateFormatError.withPath(s"/singlePropertyDisposals/$arrayIndex/acquisitionDate"),
        RuleDateRangeInvalidError.withPath(s"/singlePropertyDisposals/$arrayIndex/acquisitionDate")
      )
    }

    combine(validatedNonNegatives, validatedOptionalNonNegatives, validatedCompletionDate, validatedAcquisitionDate)
  }

}
