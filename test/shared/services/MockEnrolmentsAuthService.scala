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

package shared.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.models.auth.UserDetails
import shared.models.outcomes.AuthOutcome
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

trait MockEnrolmentsAuthService extends TestSuite with MockFactory {

  val mockEnrolmentsAuthService: EnrolmentsAuthService = mock[EnrolmentsAuthService]

  object MockedEnrolmentsAuthService {

    def authoriseUser(): Unit = {
      (mockEnrolmentsAuthService
        .authorised(_: String, _: Boolean)(_: HeaderCarrier, _: ExecutionContext))
        .expects(*, *, *, *)
        .returns(Future.successful(Right(UserDetails("mtd-id", "Individual", None))))
    }

    def authoriseAgent(mtdId: String, supportingAgentAccessAllowed: Boolean = false): CallHandler[Future[AuthOutcome]] = {
      (mockEnrolmentsAuthService
        .authorised(_: String, _: Boolean)(_: HeaderCarrier, _: ExecutionContext))
        .expects(mtdId, supportingAgentAccessAllowed, *, *)
    }

  }

}
