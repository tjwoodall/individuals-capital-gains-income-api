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

package v1.residentialPropertyDisposals.createAmendNonPpd

import cats.syntax.either.*
import common.errors.{RuleAcquisitionDateAfterDisposalDateError, RuleCompletionDateError, RuleDisposalDateErrorV1}
import shared.controllers.RequestContext
import shared.models.errors.{InternalError, MtdError, NinoFormatError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import shared.services.{BaseService, ServiceOutcome}
import v1.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCgtResidentialPropertyDisposalsService @Inject() (connector: CreateAmendCgtResidentialPropertyDisposalsConnector)
    extends BaseService {

  def createAndAmend(request: CreateAmendCgtResidentialPropertyDisposalsRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector.createAndAmend(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_PAYLOAD"           -> InternalError,
      "INVALID_DISPOSAL_DATE"     -> RuleDisposalDateErrorV1,
      "INVALID_COMPLETION_DATE"   -> RuleCompletionDateError,
      "INVALID_ACQUISITION_DATE"  -> RuleAcquisitionDateAfterDisposalDateError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )

    val extraTysErrors = Map(
      "TAX_YEAR_NOT_SUPPORTED" -> RuleTaxYearNotSupportedError,
      "INVALID_CORRELATION_ID" -> InternalError
    )

    errors ++ extraTysErrors
  }

}
