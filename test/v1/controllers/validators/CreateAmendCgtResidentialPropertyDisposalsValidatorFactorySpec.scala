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

import api.controllers.requestParsers.validators.validations.{DisposalDateErrorMessages, ValueFormatErrorMessages}
import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import mocks.MockAppConfig
import play.api.libs.json.{JsObject, JsValue, Json}
import support.UnitSpec
import v1.models.request.createAmendCgtResidentialPropertyDisposals._

class CreateAmendCgtResidentialPropertyDisposalsValidatorFactorySpec
    extends UnitSpec
    with ValueFormatErrorMessages
    with DisposalDateErrorMessages
    with MockAppConfig {
  private implicit val correlationId: String = "1234"

  val validNino    = "AA123456A"
  val validTaxYear = "2019-20"

  private val validCustomerReference = "CGTDISPOSAL01"
  private val validDisposalDate      = "2020-03-01"
  private val validCompletionDate    = "2020-03-29"
  private val validAcquisitionDate   = "2020-02-01"
  private val validValue             = 1000.12

  private def validRequestBodyJsonWith(losses: Boolean): JsValue = {
    val json = Json
      .parse(
        s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":$validValue,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":$validValue,
         |      "improvementCosts":$validValue,
         |      "additionalCosts":$validValue,
         |      "prfAmount":$validValue,
         |      "otherReliefAmount":$validValue,
         |      "lossesFromThisYear":$validValue,
         |      "lossesFromPreviousYear":$validValue,
         |      "amountOfNetLoss":$validValue
         |    }
         |  ]
         |}
""".stripMargin
      )
      .as[JsObject]

    json ++ (if (losses) Json.obj("amountOfNetLoss" -> validValue) else Json.obj("amountOfNetGain" -> validValue))
  }

  private val validRequestBodyJson: JsValue = validRequestBodyJsonWith(losses = true)

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

  private val emptyArrayJson: JsValue = Json.parse(
    """
        |{
        |  "disposals": []
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

  private val gainAndLossJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals": [
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":$validValue,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":$validValue,
         |      "improvementCosts":$validValue,
         |      "additionalCosts":$validValue,
         |      "prfAmount":$validValue,
         |      "otherReliefAmount":$validValue,
         |      "lossesFromThisYear":$validValue,
         |      "lossesFromPreviousYear":$validValue,
         |      "amountOfNetLoss":$validValue,
         |      "amountOfNetGain":$validValue
         |    }
         |  ]
         |}
""".stripMargin
  )

  private val oneBadValueFieldJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals": [
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":1000.123,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":$validValue,
         |      "improvementCosts":$validValue,
         |      "additionalCosts":$validValue,
         |      "prfAmount":$validValue,
         |      "otherReliefAmount":$validValue,
         |      "lossesFromThisYear":$validValue,
         |      "lossesFromPreviousYear":$validValue,
         |      "amountOfNetLoss":$validValue
         |    }
         |  ]
         |}
""".stripMargin
  )

  private val allBadValueFieldsWithGainsJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":1000.123,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":1000.123,
         |      "improvementCosts":1000.123,
         |      "additionalCosts":100045678987654345678987654567898765456789.12,
         |      "prfAmount":-1000.12,
         |      "otherReliefAmount":1000.123,
         |      "lossesFromThisYear":1000.123,
         |      "lossesFromPreviousYear":1000.123,
         |      "amountOfNetGain":2000.243
         |    }
         |  ]
         |}
