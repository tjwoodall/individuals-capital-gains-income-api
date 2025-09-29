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

package v1.residentialPropertyDisposals.deleteNonPpd

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v1.residentialPropertyDisposals.deleteNonPpd.model.request.DeleteCgtNonPpdRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteCgtNonPpdConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def deleteCgtNonPpd(request: DeleteCgtNonPpdRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request.*

    lazy val downstreamUri1875: DownstreamUri[Unit] = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1875")) {
      HipUri[Unit](
        s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/$nino"
      )
    } else {
      IfsUri[Unit](
        s"income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/$nino"
      )
    }

    lazy val downstreamUri1740: DownstreamUri[Unit] = IfsUri[Unit](
      s"income-tax/income/disposals/residential-property/$nino/${taxYear.asMtd}"
    )

    if (taxYear.useTaxYearSpecificApi) {
      delete(uri = downstreamUri1875)
    } else {
      delete(uri = downstreamUri1740)
    }

  }

}
