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

package v1.otherCgt.createAmend

import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.IfsUri
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v1.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendOtherCgtConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def createAndAmend(request: CreateAmendOtherCgtRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request._
    import shared.connectors.httpparsers.StandardDownstreamHttpParser._

    val downstreamUri: DownstreamUri[Unit] =
      if (taxYear.useTaxYearSpecificApi) {
        IfsUri[Unit](s"income-tax/income/disposals/other-gains/${taxYear.asTysDownstream}/${nino.nino}")
      } else {
        IfsUri[Unit](s"income-tax/income/disposals/other-gains/${nino.nino}/${taxYear.asMtd}")
      }
    put(body, downstreamUri)
  }

}
