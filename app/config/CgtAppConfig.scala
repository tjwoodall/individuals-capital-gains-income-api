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

package config

import play.api.Configuration
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}

@Singleton
class CgtAppConfig @Inject() (config: ServicesConfig, protected[config] val configuration: Configuration) {
  // API1661 Config
  def api1661BaseUrl: String                         = config.baseUrl("api1661")
  def api1661Env: String                             = config.getString("microservice.services.api1661.env")
  def api1661Token: String                           = config.getString("microservice.services.api1661.token")
  def api1661EnvironmentHeaders: Option[Seq[String]] = configuration.getOptional[Seq[String]]("microservice.services.api1661.environmentHeaders")

  def minimumPermittedTaxYear: Int = config.getInt("minimumPermittedTaxYear")
  // NRS Config
  def mtdNrsProxyBaseUrl: String = config.baseUrl("mtd-api-nrs-proxy")
}
