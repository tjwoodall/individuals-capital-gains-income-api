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

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
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

    lazy val downstreamUri = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1947")) {
      HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/ppd/${nino.nino}")
    } else {
      IfsUri[Unit](s"income-tax/income/disposals/residential-property/ppd/${taxYear.asTysDownstream}/${nino.nino}")
    }

    lazy val downstreamUri1726: DownstreamUri[Unit] =
      IfsUri[Unit](s"income-tax/income/disposals/residential-property/ppd/${nino.nino}/${taxYear.asMtd}")

    if (taxYear.useTaxYearSpecificApi) delete(downstreamUri) else delete(downstreamUri1726)
  }

}
