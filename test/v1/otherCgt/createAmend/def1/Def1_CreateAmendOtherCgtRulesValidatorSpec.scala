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

package v1.otherCgt.createAmend.def1

import api.models.domain.{Nino, TaxYear}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import mocks.MockAppConfig
import play.api.libs.json._
import support.UnitSpec
import v1.otherCgt.createAmend.def1.model.request.{Def1_CreateAmendOtherCgtRequestBody, Def1_CreateAmendOtherCgtRequestData}

class Def1_CreateAmendOtherCgtRulesValidatorSpec extends UnitSpec with MockAppConfig with JsonErrorValidators {

  private implicit val correlationId: String = "someCorrelationId"

  private val validNino    = "AA123456A"
  private val validTaxYear = "2020-21"

  private def disposalJsonWith(useLoss: Boolean, useAfterReliefLoss: Boolean) = {
    val json = Json
      .parse(
        s"""
           |{
           |  "assetType": "listed-shares",
           |  "assetDescription": "shares",
           |  "acquisitionDate": "2020-02-01",
           |  "disposalDate": "2020-03-01",
           |  "disposalProceeds": 1000.12,
           |  "allowableCosts": 1000.12,
           |  "claimOrElectionCodes": ["LET","PRR", "PRO", "GHO", "ROR", "ESH", "NVC", "SIR", "OTH", "BAD", "INV"],
           |  "rttTaxPaid": 1000.12
           |}""".stripMargin
      )
      .as[JsObject]

    json ++
      (if (useLoss) Json.obj("loss" -> 1000.12) else Json.obj("gain" -> 1000.12)) ++
      (if (useAfterReliefLoss) Json.obj("lossAfterRelief" -> 1000.12) else Json.obj("gainAfterRelief" -> 1000.12))
  }

  private val minimalDisposalJson = Json
    .parse(
      s"""
         |{
         |  "assetType": "listed-shares",
         |  "assetDescription": "shares",
         |  "acquisitionDate": "2020-02-01",
         |  "disposalDate": "2020-03-01",
         |  "disposalProceeds": 1000.12,
         |  "allowableCosts": 1000.12
         |}""".stripMargin
    )
    .as[JsObject]

  private val disposalJson = disposalJsonWith(useLoss = false, useAfterReliefLoss = false)

  private def requestBodyJson(disposals: JsValue*) =
    Json
      .parse(
        s"""
       |{
       |  "disposals": ${Json.toJson(disposals)},
       |  "nonStandardGains": {
       |    "carriedInterestGain": 1000.12,
       |    "carriedInterestRttTaxPaid": 1000.12,
       |    "attributedGains": 1000.12,
       |    "attributedGainsRttTaxPaid": 1000.12,
       |    "otherGains": 1000.12,
       |    "otherGainsRttTaxPaid": 1000.12
       |  },
       |  "losses":{
       |    "broughtForwardLossesUsedInCurrentYear": 1000.12,
       |    "setAgainstInYearGains": 1000.12,
       |    "setAgainstInYearGeneralIncome": 1000.12,
       |    "setAgainstEarlierYear": 1000.12
       |   },
       |  "adjustments": 1000.12
       |}
""".stripMargin
      )
      .as[JsObject]

  private val validRequestBodyJson = requestBodyJson(disposalJson)

  private val parsedNino    = Nino(validNino)
  private val parsedTaxYear = TaxYear.fromMtd(validTaxYear)

  private def validate(nino: String = validNino, taxYear: String = validTaxYear, body: JsValue = validRequestBodyJson) =
    new Def1_CreateAmendOtherCgtValidator(nino, taxYear, body)(mockAppConfig).validateAndWrapResult()

  private def error(mtdError: MtdError) = Left(ErrorWrapper(correlationId, mtdError))

  class Test {

