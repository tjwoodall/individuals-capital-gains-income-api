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

package v3.otherCgt.createAmend.def2.model.request

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestBody

case class Def2_CreateAmendOtherCgtRequestBody(cryptoassets: Option[Seq[Cryptoassets]],
                                               otherGains: Option[Seq[OtherGains]],
                                               unlistedShares: Option[Seq[UnlistedShares]],
                                               gainExcludedIndexedSecurities: Option[GainExcludedIndexedSecurities],
                                               qualifyingAssetHoldingCompany: Option[QualifyingAssetHoldingCompany],
                                               nonStandardGains: Option[NonStandardGains],
                                               losses: Option[Losses],
                                               adjustments: Option[Adjustments],
                                               lifetimeAllowance: Option[LifetimeAllowance])
    extends CreateAmendOtherCgtRequestBody

object Def2_CreateAmendOtherCgtRequestBody {
  implicit val reads: Reads[Def2_CreateAmendOtherCgtRequestBody] = Json.reads[Def2_CreateAmendOtherCgtRequestBody]

  implicit val writes: OWrites[Def2_CreateAmendOtherCgtRequestBody] = (
    (JsPath \ "cryptoassets").writeNullable[Seq[Cryptoassets]] and
      (JsPath \ "otherGains").writeNullable[Seq[OtherGains]] and
      (JsPath \ "unlistedShares").writeNullable[Seq[UnlistedShares]] and
      (JsPath \ "gainExcludedIndexedSecurities").writeNullable[GainExcludedIndexedSecurities] and
      (JsPath \ "qualifyingAssetHoldingCompany").writeNullable[QualifyingAssetHoldingCompany] and
      (JsPath \ "nonStandardGains").writeNullable[NonStandardGains] and
      (JsPath \ "losses").writeNullable[Losses] and
      (JsPath \ "adjustments").writeNullable[Adjustments] and
      (JsPath \ "lifeTimeAllowance").writeNullable[LifetimeAllowance]
  )(o => Tuple.fromProductTyped(o))

}
