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

package v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1

import common.errors.{PpdSubmissionIdFormatError, RuleAmountGainLossError}
import config.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import support.UnitSpec
import v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.CreateAmendCgtPpdOverridesValidatorFactory
import v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.def1.model.request.{
  Def1_CreateAmendCgtPpdOverridesRequestBody,
  Def1_CreateAmendCgtPpdOverridesRequestData
}
import v1.residentialPropertyDisposals.createAmendCgtPpdOverrides.model.request.CreateAmendCgtPpdOverridesRequestData

class Def1_CreateAmendCgtPpdOverridesRulesValidatorSpec extends UnitSpec with MockAppConfig {

  private val validNino                      = "AA123456A"
  private val validTaxYear                   = "2019-20"
  private implicit val correlationId: String = "1234"

  private val validRequestJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val validOnlyMultiplePropertyDisposalsRequestJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val validOnlySinglePropertyDisposalsRequestJson: JsValue = Json.parse(
    """
      |{
      |   "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val nonsenseRequestBodyJson: JsValue = Json.parse("""{"field": "value"}""")

  private val missingMandatoryFieldJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val emptyMultiplePropertyDisposalsRequestJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val emptySinglePropertyDisposalsRequestJson: JsValue = Json.parse(
    """
      |{
      |    "singlePropertyDisposals": []
      |}
      |""".stripMargin
  )

  private val invalidSubmissionIdRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "notAnID",
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000098",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val invalidValueRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.787385
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetLoss": -134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000092",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24999,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45346,
      |             "improvementCosts": 233.4628,
      |             "additionalCosts": 423.34829,
      |             "prfAmount": -2324.67,
      |             "otherReliefAmount": -3434.23,
      |             "lossesFromThisYear": 436.23297423,
      |             "lossesFromPreviousYear": 234.2334728,
      |             "amountOfNetGain": 4567.8974726
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000092",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": -454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45837,
      |             "improvementCosts": 233.4628,
      |             "additionalCosts": -423.34,
      |             "prfAmount": 2324.678372,
      |             "otherReliefAmount": -3434.23,
      |             "lossesFromThisYear": 436.23287,
      |             "lossesFromPreviousYear": -234.23,
      |             "amountOfNetLoss": 4567.8983724
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private def invalidDateRequestBodyJson(acquisitionDate: String, completionDate: String): JsValue = Json.parse(
    s"""
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "$completionDate",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "$acquisitionDate",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val bothGainsAndLossMultiplePropertyDisposalsRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetGain": 1234.78,
      |            "amountOfNetLoss": 134.99
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetGain": 1234.78,
      |            "amountOfNetLoss": 134.99
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val bothGainsAndLossSinglePropertyDisposalsRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetLoss": 134.99
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetGain": 1234.78
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89,
      |             "amountOfNetLoss": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val neitherGainsOrLossMultiplePropertyDisposalsRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092"
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098"
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetGain": 4567.89
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23,
      |             "amountOfNetLoss": 4567.89
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val neitherGainsOrLossSinglePropertyDisposalsRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |    "multiplePropertyDisposals": [
      |         {
      |            "ppdSubmissionId": "AB0000000092",
      |            "amountOfNetLoss": 134.99
      |         },
      |         {
      |            "ppdSubmissionId": "AB0000000098",
      |            "amountOfNetGain": 1234.78
      |         }
      |    ],
      |    "singlePropertyDisposals": [
      |         {
      |             "ppdSubmissionId": "AB0000000099",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23
      |         },
      |         {
      |             "ppdSubmissionId": "AB0000000091",
      |             "completionDate": "2020-02-28",
      |             "disposalProceeds": 454.24,
      |             "acquisitionDate": "2020-03-29",
      |             "acquisitionAmount": 3434.45,
      |             "improvementCosts": 233.45,
      |             "additionalCosts": 423.34,
      |             "prfAmount": 2324.67,
      |             "otherReliefAmount": 3434.23,
      |             "lossesFromThisYear": 436.23,
      |             "lossesFromPreviousYear": 234.23
      |         }
      |    ]
      |}
      |""".stripMargin
  )

  private val parsedNino                  = Nino(validNino)
  private val parsedTaxYear               = TaxYear.fromMtd(validTaxYear)
  private val parsedValidRequestBody      = validRequestJson.as[Def1_CreateAmendCgtPpdOverridesRequestBody]
  private val parsedValidMultipleOnlyBody = validOnlyMultiplePropertyDisposalsRequestJson.as[Def1_CreateAmendCgtPpdOverridesRequestBody]
  private val parsedValidSingleOnlyBody   = validOnlySinglePropertyDisposalsRequestJson.as[Def1_CreateAmendCgtPpdOverridesRequestBody]

  private val validatorFactory                                        = new CreateAmendCgtPpdOverridesValidatorFactory(mockAppConfig)
  private def validator(nino: String, taxYear: String, body: JsValue) = validatorFactory.validator(nino, taxYear, body)

  class Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2020)
      .anyNumberOfTimes()

  }

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, validRequestJson).validateAndWrapResult()
        result shouldBe Right(Def1_CreateAmendCgtPpdOverridesRequestData(parsedNino, parsedTaxYear, parsedValidRequestBody))
      }
    }

    "a valid request containing only multiple disposals is supplied" in new Test {
      val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
        validator(validNino, validTaxYear, validOnlyMultiplePropertyDisposalsRequestJson).validateAndWrapResult()
      result shouldBe Right(Def1_CreateAmendCgtPpdOverridesRequestData(parsedNino, parsedTaxYear, parsedValidMultipleOnlyBody))
    }

    "a valid request containing only single disposals is supplied" in new Test {
      val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
        validator(validNino, validTaxYear, validOnlySinglePropertyDisposalsRequestJson).validateAndWrapResult()
      result shouldBe Right(Def1_CreateAmendCgtPpdOverridesRequestData(parsedNino, parsedTaxYear, parsedValidSingleOnlyBody))
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator("A12344A", validTaxYear, validRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, "201718", validRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, "2017-19", validRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, "2016-17", validRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }
    "return RuleTaxYearForVersionNotSupportedError error" when {
      "a tax year after the maximum tax year is supplied" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, "2025-26", validRequestJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearForVersionNotSupportedError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {

      "a non-empty JSON body is submitted without any expected fields" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, nonsenseRequestBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "the submitted request body has missing mandatory fields" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, missingMandatoryFieldJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/multiplePropertyDisposals/0/ppdSubmissionId",
              "/multiplePropertyDisposals/1/ppdSubmissionId",
              "/singlePropertyDisposals/0/ppdSubmissionId",
              "/singlePropertyDisposals/1/ppdSubmissionId"
            ))
          )
        )
      }

      "an JSON body with empty multiplePropertyDisposals array is submitted" in new Test {

        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, emptyMultiplePropertyDisposalsRequestJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(
              Seq(
                "/multiplePropertyDisposals"
              ))
          )
        )
      }

      "an JSON body with empty singlePropertyDisposals array is submitted" in new Test {

        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, emptySinglePropertyDisposalsRequestJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(
              Seq(
                "/singlePropertyDisposals"
              ))
          )
        )
      }
    }

    "return a PpdSubmissionIdFormatError" when {
      "a body with incorrect ppdSubmissionIds is submitted" in new Test {

        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, invalidSubmissionIdRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            PpdSubmissionIdFormatError.withPaths(
              Seq(
                "/multiplePropertyDisposals/0/ppdSubmissionId"
              ))
          )
        )
      }
    }

    "return a valueFormatError" when {
      "a body with incorrect values is submitted" in new Test {

        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, invalidValueRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.withPaths(Seq(
              "/multiplePropertyDisposals/0/amountOfNetGain",
              "/multiplePropertyDisposals/1/amountOfNetLoss",
              "/singlePropertyDisposals/0/disposalProceeds",
              "/singlePropertyDisposals/0/acquisitionAmount",
              "/singlePropertyDisposals/0/improvementCosts",
              "/singlePropertyDisposals/0/additionalCosts",
              "/singlePropertyDisposals/0/prfAmount",
              "/singlePropertyDisposals/0/otherReliefAmount",
              "/singlePropertyDisposals/0/lossesFromThisYear",
              "/singlePropertyDisposals/0/lossesFromPreviousYear",
              "/singlePropertyDisposals/0/amountOfNetGain",
              "/singlePropertyDisposals/1/disposalProceeds",
              "/singlePropertyDisposals/1/acquisitionAmount",
              "/singlePropertyDisposals/1/improvementCosts",
              "/singlePropertyDisposals/1/additionalCosts",
              "/singlePropertyDisposals/1/prfAmount",
              "/singlePropertyDisposals/1/otherReliefAmount",
              "/singlePropertyDisposals/1/lossesFromThisYear",
              "/singlePropertyDisposals/1/lossesFromPreviousYear",
              "/singlePropertyDisposals/1/amountOfNetLoss"
            ))
          )
        )
      }
    }

    "return a dateFormatError" when {
      "a body with an incorrect date is provided" in new Test {
        val requestWithInvalidDates: JsValue = invalidDateRequestBodyJson(acquisitionDate = "20-02-28", completionDate = "20-02-28")
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, requestWithInvalidDates).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            DateFormatError.withPaths(
              Seq(
                "/singlePropertyDisposals/0/completionDate",
                "/singlePropertyDisposals/0/acquisitionDate"
              ))
          )
        )
      }
    }

    "return a invalid date range error" when {
      "a body with dates outside of acceptable range is provided" in new Test {
        val requestWithInvalidDates: JsValue = invalidDateRequestBodyJson(acquisitionDate = "0010-01-01", completionDate = "2101-02-28")
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, requestWithInvalidDates).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleDateRangeInvalidError.withPaths(
              Seq(
                "/singlePropertyDisposals/0/completionDate",
                "/singlePropertyDisposals/0/acquisitionDate"
              ))
          )
        )
      }
    }

    "return a RuleAmountGainLossError" when {
      "both amountOfNetGain and amountOfNetLoss are provided for multiplePropertyDisposals" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, bothGainsAndLossMultiplePropertyDisposalsRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleAmountGainLossError.withPaths(
              Seq(
                "/multiplePropertyDisposals/0",
                "/multiplePropertyDisposals/1"
              ))
          )
        )
      }

      "neither amountOfNetGain or amountOfNetLoss are provided for multiplePropertyDisposals" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, neitherGainsOrLossMultiplePropertyDisposalsRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleAmountGainLossError.withPaths(
              Seq(
                "/multiplePropertyDisposals/0",
                "/multiplePropertyDisposals/1"
              ))
          )
        )
      }

      "both amountOfNetGain and amountOfNetLoss are provided for singlePropertyDisposals" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, bothGainsAndLossSinglePropertyDisposalsRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleAmountGainLossError.withPaths(
              Seq(
                "/singlePropertyDisposals/0",
                "/singlePropertyDisposals/1"
              ))
          )
        )
      }

      "neither amountOfNetGain or amountOfNetLoss are provided for singlePropertyDisposals" in new Test {
        val result: Either[ErrorWrapper, CreateAmendCgtPpdOverridesRequestData] =
          validator(validNino, validTaxYear, neitherGainsOrLossSinglePropertyDisposalsRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleAmountGainLossError.withPaths(
              Seq(
                "/singlePropertyDisposals/0",
                "/singlePropertyDisposals/1"
              ))
          )
        )
      }
    }
  }

}
