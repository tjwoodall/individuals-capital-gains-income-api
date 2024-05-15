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

package api.controllers.validators.resolvers

import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

case class ResolveClaimOrElectionCodes(error: MtdError) extends ResolverSupport {

  val resolver: Resolver[String, String] = value =>
    if (isValid(value))
      Valid(value)
    else
      Invalid(List(error))

  def apply(value: String): Validated[Seq[MtdError], String] = resolver(value)

  def isValid(code: String): Boolean = code match {
    case "PRR" => true
    case "LET" => true
    case "GHO" => true
    case "ROR" => true
    case "PRO" => true
    case "ESH" => true
    case "NVC" => true
    case "SIR" => true
    case "OTH" => true
    case "BAD" => true
    case "INV" => true
    case _     => false
  }

}

object ResolveClaimOrElectionCodes {

  def apply(value: String, error: MtdError): Validated[Seq[MtdError], String] = {
    val resolver = ResolveClaimOrElectionCodes(error)
    resolver(value)
  }

}
