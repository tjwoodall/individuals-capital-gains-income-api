/*
 * Copyright 2026 HM Revenue & Customs
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

package v2.residentialPropertyDisposals.deleteCgtPpdOverrides.def1

import api.config.AppConfig
import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinMax}
import api.models.domain.TaxYear
import api.models.errors.*
import cats.data.Validated
import cats.implicits.*
import v2.residentialPropertyDisposals.deleteCgtPpdOverrides.def1.model.request.Def1_DeleteCgtPpdOverridesRequestData
import v2.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

import javax.inject.Inject

class Def1_DeleteCgtPpdOverridesValidator @Inject() (nino: String, taxYear: String)(appConfig: AppConfig)
    extends Validator[DeleteCgtPpdOverridesRequestData] {

  private lazy val minimumTaxYear = appConfig.minimumPermittedTaxYear

  private lazy val resolveTaxYear =
    ResolveTaxYearMinMax(
      (TaxYear.fromDownstreamInt(minimumTaxYear), TaxYear.fromMtd("2024-25")),
      RuleTaxYearNotSupportedError,
      RuleTaxYearForVersionNotSupportedError)

  def validate: Validated[Seq[MtdError], DeleteCgtPpdOverridesRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear)
    ).mapN(Def1_DeleteCgtPpdOverridesRequestData.apply)

}
