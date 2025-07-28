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

package v2.residentialPropertyDisposals.deleteNonPpd

import config.MockAppConfig
import shared.controllers.validators.Validator
import support.UnitSpec
import v2.residentialPropertyDisposals.deleteNonPpd.def1.Def1_DeleteCgtNonPpdValidator
import v2.residentialPropertyDisposals.deleteNonPpd.model.request.DeleteCgtNonPpdRequestData

class DeleteCgtNonPpdValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private val validNino    = "AA123456A"
  private val validTaxYear = "2021-22"

  private val validatorFactory = new DeleteCgtNonPpdValidatorFactory(mockAppConfig)

  "validator" should {
    "return the Def1 validator" when {
      "given a request handled by a Def1 schema" in {
        val result: Validator[DeleteCgtNonPpdRequestData] = validatorFactory.validator(validNino, validTaxYear)
        result shouldBe a[Def1_DeleteCgtNonPpdValidator]

      }
    }

  }

}
