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
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.DownstreamSourceEnum.{CUSTOMER, `HMRC HELD`}
import v3.residentialPropertyDisposals.retrieveCgtPpdOverrides.model.MtdSourceEnum.{`hmrc-held`, user}

class DownstreamSourceEnumSpec extends UnitSpec with EnumJsonSpecSupport {

  testDeserialization[DownstreamSourceEnum](
    ("HMRC HELD", `HMRC HELD`),
    ("CUSTOMER", CUSTOMER)
  )

  "DownstreamSourceEnum" when {
    ".toMtdEnum" must {
      "return the expected 'MtdSourceEnum' object" in {
        `HMRC HELD`.toMtdEnum shouldBe `hmrc-held`
        CUSTOMER.toMtdEnum shouldBe user
      }
    }
  }

}
