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

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v2.otherCgt.retrieve.model.request.RetrieveOtherCgtRequestData
import v2.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveOtherCgtConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  import shared.connectors.httpparsers.StandardDownstreamHttpParser.*

  def retrieveOtherCgt(request: RetrieveOtherCgtRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveOtherCgtResponse]] = {

    import request.*
    import schema.*

    lazy val downstreamUri1951: DownstreamUri[DownstreamResp] = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1951")) {
      HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/other-gains/${nino.value}")
    } else {
      IfsUri(s"income-tax/income/disposals/other-gains/${taxYear.asTysDownstream}/${nino.value}")
    }

    lazy val downstreamUri1737: DownstreamUri[DownstreamResp] = IfsUri(s"income-tax/income/disposals/other-gains/${nino.value}/${taxYear.asMtd}")

    if (taxYear.useTaxYearSpecificApi) {
      get(uri = downstreamUri1951)
    } else {
      get(uri = downstreamUri1737)
    }

  }

}
