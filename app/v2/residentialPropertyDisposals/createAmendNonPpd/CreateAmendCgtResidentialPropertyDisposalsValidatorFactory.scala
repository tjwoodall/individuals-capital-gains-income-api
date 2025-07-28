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

package v2.residentialPropertyDisposals.createAmendNonPpd

import config.CgtAppConfig
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import v2.residentialPropertyDisposals.createAmendNonPpd.CreateAmendCgtResidentialPropertyDisposalsSchema.Def1
import v2.residentialPropertyDisposals.createAmendNonPpd.def1.Def1_CreateAmendCgtResidentialPropertyDisposalsValidator
import v2.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

import javax.inject.Inject

class CreateAmendCgtResidentialPropertyDisposalsValidatorFactory @Inject() (appConfig: CgtAppConfig) {

  def validator(nino: String, taxYear: String, body: JsValue): Validator[CreateAmendCgtResidentialPropertyDisposalsRequestData] = {

    val schema = CreateAmendCgtResidentialPropertyDisposalsSchema.schema
    schema match {
      case Def1 => new Def1_CreateAmendCgtResidentialPropertyDisposalsValidator(nino, taxYear, body)(appConfig)
    }
  }

}
