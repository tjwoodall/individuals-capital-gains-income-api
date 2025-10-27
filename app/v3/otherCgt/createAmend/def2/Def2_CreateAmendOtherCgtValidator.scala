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

package v3.otherCgt.createAmend.def2

import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v3.otherCgt.createAmend.def2.Def2_CreateAmendOtherCgtRulesValidator.validateBusinessRules
import v3.otherCgt.createAmend.def2.model.request.{Def2_CreateAmendOtherCgtRequestBody, Def2_CreateAmendOtherCgtRequestData}
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

class Def2_CreateAmendOtherCgtValidator(nino: String, taxYear: String, body: JsValue, temporalValidationEnabled: Boolean)
    extends Validator[CreateAmendOtherCgtRequestData] {

  private lazy val resolveJson = new ResolveNonEmptyJsonObject[Def2_CreateAmendOtherCgtRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] =
    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN((validNino, validBody) => Def2_CreateAmendOtherCgtRequestData(validNino, TaxYear.fromMtd(taxYear), validBody))
      .andThen { (parsed: Def2_CreateAmendOtherCgtRequestData) =>
        validateBusinessRules(parsed, temporalValidationEnabled)
      }

}