""".stripMargin
  )

  private val allBadValueFieldsWithLossesJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":1000.123,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":1000.123,
         |      "improvementCosts":1000.123,
         |      "additionalCosts":100000000000.00,
         |      "prfAmount":-0.01,
         |      "otherReliefAmount":1000.123,
         |      "lossesFromThisYear":1000.123,
         |      "lossesFromPreviousYear":1000.123,
         |      "amountOfNetLoss":2000.243
         |    }
         |  ]
         |}
""".stripMargin
  )

  private val allBadValueFieldsMultipleDisposalsJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":100000000000.00,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":100000000000.00,
         |      "improvementCosts":100000000000.00,
         |      "additionalCosts":100000000000.00,
         |      "prfAmount":100000000000.00,
         |      "otherReliefAmount":100000000000.00,
         |      "lossesFromThisYear":100000000000.00,
         |      "lossesFromPreviousYear":100000000000.00,
         |      "amountOfNetLoss":100000000000.00
         |    },
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":-0.01,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":-0.01,
         |      "improvementCosts":-0.01,
         |      "additionalCosts":-0.01,
         |      "prfAmount":-0.01,
         |      "otherReliefAmount":-0.01,
         |      "lossesFromThisYear":-0.01,
         |      "lossesFromPreviousYear":-0.01,
         |      "amountOfNetLoss":-0.01
         |    },
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":100000000000.00,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":100000000000.00,
         |      "improvementCosts":100000000000.00,
         |      "additionalCosts":100000000000.00,
         |      "prfAmount":100000000000.00,
         |      "otherReliefAmount":100000000000.00,
         |      "lossesFromThisYear":100000000000.00,
         |      "lossesFromPreviousYear":100000000000.00,
         |      "amountOfNetGain":100000000000.00
         |    },
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":-0.01,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":-0.01,
         |      "improvementCosts":-0.01,
         |      "additionalCosts":-0.01,
         |      "prfAmount":-0.01,
         |      "otherReliefAmount":-0.01,
         |      "lossesFromThisYear":-0.01,
         |      "lossesFromPreviousYear":-0.01,
         |      "amountOfNetGain":-0.01
         |    }
         |  ]
         |}
