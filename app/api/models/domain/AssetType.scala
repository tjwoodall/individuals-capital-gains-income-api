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

package api.models.domain

import play.api.libs.json.Writes
import utils.enums.Enums

sealed trait AssetType{
  def toDownstreamString: String
}

object AssetType {

  case object `other-property` extends AssetType{
    override def toDownstreamString: String = "otherProperty"
  }

  case object `unlisted-shares` extends AssetType {
    override def toDownstreamString: String = "unlistedShares"
  }

  case object `listed-shares` extends AssetType {
    override def toDownstreamString: String = "listedShares"
  }

  case object `other-asset` extends AssetType {
    override def toDownstreamString: String = "otherAsset"
  }

  implicit val writes: Writes[AssetType]         = Enums.writes[AssetType]
  val parser: PartialFunction[String, AssetType] = Enums.parser[AssetType]
}
