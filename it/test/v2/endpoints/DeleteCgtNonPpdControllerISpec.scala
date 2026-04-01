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

package v2.endpoints

import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuthStub, DownstreamStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec

class DeleteCgtNonPpdControllerISpec extends IntegrationBaseSpec {

  "Calling the 'delete cgt non-ppd disposals' endpoint" should {
    "return a 204 status code" when {
      "any valid request is made for a pre-TYS tax year" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.DELETE, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().delete())
        response.status shouldBe NO_CONTENT
        response.body shouldBe ""
        response.header("Content-Type") shouldBe None
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "any valid request is made for a Tax Year Specific (TYS) tax year" in new TysHipTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.DELETE, downstreamUri, NO_CONTENT)
        }

        val response: WSResponse = await(request().delete())
        response.status shouldBe NO_CONTENT
        response.body shouldBe ""
        response.header("Content-Type") shouldBe None
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {

      "validation error" when {

        val input = Seq(
          ("AA1123A", "2019-20", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "20199", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2018-19", BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", "2025-26", BAD_REQUEST, RuleTaxYearForVersionNotSupportedError),
          ("AA123456A", "2019-21", BAD_REQUEST, RuleTaxYearRangeInvalidError)
        )

        input.foreach { case (requestNino, requestTaxYear, expectedStatus, expectedBody) =>
          s"Non-Tys validation fails with ${expectedBody.code} error" in new NonTysTest {

            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            override def setupStubs(): Unit = {}

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        input.foreach { case (requestNino, requestTaxYear, expectedStatus, expectedBody) =>
          s"Tys HIP validation fails with ${expectedBody.code} error" in new TysHipTest {

            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            override def setupStubs(): Unit = {}

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }
      }

      "downstream service error" when {
        def nonTysServiceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.DELETE, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        def tysHipServiceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"Tys downstream returns an $downstreamCode error and status $downstreamStatus" in new TysHipTest {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.DELETE, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val nonTysErrors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )
        nonTysErrors.foreach(args => nonTysServiceErrorTest.tupled(args))

        val tysHipErrors = List(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )
        tysHipErrors.foreach(args => tysHipServiceErrorTest.tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String   = "AA123456A"
    def mtdUri: String = s"/residential-property/$nino/$taxYear"

    def taxYear: String
    def downstreamUri: String

    def setupStubs(): Unit

    def request(): WSRequest = {
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()

      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def errorBody(`type`: String): String =
      s"""
         |{
         |  "origin": "HoD",
         |  "response": {
         |    "failures": [
         |      {
         |        "type": "${`type`}",
         |        "reason": "downstream message"
         |      }
         |    ]
         |  }
         |}
            """.stripMargin

  }

  private trait NonTysTest extends Test {
    def taxYear: String = "2019-20"

    def downstreamUri: String = s"/income-tax/income/disposals/residential-property/$nino/$taxYear"
  }

  private trait TysHipTest extends Test {
    def taxYear: String = "2023-24"

    def downstreamUri: String = s"/itsa/income-tax/v1/23-24/income/disposals/residential-property/$nino"
  }

}
