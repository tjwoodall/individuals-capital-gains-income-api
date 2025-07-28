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
import play.api.libs.json.*

case class MultiplePropertyDisposals(ppdSubmissionId: String, amountOfNetGain: Option[BigDecimal], amountOfNetLoss: Option[BigDecimal]) {

  def isAmountOfGainEmpty: Boolean = amountOfNetGain.isEmpty

  def isAmountOfLossEmpty: Boolean = amountOfNetLoss.isEmpty

  def isBothSupplied: Boolean = !isAmountOfGainEmpty && !isAmountOfLossEmpty

  def isNetAmountEmpty: Boolean = isAmountOfGainEmpty && isAmountOfLossEmpty
}

object MultiplePropertyDisposals {
  implicit val reads: Reads[MultiplePropertyDisposals] = Json.reads[MultiplePropertyDisposals]

  implicit val writes: OWrites[MultiplePropertyDisposals] = (
    (JsPath \ "ppdSubmissionId").write[String] and
      (JsPath \ "amountOfNetGain").writeNullable[BigDecimal] and
      (JsPath \ "amountOfLoss").writeNullable[BigDecimal]
  )(o => Tuple.fromProductTyped(o))

}
