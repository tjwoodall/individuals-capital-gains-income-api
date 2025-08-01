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

import shared.config.{ConfidenceLevelConfig, MockSharedAppConfig}
import shared.definition.APIStatus.{ALPHA, BETA}
import shared.definition.{APIDefinition, APIVersion, Definition}
import shared.routing.{Version1, Version2}
import support.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class CgtApiDefinitionFactorySpec extends UnitSpec with MockSharedAppConfig {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  "definition" when {
    "called" should {
      "return a valid Definition case class" in {
        List(Version1, Version2).foreach { version =>
          MockedSharedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()
          MockedSharedAppConfig.apiStatus(version) returns "BETA"
          MockedSharedAppConfig.endpointsEnabled(version) returns true
          MockedSharedAppConfig.confidenceLevelConfig
            .returns(ConfidenceLevelConfig(confidenceLevel = confidenceLevel, definitionEnabled = true, authValidationEnabled = true))
            .anyNumberOfTimes()
        }

        val apiDefinitionFactory: CgtApiDefinitionFactory = new CgtApiDefinitionFactory(mockSharedAppConfig)

        apiDefinitionFactory.definition shouldBe
          Definition(
            api = APIDefinition(
              name = "Individuals Capital Gains Income (MTD)",
              description = "An API for providing individuals capital gains data",
              context = "individuals/disposals-income",
              categories = Seq("INCOME_TAX_MTD"),
              versions = Seq(
                APIVersion(
                  version = Version1,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  version = Version2,
                  status = BETA,
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
      (true, ConfidenceLevel.L250, ConfidenceLevel.L250),
      (true, ConfidenceLevel.L200, ConfidenceLevel.L200),
      (false, ConfidenceLevel.L200, ConfidenceLevel.L50)
    ).foreach { case (definitionEnabled, configCL, expectedDefinitionCL) =>
      s"confidence-level-check.definition.enabled is $definitionEnabled and confidence-level = $configCL" should {
        s"return confidence level $expectedDefinitionCL" in {
          MockedSharedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()
          MockedSharedAppConfig.apiStatus(Version1).returns("BETA").anyNumberOfTimes()
          MockedSharedAppConfig.apiStatus(Version2).returns("BETA").anyNumberOfTimes()
          MockedSharedAppConfig.endpointsEnabled(Version1).returns(true).anyNumberOfTimes()
          MockedSharedAppConfig.endpointsEnabled(Version2).returns(true).anyNumberOfTimes()
          MockedSharedAppConfig.confidenceLevelConfig
            .returns(ConfidenceLevelConfig(confidenceLevel = configCL, definitionEnabled = definitionEnabled, authValidationEnabled = true))
            .anyNumberOfTimes()

          val apiDefinitionFactory: CgtApiDefinitionFactory = new CgtApiDefinitionFactory(mockSharedAppConfig)

          apiDefinitionFactory.confidenceLevel shouldBe expectedDefinitionCL
        }
      }
    }
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      "return the correct status" in {
        MockedSharedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()
        MockedSharedAppConfig.apiStatus(Version1).returns("BETA").anyNumberOfTimes()
        MockedSharedAppConfig.apiStatus(Version2).returns("BETA").anyNumberOfTimes()
        MockedSharedAppConfig.endpointsEnabled(Version1).returns(true).anyNumberOfTimes()
        MockedSharedAppConfig.endpointsEnabled(Version2).returns(true).anyNumberOfTimes()

        val apiDefinitionFactory: CgtApiDefinitionFactory = new CgtApiDefinitionFactory(mockSharedAppConfig)

        apiDefinitionFactory.buildAPIStatus(Version1) shouldBe BETA
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      "default to alpha" in {
        MockedSharedAppConfig.apiGatewayContext.returns("individuals/disposals-income").anyNumberOfTimes()
        MockedSharedAppConfig.apiStatus(Version1).returns("ALPHO").anyNumberOfTimes()
        MockedSharedAppConfig.apiStatus(Version2).returns("ALPHO").anyNumberOfTimes()
        MockedSharedAppConfig.endpointsEnabled(Version1).returns(true).anyNumberOfTimes()
        MockedSharedAppConfig.endpointsEnabled(Version2).returns(true).anyNumberOfTimes()

        val apiDefinitionFactory: CgtApiDefinitionFactory = new CgtApiDefinitionFactory(mockSharedAppConfig)

        apiDefinitionFactory.buildAPIStatus(Version1) shouldBe ALPHA
      }
    }
  }

}
