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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides

import common.errors.SourceFormatError
import shared.controllers.RequestContext
import shared.models.errors.{ErrorWrapper, InternalError, MtdError, NinoFormatError, NotFoundError, RuleTaxYearNotSupportedError, TaxYearFormatError}
import shared.models.outcomes.ResponseWrapper
import shared.services.{BaseService, ServiceOutcome}
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.model.response.Def1_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response.Def2_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.request.RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.response.RetrieveCgtPpdOverridesResponse

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RetrieveCgtPpdOverridesService @Inject() (connector: RetrieveCgtPpdOverridesConnector) extends BaseService {

  def retrieve(request: RetrieveCgtPpdOverridesRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCgtPpdOverridesResponse]] = {

    connector.retrieve(request).map {
      case Right(wrapper) => validateResponse(wrapper)
      case Left(wrapper)  => Left(mapDownstreamErrors(downstreamErrorMap)(wrapper))
    }
  }

  private def validateResponse(
      wrapper: ResponseWrapper[RetrieveCgtPpdOverridesResponse]): Either[ErrorWrapper, ResponseWrapper[RetrieveCgtPpdOverridesResponse]] = {
    wrapper.responseData match
      case def1: Def1_RetrieveCgtPpdOverridesResponse if def1.isEmpty =>
        Left(ErrorWrapper(wrapper.correlationId, NotFoundError))
      case def2: Def2_RetrieveCgtPpdOverridesResponse if def2.isEmpty =>
        Left(ErrorWrapper(wrapper.correlationId, NotFoundError))
      case _ => Right(wrapper)
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_VIEW"              -> SourceFormatError,
      "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_CORRELATION_ID"    -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )
  }

}