""".stripMargin
  )

  private val badDateJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"$validCustomerReference",
         |      "disposalDate":"20190601",
         |      "completionDate":"20190801",
         |      "disposalProceeds":$validValue,
         |      "acquisitionDate":"20190701",
         |      "acquisitionAmount":$validValue,
         |      "improvementCosts":$validValue,
         |      "additionalCosts":$validValue,
         |      "prfAmount":$validValue,
         |      "otherReliefAmount":$validValue,
         |      "lossesFromThisYear":$validValue,
         |      "lossesFromPreviousYear":$validValue,
         |      "amountOfNetLoss":$validValue
         |    }
         |  ]
         |}
         |""".stripMargin
  )

  private val badCustomerReferenceJson: JsValue = Json.parse(
    s"""
         |{
         |  "disposals":[
         |    {
         |      "customerReference":"",
         |      "disposalDate":"$validDisposalDate",
         |      "completionDate":"$validCompletionDate",
         |      "disposalProceeds":$validValue,
         |      "acquisitionDate":"$validAcquisitionDate",
         |      "acquisitionAmount":$validValue,
         |      "improvementCosts":$validValue,
         |      "additionalCosts":$validValue,
         |      "prfAmount":$validValue,
         |      "otherReliefAmount":$validValue,
         |      "lossesFromThisYear":$validValue,
         |      "lossesFromPreviousYear":$validValue,
         |      "amountOfNetLoss":$validValue
         |    }
         |  ]
         |}
         |""".stripMargin
  )

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private val validatorFactory = new CreateAmendCgtResidentialPropertyDisposalsValidatorFactory(mockAppConfig)

  private def validator(nino: String, taxYear: String, body: JsValue) =
    validatorFactory.validator(nino, taxYear, body)

  private val MINIMUM_YEAR = 2020
  MockedAppConfig.minimumPermittedTaxYear returns MINIMUM_YEAR

  "validator" should {
    "return the parsed domain object" when {
      "a valid request with only losses supplied" in {
        behave like validateSuccessfully(losses = false)
      }

      "a valid request with only gains supplied" in {
        behave like validateSuccessfully(losses = false)
      }

      def validateSuccessfully(losses: Boolean) = {
        val requestBodyJson = validRequestBodyJsonWith(losses)
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, requestBodyJson).validateAndWrapResult()

        result shouldBe Right(
          CreateAmendCgtResidentialPropertyDisposalsRequestData(
            parsedNino,
            parsedTaxYear,
            requestBodyJson.as[CreateAmendCgtResidentialPropertyDisposalsRequestBody]
          )
        )
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator("A12344A", validTaxYear, validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, "20178", validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, "2017-18", validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, "2019-23", validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, emptyRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }

      "a non-empty JSON body is submitted without any expected fields" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, nonsenseRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath("/disposals"))
        )
      }

      "the submitted request body is not in the correct format" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, nonValidRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/disposals/0/acquisitionAmount",
              "/disposals/0/acquisitionDate",
              "/disposals/0/completionDate",
              "/disposals/0/disposalDate",
              "/disposals/0/disposalProceeds"
            ))
          )
        )
      }

      "the submitted request body has missing mandatory fields" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, missingMandatoryFieldsJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/disposals/0/acquisitionAmount",
              "/disposals/0/acquisitionDate",
              "/disposals/0/completionDate",
              "/disposals/0/disposalDate",
              "/disposals/0/disposalProceeds"
            ))
          )
        )
      }

      "the submitted request body contains empty objects" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
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

    "return ValueFormatError error" when {
      "one field fails value validation" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, oneBadValueFieldJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              message = ZERO_MINIMUM_INCLUSIVE,
              paths = Some(Seq("/disposals/0/disposalProceeds"))
            )
          ))
      }

      "all fields fail value validation (gains)" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, allBadValueFieldsWithGainsJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              message = ZERO_MINIMUM_INCLUSIVE,
              paths = Some(Seq(
                "/disposals/0/disposalProceeds",
                "/disposals/0/acquisitionAmount",
                "/disposals/0/improvementCosts",
                "/disposals/0/additionalCosts",
                "/disposals/0/prfAmount",
                "/disposals/0/otherReliefAmount",
                "/disposals/0/lossesFromThisYear",
                "/disposals/0/lossesFromPreviousYear",
                "/disposals/0/amountOfNetGain"
              ))
            )
          ))
      }

      "all fields fail value validation (losses)" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, allBadValueFieldsWithLossesJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              message = ZERO_MINIMUM_INCLUSIVE,
              paths = Some(Seq(
                "/disposals/0/disposalProceeds",
                "/disposals/0/acquisitionAmount",
                "/disposals/0/improvementCosts",
                "/disposals/0/additionalCosts",
                "/disposals/0/prfAmount",
                "/disposals/0/otherReliefAmount",
                "/disposals/0/lossesFromThisYear",
                "/disposals/0/lossesFromPreviousYear",
                "/disposals/0/amountOfNetLoss"
              ))
            )
          ))
      }

      "all fields fail value validation (multiple disposals)" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, allBadValueFieldsMultipleDisposalsJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.copy(
              message = ZERO_MINIMUM_INCLUSIVE,
              paths = Some(Seq(
                "/disposals/0/disposalProceeds",
                "/disposals/0/acquisitionAmount",
                "/disposals/0/improvementCosts",
                "/disposals/0/additionalCosts",
                "/disposals/0/prfAmount",
                "/disposals/0/otherReliefAmount",
                "/disposals/0/lossesFromThisYear",
                "/disposals/0/lossesFromPreviousYear",
                "/disposals/0/amountOfNetLoss",
                "/disposals/1/disposalProceeds",
                "/disposals/1/acquisitionAmount",
                "/disposals/1/improvementCosts",
                "/disposals/1/additionalCosts",
                "/disposals/1/prfAmount",
                "/disposals/1/otherReliefAmount",
                "/disposals/1/lossesFromThisYear",
                "/disposals/1/lossesFromPreviousYear",
                "/disposals/1/amountOfNetLoss",
                "/disposals/2/disposalProceeds",
                "/disposals/2/acquisitionAmount",
                "/disposals/2/improvementCosts",
                "/disposals/2/additionalCosts",
                "/disposals/2/prfAmount",
                "/disposals/2/otherReliefAmount",
                "/disposals/2/lossesFromThisYear",
                "/disposals/2/lossesFromPreviousYear",
                "/disposals/2/amountOfNetGain",
                "/disposals/3/disposalProceeds",
                "/disposals/3/acquisitionAmount",
                "/disposals/3/improvementCosts",
                "/disposals/3/additionalCosts",
                "/disposals/3/prfAmount",
                "/disposals/3/otherReliefAmount",
                "/disposals/3/lossesFromThisYear",
                "/disposals/3/lossesFromPreviousYear",
                "/disposals/3/amountOfNetGain"
              ))
            )
          ))
      }
    }

    "return DateFormatError error" when {
      "supplied dates are invalid" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, badDateJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            DateFormatError.withPaths(
              Seq(
                "/disposals/0/disposalDate",
                "/disposals/0/completionDate",
                "/disposals/0/acquisitionDate"
              ))
          )
        )
      }
    }

    "return CustomerRefFormatError error" when {
      "supplied asset description is invalid" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, badCustomerReferenceJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            CustomerRefFormatError.withPath(
              "/disposals/0/customerReference"
            ))
        )
      }
    }

    "return RuleGainLossError error" when {
      "gain and loss fields are both supplied" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator(validNino, validTaxYear, gainAndLossJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleGainLossError.withPath("/disposals/0"))
        )
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        val result: Either[ErrorWrapper, CreateAmendCgtResidentialPropertyDisposalsRequestData] =
          validator("A12344A", "20178", validRequestBodyJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(NinoFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
