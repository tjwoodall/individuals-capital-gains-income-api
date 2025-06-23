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

package v2.otherCgt.retrieve

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v2.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData

trait MockRetrieveOtherCgtValidatorFactory extends TestSuite with MockFactory {

  val mockRetrieveOtherCgtValidatorFactory: RetrieveOtherCgtValidatorFactory =
    mock[RetrieveOtherCgtValidatorFactory]

  object MockedRetrieveOtherCgtValidatorFactory {

    def validator(): CallHandler[Validator[RetrieveOtherCgtRequestData]] =
      (mockRetrieveOtherCgtValidatorFactory.validator(_: String, _: String)).expects(*, *)

  }

  def willUseValidator(use: Validator[RetrieveOtherCgtRequestData]): CallHandler[Validator[RetrieveOtherCgtRequestData]] = {
    MockedRetrieveOtherCgtValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrieveOtherCgtRequestData): Validator[RetrieveOtherCgtRequestData] =
    new Validator[RetrieveOtherCgtRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveOtherCgtRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrieveOtherCgtRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrieveOtherCgtRequestData] =
    new Validator[RetrieveOtherCgtRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveOtherCgtRequestData] = Invalid(result)
    }

}
