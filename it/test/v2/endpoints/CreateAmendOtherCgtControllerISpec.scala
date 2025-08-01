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

import com.github.tomakehurst.wiremock.client.WireMock.*
import common.errors.*
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.*
import shared.support.{IntegrationBaseSpec, WireMockMethods}

class CreateAmendOtherCgtControllerISpec extends IntegrationBaseSpec with WireMockMethods {

  val validRequestJson: JsValue = Json.parse(
    """
      |{
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

  val noMeaningfulDataJson: JsValue = Json.parse(
    """
      |{
      |  "aField": "aValue"
      |}
       """.stripMargin
  )

  val emptyFieldsJson: JsValue = Json.parse(
    """
      |{
      |   "disposals": [],
      |   "nonStandardGains": {},
      |   "losses": {}
      |}
     """.stripMargin
  )

  val emptyFieldsError: MtdError = RuleIncorrectOrEmptyBodyError.copy(
    paths = Some(Seq("/disposals", "/nonStandardGains", "/losses"))
  )

  val missingFieldsJson: JsValue = Json.parse(
    """
      |{
      |   "disposals": [{}]
      |}
     """.stripMargin
  )

  val missingFieldsError: MtdError = RuleIncorrectOrEmptyBodyError.copy(
    paths = Some(
      Seq(
        "/disposals/0/acquisitionDate",
        "/disposals/0/allowableCosts",
        "/disposals/0/assetDescription",
        "/disposals/0/assetType",
        "/disposals/0/disposalDate",
        "/disposals/0/disposalProceeds"
      ))
  )

  val gainAndLossJson: JsValue = Json.parse(
    """
      |{
      |   "disposals": [
      |     {
      |       "assetType":"other-property",
      |       "assetDescription":"string",
      |       "acquisitionDate":"2021-05-07",
      |       "disposalDate":"2021-05-07",
      |       "disposalProceeds":59999999999.99,
      |       "allowableCosts":59999999999.99,
      |       "gain":59999999999.99,
      |       "loss":1234123.44,
      |       "claimOrElectionCodes":[
      |          "OTH"
      |       ],
      |       "gainAfterRelief":59999999999.99,
      |       "lossAfterRelief":59999999999.99,
      |       "rttTaxPaid":59999999999.99
      |     }
      |   ]
      |}
     """.stripMargin
  )

  val gainAndLossErrors: ErrorWrapper = ErrorWrapper(
    correlationId = "",
    BadRequestError,
    Some(
      Seq(
        RuleGainAfterReliefLossAfterReliefError.copy(
          paths = Some(
            Seq(
              "/disposals/0"
            ))
        ),
        RuleGainLossError.copy(
          paths = Some(
            Seq(
              "/disposals/0"
            ))
        )))
  )

  val decimalsTooBigJson: JsValue = Json.parse(
    """
      |{
      |   "disposals":[
      |      {
      |         "assetType":"other-property",
      |         "assetDescription":"string",
      |         "acquisitionDate":"2021-05-07",
      |         "disposalDate":"2021-05-07",
      |         "disposalProceeds": 999999999999.99,
      |         "allowableCosts": 999999999999.99,
      |         "gain": 999999999999.99,
      |         "claimOrElectionCodes":[
      |            "OTH"
      |         ],
      |         "gainAfterRelief": 999999999999.99,
      |         "rttTaxPaid": 999999999999.99
      |      }
      |   ],
      |   "nonStandardGains":{
      |      "carriedInterestGain": 999999999999.99,
      |      "carriedInterestRttTaxPaid": 999999999999.99,
      |      "attributedGains": 999999999999.99,
      |      "attributedGainsRttTaxPaid": 999999999999.99,
      |      "otherGains": 999999999999.99,
      |      "otherGainsRttTaxPaid": 999999999999.99
      |   },
      |   "losses":{
      |      "broughtForwardLossesUsedInCurrentYear": 999999999999.99,
      |      "setAgainstInYearGains": 999999999999.99,
      |      "setAgainstInYearGeneralIncome": 999999999999.99,
      |      "setAgainstEarlierYear": 999999999999.99
      |   },
      |   "adjustments": 999999999999.99
      |}
     """.stripMargin
  )

  val decimalsTooSmallJson: JsValue = Json.parse(
    """
      |{
      |   "disposals":[
      |      {
      |         "assetType":"other-property",
      |         "assetDescription":"string",
      |         "acquisitionDate":"2021-05-07",
      |         "disposalDate":"2021-05-07",
      |         "disposalProceeds": -0.01,
      |         "allowableCosts": -0.01,
      |         "gain": -0.01,
      |         "claimOrElectionCodes":[
      |            "OTH"
      |         ],
      |         "gainAfterRelief": -0.01,
      |         "rttTaxPaid": -0.01
      |      }
      |   ],
      |   "nonStandardGains":{
      |      "carriedInterestGain": -0.01,
      |      "carriedInterestRttTaxPaid": -0.01,
      |      "attributedGains": -0.01,
      |      "attributedGainsRttTaxPaid": -0.01,
      |      "otherGains": -0.01,
      |      "otherGainsRttTaxPaid": -0.01
      |   },
      |   "losses":{
      |      "broughtForwardLossesUsedInCurrentYear": -0.01,
      |      "setAgainstInYearGains": -0.01,
      |      "setAgainstInYearGeneralIncome": -0.01,
      |      "setAgainstEarlierYear": -0.01
      |   },
      |   "adjustments": -999999999999.99
      |}
     """.stripMargin
  )

  val positiveDecimalsOutOfRangeError: MtdError = ValueFormatError.copy(
    message = "The value must be between 0 and 99999999999.99",
    paths = Some(
      Seq(
        "/disposals/0/disposalProceeds",
        "/disposals/0/allowableCosts",
        "/disposals/0/gain",
        "/disposals/0/gainAfterRelief",
        "/disposals/0/rttTaxPaid",
        "/nonStandardGains/carriedInterestGain",
        "/nonStandardGains/carriedInterestRttTaxPaid",
        "/nonStandardGains/attributedGains",
        "/nonStandardGains/attributedGainsRttTaxPaid",
        "/nonStandardGains/otherGains",
        "/nonStandardGains/otherGainsRttTaxPaid",
        "/losses/broughtForwardLossesUsedInCurrentYear",
        "/losses/setAgainstInYearGains",
        "/losses/setAgainstInYearGeneralIncome",
        "/losses/setAgainstEarlierYear"
      ))
  )

  val decimalsOutOfRangeErrors: ErrorWrapper = ErrorWrapper(
    correlationId = "",
    BadRequestError,
    Some(
      Seq(
        positiveDecimalsOutOfRangeError,
        ValueFormatError.copy(
          message = "The value must be between -99999999999.99 and 99999999999.99",
          paths = Some(
            Seq(
              "/adjustments"
            ))
        )
      ))
  )

  val formatDisposalsJson: JsValue = Json.parse(
    """
      |{
      |   "disposals":[
      |      {
      |         "assetType":"notEnumValue",
      |         "assetDescription":"",
      |         "acquisitionDate":"",
      |         "disposalDate":"",
      |         "disposalProceeds":59999999999.99,
      |         "allowableCosts":59999999999.99,
      |         "gain":59999999999.99,
      |         "claimOrElectionCodes":[
      |            "bip",
      |            "bop"
      |         ],
      |         "gainAfterRelief":59999999999.99,
      |         "rttTaxPaid":59999999999.99
      |      }
      |   ]
      |}
     """.stripMargin
  )

  val formatDisposalsErrors: ErrorWrapper = ErrorWrapper(
    correlationId = "",
    BadRequestError,
    Some(
      Seq(
        AssetDescriptionFormatError.copy(
          paths = Some(
            Seq(
              "/disposals/0/assetDescription"
            ))
        ),
        AssetTypeFormatError.copy(
          paths = Some(
            Seq(
              "/disposals/0/assetType"
            ))
        ),
        ClaimOrElectionCodesFormatError.copy(
          paths = Some(
            Seq(
              "/disposals/0/claimOrElectionCodes/0",
              "/disposals/0/claimOrElectionCodes/1"
            ))
        ),
        DateFormatError.copy(
          paths = Some(
            Seq(
              "/disposals/0/disposalDate",
              "/disposals/0/acquisitionDate"
            ))
        )
      ))
  )

  val formatNonStandardGainsJson: JsValue = Json.parse(
    """
      |{
      |   "nonStandardGains":{
      |      "carriedInterestRttTaxPaid":19999999999.99,
      |      "attributedGainsRttTaxPaid":19999999999.99,
      |      "otherGainsRttTaxPaid":19999999999.99
      |   }
      |}
     """.stripMargin
  )

  val formatNonStandardGainsError: MtdError = RuleIncorrectOrEmptyBodyError.copy(
    paths = Some(Seq("/nonStandardGains"))
  )

  private trait Test {
    val nino: String    = "AA123456A"
    val taxYear: String = "2021-22"

    def uri: String = s"/other-gains/$nino/$taxYear"

    def setupStubs(): Unit = ()

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    def verifyNrs(payload: JsValue): Unit =
      verify(
        postRequestedFor(urlEqualTo(s"/mtd-api-nrs-proxy/$nino/itsa-cgt-disposal-other"))
          .withRequestBody(equalToJson(payload.toString())))

  }

  private trait NonTysTest extends Test {
    def downstreamUrl: String = s"/income-tax/income/disposals/other-gains/$nino/$taxYear"

  }

  private trait TysIfsTest extends Test {

    override val taxYear: String = "2023-24"

    override def request: WSRequest =
      super.request.addHttpHeaders("suspend-temporal-validations" -> "true")

    def downstreamUrl: String = s"/income-tax/income/disposals/other-gains/23-24/$nino"

  }

  "Calling the 'create and amend other CGT' endpoint" should {
    "return a 200 status code" when {
      "any valid request is made" in new NonTysTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, NO_CONTENT, JsObject.empty)
        }

        val response: WSResponse = await(request.put(validRequestJson))
        response.status shouldBe OK
        verifyNrs(validRequestJson)
      }

      "any valid request is made TYS" in new TysIfsTest {

        override def setupStubs(): Unit = {
          DownstreamStub.onSuccess(DownstreamStub.PUT, downstreamUrl, NO_CONTENT, JsObject.empty)
        }

        val response: WSResponse = await(request.put(validRequestJson))
        response.status shouldBe OK
        verifyNrs(validRequestJson)
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedError: MtdError,
                                expectedErrors: Option[ErrorWrapper],
                                scenario: Option[String]): Unit = {
          s"validation fails with ${expectedError.code} error${scenario.fold("")(scenario => s" for $scenario scenario")}" in new NonTysTest {

            override val nino: String    = requestNino
            override val taxYear: String = requestTaxYear

            val response: WSResponse = await(request.put(requestBody))
            response.status shouldBe expectedStatus
            response.json shouldBe expectedErrors.fold(Json.toJson(expectedError))(errorWrapper => Json.toJson(errorWrapper))
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          // Path errors
          ("AA1123A", "2019-20", validRequestJson, BAD_REQUEST, NinoFormatError, None, None),
          ("AA123456A", "20177", validRequestJson, BAD_REQUEST, TaxYearFormatError, None, None),
          ("AA123456A", "2015-17", validRequestJson, BAD_REQUEST, RuleTaxYearRangeInvalidError, None, None),
          ("AA123456A", "2018-19", validRequestJson, BAD_REQUEST, RuleTaxYearNotSupportedError, None, None),

          // Body errors
          ("AA123456A", "2021-22", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError, None, Some("emptyBody")),
          ("AA123456A", "2021-22", noMeaningfulDataJson, BAD_REQUEST, RuleIncorrectOrEmptyBodyError, None, Some("nonsenseBody")),
          ("AA123456A", "2021-22", emptyFieldsJson, BAD_REQUEST, emptyFieldsError, None, Some("emptyFields")),
          ("AA123456A", "2021-22", missingFieldsJson, BAD_REQUEST, missingFieldsError, None, Some("missingFields")),
          ("AA123456A", "2021-22", gainAndLossJson, BAD_REQUEST, BadRequestError, Some(gainAndLossErrors), Some("gainAndLossRule")),
          ("AA123456A", "2021-22", decimalsTooBigJson, BAD_REQUEST, BadRequestError, Some(decimalsOutOfRangeErrors), Some("decimalsTooBig")),
          ("AA123456A", "2021-22", decimalsTooSmallJson, BAD_REQUEST, BadRequestError, Some(decimalsOutOfRangeErrors), Some("decimalsTooSmall")),
          ("AA123456A", "2021-22", formatDisposalsJson, BAD_REQUEST, BadRequestError, Some(formatDisposalsErrors), Some("formatDisposals")),
          ("AA123456A", "2021-22", formatNonStandardGainsJson, BAD_REQUEST, formatNonStandardGainsError, None, Some("formatNonStandardGains"))
        )
        input.foreach(args => (validationErrorTest).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysIfsTest {

            override def setupStubs(): Unit = {
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUrl, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request.put(validRequestJson))
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

        val errorInput = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "INVALID_DISPOSAL_DATE", BAD_REQUEST, RuleDisposalDateNotFutureError),
          (UNPROCESSABLE_ENTITY, "INVALID_ACQUISITION_DATE", BAD_REQUEST, RuleAcquisitionDateError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        val tysErrorInput = Seq(
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )
        (errorInput ++ tysErrorInput).foreach(args => (serviceErrorTest).tupled(args))
      }
    }
  }

}
