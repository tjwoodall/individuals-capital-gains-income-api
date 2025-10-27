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

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

trait MockCreateAmendOtherCgtValidatorFactory extends TestSuite with MockFactory {

  val mockCreateAmendOtherCgtValidatorFactory: CreateAmendOtherCgtValidatorFactory =
    mock[CreateAmendOtherCgtValidatorFactory]

  object MockedCreateAmendCgtPpdOverridesValidatorFactory {

    def validator(): CallHandler[Validator[CreateAmendOtherCgtRequestData]] =
      (mockCreateAmendOtherCgtValidatorFactory.validator(_: String, _: String, _: JsValue, _: Boolean)).expects(*, *, *, *)

  }

  def willUseValidator(use: Validator[CreateAmendOtherCgtRequestData]): CallHandler[Validator[CreateAmendOtherCgtRequestData]] = {
    MockedCreateAmendCgtPpdOverridesValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: CreateAmendOtherCgtRequestData): Validator[CreateAmendOtherCgtRequestData] =
    new Validator[CreateAmendOtherCgtRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[CreateAmendOtherCgtRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[CreateAmendOtherCgtRequestData] =
    new Validator[CreateAmendOtherCgtRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendOtherCgtRequestData] = Invalid(result)
    }

}
