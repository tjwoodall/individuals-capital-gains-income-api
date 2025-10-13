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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides

import common.utils.JsonErrorValidators
import config.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import support.UnitSpec
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.Def1_CreateAmendCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.def2.Def2_CreateAmendCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

class CreateAmendCgtPpdOverridesValidatorFactorySpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private trait Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2020)
      .anyNumberOfTimes()

  }

  private val validNino    = "AA123456A"
  private val validTaxYear = "2019-20"

  def requestBodyJson(): JsValue = Json.parse(
    s"""
       |{
       |
       |}
     """.stripMargin
  )

  private val validRequestBody = requestBodyJson()

  private val validatorFactory = new CreateAmendCgtPpdOverridesValidatorFactory

  "CreateAmendCgtPpdOverridesValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in new Test {
        val result: Validator[CreateAmendCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, validTaxYear, validRequestBody)
        result shouldBe a[Def1_CreateAmendCgtPpdOverridesValidator]
      }
    }

    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in new Test {
        val result: Validator[CreateAmendCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, "2025-26", validRequestBody)
        result shouldBe a[Def2_CreateAmendCgtPpdOverridesValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in new Test {
        val result: Validator[CreateAmendCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, "BAD_TAX_YEAR", validRequestBody)
        result shouldBe a[AlwaysErrorsValidator]
      }
    }
  }

}
