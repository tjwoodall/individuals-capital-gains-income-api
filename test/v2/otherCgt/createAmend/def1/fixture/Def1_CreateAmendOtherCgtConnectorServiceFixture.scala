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

package v2.otherCgt.createAmend.def1.fixture

import v2.otherCgt.createAmend.def1.model.request.*

object Def1_CreateAmendOtherCgtConnectorServiceFixture {

  val disposal: Disposal = Disposal(
    AssetType.`other-property`.toString,
    "Property Sale",
    "2021-01-01",
    "2021-02-01",
    1000.12,
    100.13,
    Some(900.12),
    None,
    Some(List(ClaimOrElectionCodes.PRR.toString)),
    Some(10.12),
    None,
    Some(12.12)
  )

  val nonStandardGains: NonStandardGains = NonStandardGains(
    Some(101.99),
    Some(102.99),
    Some(103.99),
    Some(104.99),
    Some(105.99),
    Some(106.99)
  )

  val losses: Losses = Losses(
    Some(120.99),
    Some(130.99),
    Some(140.99),
    Some(150.99)
  )

  val mtdRequestBody: Def1_CreateAmendOtherCgtRequestBody =
    Def1_CreateAmendOtherCgtRequestBody(Some(Seq(disposal)), Some(nonStandardGains), Some(losses), Some(160.99))

}
