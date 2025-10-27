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

package v3.otherCgt.createAmend

import common.utils.JsonErrorValidators
import config.MockAppConfig
import play.api.libs.json.JsObject
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import support.UnitSpec
import v3.otherCgt.createAmend.def1.Def1_CreateAmendOtherCgtValidator
import v3.otherCgt.createAmend.def2.Def2_CreateAmendOtherCgtValidator
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

class CreateAmendOtherCgtValidatorFactorySpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private def validatorFor(taxYear: String): Validator[CreateAmendOtherCgtRequestData] = new CreateAmendOtherCgtValidatorFactory().validator(
    nino = "ignoredNino",
    taxYear = taxYear,
    body = JsObject.empty,
    temporalValidationEnabled = true
  )

  private trait Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  "CreateAmendOtherCgtValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in new Test {
        validatorFor("2024-25") shouldBe a[Def1_CreateAmendOtherCgtValidator]
      }
    }

    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in new Test {
        validatorFor("2025-26") shouldBe a[Def2_CreateAmendOtherCgtValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in new Test {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
