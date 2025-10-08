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

package v3.residentialPropertyDisposals.createAmendNonPpd

import config.MockAppConfig
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import common.utils.JsonErrorValidators
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v3.residentialPropertyDisposals.createAmendNonPpd.def1.Def1_CreateAmendCgtResidentialPropertyDisposalsValidator
import v3.residentialPropertyDisposals.createAmendNonPpd.def2.Def2_CreateAmendCgtResidentialPropertyDisposalsValidator
import v3.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

class CreateAmendCgtResidentialPropertyDisposalsValidatorFactorySpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  def requestBodyJson(): JsValue = Json.parse(
    s"""
       |{
       |
       |}
     """.stripMargin
  )

  private trait Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  private val validRequestBody = requestBodyJson()

  private def validatorFactory(taxYear: String): Validator[CreateAmendCgtResidentialPropertyDisposalsRequestData] =
    new CreateAmendCgtResidentialPropertyDisposalsValidatorFactory().validator(nino = "ignoredNino", taxYear = taxYear, validRequestBody)

  "running a validation" should {
    "return the Def1 validator" when {
      "given a request handled by a Def1 schema" in new Test {
        validatorFactory("2024-25") shouldBe a[Def1_CreateAmendCgtResidentialPropertyDisposalsValidator]
      }
    }

    "return the Def2 validator" when {
      "given a request handled by a Def2 schema" in new Test {
        validatorFactory("2025-26") shouldBe a[Def2_CreateAmendCgtResidentialPropertyDisposalsValidator]
      }
    }

    "return no valid schema" when {
      "given a request handled by no valid schema" in new Test {
        validatorFactory("INVALID_TAX_YEAR") shouldBe a[AlwaysErrorsValidator]
      }
    }

  }

}
