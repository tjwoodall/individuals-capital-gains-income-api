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

package v3.createAmendOtherCgt

import com.github.tomakehurst.wiremock.client.WireMock.*
import common.errors.*
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.WSBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.*
import shared.models.domain.TaxYear
import shared.models.errors.*
import shared.services.*
import shared.support.{IntegrationBaseSpec, WireMockMethods}
import shared.utils.DateUtils.getCurrentDate
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.*

class Def2_CreateAmendOtherCgtControllerHipISpec extends IntegrationBaseSpec with WireMockMethods {

  private def futureDisposalDatesRequestBodyMtdJson(futureDisposalDate: String): JsValue = Json.parse(
    s"""
      |{
      |  "cryptoassets": [
      |    {
      |      "numberOfDisposals": 1,
      |      "assetDescription": "description string",
      |      "tokenName": "Name of token",
      |      "acquisitionDate": "2025-08-04",
      |      "disposalDate": "$futureDisposalDate",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "amountOfNetGain": 99999999999.99
      |    }
      |  ],
      |  "otherGains": [
      |    {
      |      "assetType": "other-property",
      |      "numberOfDisposals": 1,
      |      "assetDescription": "example of this asset",
      |      "acquisitionDate": "2025-04-07",
      |      "disposalDate": "$futureDisposalDate",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "amountOfNetGain": 99999999999.99
      |    }
      |  ],
      |  "unlistedShares": [
      |    {
      |      "numberOfDisposals": 1,
      |      "assetDescription": "My asset",
      |      "companyName": "Bob the Builder",
      |      "acquisitionDate": "2025-04-10",
      |      "disposalDate": "$futureDisposalDate",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99
      |    }
      |  ]
      |}
    """.stripMargin
  )

