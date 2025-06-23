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

package v2.residentialPropertyDisposals.retrieveAll

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v2.residentialPropertyDisposals.retrieveAll.model.request.RetrieveAllResidentialPropertyCgtRequestData

trait MockRetrieveAllResidentialPropertyCgtValidatorFactory extends TestSuite with MockFactory {

  val mockRetrieveAllResidentialPropertyCgtValidatorFactory: RetrieveAllResidentialPropertyCgtValidatorFactory =
    mock[RetrieveAllResidentialPropertyCgtValidatorFactory]

  object MockedRetrieveAllResidentialPropertyCgtValidatorFactory {

    def validator(): CallHandler[Validator[RetrieveAllResidentialPropertyCgtRequestData]] =
      (mockRetrieveAllResidentialPropertyCgtValidatorFactory.validator(_: String, _: String, _: Option[String])).expects(*, *, *)

  }

  def willUseValidator(
      use: Validator[RetrieveAllResidentialPropertyCgtRequestData]): CallHandler[Validator[RetrieveAllResidentialPropertyCgtRequestData]] = {
    MockedRetrieveAllResidentialPropertyCgtValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrieveAllResidentialPropertyCgtRequestData): Validator[RetrieveAllResidentialPropertyCgtRequestData] =
    new Validator[RetrieveAllResidentialPropertyCgtRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveAllResidentialPropertyCgtRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrieveAllResidentialPropertyCgtRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrieveAllResidentialPropertyCgtRequestData] =
    new Validator[RetrieveAllResidentialPropertyCgtRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveAllResidentialPropertyCgtRequestData] = Invalid(result)
    }

}
