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

package shared.controllers.validators.resolvers

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.errors.ValueFormatError
import shared.utils.UnitSpec

class ResolveBigIntegerSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks {

  private val path = "/some/path"

  "validate" when {
    "min and max are specified" must {
      val min: BigInt = -99999999999L
      val max: BigInt = 99999999999L

      val error   = ValueFormatError.forIntegerPathAndRange(path, s"$min", s"$max")
      val resolve = ResolveBigInteger(min, max)

      "return the error with the correct message if and only if the value is outside the inclusive range" when {
        "using validate" in {
          forAll { (money: BigInt) =>
            val expected = if (min <= money && money <= max) Valid(money) else Invalid(List(error))
            val result   = resolve(money, path)
            result shouldBe expected
          }
        }

        "using validateOptional" in {
          forAll { (money: BigInt) =>
            val expected = if (min <= money && money <= max) Valid(Some(money)) else Invalid(List(error))
            val result   = resolve(Some(money), path)
            result shouldBe expected
          }
        }
      }

      "return no error" when {
        "no number is supplied to validateOptional" in {
          val result = resolve(None, path)
          result shouldBe Valid(None)
        }
      }
    }
  }

}
