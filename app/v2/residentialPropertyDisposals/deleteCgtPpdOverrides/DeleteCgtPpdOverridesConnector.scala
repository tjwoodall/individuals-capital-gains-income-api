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

package v2.residentialPropertyDisposals.deleteCgtPpdOverrides

import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.{DesUri, IfsUri}
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v2.residentialPropertyDisposals.deleteCgtPpdOverrides.model.request.DeleteCgtPpdOverridesRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteCgtPpdOverridesConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  import shared.connectors.httpparsers.StandardDownstreamHttpParser.*

  def deleteCgtPpdOverrides(request: DeleteCgtPpdOverridesRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request.*

    val downstreamUri = if (taxYear.useTaxYearSpecificApi) {
      IfsUri[Unit](s"income-tax/income/disposals/residential-property/ppd/${taxYear.asTysDownstream}/${nino.nino}")
    } else {
      DesUri[Unit](s"income-tax/income/disposals/residential-property/ppd/${nino.nino}/${taxYear.asMtd}")
    }

    delete(downstreamUri)
  }

}
