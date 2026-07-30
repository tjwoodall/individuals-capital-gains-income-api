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

package v3.otherCgt.createAmend.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import v3.otherCgt.createAmend.def3.Def3_CreateAmendOtherCgtRulesValidator.validateBusinessRules
import v3.otherCgt.createAmend.def3.model.request.{Def3_CreateAmendOtherCgtRequestBody, Def3_CreateAmendOtherCgtRequestData}
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

class Def3_CreateAmendOtherCgtValidator(nino: String, taxYear: String, body: JsValue, temporalValidationEnabled: Boolean, r22CgtEnabled: Boolean)
    extends Validator[CreateAmendOtherCgtRequestData] {

  private lazy val resolveJson = new ResolveNonEmptyJsonObject[Def3_CreateAmendOtherCgtRequestBody]()

  def validate: Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] =
    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN((validNino, validBody) => Def3_CreateAmendOtherCgtRequestData(validNino, TaxYear.fromMtd(taxYear), validBody))
      .andThen { (parsed: Def3_CreateAmendOtherCgtRequestData) =>
        validateBusinessRules(parsed, temporalValidationEnabled, r22CgtEnabled)
      }

}
