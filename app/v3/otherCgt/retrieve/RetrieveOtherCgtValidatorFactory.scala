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

package v3.otherCgt.retrieve

import config.CgtAppConfig
import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import v3.otherCgt.retrieve.RetrieveOtherCgtSchema.{Def1, Def2}
import v3.otherCgt.retrieve.def1.Def1_RetrieveOtherCgtValidator
import v3.otherCgt.retrieve.def2.Def2_RetrieveOtherCgtValidator
import v3.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class RetrieveOtherCgtValidatorFactory @Inject() (implicit appConfig: CgtAppConfig) {

  def validator(nino: String, taxYear: String): Validator[RetrieveOtherCgtRequestData] = {
    val schema = RetrieveOtherCgtSchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_RetrieveOtherCgtValidator(nino, taxYear)
      case Valid(Def2)     => new Def2_RetrieveOtherCgtValidator(nino, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
