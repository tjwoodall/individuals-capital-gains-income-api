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

package v1.residentialPropertyDisposals.deleteCgtPpdOverrides

import config.CgtAppConfig
import shared.controllers.validators.Validator
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.DeleteCgtPpdOverridesSchema.Def1
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.def1.Def1_DeleteCgtPpdOverridesValidator
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class DeleteCgtPpdOverridesValidatorFactory @Inject() (appConfig: CgtAppConfig) {

  def validator(nino: String, taxYear: String): Validator[DeleteCgtPpdOverridesRequestData] = {

    val schema = DeleteCgtPpdOverridesSchema.schema

    schema match {
      case Def1 => new Def1_DeleteCgtPpdOverridesValidator(nino, taxYear)(appConfig)
    }
  }

}
