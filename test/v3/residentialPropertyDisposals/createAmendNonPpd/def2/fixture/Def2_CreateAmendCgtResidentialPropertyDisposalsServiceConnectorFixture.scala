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

package v3.residentialPropertyDisposals.createAmendNonPpd.def2.fixture

import v3.residentialPropertyDisposals.createAmendNonPpd.def2.model.request
import v3.residentialPropertyDisposals.createAmendNonPpd.def2.model.request.{Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody, Disposal}

object Def2_CreateAmendCgtResidentialPropertyDisposalsServiceConnectorFixture {

  val disposalsModels: Seq[Disposal] =
    Seq(
      Disposal(
        3,
        Some("ABC-2345"),
        "2021-01-29",
        "2021-04-25",
        2345.67,
        "2021-03-22",
        2341.45,
        Some(345.34),
        Some(234.89),
        Some(67.9),
        Some(123.89),
        Some(456.89),
        243.99,
        None,
        Some(Seq("PRR")),
        Some(234.89),
        Some(234.89)
      ),
      Disposal(
        3,
        Some("ABC-2345"),
        "2021-02-12",
        "2021-03-42",
        2345.67,
        "2021-03-22",
        2341.45,
        Some(345.34),
        Some(234.89),
        Some(67.9),
        Some(123.89),
        Some(456.89),
        243.99,
        None,
        Some(Seq("PRR")),
        Some(124.87),
        Some(124.87)
      )
    )

  val requestBody: Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody =
    request.Def2_CreateAmendCgtResidentialPropertyDisposalsRequestBody(
      disposalsModels
    )

}
