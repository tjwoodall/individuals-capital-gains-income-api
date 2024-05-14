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
import api.controllers.validators.resolvers.{ResolveIsoDate, ResolveParsedNumber, ResolveStringPattern}
import api.models.errors.{CustomerRefFormatError, DateFormatError, MtdError, RuleGainLossError}
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits._
import v1.models.request.createAmendCgtResidentialPropertyDisposals.{CreateAmendCgtResidentialPropertyDisposalsRequestData, Disposal}

object CreateAmendCgtResidentialPropertyDisposalsRulesValidator extends RulesValidator[CreateAmendCgtResidentialPropertyDisposalsRequestData] {

  private val resolveNonNegativeParsedNumber = ResolveParsedNumber()
  private val regex                          = "^[0-9a-zA-Z{À-˿'}\\- _&`():.'^]{1,90}$".r

  def validateBusinessRules(parsed: CreateAmendCgtResidentialPropertyDisposalsRequestData)
      : Validated[Seq[MtdError], CreateAmendCgtResidentialPropertyDisposalsRequestData] = {

    import parsed.body._

    combine(
      disposals.zipWithIndex.traverse_ { case (disposal, index) =>
        validateDisposal(disposal, index)
      }
    ).onSuccess(parsed)
  }

  private def validateDisposal(disposal: Disposal, index: Int): Validated[Seq[MtdError], Unit] = {
    import disposal._

    val validatedMandatoryDecimalNumbers = List(
      (disposalProceeds, s"/disposals/$index/disposalProceeds"),
      (acquisitionAmount, s"/disposals/$index/acquisitionAmount")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedOptionalDecimalNumbers = List(
      (improvementCosts, s"/disposals/$index/improvementCosts"),
      (additionalCosts, s"/disposals/$index/additionalCosts"),
      (prfAmount, s"/disposals/$index/prfAmount"),
      (otherReliefAmount, s"/disposals/$index/otherReliefAmount"),
      (lossesFromThisYear, s"/disposals/$index/lossesFromThisYear"),
      (lossesFromPreviousYear, s"/disposals/$index/lossesFromPreviousYear"),
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

    combine(validatedMandatoryDecimalNumbers, validatedOptionalDecimalNumbers, validatedDates, validatedCustomerRef, validatedLossOrGains)
  }

}
