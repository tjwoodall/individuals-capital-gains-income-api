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

package v1.residentialPropertyDisposals.deleteCgtPpdOverrides

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v1.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

trait MockDeleteCgtPpdOverridesValidatorFactory extends TestSuite with MockFactory {

  val mockDeleteCgtPpdOverridesValidatorFactory: DeleteCgtPpdOverridesValidatorFactory =
    mock[DeleteCgtPpdOverridesValidatorFactory]

  object MockedDeleteCgtPpdOverridesValidatorFactory {

    def validator(): CallHandler[Validator[DeleteCgtPpdOverridesRequestData]] =
      (mockDeleteCgtPpdOverridesValidatorFactory.validator(_: String, _: String)).expects(*, *)

  }

  def willUseValidator(use: Validator[DeleteCgtPpdOverridesRequestData]): CallHandler[Validator[DeleteCgtPpdOverridesRequestData]] = {
    MockedDeleteCgtPpdOverridesValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: DeleteCgtPpdOverridesRequestData): Validator[DeleteCgtPpdOverridesRequestData] =
    new Validator[DeleteCgtPpdOverridesRequestData] {
      def validate: Validated[Seq[MtdError], DeleteCgtPpdOverridesRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[DeleteCgtPpdOverridesRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[DeleteCgtPpdOverridesRequestData] =
    new Validator[DeleteCgtPpdOverridesRequestData] {
      def validate: Validated[Seq[MtdError], DeleteCgtPpdOverridesRequestData] = Invalid(result)
    }

}
