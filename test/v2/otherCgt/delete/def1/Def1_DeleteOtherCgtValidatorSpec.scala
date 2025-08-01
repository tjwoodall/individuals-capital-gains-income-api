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

package v2.otherCgt.delete.def1

import config.MockAppConfig
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import support.UnitSpec
import v2.otherCgt.delete.def1.model.request.Def1_DeleteOtherCgtRequestData
import v2.otherCgt.delete.model.request.DeleteOtherCgtRequestData

class Def1_DeleteOtherCgtValidatorSpec extends UnitSpec with MockAppConfig {
  private implicit val correlationId: String = "1234"
  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2020-21"

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String, taxYear: String) = new Def1_DeleteOtherCgtValidator(nino, taxYear)(mockAppConfig).validateAndWrapResult()

  class Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator(validNino, validTaxYear)

        result shouldBe Right(Def1_DeleteOtherCgtRequestData(parsedNino, parsedTaxYear))

      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator("A12344A", validTaxYear)

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator(validNino, "20178")

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator(validNino, "2019-21")

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an invalid tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator(validNino, "2018-19")

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in new Test {
        val result: Either[ErrorWrapper, DeleteOtherCgtRequestData] =
          validator("A12344A", "20178")

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
