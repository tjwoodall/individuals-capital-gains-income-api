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

package v1.residentialPropertyDisposals.retreiveAll.def1.model.request

import api.models.domain.{MtdSourceEnum, Nino, TaxYear}
import v1.residentialPropertyDisposals.retreiveAll.RetrieveAllResidentaialPropertySchema
import v1.residentialPropertyDisposals.retreiveAll.model.request.RetrieveAllResidentialPropertyCgtRequestData

case class Def1_RetrieveAllResidentialPropertyRequestData(nino: Nino, taxYear: TaxYear, source: MtdSourceEnum) extends RetrieveAllResidentialPropertyCgtRequestData {
  val schema: RetrieveAllResidentaialPropertySchema = RetrieveAllResidentaialPropertySchema.Def1
}
