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

package v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1

import cats.data.Validated
import cats.implicits.*
import config.CgtAppConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearForVersionNotSupportedError, RuleTaxYearNotSupportedError}
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.Def1_CreateAmendCgtPpdOverridesRulesValidator.validateBusinessRules
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.model.request.{
  Def1_CreateAmendCgtPpdOverridesRequestBody,
  Def1_CreateAmendCgtPpdOverridesRequestData
}
import v2.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

class Def1_CreateAmendCgtPpdOverridesValidator(nino: String, taxYear: String, body: JsValue)(appConfig: CgtAppConfig)
    extends Validator[CreateAmendCgtPpdOverridesRequestData] {

  private lazy val resolveTaxYear = ResolveTaxYearMinMax(
    (TaxYear.fromDownstreamInt(appConfig.minimumPermittedTaxYear), TaxYear.fromMtd("2024-25")),
    RuleTaxYearNotSupportedError,
    RuleTaxYearForVersionNotSupportedError
  )

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreateAmendCgtPpdOverridesRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendCgtPpdOverridesRequestData] = (
    ResolveNino(nino),
    resolveTaxYear(taxYear),
    resolveJson(body)
  ).mapN(Def1_CreateAmendCgtPpdOverridesRequestData.apply) andThen validateBusinessRules

}
