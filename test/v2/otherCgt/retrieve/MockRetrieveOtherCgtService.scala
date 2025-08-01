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

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import v2.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData
import v2.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockRetrieveOtherCgtService extends TestSuite with MockFactory {

  val mockRetrieveOtherCgtService: RetrieveOtherCgtService =
    mock[RetrieveOtherCgtService]

  object MockRetrieveOtherCgtService {

    def retrieve(requestData: RetrieveOtherCgtRequestData): CallHandler[Future[ServiceOutcome[RetrieveOtherCgtResponse]]] =
      (mockRetrieveOtherCgtService
        .retrieve(_: RetrieveOtherCgtRequestData)(_: RequestContext, _: ExecutionContext))
        .expects(requestData, *, *)

  }

}
