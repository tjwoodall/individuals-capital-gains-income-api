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

package definition

import shared.config.SharedAppConfig
import shared.definition.*
import shared.routing.{Version, Version1, Version2}
import uk.gov.hmrc.auth.core.ConfidenceLevel

import javax.inject.{Inject, Singleton}

@Singleton
class CgtApiDefinitionFactory @Inject() (sharedAppConfig: SharedAppConfig) extends ApiDefinitionFactory {

  override protected val appConfig: SharedAppConfig = sharedAppConfig

  lazy val confidenceLevel: ConfidenceLevel = {
    val clConfig = appConfig.confidenceLevelConfig

    if (clConfig.definitionEnabled) clConfig.confidenceLevel else ConfidenceLevel.L50
  }

  val definition: Definition =
    Definition(
      api = APIDefinition(
        name = "Individuals Capital Gains Income (MTD)",
        description = "An API for providing individuals capital gains data",
        context = appConfig.apiGatewayContext,
        categories = Seq("INCOME_TAX_MTD"),
        versions = Seq(
          APIVersion(
            version = Version1,
            status = buildAPIStatus(Version1),
            endpointsEnabled = appConfig.endpointsEnabled(Version1)
          ),
          APIVersion(
            version = Version2,
            status = buildAPIStatus(Version2),
            endpointsEnabled = appConfig.endpointsEnabled(Version2)
          )
        ),
        requiresTrust = None
      )
    )

  override def buildAPIStatus(version: Version): APIStatus = {
    APIStatus.parser
      .lift(appConfig.apiStatus(version))
      .getOrElse {
        logger.error(s"[ApiDefinition][buildApiStatus] no API Status found in config.  Reverting to Alpha")
        APIStatus.ALPHA
      }
  }

}
