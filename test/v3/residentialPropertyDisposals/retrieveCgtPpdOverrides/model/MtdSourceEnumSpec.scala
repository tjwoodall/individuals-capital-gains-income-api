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

package v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model

import shared.utils.UnitSpec
import shared.utils.enums.EnumJsonSpecSupport
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum.{`hmrc-held`, latest, user}

class MtdSourceEnumSpec extends UnitSpec with EnumJsonSpecSupport {

  testSerialization[MtdSourceEnum](
    (`hmrc-held`, "hmrc-held"),
    (user, "user"),
    (latest, "latest")
  )

  "MtdSourceEnum" when {
    ".toDownstreamViewString" must {
      "return the expected downstream view string value" in {
        `hmrc-held`.toDownstreamViewString shouldBe "HMRC-HELD"
        user.toDownstreamViewString shouldBe "CUSTOMER"
        latest.toDownstreamViewString shouldBe "LATEST"
      }
    }
  }

}
