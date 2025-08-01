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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.*
import shared.support.IntegrationBaseSpec

class RetrieveOtherCgtControllerISpec extends IntegrationBaseSpec {

  "Calling the 'retrieve other CGT' endpoint" should {
    "return a 200 status code" when {
      "any valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamResponse)
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.json shouldBe mtdResponse
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "any valid request with a Tax Year Specific (TYS) tax year is made" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamResponse)
        }

        val response: WSResponse = await(request.get())
        response.status shouldBe OK
        response.json shouldBe mtdResponse
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String, requestTaxYear: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {

            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request.get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          ("AA1123A", "2019-20", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "20177", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "2015-17", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "2018-19", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )
        input.foreach(args => (validationErrorTest).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request.get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        def errorBody(code: String): String =
          s"""
             |{
             |   "code": "$code",
             |   "reason": "downstream message"
             |}
            """.stripMargin

        val errors = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NO_DATA_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        val extraTysErrors = Seq(
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest).tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String = "AA123456A"
    def taxYear: String
    def downstreamUri: String

    val downstreamResponse: JsValue = Json.parse(
      """
        |{
        |   "submittedOn":"2021-05-07T16:18:44.403Z",
        |   "disposals":[
        |      {
        |         "assetType":"otherProperty",
        |         "assetDescription":"string",
        |         "acquisitionDate":"2021-05-07",
        |         "disposalDate":"2021-05-07",
        |         "disposalProceeds":59999999999.99,
        |         "allowableCosts":59999999999.99,
        |         "gain":59999999999.99,
        |         "claimOrElectionCodes":[
        |            "OTH"
        |         ],
        |         "gainAfterRelief":59999999999.99,
        |         "rttTaxPaid":59999999999.99
        |      }
        |   ],
        |   "nonStandardGains":{
        |      "carriedInterestGain":19999999999.99,
        |      "carriedInterestRttTaxPaid":19999999999.99,
        |      "attributedGains":19999999999.99,
        |      "attributedGainsRttTaxPaid":19999999999.99,
        |      "otherGains":19999999999.99,
        |      "otherGainsRttTaxPaid":19999999999.99
        |   },
        |   "losses":{
        |      "broughtForwardLossesUsedInCurrentYear":29999999999.99,
        |      "setAgainstInYearGains":29999999999.99,
        |      "setAgainstInYearGeneralIncome":29999999999.99,
        |      "setAgainstEarlierYear":29999999999.99
        |   },
        |   "adjustments":-39999999999.99
        |}
     """.stripMargin
    )

    val mtdResponse: JsValue = Json.parse(
      """
        |{
        |   "submittedOn":"2021-05-07T16:18:44.403Z",
        |   "disposals":[
        |      {
        |         "assetType":"other-property",
        |         "assetDescription":"string",
        |         "acquisitionDate":"2021-05-07",
        |         "disposalDate":"2021-05-07",
        |         "disposalProceeds":59999999999.99,
        |         "allowableCosts":59999999999.99,
        |         "gain":59999999999.99,
        |         "claimOrElectionCodes":[
        |            "OTH"
        |         ],
        |         "gainAfterRelief":59999999999.99,
        |         "rttTaxPaid":59999999999.99
        |      }
        |   ],
        |   "nonStandardGains":{
        |      "carriedInterestGain":19999999999.99,
        |      "carriedInterestRttTaxPaid":19999999999.99,
        |      "attributedGains":19999999999.99,
        |      "attributedGainsRttTaxPaid":19999999999.99,
        |      "otherGains":19999999999.99,
        |      "otherGainsRttTaxPaid":19999999999.99
        |   },
        |   "losses":{
        |      "broughtForwardLossesUsedInCurrentYear":29999999999.99,
        |      "setAgainstInYearGains":29999999999.99,
        |      "setAgainstInYearGeneralIncome":29999999999.99,
        |      "setAgainstEarlierYear":29999999999.99
        |   },
        |   "adjustments":-39999999999.99
        |}
     """.stripMargin
    )

    def uri: String = s"/other-gains/$nino/$taxYear"

    def setupStubs(): StubMapping

    def request: WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

  }

  private trait NonTysTest extends Test {
    def taxYear: String       = "2019-20"
    def downstreamUri: String = s"/income-tax/income/disposals/other-gains/$nino/2019-20"
  }

  private trait TysIfsTest extends Test {
    def taxYear: String       = "2023-24"
    def downstreamUri: String = s"/income-tax/income/disposals/other-gains/23-24/$nino"
  }

}