  private val allInvalidFieldsRequestBodyJson: JsValue = Json.parse(
    s"""
      |{
      |  "cryptoassets": [
      |    {
      |      "numberOfDisposals": 0,
      |      "assetDescription": "??",
      |      "tokenName": "??",
      |      "acquisitionDate": "2025",
      |      "disposalDate": "2025",
      |      "disposalProceeds": -99999999999.99,
      |      "allowableCosts": 99999999999.999,
      |      "gainsWithBadr": -99999999999.99,
      |      "gainsBeforeLosses": 99999999999.999,
      |      "losses": -99999999999.99,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "NVC", "OTH", "BAD", "CODE"],
      |      "amountOfNetGain": 99999999999.999,
      |      "amountOfNetLoss": -99999999999.99,
      |      "rttTaxPaid": 99999999999.999
      |    },
      |    {
      |      "numberOfDisposals": 1,
      |      "assetDescription": "description string",
      |      "tokenName": "Name of token",
      |      "acquisitionDate": "2025-04-06",
      |      "disposalDate": "2025-04-05",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "amountOfNetGain": 99999999999.99
      |    }
      |  ],
      |  "otherGains": [
      |    {
      |      "assetType": "non-uk-residential-property",
      |      "numberOfDisposals": 999999999999,
      |      "assetDescription": "??",
      |      "companyName": "${"a" * 161}",
      |      "companyRegistrationNumber": "??",
      |      "acquisitionDate": "2025",
      |      "disposalDate": "2025",
      |      "disposalProceeds": -99999999999.99,
      |      "allowableCosts": 99999999999.999,
      |      "gainsWithBadr": -99999999999.99,
      |      "gainsWithInv": 99999999999.999,
      |      "gainsBeforeLosses": -99999999999.99,
      |      "losses": 99999999999.999,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "ESH", "NVC", "OTH", "BAD", "INV", "EOT", "PRR", "LET"],
      |      "amountOfNetGain": -99999999999.99,
      |      "amountOfNetLoss": 99999999999.999,
      |      "rttTaxPaid": -99999999999.99
      |    },
      |    {
      |      "assetType": "listed-shares",
      |      "numberOfDisposals": 1,
      |      "assetDescription": "example of this asset",
      |      "acquisitionDate": "2025-04-07",
      |      "disposalDate": "2026-04-06",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "ESH", "NVC", "OTH", "BAD", "INV", "EOT", "PRR", "LET", "CODE"],
      |      "amountOfNetLoss": 99999999999.99
      |    },
      |    {
      |      "assetType": "other-property",
      |      "numberOfDisposals": 1,
      |      "assetDescription": "example of this asset",
      |      "acquisitionDate": "2025-07-11",
      |      "disposalDate": "2025-07-10",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "ESH", "NVC", "OTH", "BAD", "INV", "EOT", "PRR", "LET"],
      |      "amountOfNetGain": 99999999999.99
      |    },
      |    {
      |      "assetType": "other-assets",
      |      "numberOfDisposals": 1,
      |      "assetDescription": "example of this asset",
      |      "acquisitionDate": "2025-04-07",
      |      "disposalDate": "2025-07-10",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "ESH", "NVC", "OTH", "BAD", "INV", "EOT", "PRR", "LET"],
      |      "amountOfNetLoss": 99999999999.99
      |    },
      |    {
      |      "assetType": "??",
      |      "numberOfDisposals": 1,
      |      "assetDescription": "example of this asset",
      |      "acquisitionDate": "2025-04-07",
      |      "disposalDate": "2025-07-10",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99,
      |      "amountOfNetLoss": 99999999999.99
      |    }
      |  ],
      |  "unlistedShares": [
      |    {
      |      "numberOfDisposals": -1,
      |      "assetDescription": "??",
      |      "companyName": "${"a" * 161}",
      |      "companyRegistrationNumber": "??",
      |      "acquisitionDate": "2025",
      |      "disposalDate": "2025",
      |      "disposalProceeds": 99999999999.999,
      |      "allowableCosts": -99999999999.99,
      |      "gainsWithBadr": 99999999999.999,
      |      "gainsWithInv": -99999999999.99,
      |      "gainsBeforeLosses": 99999999999.999,
      |      "losses": -99999999999.99,
      |      "claimOrElectionCodes": ["GHO", "ROR", "PRO", "NVC", "ESH", "OTH", "BAD", "INV", "EOT", "CODE"],
      |      "gainsReportedOnRtt": 99999999999.999,
      |      "gainsExceedingLifetimeLimit": -99999999999.99,
      |      "gainsUnderSeis": 99999999999.999,
      |      "lossUsedAgainstGeneralIncome": -99999999999.999,
      |      "eisOrSeisReliefDueCurrentYear": -99999999999.99,
      |      "lossesUsedAgainstGeneralIncomePreviousYear": 99999999999.999,
      |      "eisOrSeisReliefDuePreviousYear": -99999999999.99,
      |      "rttTaxPaid": 99999999999.999
      |    },
      |    {
      |      "numberOfDisposals": 1,
      |      "assetDescription": "My asset",
      |      "companyName": "Bob the Builder",
      |      "companyRegistrationNumber": "11111111",
      |      "acquisitionDate": "2025-04-06",
      |      "disposalDate": "2025-04-05",
      |      "disposalProceeds": 99999999999.99,
      |      "allowableCosts": 99999999999.99,
      |      "gainsBeforeLosses": 99999999999.99
      |    }
      |  ],
      |  "gainExcludedIndexedSecurities": {
      |    "gainsFromExcludedSecurities": -99999999999.99
      |  },
      |  "qualifyingAssetHoldingCompany": {
      |    "gainsFromQahcBeforeLosses": 99999999999.999,
      |    "lossesFromQahc": -99999999999.99
      |  },
      |  "nonStandardGains": {
      |    "attributedGains": 99999999999.999,
      |    "attributedGainsRttTaxPaid": -99999999999.99,
      |    "otherGains": 99999999999.999,
      |    "otherGainsRttTaxPaid": -99999999999.99
      |  },
      |  "losses": {
      |    "broughtForwardLossesUsedInCurrentYear": 99999999999.999,
      |    "setAgainstInYearGains": -99999999999.99,
      |    "setAgainstEarlierYear": 99999999999.999,
      |    "lossesToCarryForward": -99999999999.99
      |  },
      |  "adjustments": {
      |    "adjustmentAmount": -99999999999.99
      |  },
      |  "lifetimeAllowance": {
      |    "lifetimeAllowanceBadr": -99999999999.99,
      |    "lifetimeAllowanceInv": 99999999999.999
      |  }
      |}
    """.stripMargin
  )

