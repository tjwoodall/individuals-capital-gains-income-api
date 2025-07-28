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

package config

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite

trait MockAppConfig extends TestSuite with MockFactory {

  implicit val mockAppConfig: CgtAppConfig = mock[CgtAppConfig]

  object MockedAppConfig {
    // api1661 Config
    def api1661BaseUrl: CallHandler[String] = (() => mockAppConfig.api1661BaseUrl).expects()

    def api1661Token: CallHandler[String] = (() => mockAppConfig.api1661Token).expects()

    def api1661Environment: CallHandler[String] = (() => mockAppConfig.api1661Env).expects()

    def api1661EnvironmentHeaders: CallHandler[Option[Seq[String]]] = (() => mockAppConfig.api1661EnvironmentHeaders).expects()

    // MTD IF Lookup Config
    def minimumPermittedTaxYear: CallHandler[Int] = (() => mockAppConfig.minimumPermittedTaxYear).expects()

  }

}
