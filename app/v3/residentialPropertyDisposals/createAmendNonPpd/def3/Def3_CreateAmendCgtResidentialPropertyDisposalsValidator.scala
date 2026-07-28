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

package v3.residentialPropertyDisposals.createAmendNonPpd.def3

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveNino, ResolveNonEmptyJsonObject}
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits.*
import play.api.libs.json.JsValue
import v3.residentialPropertyDisposals.createAmendNonPpd.def3.Def3_CreateAmendCgtResidentialPropertyDisposalsRulesValidator.validateBusinessRules
import v3.residentialPropertyDisposals.createAmendNonPpd.def3.model.request.{
  Def3_CreateAmendCgtResidentialPropertyDisposalsRequestBody,
  Def3_CreateAmendCgtResidentialPropertyDisposalsRequestData
}
import v3.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

class Def3_CreateAmendCgtResidentialPropertyDisposalsValidator(nino: String, taxYear: String, body: JsValue, r22CgtEnabled: Boolean)
    extends Validator[CreateAmendCgtResidentialPropertyDisposalsRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def3_CreateAmendCgtResidentialPropertyDisposalsRequestBody]()

  override def validate: Validated[Seq[MtdError], Def3_CreateAmendCgtResidentialPropertyDisposalsRequestData] = {

    (
      ResolveNino(nino),
      resolveJson(body)
    ).mapN { (validNino, requestBody) =>
      Def3_CreateAmendCgtResidentialPropertyDisposalsRequestData(
        nino = validNino,
        taxYear = TaxYear.fromMtd(taxYear),
        body = requestBody
      )
    }.andThen(parsed => validateBusinessRules(parsed, r22CgtEnabled))
  }

}
