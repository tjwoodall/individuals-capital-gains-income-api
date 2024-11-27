/*
 * Copyright 2023 HM Revenue & Customs
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

package v1.residentialPropertyDisposals.retrieveAll

import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.residentialPropertyDisposals.retrieveAll.model.request.RetrieveAllResidentialPropertyCgtRequestData
import v1.residentialPropertyDisposals.retrieveAll.model.response.RetrieveAllResidentialPropertyCgtResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAllResidentialPropertyCgtConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieve(request: RetrieveAllResidentialPropertyCgtRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveAllResidentialPropertyCgtResponse]] = {

    import api.connectors.httpparsers.StandardDownstreamHttpParser._
    import request._
    import schema._

    val view        = source.toDesViewString
    val queryParams = Seq(("view", view))



    val downstreamUri: DownstreamUri[DownstreamResp] = taxYear match {
      case ty if ty.useTaxYearSpecificApi =>
        TaxYearSpecificIfsUri(s"income-tax/income/disposals/residential-property/${taxYear.asTysDownstream}/${nino.value}")
      case _ =>
        DesUri(s"income-tax/income/disposals/residential-property/${nino.value}/${taxYear.asMtd}")
    }
    get(downstreamUri, queryParams)
  }

}