    MockedAppConfig.minimumPermittedTaxYear
      .returns(2021)
      .anyNumberOfTimes()

  }

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is supplied with loss & lossAfterRelief" in new Test {
        behave like validateSuccessfully(useLoss = true, useAfterReliefLoss = true)
      }

      "a valid request is supplied with loss & gainAfterRelief" in new Test {
        behave like validateSuccessfully(useLoss = true, useAfterReliefLoss = false)
      }

      "a valid request is supplied with gain & lossAfterRelief" in new Test {
        behave like validateSuccessfully(useLoss = false, useAfterReliefLoss = true)
      }

      "a valid request is supplied with gain & gainAfterRelief" in new Test {
        behave like validateSuccessfully(useLoss = false, useAfterReliefLoss = false)
      }

      def validateSuccessfully(useLoss: Boolean, useAfterReliefLoss: Boolean) = {
        val body = requestBodyJson(disposalJsonWith(useLoss, useAfterReliefLoss))

        validate(validNino, validTaxYear, body) shouldBe
          Right(Def1_CreateAmendOtherCgtRequestData(parsedNino, parsedTaxYear, body.as[Def1_CreateAmendOtherCgtRequestBody]))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in new Test {
        validate(nino = "A12344A") shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in new Test {
        validate(taxYear = "201718") shouldBe error(TaxYearFormatError)
      }
    }

    "return RuleTaxYearNotSupportedError error" when {
      "an out of range tax year is supplied" in new Test {
        validate(taxYear = "2016-17") shouldBe error(RuleTaxYearNotSupportedError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in new Test {
        validate(taxYear = "2017-19") shouldBe error(RuleTaxYearRangeInvalidError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "an empty JSON body is submitted" in new Test {
        validate(body = JsObject.empty) shouldBe error(RuleIncorrectOrEmptyBodyError)
      }

      "a non-empty JSON body is submitted without any expected fields" in new Test {
        validate(body = Json.parse("""{"field": "value"}""")) shouldBe error(RuleIncorrectOrEmptyBodyError)
      }

      "the submitted request body has fields incorrect type" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/disposalDate", JsBoolean(true)))) shouldBe
          error(RuleIncorrectOrEmptyBodyError.withPaths(Seq("/disposals/0/disposalDate")))
      }

      "the submitted request body has missing mandatory fields" in new Test {
        val body: JsValue = Json.parse(
          """
            |{
            |  "disposals": [{}]
            |}""".stripMargin
        )

        validate(body = body) shouldBe
          error(
            RuleIncorrectOrEmptyBodyError.withPaths(Seq(
              "/disposals/0/acquisitionDate",
              "/disposals/0/allowableCosts",
              "/disposals/0/assetDescription",
              "/disposals/0/assetType",
              "/disposals/0/disposalDate",
              "/disposals/0/disposalProceeds"
            ))
          )
      }

      "the submitted request body contains an empty disposals array" in new Test {
        val body: JsValue = Json.parse("""
            |{
            |  "disposals": []
            |}""".stripMargin)

        validate(body = body) shouldBe error(RuleIncorrectOrEmptyBodyError.withPath("/disposals"))
      }

      "no claimOrElectionCodes are supplied" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/claimOrElectionCodes", JsArray.empty))) shouldBe
          error(RuleIncorrectOrEmptyBodyError.withPath("/disposals/0/claimOrElectionCodes"))
      }

      "nonStandardGains does not contain one of carriedInterestGain,attributedGains or otherGains" in new Test {
        validate(body = validRequestBodyJson
          .removeProperty("/nonStandardGains/carriedInterestGain")
          .removeProperty("/nonStandardGains/attributedGains")
          .removeProperty("/nonStandardGains/otherGains")) shouldBe
          error(RuleIncorrectOrEmptyBodyError.withPath("/nonStandardGains"))
      }

    }

    "return multiple errors" when {
      "multiple invalid parameters are provided" in new Test {
        validate(nino = "not-a-nino", taxYear = "2017-19") shouldBe
          Left(
            ErrorWrapper(
              correlationId,
              BadRequestError,
              Some(List(NinoFormatError, RuleTaxYearRangeInvalidError))
            )
          )
      }
    }

    "return ValueFormatError" when {
      def checkNonNegative(bodyFrom: JsNumber => JsValue, path: String): Unit =
        s"$path" must {
          val error = ValueFormatError.withPath(path)
          behave like disallowsUnsupportedDecimals(bodyFrom, error)
          behave like disallowsNegatives(bodyFrom, error)
        }

      def checkAllowsNegative(bodyFrom: JsNumber => JsValue, path: String): Unit =
        s"$path" must {
          val error = ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99")
          behave like disallowsUnsupportedDecimals(bodyFrom, error)
          behave like allowsNegatives(bodyFrom)
        }

      def disallowsUnsupportedDecimals(bodyFrom: JsNumber => JsValue, mtdError: MtdError): Unit =
        "disallows unsupported decimals" in new Test {
          validate(body = bodyFrom(JsNumber(123.456))) shouldBe error(mtdError)
        }

      def disallowsNegatives(bodyFrom: JsNumber => JsValue, mtdError: MtdError): Unit =
        "disallows negatives" in new Test {
          validate(body = bodyFrom(JsNumber(-0.01))) shouldBe error(mtdError)
        }

      def allowsNegatives(bodyFrom: JsNumber => JsValue): Unit =
        "allows negatives" in new Test {
          validate(body = bodyFrom(JsNumber(-0.01))) shouldBe a[Right[_, _]]
        }

      "a disposal field is invalid" when {
        def bodyCreator(field: String)(value: JsNumber) = requestBodyJson(minimalDisposalJson.update(field, value))

        Seq(
          "disposalProceeds",
          "allowableCosts",
          "gain",
          "loss",
          "gainAfterRelief",
          "lossAfterRelief",
          "rttTaxPaid"
        ).foreach(field => checkNonNegative(bodyCreator(field), path = s"/disposals/0/$field"))
      }

      "a nonStandardGains field is invalid" when {
        def bodyCreator(field: String)(value: JsNumber) = validRequestBodyJson.update(field, value)

        Seq(
          "/nonStandardGains/carriedInterestGain",
          "/nonStandardGains/carriedInterestRttTaxPaid",
          "/nonStandardGains/attributedGains",
          "/nonStandardGains/attributedGainsRttTaxPaid",
          "/nonStandardGains/otherGains",
          "/nonStandardGains/otherGainsRttTaxPaid"
        ).foreach(field => checkNonNegative(bodyCreator(field), path = field))
      }

      "a losses field is invalid" when {
        def bodyCreator(field: String)(value: JsNumber) = validRequestBodyJson.update(field, value)

        Seq(
          "/losses/broughtForwardLossesUsedInCurrentYear",
          "/losses/setAgainstInYearGains",
          "/losses/setAgainstInYearGeneralIncome",
          "/losses/setAgainstEarlierYear"
        ).foreach(field => checkNonNegative(bodyCreator(field), path = field))
      }

      "a adjustments field is invalid" when {
        def bodyCreator(field: String)(value: JsNumber) = validRequestBodyJson.update(field, value)

        Seq(
          "/adjustments"
        ).foreach(field => checkAllowsNegative(bodyCreator(field), path = field))
      }

      "multiple fields are invalid" in new Test {
        val badValue = JsNumber(123.456)

        val body = requestBodyJson(
          disposalJson
            .update("/disposalProceeds", badValue)
            .update("/rttTaxPaid", badValue)
        )
          .update("/nonStandardGains/carriedInterestGain", badValue)
          .update("/losses/broughtForwardLossesUsedInCurrentYear", badValue)

        validate(body = body) shouldBe
          error(
            ValueFormatError.withPaths(
              Seq(
                "/disposals/0/disposalProceeds",
                "/disposals/0/rttTaxPaid",
                "/nonStandardGains/carriedInterestGain",
                "/losses/broughtForwardLossesUsedInCurrentYear"
              ))
          )
      }
    }

    "return DateFormatError error" when {
      "supplied dates are invalid" in new Test {
        val body = requestBodyJson(
          disposalJson
            .update("/acquisitionDate", JsString("BAD_DATE"))
            .update("/disposalDate", JsString("BAD_DATE"))
        )

        validate(body = body) shouldBe
          error(
            DateFormatError.withPaths(
              Seq(
                "/disposals/0/disposalDate",
                "/disposals/0/acquisitionDate"
              )))
      }
    }

    "return AssetTypeFormatError error" when {
      "incorrectAssetType" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/assetType", JsString("wrong")))) shouldBe
          error(AssetTypeFormatError.withPath("/disposals/0/assetType"))

      }
    }

    "return AssetDescriptionFormatError error" when {
      "description is too long" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/assetDescription", JsString("A" * 91)))) shouldBe
          error(AssetDescriptionFormatError.withPath("/disposals/0/assetDescription"))
      }

      "description is too short" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/assetDescription", JsString("")))) shouldBe
          error(AssetDescriptionFormatError.withPath("/disposals/0/assetDescription"))
      }
    }

    "return RuleGainLossError error" when {
      "both gain and loss are supplied" in new Test {
        validate(validNino, validTaxYear, requestBodyJson(disposalJson + ("loss" -> JsNumber(1.2)))) shouldBe
          error(RuleGainLossError.withPath("/disposals/0"))
      }
    }

    "return RuleGainAfterReliefLossAfterReliefError error" when {
      "both gainAfterRelief and lossAfterRelief are supplied" in new Test {
        validate(body = requestBodyJson(disposalJson + ("lossAfterRelief" -> JsNumber(1.2)))) shouldBe
          error(RuleGainAfterReliefLossAfterReliefError.withPath("/disposals/0"))
      }
    }

    "return ClaimOrElectionCodesFormatError error" when {
      "incorrect claimOrElectionCodes supplied" in new Test {
        validate(body = requestBodyJson(disposalJson.update("/claimOrElectionCodes", Json.arr("LET", "*BAD*", "PRR", "PRO", "*BAD*")))) shouldBe
          error(
            ClaimOrElectionCodesFormatError.withPaths(
              Seq(
                "/disposals/0/claimOrElectionCodes/1",
                "/disposals/0/claimOrElectionCodes/4"
              ))
          )
      }
    }
  }

}
