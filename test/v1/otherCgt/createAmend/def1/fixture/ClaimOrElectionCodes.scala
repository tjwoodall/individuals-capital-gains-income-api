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

package v1.otherCgt.createAmend.def1.fixture

import play.api.libs.json.Format
import shared.utils.enums.Enums

enum ClaimOrElectionCodes {
  case PRR extends ClaimOrElectionCodes
  case LET extends ClaimOrElectionCodes
  case GHO extends ClaimOrElectionCodes
  case ROR extends ClaimOrElectionCodes
  case PRO extends ClaimOrElectionCodes
  case ESH extends ClaimOrElectionCodes
  case NVC extends ClaimOrElectionCodes
  case SIR extends ClaimOrElectionCodes
  case OTH extends ClaimOrElectionCodes
  case BAD extends ClaimOrElectionCodes
  case INV extends ClaimOrElectionCodes
}

object ClaimOrElectionCodes {

  given Format[ClaimOrElectionCodes]                        = Enums.format(values)
  val parser: PartialFunction[String, ClaimOrElectionCodes] = Enums.parser[ClaimOrElectionCodes](values)
}
