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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2

import cats.data.Validated
import cats.implicits.*
import common.errors.SourceFormatError
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolverSupport}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.request.Def2_RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.request.RetrieveCgtPpdOverridesRequestData

import javax.inject.Inject

class Def2_RetrieveCgtPpdOverridesValidator @Inject() (nino: String, taxYear: String, source: Option[String])
    extends Validator[RetrieveCgtPpdOverridesRequestData]
    with ResolverSupport {

  def validate: Validated[Seq[MtdError], RetrieveCgtPpdOverridesRequestData] =
    (
      ResolveNino(nino),
      resolveMtdSource(source)
    ).mapN((validNino, validSource) => Def2_RetrieveCgtPpdOverridesRequestData(validNino, TaxYear.fromMtd(taxYear), validSource))

  private val resolveMtdSource: Resolver[Option[String], MtdSourceEnum] =
    resolvePartialFunction(SourceFormatError)(MtdSourceEnum.parser).resolveOptionallyWithDefault(MtdSourceEnum.latest)

}
