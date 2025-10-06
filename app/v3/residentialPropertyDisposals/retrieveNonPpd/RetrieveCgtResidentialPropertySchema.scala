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

import cats.data.Validated
import cats.data.Validated.Valid
import config.CgtAppConfig
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYearMinimum
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response.Def1_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.def2.model.response.Def2_RetrieveCgtResidentialPropertyResponse
import v3.residentialPropertyDisposals.retrieveNonPpd.model.response.RetrieveCgtResidentialPropertyResponse

import scala.math.Ordering.Implicits.infixOrderingOps

sealed trait RetrieveCgtResidentialPropertySchema extends DownstreamReadable[RetrieveCgtResidentialPropertyResponse]

object RetrieveCgtResidentialPropertySchema {

  case object Def1 extends RetrieveCgtResidentialPropertySchema {
    type DownstreamResp = Def1_RetrieveCgtResidentialPropertyResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveCgtResidentialPropertyResponse.format.reads(_)
  }

  case object Def2 extends RetrieveCgtResidentialPropertySchema {
    type DownstreamResp = Def2_RetrieveCgtResidentialPropertyResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveCgtResidentialPropertyResponse.format.reads(_)
  }

  def schemaFor(taxYearString: String)(implicit appConfig: CgtAppConfig): Validated[Seq[MtdError], RetrieveCgtResidentialPropertySchema] =
    ResolveTaxYearMinimum(TaxYear.ending(appConfig.minimumPermittedTaxYear))(taxYearString) andThen schemaFor

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveCgtResidentialPropertySchema] = {
    if (taxYear >= TaxYear.fromMtd("2025-26")) Valid(Def2) else Valid(Def1)
  }

}
