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
import play.api.libs.json.Writes
import shared.models.audit.AuditEvent
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.http.connector.AuditResult

import scala.concurrent.{ExecutionContext, Future}

trait MockAuditService extends TestSuite with MockFactory {

  val mockAuditService: AuditService = stub[AuditService]

  object MockedAuditService {

    def verifyAuditEvent[T](event: AuditEvent[T]): CallHandler[Future[AuditResult]] = {
      (mockAuditService
        .auditEvent(_: AuditEvent[T])(_: HeaderCarrier, _: ExecutionContext, _: Writes[T]))
        .verify(event, *, *, *)
        .returning(Future.successful(AuditResult.Success))
    }

  }

}
