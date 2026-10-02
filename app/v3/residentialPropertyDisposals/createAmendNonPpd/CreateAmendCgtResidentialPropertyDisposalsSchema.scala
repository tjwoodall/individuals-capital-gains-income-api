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

package v3.residentialPropertyDisposals.createAmendNonPpd

import api.config.AppConfig
import api.controllers.validators.resolvers.ResolveDetailedTaxYear
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.Valid

import scala.math.Ordering.Implicits.infixOrderingOps

sealed trait CreateAmendCgtResidentialPropertyDisposalsSchema

object CreateAmendCgtResidentialPropertyDisposalsSchema {

  case object Def1 extends CreateAmendCgtResidentialPropertyDisposalsSchema
  case object Def2 extends CreateAmendCgtResidentialPropertyDisposalsSchema
  case object Def3 extends CreateAmendCgtResidentialPropertyDisposalsSchema

  def schemaFor(
      taxYearString: String
  )(implicit appConfig: AppConfig): Validated[Seq[MtdError], CreateAmendCgtResidentialPropertyDisposalsSchema] =
    ResolveDetailedTaxYear(minimumTaxYear = TaxYear.ending(appConfig.minimumPermittedTaxYear))
      .apply(taxYearString)
      .andThen(schemaFor)

  private def schemaFor(
      taxYear: TaxYear
  ): Validated[Seq[MtdError], CreateAmendCgtResidentialPropertyDisposalsSchema] =
    if (taxYear >= TaxYear.fromMtd("2026-27")) Valid(Def3)
    else if (taxYear == TaxYear.fromMtd("2025-26")) Valid(Def2)
    else Valid(Def1)

}
