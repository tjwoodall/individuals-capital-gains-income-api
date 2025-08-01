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

package shared.controllers.validators.resolvers

import cats.data.Validated
import shared.models.domain.CalculationId
import shared.models.errors.{CalculationIdFormatError, MtdError}

object ResolveCalculationId extends ResolverSupport {

  private val calculationIdRegex = """^[0-9]{8}|[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$""".r

  val resolver: Resolver[String, CalculationId] =
    ResolveStringPattern(calculationIdRegex, CalculationIdFormatError).resolver.map(CalculationId.apply)

  def apply(value: String): Validated[Seq[MtdError], CalculationId] = resolver(value)

}
