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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.RequestContext
import shared.services.ServiceOutcome
import uk.gov.hmrc.http.HeaderCarrier
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.request.RetrieveCgtPpdOverridesRequestData
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.response.RetrieveCgtPpdOverridesResponse

import scala.concurrent.{ExecutionContext, Future}

trait MockRetrieveCgtPpdOverridesService extends TestSuite with MockFactory {

  val mockRetrieveCgtPpdOverridesService: RetrieveCgtPpdOverridesService =
    mock[RetrieveCgtPpdOverridesService]

  object MockRetrieveCgtPpdOverridesService {

    def retrieve(requestData: RetrieveCgtPpdOverridesRequestData): CallHandler[Future[ServiceOutcome[RetrieveCgtPpdOverridesResponse]]] =
      (
        mockRetrieveCgtPpdOverridesService
          .retrieve(_: RetrieveCgtPpdOverridesRequestData)(
            _: RequestContext,
            _: ExecutionContext
          )
        )
        .expects(requestData, *, *)

  }

}
