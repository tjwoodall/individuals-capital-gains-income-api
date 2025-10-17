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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides

import config.MockAppConfig
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import support.UnitSpec
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def1.Def1_RetrieveCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.Def2_RetrieveCgtPpdOverridesValidator
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.request.RetrieveCgtPpdOverridesRequestData

class RetrieveCgtPpdOverridesValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private val validNino        = "AA123456A"
  private val def1TaxYear      = "2021-22"
  private val def2TaxYear      = "2025-26"
  private val validSource      = Some("source")
  private val validatorFactory = new RetrieveCgtPpdOverridesValidatorFactory

  trait Test {
    MockedAppConfig.minimumPermittedTaxYear.returns(2020).anyNumberOfTimes()
  }

  "validator" should {
    "return the Def1 validator" when {
      "given a request handled by a Def1 schema" in new Test {
        val result: Validator[RetrieveCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, def1TaxYear, validSource)
        result shouldBe a[Def1_RetrieveCgtPpdOverridesValidator]
      }
    }

    "return the Def2 validator" when {
      "given a request handled by a Def2 schema" in new Test {
        val result: Validator[RetrieveCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, def2TaxYear, validSource)
        result shouldBe a[Def2_RetrieveCgtPpdOverridesValidator]
      }
    }

    "return a validator returning the errors" when {
      "given a request where no valid schema could be determined" in new Test {
        val result: Validator[RetrieveCgtPpdOverridesRequestData] = validatorFactory.validator(validNino, "BAD_TAX_YEAR", validSource)
        result shouldBe an[AlwaysErrorsValidator]
      }
    }

  }

}
