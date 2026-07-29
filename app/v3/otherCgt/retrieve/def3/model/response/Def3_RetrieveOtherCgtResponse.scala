/*
 * Copyright 2026 HM Revenue & Customs
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

package v3.otherCgt.retrieve.def3.model.response

import api.models.domain.Timestamp
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.*
import v3.otherCgt.retrieve.model.response.RetrieveOtherCgtResponse

case class Def3_RetrieveOtherCgtResponse(submittedOn: Timestamp,
                                         cryptoassets: Option[Seq[Cryptoassets]],
                                         otherGains: Option[Seq[OtherGains]],
                                         unlistedShares: Option[Seq[UnlistedShares]],
                                         gainExcludedIndexedSecurities: Option[GainExcludedIndexedSecurities],
                                         qualifyingAssetHoldingCompany: Option[QualifyingAssetHoldingCompany],
                                         losses: Option[Losses],
                                         adjustments: Option[Adjustments],
                                         lifetimeAllowance: Option[LifetimeAllowance])
    extends RetrieveOtherCgtResponse

object Def3_RetrieveOtherCgtResponse {

  implicit val reads: Reads[Def3_RetrieveOtherCgtResponse] = (
    (JsPath \ "submittedOn").read[Timestamp] and
      (JsPath \ "cryptoassets").readNullable[Seq[Cryptoassets]] and
      (JsPath \ "otherGains").readNullable[Seq[OtherGains]] and
      (JsPath \ "unlistedShares").readNullable[Seq[UnlistedShares]] and
      (JsPath \ "gainExcludedIndexedSecurities").readNullable[GainExcludedIndexedSecurities] and
      (JsPath \ "qualifyingAssetHoldingCompany").readNullable[QualifyingAssetHoldingCompany] and
      (JsPath \ "losses").readNullable[Losses] and
      (JsPath \ "adjustments").readNullable[Adjustments] and
      (JsPath \ "lifeTimeAllowance").readNullable[LifetimeAllowance]
  )(Def3_RetrieveOtherCgtResponse.apply)

  implicit val writes: OWrites[Def3_RetrieveOtherCgtResponse] = Json.writes[Def3_RetrieveOtherCgtResponse]
}
