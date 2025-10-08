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

package v3.residentialPropertyDisposals.createAmendNonPpd.def2

import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v3.residentialPropertyDisposals.createAmendNonPpd.def2.Def2_CreateAmendCgtResidentialPropertyDisposalsRulesValidator.validateBusinessRules
import v3.residentialPropertyDisposals.createAmendNonPpd.def2.model.request.{
  Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody,
  Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData
}
import v3.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

class Def2_CreateAmendCgtResidentialPropertyDisposalsValidator(nino: String, taxYear: String, body: JsValue)
    extends Validator[CreateAmendCgtResidentialPropertyDisposalsRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody]()

  override def validate: Validated[Seq[MtdError], Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData] = {

    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN { (validNino, requestBody) =>
      Def2_CreateAmendCgtResidentialPropertyDisposalsRequestData(
        nino = validNino,
        taxYear = TaxYear.fromMtd(taxYear),
        body = requestBody
      )
    }.andThen(validateBusinessRules)
  }

}
