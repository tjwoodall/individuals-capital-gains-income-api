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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2

import common.errors.SourceFormatError
import config.MockAppConfig
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.{ErrorWrapper, NinoFormatError}
import support.UnitSpec
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.RetrieveCgtPpdOverridesValidatorFactory
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.request.Def2_RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.request.RetrieveCgtPpdOverridesRequestData

class Def2_RetrieveCgtPpdOverridesValidatorSpec extends UnitSpec with MockAppConfig {
  private implicit val correlationId: String = "1234"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2025-26"
  private val validSource                    = Some("hmrc-held")

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)
  private val parsedSource  = MtdSourceEnum.parser("hmrc-held")

  private val validatorFactory                                                 = new RetrieveCgtPpdOverridesValidatorFactory
  private def validator(nino: String, taxYear: String, source: Option[String]) = validatorFactory.validator(nino, taxYear, source)

  private trait Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied with source" in new Test {
        val result: Either[ErrorWrapper, RetrieveCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, validSource).validateAndWrapResult()

        result shouldBe Right(Def2_RetrieveCgtPpdOverridesRequestData(parsedNino, parsedTaxYear, parsedSource))
      }

      "a valid request is supplied with no source" in new Test {
        val result: Either[ErrorWrapper, RetrieveCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, None).validateAndWrapResult()

        result shouldBe Right(Def2_RetrieveCgtPpdOverridesRequestData(parsedNino, parsedTaxYear, MtdSourceEnum.latest))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        val result: Either[ErrorWrapper, RetrieveCgtPpdOverridesRequestData] =
          validator("A12344A", validTaxYear, validSource).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return SourceFormatError error" when {
      "an invalid MTD source is supplied" in new Test {
        val result: Either[ErrorWrapper, RetrieveCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, Some("invalidMtdSource")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, SourceFormatError))
      }
    }
  }

}
