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

package v1.residentialPropertyDisposals.deleteCgtPpdOverrides.def1

import cats.data.Validated
import cats.implicits.*
import config.CgtAppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearForVersionNotSupportedError, RuleTaxYearNotSupportedError}
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.def1.model.request.Def1_DeleteCgtPpdOverridesRequestData
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

import javax.inject.Inject

class Def1_DeleteCgtPpdOverridesValidator @Inject() (nino: String, taxYear: String)(appConfig: CgtAppConfig)
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
