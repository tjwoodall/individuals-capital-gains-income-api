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

package v1.residentialPropertyDisposals.retrieveAll.def1

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.*
import common.errors.SourceFormatError
import config.CgtAppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v1.residentialPropertyDisposals.retrieveAll.def1.model.MtdSourceEnum
import v1.residentialPropertyDisposals.retrieveAll.def1.model.request.Def1_RetrieveAllResidentialPropertyRequestData
import v1.residentialPropertyDisposals.retrieveAll.model.request.RetrieveAllResidentialPropertyCgtRequestData

import javax.inject.{Inject, Singleton}
import scala.util.{Failure, Success, Try}

@Singleton
class Def1_RetrieveAllResidentialPropertyCgtValidator @Inject() (nino: String, taxYear: String, source: Option[String])(appConfig: CgtAppConfig)
    extends Validator[RetrieveAllResidentialPropertyCgtRequestData] {

  private lazy val minimumTaxYear = appConfig.minimumPermittedTaxYear
  private lazy val resolveTaxYear = ResolveTaxYearMinimum(TaxYear.fromDownstreamInt(minimumTaxYear))

  def validate: Validated[Seq[MtdError], RetrieveAllResidentialPropertyCgtRequestData] =
    (
      ResolveNino(nino),
      resolveTaxYear(taxYear),
      resolveMtdSource(source)
    ).mapN(Def1_RetrieveAllResidentialPropertyRequestData.apply)

  private def resolveMtdSource(maybeSource: Option[String]): Validated[Seq[MtdError], MtdSourceEnum] = {
    maybeSource
      .map { source =>
        Try {
          MtdSourceEnum.parser(source)
        } match {
          case Success(mtdSource) => Valid(mtdSource)
          case Failure(_)         => Invalid(List(SourceFormatError))
        }
      }
      .getOrElse(Valid(MtdSourceEnum.latest))
  }

}
