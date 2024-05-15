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

package v1.controllers.validators

import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.request.createAmendOtherCgt.{CreateAmendOtherCgtRequestBody, CreateAmendOtherCgtRequestData}

class CreateAmendOtherCgtValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2020-21"

  private val validAssetType       = "listed-shares"
  private val validDescription     = "shares"
  private val validDisposalDate    = "2020-03-01"
  private val validAcquisitionDate = "2020-02-01"
  private val validValue           = 1000.12
  private val validCodes            ="""["LET","PRR", "PRO", "GHO", "ROR", "ESH", "NVC", "SIR", "OTH", "BAD", "INV"]"""

  private val validRequestBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val oneBadValueFieldJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":1000.123,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val allBadValueFieldsJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":1000.123,
       |      "allowableCosts":1000.123,
       |      "gain":1000.123,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":1000.123,
       |      "rttTaxPaid":1000.123
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val badDateBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"20190601",
       |      "disposalDate":"20190601",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val incorrectAssetTypeBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"wrong",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val longDescriptionBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"This is 91 characters long so that the rule will fail |||||||||||||||||||||||||||||||||||||",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val bothLossGainBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "loss":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val bothAfterReliefLossGainBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":$validCodes,
       |      "gainAfterRelief":$validValue,
       |      "lossAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val incorrectCodeBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":["LER"],
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val incorrectCodesBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":["LER", "PER"],
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val incorrectAndCorrectCodesBodyJson: JsValue = Json.parse(
    s"""
       |{
       |  "disposals":[
       |    {
       |      "assetType":"$validAssetType",
       |      "assetDescription":"$validDescription",
       |      "acquisitionDate":"$validAcquisitionDate",
       |      "disposalDate":"$validDisposalDate",
       |      "disposalProceeds":$validValue,
       |      "allowableCosts":$validValue,
       |      "gain":$validValue,
       |      "claimOrElectionCodes":["PRR","LER", "PER"],
       |      "gainAfterRelief":$validValue,
       |      "rttTaxPaid":$validValue
       |    }
       |  ]
       |}
""".stripMargin
  )

  private val emptyRequestBodyJson: JsValue = Json.parse("""{}""")

  private val nonsenseRequestBodyJson: JsValue = Json.parse("""{"field": "value"}""")

  private val nonValidRequestBodyJson: JsValue = Json.parse(
    """
      |{
      |  "disposals": [
      |    {
      |      "disposalDate": true
      |    }
      |  ]
      |}
""".stripMargin
  )

  private val missingMandatoryFieldsJson: JsValue = Json.parse(
    """
      |{
      |  "disposals":[{}]
      |}
""".stripMargin
  )

  private val emptyArrayJson: JsValue = Json.parse(
    """
      |{
      |  "disposals": []
      |}
""".stripMargin
  )

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)
  private val parsedBody    = validRequestBodyJson.as[CreateAmendOtherCgtRequestBody]

  val validatorFactory = new CreateAmendOtherCgtValidatorFactory(mockAppConfig)

  private def validator(nino: String, taxYear: String, body: JsValue) =
    validatorFactory.validator(nino, taxYear, body)

  MockedAppConfig.minimumPermittedTaxYear
    .returns(2021)
    .anyNumberOfTimes()

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied" in {
        val result = validator(validNino, validTaxYear, validRequestBodyJson).validateAndWrapResult()
        result shouldBe Right(CreateAmendOtherCgtRequestData(parsedNino, parsedTaxYear, parsedBody))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result = validator("A12344A", validTaxYear, validRequestBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result = validator(validNino, "201718", validRequestBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in {
        val result = validator(validNino, "2016-17", validRequestBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in {
        val result = validator(validNino, "2017-19", validRequestBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, emptyRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "a non-empty JSON body is submitted without any expected fields" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, nonsenseRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "the submitted request body is not in the correct format" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, nonValidRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/disposals/0/acquisitionDate",
              "/disposals/0/allowableCosts",
              "/disposals/0/assetDescription",
              "/disposals/0/assetType",
              "/disposals/0/disposalDate",
              "/disposals/0/disposalProceeds"
            ))
          )
        )
      }

      "the submitted request body has missing mandatory fields" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, missingMandatoryFieldsJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/disposals/0/acquisitionDate",
              "/disposals/0/allowableCosts",
              "/disposals/0/assetDescription",
              "/disposals/0/assetType",
              "/disposals/0/disposalDate",
              "/disposals/0/disposalProceeds"
            ))
          )
        )
      }

      "the submitted request body contains empty objects" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, emptyArrayJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPath(
              "/disposals"
            )
          ))
      }
    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in {
        val result = validator("not-a-nino", "2017-19", validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, RuleTaxYearRangeInvalidError))
          )
        )
      }
    }

    "return ValueFormatError error" when {
      "one field fails value validation" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, oneBadValueFieldJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              paths = Some(Seq(
                "/disposals/0/disposalProceeds"
              ))
            )
          ))
      }

      "multiple fields fails value validation" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, allBadValueFieldsJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              paths = Some(Seq(
                "/disposals/0/disposalProceeds",
                "/disposals/0/allowableCosts",
                "/disposals/0/gain",
                "/disposals/0/gainAfterRelief",
                "/disposals/0/rttTaxPaid"
              ))
            )
          ))
      }
    }

    "return DateFormatError error" when {
      "supplied dates are invalid" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] =
          validator(validNino, validTaxYear, badDateBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            DateFormatError.withPaths(
              Seq(
                "/disposals/0/disposalDate",
                "/disposals/0/acquisitionDate"
              ))
          )
        )
      }
    }

    "return AssetTypeFormatError error" when {
      "incorrectAssetType" in {
        val result = validator(validNino, validTaxYear, incorrectAssetTypeBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, AssetTypeFormatError.withPath("/disposals/0/assetType"))
        )
      }
    }

    "return AssetDescriptionFormatError error" when {
      "description is too long" in {
        val result = validator(validNino, validTaxYear, longDescriptionBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, AssetDescriptionFormatError.withPath("/disposals/0/assetDescription"))
        )
      }
    }

    "return RuleGainLossError error" when {
      "both gain and loss are supplied" in {
        val result = validator(validNino, validTaxYear, bothLossGainBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleGainLossError.withPath("/disposals/0"))
        )
      }
    }

    "return RuleGainAfterReliefLossAfterReliefError error" when {
      "both gainAfterRelief and lossAfterRelief are supplied" in {
        val result = validator(validNino, validTaxYear, bothAfterReliefLossGainBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleGainAfterReliefLossAfterReliefError.withPath("/disposals/0"))
        )
      }
    }

    "return ClaimOrElectionCodesFormatError error" when {
      "only incorrect code supplied" in {
        val result = validator(validNino, validTaxYear, incorrectCodeBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, ClaimOrElectionCodesFormatError.withPath("/disposals/0/claimOrElectionCodes/0"))
        )
      }

      "multiple incorrect codes supplied" in {
        val result = validator(validNino, validTaxYear, incorrectCodesBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, ClaimOrElectionCodesFormatError.withPaths(Seq(
            "/disposals/0/claimOrElectionCodes/0",
            "/disposals/0/claimOrElectionCodes/1"
          )))
        )
      }

      "correct and incorrect codes supplied" in {
        val result = validator(validNino, validTaxYear, incorrectAndCorrectCodesBodyJson).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, ClaimOrElectionCodesFormatError.withPaths(Seq(
            "/disposals/0/claimOrElectionCodes/1",
            "/disposals/0/claimOrElectionCodes/2"
          )))
        )
      }
    }
  }

}
