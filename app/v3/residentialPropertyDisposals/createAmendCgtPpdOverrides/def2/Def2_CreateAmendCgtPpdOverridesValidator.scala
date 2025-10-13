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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2

import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.Def2_CreateAmendCgtPpdOverridesRulesValidator.validateBusinessRules
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.model.request.{
  Def2_CreateAmendCgtPpdOverridesRequestBody,
  Def2_CreateAmendCgtPpdOverridesRequestData
}
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

class Def2_CreateAmendCgtPpdOverridesValidator(nino: String, taxYear: String, body: JsValue)
    extends Validator[CreateAmendCgtPpdOverridesRequestData] {
  private val resolveJson = new ResolveNonEmptyJsonObject[Def2_CreateAmendCgtPpdOverridesRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendCgtPpdOverridesRequestData] = (
    ResolveNino(nino),
    resolveJson(body)
  ).mapN((validNino, validBody) =>
    Def2_CreateAmendCgtPpdOverridesRequestData(validNino, TaxYear.fromMtd(taxYear), validBody)) andThen validateBusinessRules

}
