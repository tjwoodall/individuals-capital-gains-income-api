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

package shared.services

import play.api.http.Status.BAD_REQUEST
import play.api.libs.json.{Format, Json}
import shared.controllers.EndpointLogContext
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.utils.{Logging, UnitSpec}

class DownstreamResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext = EndpointLogContext("ctrl", "ep")

  val mapping: DownstreamResponseMappingSupport & Logging = new DownstreamResponseMappingSupport with Logging {}

  val correlationId = "someCorrelationId"

  val errorCodeMap: PartialFunction[String, MtdError] = {
    case "ERR1"                   => Error1
    case "ERR2"                   => Error2
    case "DS"                     => InternalError
    case "UNMATCHED_STUB_ERROR"   => RuleIncorrectGovTestScenarioError
    case "INVALID_CORRELATION_ID" => InternalError
    case "INVALID_CORRELATIONID"  => InternalError
  }

  case class TestClass(field: Option[String])

  object TestClass {
    implicit val format: Format[TestClass] = Json.format[TestClass]
  }

  object Error1 extends MtdError("msg", "code1", BAD_REQUEST)

  object Error2 extends MtdError("msg", "code2", BAD_REQUEST)

  object ErrorBvrMain extends MtdError("msg", "bvrMain", BAD_REQUEST)

  object ErrorBvr extends MtdError("msg", "bvr", BAD_REQUEST)

  "mapping Downstream errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERR1")))) shouldBe
            ErrorWrapper(correlationId, Error1)
        }
      }

      "the error code is not in the map provided" must {
        "default to DownstreamError and wrap" in {
          mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }
    }

    "downstream returns UNMATCHED_STUB_ERROR" must {
      "return INVALID_CORRELATION_ID error" in {
        mapping.mapDownstreamErrors(errorCodeMap)(
          ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("UNMATCHED_STUB_ERROR")))) shouldBe
          ErrorWrapper(correlationId, RuleIncorrectGovTestScenarioError)
      }
    }

    "downstream returns INVALID_CORRELATION_ID or INVALID_CORRELATIONID" must {
      "return InternalError for INVALID_CORRELATION_ID" in {
        mapping.mapDownstreamErrors(errorCodeMap)(
          ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("INVALID_CORRELATION_ID")))) shouldBe
          ErrorWrapper(correlationId, InternalError)
      }

      "return InternalError for INVALID_CORRELATIONID" in {
        mapping.mapDownstreamErrors(errorCodeMap)(
          ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("INVALID_CORRELATIONID")))) shouldBe
          ErrorWrapper(correlationId, InternalError)
      }
    }

    "multiple errors" when {
      "the error codes is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(Error1, Error2)))
        }
      }

      "the error code is not in the map provided" must {
        "default main error to DownstreamError ignore other errors" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("DS"))))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(ErrorBvrMain))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(ErrorBvrMain, Some(Seq(ErrorBvr))))) shouldBe
          ErrorWrapper(correlationId, ErrorBvrMain, Some(Seq(ErrorBvr)))
      }
    }
  }

}
