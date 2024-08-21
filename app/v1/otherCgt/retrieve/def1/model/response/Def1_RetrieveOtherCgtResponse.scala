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

package v1.otherCgt.retrieve.def1.model.response

import api.models.domain.Timestamp
import play.api.libs.json.{Json, OWrites, Reads}
import v1.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

case class Def1_RetrieveOtherCgtResponse(submittedOn: Timestamp,
                                         disposals: Option[Seq[Disposal]],
                                         nonStandardGains: Option[NonStandardGains],
                                         losses: Option[Losses],
                                         adjustments: Option[BigDecimal])
    extends RetrieveOtherCgtResponse

object Def1_RetrieveOtherCgtResponse {
  implicit val reads: Reads[Def1_RetrieveOtherCgtResponse] = Json.reads[Def1_RetrieveOtherCgtResponse]

  implicit val writes: OWrites[Def1_RetrieveOtherCgtResponse] = Json.writes[Def1_RetrieveOtherCgtResponse]
}
