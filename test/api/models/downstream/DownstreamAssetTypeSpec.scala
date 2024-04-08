/*
 * Copyright 2024 HM Revenue & Customs
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

package api.models.downstream

import api.models.domain.AssetType
import api.models.downstream.DownstreamAssetType._
import support.UnitSpec
import utils.enums.EnumJsonSpecSupport

class DownstreamAssetTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testDeserialization[DownstreamAssetType](
    "otherProperty"  -> `otherProperty`,
    "unlistedShares" -> `unlistedShares`,
    "listedShares"   -> `listedShares`,
    "otherAsset"     -> `otherAsset`
  )

  "AssetType" must {
    "convert to downstream string correctly" in {
      `otherProperty`.toMtd shouldBe AssetType.`other-property`
      `unlistedShares`.toMtd shouldBe AssetType.`unlisted-shares`
      `listedShares`.toMtd shouldBe AssetType.`listed-shares`
      `otherAsset`.toMtd shouldBe AssetType.`other-asset`
    }
  }

}
