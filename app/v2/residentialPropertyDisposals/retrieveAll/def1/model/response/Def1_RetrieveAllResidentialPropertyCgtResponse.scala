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

package v2.residentialPropertyDisposals.retrieveAll.def1.model.response

import play.api.libs.json.{Json, OWrites, Reads}
import v2.residentialPropertyDisposals.retrieveAll.model.response.RetrieveAllResidentialPropertyCgtResponse

case class Def1_RetrieveAllResidentialPropertyCgtResponse(customerAddedDisposals: Option[CustomerAddedDisposals], ppdService: Option[PpdService])
    extends RetrieveAllResidentialPropertyCgtResponse

object Def1_RetrieveAllResidentialPropertyCgtResponse {
  implicit val reads: Reads[Def1_RetrieveAllResidentialPropertyCgtResponse] = Json.reads[Def1_RetrieveAllResidentialPropertyCgtResponse]

  implicit val writes: OWrites[Def1_RetrieveAllResidentialPropertyCgtResponse] = Json.writes[Def1_RetrieveAllResidentialPropertyCgtResponse]
}