  private val allInvalidFieldsRequestErrors: List[MtdError] = List(
    AssetDescriptionFormatError.withPaths(
      Seq(
        "/cryptoassets/0/assetDescription",
        "/otherGains/0/assetDescription",
        "/unlistedShares/0/assetDescription"
      )
    ),
    AssetTypeFormatError.withPath("/otherGains/4/assetType"),
    ClaimOrElectionCodesFormatError.withPaths(
      Seq(
        "/cryptoassets/0/claimOrElectionCodes/6",
        "/otherGains/1/claimOrElectionCodes/11",
        "/unlistedShares/0/claimOrElectionCodes/9"
      )
    ),
    CompanyNameFormatError.withPaths(Seq("/otherGains/0/companyName", "/unlistedShares/0/companyName")),
    CompanyRegistrationNumberFormatError.withPaths(
      Seq("/otherGains/0/companyRegistrationNumber", "/unlistedShares/0/companyRegistrationNumber")
    ),
    DateFormatError.withPaths(
      Seq(
        "/cryptoassets/0/acquisitionDate",
        "/cryptoassets/0/disposalDate",
        "/otherGains/0/acquisitionDate",
        "/otherGains/0/disposalDate",
        "/unlistedShares/0/acquisitionDate",
        "/unlistedShares/0/disposalDate"
      )
    ),
    TokenNameFormatError.withPath("/cryptoassets/0/tokenName"),
    ValueFormatError.copy(
      message = "The value must be an integer between 1 and 99999999999",
      paths = Some(
        List(
          "/cryptoassets/0/numberOfDisposals",
          "/otherGains/0/numberOfDisposals",
          "/unlistedShares/0/numberOfDisposals"
        )
      )
    ),
    ValueFormatError.withPaths(
      Seq(
        "/cryptoassets/0/disposalProceeds",
        "/cryptoassets/0/allowableCosts",
        "/cryptoassets/0/gainsBeforeLosses",
        "/cryptoassets/0/gainsWithBadr",
        "/cryptoassets/0/losses",
        "/cryptoassets/0/amountOfNetGain",
        "/cryptoassets/0/amountOfNetLoss",
        "/cryptoassets/0/rttTaxPaid",
        "/otherGains/0/disposalProceeds",
        "/otherGains/0/allowableCosts",
        "/otherGains/0/gainsBeforeLosses",
        "/otherGains/0/gainsWithBadr",
        "/otherGains/0/gainsWithInv",
        "/otherGains/0/losses",
        "/otherGains/0/amountOfNetGain",
        "/otherGains/0/amountOfNetLoss",
        "/otherGains/0/rttTaxPaid",
        "/unlistedShares/0/disposalProceeds",
        "/unlistedShares/0/allowableCosts",
        "/unlistedShares/0/gainsBeforeLosses",
        "/unlistedShares/0/gainsWithBadr",
        "/unlistedShares/0/gainsWithInv",
        "/unlistedShares/0/losses",
        "/unlistedShares/0/gainsReportedOnRtt",
        "/unlistedShares/0/gainsExceedingLifetimeLimit",
        "/unlistedShares/0/gainsUnderSeis",
        "/unlistedShares/0/lossUsedAgainstGeneralIncome",
        "/unlistedShares/0/eisOrSeisReliefDueCurrentYear",
        "/unlistedShares/0/lossesUsedAgainstGeneralIncomePreviousYear",
        "/unlistedShares/0/eisOrSeisReliefDuePreviousYear",
        "/unlistedShares/0/rttTaxPaid",
        "/gainExcludedIndexedSecurities/gainsFromExcludedSecurities",
        "/qualifyingAssetHoldingCompany/gainsFromQahcBeforeLosses",
        "/qualifyingAssetHoldingCompany/lossesFromQahc",
        "/nonStandardGains/attributedGains",
        "/nonStandardGains/attributedGainsRttTaxPaid",
        "/nonStandardGains/otherGains",
        "/nonStandardGains/otherGainsRttTaxPaid",
        "/losses/broughtForwardLossesUsedInCurrentYear",
        "/losses/setAgainstInYearGains",
        "/losses/setAgainstEarlierYear",
        "/losses/lossesToCarryForward",
        "/adjustments/adjustmentAmount",
        "/lifetimeAllowance/lifetimeAllowanceBadr",
        "/lifetimeAllowance/lifetimeAllowanceInv"
      )
    ),
    RuleAcquisitionDateError.withPaths(Seq("/cryptoassets/1", "/otherGains/2", "/unlistedShares/1")),
    RuleAmountGainLossError.withPaths(Seq("/cryptoassets/0", "/otherGains/0")),
    RuleDisposalDateNotFutureError.withPaths(
      Seq(
        "/cryptoassets/1/disposalDate",
        "/otherGains/1/disposalDate",
        "/unlistedShares/1/disposalDate"
      )
    ),
    RuleInvalidClaimDisposalsError.withPaths(
      Seq(
        "/otherGains/0/claimOrElectionCodes",
        "/otherGains/1/claimOrElectionCodes",
        "/otherGains/2/claimOrElectionCodes",
        "/otherGains/3/claimOrElectionCodes",
        "/unlistedShares/0/claimOrElectionCodes"
      )
    ),
    RuleInvalidClaimOrElectionCodesError.forListedShares.withPath("/otherGains/1"),
    RuleInvalidClaimOrElectionCodesError.withPath("/otherGains/0"),
    RuleMissingCompanyNameError.withPath("/otherGains/1")
  )

