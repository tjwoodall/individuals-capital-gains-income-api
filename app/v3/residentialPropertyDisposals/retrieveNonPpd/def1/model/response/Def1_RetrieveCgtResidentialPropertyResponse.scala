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

package v3.residentialPropertyDisposals.retrieveNonPpd.def1.model.response

import play.api.libs.json.{Json, OFormat}
import v3.residentialPropertyDisposals.retrieveNonPpd.model.response.RetrieveCgtResidentialPropertyResponse

case class Def1_RetrieveCgtResidentialPropertyResponse(customerAddedDisposals: Option[CustomerAddedDisposals])
    extends RetrieveCgtResidentialPropertyResponse

object Def1_RetrieveCgtResidentialPropertyResponse {
  implicit val format: OFormat[Def1_RetrieveCgtResidentialPropertyResponse] = Json.format[Def1_RetrieveCgtResidentialPropertyResponse]

}
