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

package v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.JsonUtils
import v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestBody

case class Def1_CreateAmendCgtPpdOverridesRequestBody(multiplePropertyDisposals: Option[Seq[MultiplePropertyDisposals]],
                                                      singlePropertyDisposals: Option[Seq[SinglePropertyDisposals]])
    extends CreateAmendCgtPpdOverridesRequestBody

object Def1_CreateAmendCgtPpdOverridesRequestBody extends JsonUtils {
  val empty: Def1_CreateAmendCgtPpdOverridesRequestBody = Def1_CreateAmendCgtPpdOverridesRequestBody(None, None)

  implicit val reads: Reads[Def1_CreateAmendCgtPpdOverridesRequestBody] = Json.format[Def1_CreateAmendCgtPpdOverridesRequestBody]

  implicit val writes: OWrites[Def1_CreateAmendCgtPpdOverridesRequestBody] = (
    (JsPath \ "multiplePropertyDisposals").writeNullable[Seq[MultiplePropertyDisposals]] and
      (JsPath \ "singlePropertyDisposals").writeNullable[Seq[SinglePropertyDisposals]]
  )(o => Tuple.fromProductTyped(o))

}
