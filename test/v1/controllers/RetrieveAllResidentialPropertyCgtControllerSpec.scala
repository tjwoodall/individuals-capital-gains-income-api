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

package v1.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.mocks.MockIdGenerator
import api.models.domain.{MtdSourceEnum, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import mocks.MockAppConfig
import play.api.mvc.Result
import v1.controllers.validators.MockRetrieveAllResidentialPropertyCgtValidatorFactory
import v1.fixtures.RetrieveAllResidentialPropertyCgtControllerFixture._
import v1.mocks.services.MockRetrieveAllResidentialPropertyCgtService
import v1.models.request.retrieveAllResidentialPropertyCgt.RetrieveAllResidentialPropertyCgtRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveAllResidentialPropertyCgtControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveAllResidentialPropertyCgtService
    with MockRetrieveAllResidentialPropertyCgtValidatorFactory
    with MockIdGenerator
    with MockAppConfig {

  val taxYear: String        = "2019-20"
  val source: Option[String] = Some("latest")

  val requestData: RetrieveAllResidentialPropertyCgtRequestData = RetrieveAllResidentialPropertyCgtRequestData(
    nino = Nino(validNino),
    taxYear = TaxYear.fromMtd(taxYear),
    source = MtdSourceEnum.latest
  )

  "retrieveAll" should {
    "return a successful response with status 200 (OK)" when {
      "given a valid request" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveAllResidentialPropertyCgtService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseModel))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveAllResidentialPropertyCgtService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest {

    val controller = new RetrieveAllResidentialPropertyCgtController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveAllResidentialPropertyCgtValidatorFactory,
      service = mockRetrieveAllResidentialPropertyCgtService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.retrieveAll(validNino, taxYear, source)(fakeGetRequest)

  }

}