  private val wrappedErrors: ErrorWrapper = ErrorWrapper(
    correlationId = "ignored",
    error = BadRequestError,
    errors = Some(allInvalidFieldsRequestErrors)
  )

  private val emptyObjectsAndArraysError: MtdError = RuleIncorrectOrEmptyBodyError.withPaths(
    Seq(
      "/cryptoassets",
      "/otherGains",
      "/unlistedShares",
      "/gainExcludedIndexedSecurities",
      "/qualifyingAssetHoldingCompany",
      "/nonStandardGains",
      "/losses",
      "/adjustments",
      "/lifetimeAllowance"
    )
  )

  private val missingMandatoryFieldsError: MtdError = RuleIncorrectOrEmptyBodyError.withPaths(
    Seq(
      "/cryptoassets/0/acquisitionDate",
      "/cryptoassets/0/allowableCosts",
      "/cryptoassets/0/assetDescription",
      "/cryptoassets/0/disposalDate",
      "/cryptoassets/0/disposalProceeds",
      "/cryptoassets/0/gainsBeforeLosses",
      "/cryptoassets/0/numberOfDisposals",
      "/cryptoassets/0/tokenName",
      "/otherGains/0/acquisitionDate",
      "/otherGains/0/allowableCosts",
      "/otherGains/0/assetDescription",
      "/otherGains/0/assetType",
      "/otherGains/0/disposalDate",
      "/otherGains/0/disposalProceeds",
      "/otherGains/0/gainsBeforeLosses",
      "/otherGains/0/numberOfDisposals",
      "/unlistedShares/0/acquisitionDate",
      "/unlistedShares/0/allowableCosts",
      "/unlistedShares/0/assetDescription",
      "/unlistedShares/0/companyName",
      "/unlistedShares/0/disposalDate",
      "/unlistedShares/0/disposalProceeds",
      "/unlistedShares/0/gainsBeforeLosses",
      "/unlistedShares/0/numberOfDisposals"
    )
  )

  private trait Test {
    val nino: String    = "AA123456A"
    val taxYear: String = "2025-26"

    private def mtdUri: String = s"/other-gains/$nino/$taxYear"

    def downstreamUrl: String = s"/itsa/income-tax/v1/25-26/income/disposals/other-gains/$nino"

    val suspendTemporalValidations: String = "false"

    def setupStubs(): Unit = ()

    def request: WSRequest = {
      AuditStub.audit()
      AuthStub.authorised()
      MtdIdLookupStub.ninoFound(nino)
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123"),
          ("suspend-temporal-validations", suspendTemporalValidations)
        )
    }

