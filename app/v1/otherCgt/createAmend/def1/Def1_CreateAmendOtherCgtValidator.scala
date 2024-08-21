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

package v1.otherCgt.createAmend.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers._
import api.models.domain.TaxYear
import api.models.errors._
import cats.data.Validated
import cats.implicits._
import config.AppConfig
import play.api.libs.json.JsValue
import v1.otherCgt.createAmend.def1.Def1_CreateAmendOtherCgtRulesValidator.validateBusinessRules
import v1.otherCgt.createAmend.def1.model.request.{Def1_CreateAmendOtherCgtRequestBody, Def1_CreateAmendOtherCgtRequestData}
import v1.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

class Def1_CreateAmendOtherCgtValidator(nino: String, taxYear: String, body: JsValue)(appConfig: AppConfig)
    extends Validator[CreateAmendOtherCgtRequestData] {

  private lazy val minimumTaxYear = appConfig.minimumPermittedTaxYear
  private lazy val resolveTaxYear = ResolveTaxYearMinimum(TaxYear.fromDownstreamInt(minimumTaxYear))
  private lazy val resolveJson    = new ResolveNonEmptyJsonObject[Def1_CreateAmendOtherCgtRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def1_CreateAmendOtherCgtRequestData) andThen validateBusinessRules

}
