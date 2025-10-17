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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.*
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.response.RetrieveCgtPpdOverridesResponse

case class Def2_RetrieveCgtPpdOverridesResponse(ppdYearToDate: Option[BigDecimal],
                                                multiplePropertyDisposals: Option[Seq[MultiplePropertyDisposals]],
                                                singlePropertyDisposals: Option[Seq[SinglePropertyDisposals]])
    extends RetrieveCgtPpdOverridesResponse {

  def isEmpty: Boolean = ppdYearToDate.isEmpty && multiplePropertyDisposals.isEmpty && singlePropertyDisposals.isEmpty

}

object Def2_RetrieveCgtPpdOverridesResponse {

  implicit val reads: Reads[Def2_RetrieveCgtPpdOverridesResponse] = (
    (JsPath \ "ppdService" \ "ppdYearToDate").readNullable[BigDecimal] and
      (JsPath \ "ppdService" \ "multiplePropertyDisposals").readNullable[Seq[MultiplePropertyDisposals]] and
      (JsPath \ "ppdService" \ "singlePropertyDisposals").readNullable[Seq[SinglePropertyDisposals]]
  )(Def2_RetrieveCgtPpdOverridesResponse.apply)

  implicit val writes: OWrites[Def2_RetrieveCgtPpdOverridesResponse] = Json.writes[Def2_RetrieveCgtPpdOverridesResponse]

}
