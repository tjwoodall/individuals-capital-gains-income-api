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

package v1

import config.CgtAppConfig
import play.api.libs.json.JsValue
import play.api.libs.ws.writeableOf_JsValue
import uk.gov.hmrc.http.HttpReads.Implicits.*
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps, UpstreamErrorResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class NrsProxyConnector @Inject() (http: HttpClientV2, appConfig: CgtAppConfig)(implicit ec: ExecutionContext) {

  def submit(nino: String, notableEvent: String, body: JsValue)(implicit hc: HeaderCarrier): Future[Either[UpstreamErrorResponse, Unit]] = {

    val url = s"${appConfig.mtdNrsProxyBaseUrl}/mtd-api-nrs-proxy/$nino/$notableEvent"

    http.post(url"$url").withBody(body).execute[Either[UpstreamErrorResponse, Unit]]
  }

}
