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

package v2.residentialPropertyDisposals.createAmendNonPpd.def1

import cats.data.Validated
import cats.implicits.*
import config.CgtAppConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleTaxYearForVersionNotSupportedError, RuleTaxYearNotSupportedError}
import v2.residentialPropertyDisposals.createAmendNonPpd.def1.Def1_CreateAmendCgtResidentialPropertyDisposalsRulesValidator.validateBusinessRules
import v2.residentialPropertyDisposals.createAmendNonPpd.def1.model.request.{
  Def1_CreateAmendCgtResidentialPropertyDisposalsRequestBody,
  Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData
}
import v2.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

class Def1_CreateAmendCgtResidentialPropertyDisposalsValidator(nino: String, taxYear: String, body: JsValue)(appConfig: CgtAppConfig)
    extends Validator[CreateAmendCgtResidentialPropertyDisposalsRequestData] {

  private lazy val minimumTaxYear = appConfig.minimumPermittedTaxYear

  private lazy val resolveTaxYear = ResolveTaxYearMinMax(
    (TaxYear.fromDownstreamInt(minimumTaxYear), TaxYear.fromMtd("2024-25")),
    RuleTaxYearNotSupportedError,
    RuleTaxYearForVersionNotSupportedError)

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreateAmendCgtResidentialPropertyDisposalsRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendCgtResidentialPropertyDisposalsRequestData] = (
    ResolveNino(nino),
    resolveTaxYear(taxYear),
    resolveJson(body)
  ).mapN(Def1_CreateAmendCgtResidentialPropertyDisposalsRequestData.apply) andThen validateBusinessRules

}
