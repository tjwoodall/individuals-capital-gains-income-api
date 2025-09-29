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

package v1.residentialPropertyDisposals.createAmendNonPpd

import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v1.residentialPropertyDisposals.createAmendNonPpd.model.request.CreateAmendCgtResidentialPropertyDisposalsRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCgtResidentialPropertyDisposalsConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig)
    extends BaseDownstreamConnector {

  def createAndAmend(request: CreateAmendCgtResidentialPropertyDisposalsRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request.*
    import shared.connectors.httpparsers.StandardDownstreamHttpParser.*

    lazy val downstreamUri1952: DownstreamUri[Unit] =
      if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1952")) {
        HipUri(s"itsa/income-tax/v1/${taxYear.asTysDownstream}/income/disposals/residential-property/$nino")
      } else {
        IfsUri(s"income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/${nino.nino}")
      }

    lazy val downstreamUri1738: DownstreamUri[Unit] =
      IfsUri(s"income-tax/income/disposals/residential-property/${nino.nino}/${taxYear.asMtd}")

    val downstreamUri: DownstreamUri[Unit] =
      if (taxYear.useTaxYearSpecificApi) downstreamUri1952 else downstreamUri1738

    put(body, downstreamUri)

  }

}
