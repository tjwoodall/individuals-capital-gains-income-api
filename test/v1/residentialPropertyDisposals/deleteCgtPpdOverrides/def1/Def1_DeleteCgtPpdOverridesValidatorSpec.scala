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

package v1.residentialPropertyDisposals.deleteCgtPpdOverrides.def1

import config.MockAppConfig
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import support.UnitSpec
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.DeleteCgtPpdOverridesValidatorFactory
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.def1.model.request.Def1_DeleteCgtPpdOverridesRequestData

class Def1_DeleteCgtPpdOverridesValidatorSpec extends UnitSpec with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2020-21"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  val validatorFactory = new DeleteCgtPpdOverridesValidatorFactory(mockAppConfig)

  private def validator(nino: String, taxYear: String) =
    validatorFactory.validator(nino, taxYear)

  class Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in new Test {
        val result = validator(validNino, validTaxYear).validateAndWrapResult()
        result shouldBe Right(Def1_DeleteCgtPpdOverridesRequestData(parsedNino, parsedTaxYear))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        val result = validator("A12344A", validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        val result = validator(validNino, "201718").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "a tax year before the minimum tax year is supplied" in new Test {
        val result = validator(validNino, "2016-17").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return RuleTaxYearForVersionNotSupportedError error" when {
      "a tax year after the maximum tax year is supplied" in new Test {
        val result = validator(validNino, "2025-26").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearForVersionNotSupportedError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in new Test {
        val result = validator(validNino, "2017-19").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in new Test {
        val result = validator("not-a-nino", "2017-19").validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, RuleTaxYearRangeInvalidError))
          )
        )
      }
    }
  }

}
