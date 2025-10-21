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

package common.errors

import play.api.http.Status.*
import shared.models.errors.MtdError

object PpdSubmissionIdFormatError  extends MtdError("FORMAT_PPD_SUBMISSION_ID", "The provided ppdSubmissionId is invalid", BAD_REQUEST)
object CustomerRefFormatError      extends MtdError("FORMAT_CUSTOMER_REF", "The provided customer reference is invalid", BAD_REQUEST)
object AssetDescriptionFormatError extends MtdError("FORMAT_ASSET_DESCRIPTION", "The provided asset description is invalid", BAD_REQUEST)
object AssetTypeFormatError        extends MtdError("FORMAT_ASSET_TYPE", "The format of the assetType value is invalid", BAD_REQUEST)
object SourceFormatError           extends MtdError("FORMAT_SOURCE", "The provided source is invalid", BAD_REQUEST)

object ClaimOrElectionCodesFormatError
    extends MtdError("FORMAT_CLAIM_OR_ELECTION_CODES", "The format of the claimOrElectionCodes value is invalid", BAD_REQUEST)

// Rule Errors
object RuleDuplicatedPpdSubmissionIdError
    extends MtdError("RULE_DUPLICATED_PPD_SUBMISSION_ID", "A provided ppdSubmissionId is duplicated", BAD_REQUEST)

object RuleIncorrectDisposalTypeError
    extends MtdError("RULE_INCORRECT_DISPOSAL_TYPE", "A provided ppdSubmissionId is being used for the incorrect disposal type", BAD_REQUEST)

object RuleGainLossError extends MtdError("RULE_GAIN_LOSS", "Only one of gain or loss values can be provided", BAD_REQUEST)

object RuleIncorrectLossesSubmittedError
    extends MtdError(
      "RULE_INCORRECT_LOSSES_SUBMITTED",
      "LossesFromThisYear can not be submitted if the numberOfDisposals is less than or equal to 1",
      BAD_REQUEST)

object RuleDisposalDateErrorV1 extends MtdError("RULE_DISPOSAL_DATE", "The disposalDate must be within the specified tax year", BAD_REQUEST)

object RuleCompletionDateError
    extends MtdError(
      "RULE_COMPLETION_DATE",
      "The completionDate must be within the specific tax year and not in the future. If the specified tax year has not ended, the completionDate must be between 7th March and 5th April",
      BAD_REQUEST)

object RuleAcquisitionDateAfterDisposalDateError
    extends MtdError("RULE_ACQUISITION_DATE_AFTER_DISPOSAL_DATE", "The acquisitionDate must not be later than disposalDate", BAD_REQUEST)

object RuleGainAfterReliefLossAfterReliefError
    extends MtdError("RULE_GAIN_AFTER_RELIEF_LOSS_AFTER_RELIEF", "Only one of gainAfterRelief or lossAfterRelief values can be provided", BAD_REQUEST)

object RuleAcquisitionDateError extends MtdError("RULE_ACQUISITION_DATE", "The acquisitionDate must not be later than disposalDate", BAD_REQUEST)

object RuleDisposalDateNotFutureError
    extends MtdError(
      "RULE_DISPOSAL_DATE_NOT_FUTURE",
      "The disposalDate must be in the specified tax year and no later than today's date",
      BAD_REQUEST)

object RuleAmountGainLossError
    extends MtdError("RULE_AMOUNT_GAIN_LOSS", "Either amountOfNetGain or amountOfNetLoss, must be provided but not both", BAD_REQUEST)

object RuleOutsideAmendmentWindowError extends MtdError("RULE_OUTSIDE_AMENDMENT_WINDOW", "You are outside the amendment window", BAD_REQUEST)

// Not found errors
object PpdSubmissionIdNotFoundError extends MtdError("PPD_SUBMISSION_ID_NOT_FOUND", "Matching resource not found", NOT_FOUND)
