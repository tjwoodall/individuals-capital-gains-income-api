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

import cats.data.Validated
import cats.data.Validated.Valid
import config.CgtAppConfig
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYearMinimum
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.model.response.Def1_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response.Def2_RetrieveCgtPpdOverridesResponse
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.response.RetrieveCgtPpdOverridesResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveCgtPpdOverridesSchema extends DownstreamReadable[RetrieveCgtPpdOverridesResponse]

object RetrieveCgtPpdOverridesSchema {

  case object Def1 extends RetrieveCgtPpdOverridesSchema {
    type DownstreamResp = Def1_RetrieveCgtPpdOverridesResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveCgtPpdOverridesResponse.reads
  }

  case object Def2 extends RetrieveCgtPpdOverridesSchema {
    type DownstreamResp = Def2_RetrieveCgtPpdOverridesResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveCgtPpdOverridesResponse.reads
  }

  def schemaFor(taxYearString: String)(implicit appConfig: CgtAppConfig): Validated[Seq[MtdError], RetrieveCgtPpdOverridesSchema] = {
    ResolveTaxYearMinimum(TaxYear.ending(appConfig.minimumPermittedTaxYear))(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveCgtPpdOverridesSchema] = {
    if (taxYear >= TaxYear.fromMtd("2025-26")) Valid(Def2) else Valid(Def1)
  }

}
