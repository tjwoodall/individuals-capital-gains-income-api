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

package v2.residentialPropertyDisposals.retrieveAll.def1.model.response

import play.api.libs.json.Format
import shared.utils.enums.Enums
import v2.residentialPropertyDisposals.retrieveAll.def1.model.MtdSourceEnum

enum DownstreamSourceEnum(val toMtdEnum: MtdSourceEnum) {
  case `HMRC HELD` extends DownstreamSourceEnum(MtdSourceEnum.`hmrc-held`)
  case CUSTOMER    extends DownstreamSourceEnum(MtdSourceEnum.user)
}

object DownstreamSourceEnum {
  given Format[DownstreamSourceEnum] = Enums.format(values)
}
