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

package v3.residentialPropertyDisposals.retrieveNonPpd

import shared.controllers.RequestContext
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.{BaseService, ServiceOutcome}
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response.Def1_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.def2.model.response.Def2_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.model.request.RetrieveCgtResidentialPropertyRequestData
import v3.residentialPropertyDisposals.retrieveNonPpd.model.response.RetrieveCgtResidentialPropertyResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCgtResidentialPropertyService @Inject() (connector: RetrieveCgtResidentialPropertyConnector) extends BaseService {

  def retrieve(request: RetrieveCgtResidentialPropertyRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveCgtResidentialPropertyResponse]] = {

    connector.retrieve(request).map {
      case Right(wrapper) => validateResponse(wrapper)
      case Left(wrapper)  => Left(mapDownstreamErrors(downstreamErrorMap)(wrapper))
    }
  }

  private def validateResponse(wrapper: ResponseWrapper[RetrieveCgtResidentialPropertyResponse])
      : Either[ErrorWrapper, ResponseWrapper[RetrieveCgtResidentialPropertyResponse]] =
    wrapper.responseData match {
      case def1: Def1_RetrieveCgtResidentialPropertyResponse if def1.customerAddedDisposals.isEmpty =>
        Left(ErrorWrapper(wrapper.correlationId, NotFoundError))
      case def2: Def2_RetrieveCgtResidentialPropertyResponse if def2.customerAddedDisposals.isEmpty =>
        Left(ErrorWrapper(wrapper.correlationId, NotFoundError))
      case _ => Right(wrapper)
    }

  private val downstreamErrorMap: Map[String, MtdError] = {
    Map(
      "INVALID_TAXABLE_ENTITY_ID" -> NinoFormatError,
      "INVALID_TAX_YEAR"          -> TaxYearFormatError,
      "INVALID_VIEW"              -> InternalError,
      "TAX_YEAR_NOT_SUPPORTED"    -> RuleTaxYearNotSupportedError,
      "INVALID_CORRELATIONID"     -> InternalError,
      "INVALID_CORRELATION_ID"    -> InternalError,
      "NO_DATA_FOUND"             -> NotFoundError,
      "SERVER_ERROR"              -> InternalError,
      "SERVICE_UNAVAILABLE"       -> InternalError
    )
  }

}