    def verifyNrs(payload: JsValue): Unit =
      verify(
        postRequestedFor(urlEqualTo(s"/mtd-api-nrs-proxy/$nino/itsa-cgt-disposal-other"))
          .withRequestBody(equalToJson(payload.toString())))

  }

  "Calling the 'Create and Amend Other Capital Gains and Disposals' endpoint" should {
    "return a 204 status code" when {
      "any valid request is made with past disposalDates within a non-future tax year and suspendTemporalValidations is false" in new Test {
        override def setupStubs(): Unit = DownstreamStub.onSuccess(
          DownstreamStub.PUT,
          downstreamUrl,
          NO_CONTENT,
          JsObject.empty
        )

        val response: WSResponse = await(request.put(fullRequestBodyMtdJson))
        response.status shouldBe NO_CONTENT
        verifyNrs(fullRequestBodyMtdJson)
      }

      "any valid request is made with future disposalDates within the current tax year and suspendTemporalValidations is true" in new Test {
        val currentTaxYear: TaxYear    = TaxYear.currentTaxYear
        val futureDisposalDate: String = getCurrentDate.plusDays(1).toString

        override val taxYear: String                    = currentTaxYear.asMtd
        override val suspendTemporalValidations: String = "true"

        override def downstreamUrl: String = s"/itsa/income-tax/v1/${currentTaxYear.asTysDownstream}/income/disposals/other-gains/$nino"

        override def setupStubs(): Unit = DownstreamStub.onSuccess(
          DownstreamStub.PUT,
          downstreamUrl,
          NO_CONTENT,
          JsObject.empty
        )

        val response: WSResponse = await(request.put(futureDisposalDatesRequestBodyMtdJson(futureDisposalDate)))
        response.status shouldBe NO_CONTENT
        verifyNrs(futureDisposalDatesRequestBodyMtdJson(futureDisposalDate))
      }

      "any valid request is made with future disposalDates within a future tax year and suspendTemporalValidations is true" in new Test {
        val futureTaxYear: TaxYear     = TaxYear.ending(TaxYear.currentTaxYear.year + 1)
        val futureDisposalDate: String = futureTaxYear.startDate.toString

        override val taxYear: String                    = futureTaxYear.asMtd
        override val suspendTemporalValidations: String = "true"

        override def downstreamUrl: String = s"/itsa/income-tax/v1/${futureTaxYear.asTysDownstream}/income/disposals/other-gains/$nino"

        override def setupStubs(): Unit = DownstreamStub.onSuccess(
          DownstreamStub.PUT,
          downstreamUrl,
          NO_CONTENT,
          JsObject.empty
        )

        val response: WSResponse = await(request.put(futureDisposalDatesRequestBodyMtdJson(futureDisposalDate)))
        response.status shouldBe NO_CONTENT
        verifyNrs(futureDisposalDatesRequestBodyMtdJson(futureDisposalDate))
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
          s"validation fails with ${expectedError.code} error${scenario.fold("")(scenario => s" for '$scenario' scenario")}" in new Test {
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
          ("AA1123A", "2025-26", fullRequestBodyMtdJson, BAD_REQUEST, NinoFormatError, None, None),
          ("AA123456A", "20256", fullRequestBodyMtdJson, BAD_REQUEST, TaxYearFormatError, None, None),
          ("AA123456A", "2025-27", fullRequestBodyMtdJson, BAD_REQUEST, RuleTaxYearRangeInvalidError, None, None),
          ("AA123456A", "2018-19", fullRequestBodyMtdJson, BAD_REQUEST, RuleTaxYearNotSupportedError, None, None),

          // Body errors
          ("AA123456A", "2025-26", JsObject.empty, BAD_REQUEST, RuleIncorrectOrEmptyBodyError, None, Some("empty body")),
          ("AA123456A", "2025-26", emptyObjectsAndArraysMtdJson, BAD_REQUEST, emptyObjectsAndArraysError, None, Some("empty objects and arrays")),
          ("AA123456A", "2025-26", missingMandatoryFieldsMtdJson, BAD_REQUEST, missingMandatoryFieldsError, None, Some("missing mandatory fields")),
          ("AA123456A", "2025-26", allInvalidFieldsRequestBodyJson, BAD_REQUEST, BadRequestError, Some(wrappedErrors), Some("all field errors"))
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns a downstream $downstreamCode error and status $downstreamStatus" in new Test {
            override def setupStubs(): Unit = DownstreamStub.onError(
              DownstreamStub.PUT,
              downstreamUrl,
              downstreamStatus,
              errorBody(downstreamCode)
            )

            val response: WSResponse = await(request.put(fullRequestBodyMtdJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        def errorBody(code: String): String =
          s"""
            |{
            |  "origin": "HoD",
            |  "response": {
            |    "failures": [
            |      {
            |        "type": "$code",
            |        "reason": "message"
            |      }
            |    ]
            |  }
            |}
          """.stripMargin

        val errorInput = Seq(
          (BAD_REQUEST, "INVALID_TAXABLE_ENTITY_ID", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "INVALID_DISPOSAL_DATE", BAD_REQUEST, RuleDisposalDateNotFutureError),
          (UNPROCESSABLE_ENTITY, "INVALID_ACQUISITION_DATE", BAD_REQUEST, RuleAcquisitionDateError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        val tysErrorInput = Seq(
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (UNPROCESSABLE_ENTITY, "INVALID_CLAIM_DISPOSALS", BAD_REQUEST, RuleInvalidClaimDisposalsError),
          (UNPROCESSABLE_ENTITY, "INVALID_PROPERTY_DISPOSALS", BAD_REQUEST, RuleInvalidClaimOrElectionCodesError),
          (UNPROCESSABLE_ENTITY, "MISSING_COMPANY_NAME", BAD_REQUEST, RuleMissingCompanyNameError)
        )

        (errorInput ++ tysErrorInput).foreach(serviceErrorTest.tupled)
      }
    }
  }

}
