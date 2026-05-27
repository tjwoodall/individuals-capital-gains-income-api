/*
 * Copyright 2026 HM Revenue & Customs
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

package v3.residentialPropertyDisposals.createAmendCgtPpdOverrides

import api.config.AppConfig
import api.connectors.DownstreamUri.{HipUri, IfsUri}
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import com.google.inject.Singleton
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v3.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCgtPpdOverridesConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def createAmend(request: CreateAmendCgtPpdOverridesRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import api.connectors.httpparsers.StandardDownstreamHttpParser.*
    import request.*

    lazy val downstream1946 =
      HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/ppd/${nino.value}")

    lazy val downstream1724 = IfsUri[Unit](s"income-tax/income/disposals/residential-property/ppd/$nino/${taxYear.asMtd}")

    if (taxYear.useTaxYearSpecificApi) put(body, downstream1946) else put(body, downstream1724)
  }

}
