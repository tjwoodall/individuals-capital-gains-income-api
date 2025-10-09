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

package v3.otherCgt.retrieve.def2

import cats.data.Validated
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.ResolveNino
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v3.otherCgt.retrieve.def2.model.request.Def2_RetrieveOtherCgtRequestData
import v3.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class Def2_RetrieveOtherCgtValidator @Inject() (nino: String, taxYear: String) extends Validator[RetrieveOtherCgtRequestData] {

  override def validate: Validated[Seq[MtdError], Def2_RetrieveOtherCgtRequestData] =
    ResolveNino(nino).map(validNino => Def2_RetrieveOtherCgtRequestData(validNino, TaxYear.fromMtd(taxYear)))

}
