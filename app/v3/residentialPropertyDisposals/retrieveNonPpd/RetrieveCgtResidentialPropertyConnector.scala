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

package v3.residentialPropertyDisposals.retrieveNonPpd

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.*
import shared.connectors.*
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v3.residentialPropertyDisposals.retrieveNonPpd.model.request.RetrieveCgtResidentialPropertyRequestData
import v3.residentialPropertyDisposals.retrieveNonPpd.model.response.RetrieveCgtResidentialPropertyResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCgtResidentialPropertyConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  import shared.connectors.httpparsers.StandardDownstreamHttpParser.*

  def retrieve(request: RetrieveCgtResidentialPropertyRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveCgtResidentialPropertyResponse]] = {

    import request.*
    import schema.*

    val downstreamUri: DownstreamUri[DownstreamResp] = taxYear match {
      case ty if ty.useTaxYearSpecificApi =>
        if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1881")) {
          HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/${nino.value}")
        } else {
          IfsUri(s"income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/${nino.value}")
        }
      case _ =>
        DesUri(s"income-tax/income/disposals/residential-property/${nino.value}/${taxYear.asMtd}")
    }

    val maybeIntent: Option[String] = if (ConfigFeatureSwitches().isEnabled("passIntentHeader")) Some("NON_PPD") else None

    get(downstreamUri, maybeIntent = maybeIntent)
  }

}
