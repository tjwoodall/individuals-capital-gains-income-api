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

package api.models.domain

import api.models.domain.AssetType._
import support.UnitSpec
import utils.enums.EnumJsonSpecSupport

class AssetTypeSpec extends UnitSpec with EnumJsonSpecSupport {

  testSerialization[AssetType](
    `other-property`   -> "other-property",
    `unlisted-shares`  -> "unlisted-shares",
    `listed-shares`  -> "listed-shares",
    `other-asset` -> "other-asset"
  )

  "AssetType" must {
    "convert to downstream string correctly" in {
      `other-property`.toDownstreamString shouldBe "otherProperty"
      `unlisted-shares`.toDownstreamString shouldBe "unlistedShares"
      `listed-shares`.toDownstreamString shouldBe "listedShares"
      `other-asset`.toDownstreamString shouldBe "otherAsset"
    }
  }

}