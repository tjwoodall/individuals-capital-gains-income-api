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

package v1.otherCgt.createAmend.def1.model.request

import api.models.domain.AssetType
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Disposal(assetType: String,
                    assetDescription: String,
                    acquisitionDate: String,
                    disposalDate: String,
                    disposalProceeds: BigDecimal,
                    allowableCosts: BigDecimal,
                    gain: Option[BigDecimal],
                    loss: Option[BigDecimal],
                    claimOrElectionCodes: Option[Seq[String]],
                    gainAfterRelief: Option[BigDecimal],
                    lossAfterRelief: Option[BigDecimal],
                    rttTaxPaid: Option[BigDecimal]) {
  def gainAndLossBothSupplied: Boolean                          = gain.isDefined && loss.isDefined
  def gainAfterReliefAndLossAfterReliefAreBothSupplied: Boolean = gainAfterRelief.isDefined && lossAfterRelief.isDefined
}

object Disposal {

  implicit val reads: Reads[Disposal] = Json.reads[Disposal]

  implicit val disposalWrites: OWrites[Disposal] = (
    (JsPath \ "assetType").write[String].contramap[String](assetType => AssetType.parser(assetType).toDownstreamString) and
      (JsPath \ "assetDescription").write[String] and
      (JsPath \ "acquisitionDate").write[String] and
      (JsPath \ "disposalDate").write[String] and
      (JsPath \ "disposalProceeds").write[BigDecimal] and
      (JsPath \ "allowableCosts").write[BigDecimal] and
      (JsPath \ "gain").writeNullable[BigDecimal] and
      (JsPath \ "loss").writeNullable[BigDecimal] and
      (JsPath \ "claimOrElectionCodes").writeNullable[Seq[String]] and
      (JsPath \ "gainAfterRelief").writeNullable[BigDecimal] and
      (JsPath \ "lossAfterRelief").writeNullable[BigDecimal] and
      (JsPath \ "rttTaxPaid").writeNullable[BigDecimal]
  )(unlift(Disposal.unapply))

}
