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

package v3.residentialPropertyDisposals.retrieveNonPpd

import cats.data.Validated.{Invalid, Valid}
import config.CgtAppConfig
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v3.residentialPropertyDisposals.retrieveNonPpd.RetrieveCgtResidentialPropertySchema.{Def1, Def2}
import v3.residentialPropertyDisposals.retrieveNonPpd.def1.Def1_RetrieveCgtResidentialPropertyValidator
import v3.residentialPropertyDisposals.retrieveNonPpd.def2.Def2_RetrieveCgtResidentialPropertyValidator
import v3.residentialPropertyDisposals.retrieveNonPpd.model.request.RetrieveCgtResidentialPropertyRequestData

import javax.inject.Inject

class RetrieveCgtResidentialPropertyValidatorFactory @Inject() (implicit appConfig: CgtAppConfig) {

  def validator(nino: String, taxYear: String): Validator[RetrieveCgtResidentialPropertyRequestData] = {
    val schema = RetrieveCgtResidentialPropertySchema.schemaFor(taxYear)

    schema match {
      case Valid(Def1)     => new Def1_RetrieveCgtResidentialPropertyValidator(nino, taxYear)
      case Valid(Def2)     => new Def2_RetrieveCgtResidentialPropertyValidator(nino, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }

  }

}
