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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides

import cats.data.Validated.{Invalid, Valid}
import config.CgtAppConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.CreateAmendCgtPpdOverridesSchema.{Def1, Def2}
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.Def1_CreateAmendCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.Def2_CreateAmendCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

import javax.inject.Inject

class CreateAmendCgtPpdOverridesValidatorFactory @Inject() (implicit appConfig: CgtAppConfig) {

  def validator(nino: String, taxYear: String, body: JsValue): Validator[CreateAmendCgtPpdOverridesRequestData] = {

    val schema = CreateAmendCgtPpdOverridesSchema.schemaFor(taxYear)
    schema match {
      case Valid(Def1)     => new Def1_CreateAmendCgtPpdOverridesValidator(nino, taxYear, body)
      case Valid(Def2)     => new Def2_CreateAmendCgtPpdOverridesValidator(nino, taxYear, body)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
