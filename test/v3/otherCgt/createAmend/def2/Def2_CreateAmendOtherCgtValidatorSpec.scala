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

package v3.otherCgt.createAmend.def2

import common.errors.*
import play.api.libs.json.*
import shared.models.domain.{Nino, TaxYear}
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.utils.DateUtils.getCurrentDate
import support.UnitSpec
import v3.otherCgt.createAmend.def2.fixture.Def2_CreateAmendOtherCgtFixture.*
import v3.otherCgt.createAmend.def2.model.request.{Def2_CreateAmendOtherCgtRequestBody, Def2_CreateAmendOtherCgtRequestData}
import v3.otherCgt.createAmend.model.request.CreateAmendOtherCgtRequestData

class Def2_CreateAmendOtherCgtValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino: String    = "AA123456A"
  private val validTaxYear: String = "2025-26"

  private val parsedNino: Nino       = Nino(validNino)
  private val parsedTaxYear: TaxYear = TaxYear.fromMtd(validTaxYear)

  private def updateArrayField(arrayField: String,
                               subField: String,
                               value: JsValue,
                               index: Int = 0,
                               json: JsValue = fullRequestBodyMtdJson): JsValue = {
    json
      .transform(
        (JsPath \ arrayField).json.update(
          JsPath.read[JsArray].map { arr =>
            val updated = arr.value.updated(index, arr(index).as[JsObject] + (subField -> value))
            JsArray(updated)
          }
        )
      )
      .getOrElse(json)
  }

  private def updateArrayOrObjectField(path: String, value: JsValue, json: JsValue = fullRequestBodyMtdJson): JsValue = {
    val segments: Array[String] = path.split("/").filter(_.nonEmpty)

    if (segments.length >= 3 && segments(1).forall(_.isDigit)) {
      updateArrayField(segments(0), segments(2), value, segments(1).toInt, json)
    } else {
      json.update(path, value)
    }
  }

  private def nullifyCorrespondingNetGainField(fieldPath: String, json: JsValue): JsValue = {
    val gainPath: String = fieldPath.replace("amountOfNetLoss", "amountOfNetGain")
    updateArrayOrObjectField(gainPath, JsNull, json)
  }

  private def validator(nino: String = validNino,
                        taxYear: String = validTaxYear,
                        body: JsValue = fullRequestBodyMtdJson,
                        temporalValidationEnabled: Boolean = true) =
    new Def2_CreateAmendOtherCgtValidator(
      nino = nino,
      taxYear = taxYear,
      body = body,
      temporalValidationEnabled = temporalValidationEnabled
    )

  "validator" should {
    "return the parsed domain object" when {
      def updateAllDisposalDatesJson(disposalDate: String): JsValue = Seq("cryptoassets", "otherGains", "unlistedShares")
        .foldLeft(fullRequestBodyMtdJson) { case (updatedJson, arrayField) =>
          updateArrayField(arrayField, "disposalDate", JsString(disposalDate), json = updatedJson)
        }

      def updateAllDisposalDatesModel(disposalDate: String): Def2_CreateAmendOtherCgtRequestBody = fullRequestBodyModel.copy(
        cryptoassets = fullRequestBodyModel.cryptoassets.map(_.map(_.copy(disposalDate = disposalDate))),
        otherGains = fullRequestBodyModel.otherGains.map(_.map(_.copy(disposalDate = disposalDate))),
        unlistedShares = fullRequestBodyModel.unlistedShares.map(_.map(_.copy(disposalDate = disposalDate)))
      )

      "a valid request with past disposalDates within a non-future tax year is supplied and temporal validation is enabled" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator().validateAndWrapResult()

        result shouldBe Right(Def2_CreateAmendOtherCgtRequestData(parsedNino, parsedTaxYear, fullRequestBodyModel))
      }

      "a valid request with future disposalDates within the current tax year is supplied and temporal validation is disabled" in {
        val currentTaxYear: TaxYear    = TaxYear.currentTaxYear
        val futureDisposalDate: String = getCurrentDate.plusDays(1).toString

        val requestBodyJson: JsValue                              = updateAllDisposalDatesJson(futureDisposalDate)
        val requestBodyModel: Def2_CreateAmendOtherCgtRequestBody = updateAllDisposalDatesModel(futureDisposalDate)

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(
          taxYear = currentTaxYear.asMtd,
          body = requestBodyJson,
          temporalValidationEnabled = false
        ).validateAndWrapResult()

        result shouldBe Right(Def2_CreateAmendOtherCgtRequestData(parsedNino, currentTaxYear, requestBodyModel))
      }

      "a valid request with future disposalDates within a future tax year is supplied and temporal validation is disabled" in {
        val futureTaxYear: TaxYear     = TaxYear.ending(TaxYear.currentTaxYear.year + 1)
        val futureDisposalDate: String = futureTaxYear.startDate.toString

        val requestBodyJson: JsValue                              = updateAllDisposalDatesJson(futureDisposalDate)
        val requestBodyModel: Def2_CreateAmendOtherCgtRequestBody = updateAllDisposalDatesModel(futureDisposalDate)

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(
          taxYear = futureTaxYear.asMtd,
          body = requestBodyJson,
          temporalValidationEnabled = false
        ).validateAndWrapResult()

        result shouldBe Right(Def2_CreateAmendOtherCgtRequestData(parsedNino, futureTaxYear, requestBodyModel))
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator("A12344A").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }
    }

    "return RuleIncorrectOrEmptyBodyError error" when {
      "passed an empty body" in {
        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = Json.obj()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      "passed a body with empty arrays and objects" in {
        val invalidJson: JsValue = emptyObjectsAndArraysMtdJson

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(
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
          )
        )
      }

      "passed a body with missing mandatory fields" in {
        val invalidJson: JsValue = missingMandatoryFieldsMtdJson

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(
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
          )
        )
      }

      fullRequestBodyMtdJson.collectPaths().foreach { path =>
        s"passed a body with an incorrect type for field $path" in {
          val invalidJson: JsValue = updateArrayOrObjectField(path, JsBoolean(true))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.withPath(path)))
        }
      }
    }

    "return AssetTypeFormatError error" when {
      "passed a body with an incorrectly formatted otherGains assetType" in {
        val invalidJson: JsValue = updateArrayField("otherGains", "assetType", JsString("asset"))

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, AssetTypeFormatError.withPath("/otherGains/0/assetType")))
      }
    }

    "return AssetDescriptionFormatError error" when {
      Seq("cryptoassets", "otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with an incorrectly formatted $arrayField assetDescription" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "assetDescription", JsString("example asset?"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, AssetDescriptionFormatError.withPath(s"/$arrayField/0/assetDescription")))
        }
      }
    }

    "return TokenNameFormatError error" when {
      "passed a body with an incorrectly formatted cryptoassets tokenName" in {
        val invalidJson: JsValue = updateArrayField("cryptoassets", "tokenName", JsString("token?"))

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TokenNameFormatError.withPath("/cryptoassets/0/tokenName")))
      }
    }

    "return DateFormatError error" when {
      Seq("cryptoassets", "otherGains", "unlistedShares").foreach { arrayField =>
        Seq("acquisitionDate", "disposalDate").foreach { dateField =>
          s"passed a body with an incorrectly formatted $arrayField $dateField" in {
            val invalidJson: JsValue = updateArrayField(arrayField, dateField, JsString("2025"))

            val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

            result shouldBe Left(ErrorWrapper(correlationId, DateFormatError.withPath(s"/$arrayField/0/$dateField")))
          }
        }
      }
    }

    "return CompanyNameFormatError error" when {
      Seq("otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with an incorrectly formatted $arrayField companyName" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "companyName", JsString("a" * 161))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, CompanyNameFormatError.withPath(s"/$arrayField/0/companyName")))
        }
      }
    }

    "return CompanyRegistrationNumberFormatError error" when {
      Seq("otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with an incorrectly formatted $arrayField companyRegistrationNumber" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "companyRegistrationNumber", JsString("11"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(
            ErrorWrapper(correlationId, CompanyRegistrationNumberFormatError.withPath(s"/$arrayField/0/companyRegistrationNumber"))
          )
        }
      }
    }

    "return ClaimOrElectionCodesFormatError error" when {
      Seq("cryptoassets", "otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with an incorrectly formatted $arrayField claimOrElectionCodes" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "claimOrElectionCodes", Json.arr("BAD", "inv"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, ClaimOrElectionCodesFormatError.withPath(s"/$arrayField/0/claimOrElectionCodes/1")))
        }
      }
    }

    "return ValueFormatError error" when {
      Seq(
        ("/cryptoassets/0/numberOfDisposals", "integer"),
        ("/cryptoassets/0/disposalProceeds", "decimal"),
        ("/cryptoassets/0/allowableCosts", "decimal"),
        ("/cryptoassets/0/gainsBeforeLosses", "decimal"),
        ("/cryptoassets/0/gainsWithBadr", "decimal"),
        ("/cryptoassets/0/losses", "decimal"),
        ("/cryptoassets/0/amountOfNetGain", "decimal"),
        ("/cryptoassets/0/amountOfNetLoss", "decimal"),
        ("/cryptoassets/0/rttTaxPaid", "decimal"),
        ("/otherGains/0/numberOfDisposals", "integer"),
        ("/otherGains/0/disposalProceeds", "decimal"),
        ("/otherGains/0/allowableCosts", "decimal"),
        ("/otherGains/0/gainsBeforeLosses", "decimal"),
        ("/otherGains/0/gainsWithBadr", "decimal"),
        ("/otherGains/0/gainsWithInv", "decimal"),
        ("/otherGains/0/losses", "decimal"),
        ("/otherGains/0/amountOfNetGain", "decimal"),
        ("/otherGains/0/amountOfNetLoss", "decimal"),
        ("/otherGains/0/rttTaxPaid", "decimal"),
        ("/unlistedShares/0/numberOfDisposals", "integer"),
        ("/unlistedShares/0/disposalProceeds", "decimal"),
        ("/unlistedShares/0/allowableCosts", "decimal"),
        ("/unlistedShares/0/gainsBeforeLosses", "decimal"),
        ("/unlistedShares/0/gainsWithBadr", "decimal"),
        ("/unlistedShares/0/gainsWithInv", "decimal"),
        ("/unlistedShares/0/gainsReportedOnRtt", "decimal"),
        ("/unlistedShares/0/gainsExceedingLifetimeLimit", "decimal"),
        ("/unlistedShares/0/gainsUnderSeis", "decimal"),
        ("/unlistedShares/0/lossUsedAgainstGeneralIncome", "decimal"),
        ("/unlistedShares/0/eisOrSeisReliefDueCurrentYear", "decimal"),
        ("/unlistedShares/0/lossesUsedAgainstGeneralIncomePreviousYear", "decimal"),
        ("/unlistedShares/0/eisOrSeisReliefDuePreviousYear", "decimal"),
        ("/unlistedShares/0/rttTaxPaid", "decimal"),
        ("/gainExcludedIndexedSecurities/gainsFromExcludedSecurities", "decimal"),
        ("/qualifyingAssetHoldingCompany/gainsFromQahcBeforeLosses", "decimal"),
        ("/qualifyingAssetHoldingCompany/lossesFromQahc", "decimal"),
        ("/nonStandardGains/attributedGains", "decimal"),
        ("/nonStandardGains/attributedGainsRttTaxPaid", "decimal"),
        ("/nonStandardGains/otherGains", "decimal"),
        ("/nonStandardGains/otherGainsRttTaxPaid", "decimal"),
        ("/losses/broughtForwardLossesUsedInCurrentYear", "decimal"),
        ("/losses/setAgainstInYearGains", "decimal"),
        ("/losses/setAgainstEarlierYear", "decimal"),
        ("/losses/lossesToCarryForward", "decimal"),
        ("/adjustments/adjustmentAmount", "decimal"),
        ("/lifetimeAllowance/lifetimeAllowanceBadr", "decimal"),
        ("/lifetimeAllowance/lifetimeAllowanceInv", "decimal")
      ).foreach { case (fieldPath, fieldType) =>
        s"passed a body with an incorrectly formatted $fieldType field $fieldPath" in {
          val (invalidValue, expectedError): (JsValue, MtdError) = fieldType match {
            case "integer" => (JsNumber(0), ValueFormatError.forIntegerPathAndRange(fieldPath, "1", "99999999999"))
            case "decimal" => (JsNumber(-1.99), ValueFormatError.withPath(fieldPath))
          }

          val invalidJsonBase: JsValue = updateArrayOrObjectField(fieldPath, invalidValue)

          val invalidJson: JsValue = if (fieldPath.endsWith("amountOfNetLoss")) {
            nullifyCorrespondingNetGainField(fieldPath, invalidJsonBase)
          } else {
            invalidJsonBase
          }

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, expectedError))
        }
      }
    }

    "return RuleAcquisitionDateError error" when {
      Seq("cryptoassets", "otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with acquisitionDate later than disposalDate supplied for $arrayField" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "acquisitionDate", JsString("2025-12-31"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleAcquisitionDateError.withPath(s"/$arrayField/0")))
        }
      }
    }

    "return RuleDisposalDateNotFutureError error" when {
      Seq("cryptoassets", "otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with disposalDate in the future supplied for $arrayField" in {
          val currentTaxYear: String     = TaxYear.currentTaxYear.asMtd
          val futureDisposalDate: String = getCurrentDate.plusDays(1).toString

          val invalidJson: JsValue = updateArrayField(arrayField, "disposalDate", JsString(futureDisposalDate))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(
            taxYear = currentTaxYear,
            body = invalidJson
          ).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleDisposalDateNotFutureError.withPath(s"/$arrayField/0/disposalDate")))
        }

        s"passed a body with disposalDate before the start of the tax year supplied for $arrayField" in {
          val invalidJsonBase: JsValue = updateArrayField(arrayField, "acquisitionDate", JsString("2025-01-05"))

          val invalidJson: JsValue = updateArrayField(arrayField, "disposalDate", JsString("2025-04-05"), json = invalidJsonBase)

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleDisposalDateNotFutureError.withPath(s"/$arrayField/0/disposalDate")))
        }

        s"passed a body with disposalDate after the end of the tax year supplied for $arrayField" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "disposalDate", JsString("2026-04-06"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleDisposalDateNotFutureError.withPath(s"/$arrayField/0/disposalDate")))
        }
      }
    }

    "return RuleMissingCompanyNameError error" when {
      "passed a body without companyName supplied for otherGains asset type listed-shares" in {
        val invalidJson: JsValue = updateArrayField("otherGains", "companyName", JsNull)

        val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleMissingCompanyNameError.withPath("/otherGains/0")))
      }
    }

    "return RuleInvalidClaimOrElectionCodesError error" when {
      Seq(
        ("listed-shares", "PRR", RuleInvalidClaimOrElectionCodesError.forListedShares),
        ("listed-shares", "LET", RuleInvalidClaimOrElectionCodesError.forListedShares),
        ("non-uk-residential-property", "INV", RuleInvalidClaimOrElectionCodesError)
      ).foreach { case (assetType, code, expectedError) =>
        s"passed a body with $code claimOrElectionCode supplied for otherGains asset type $assetType" in {
          val invalidJsonBase: JsValue = updateArrayField("otherGains", "assetType", JsString(assetType))

          val invalidJson: JsValue = updateArrayField("otherGains", "claimOrElectionCodes", Json.arr(code), json = invalidJsonBase)

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, expectedError.withPath("/otherGains/0")))
        }
      }
    }

    "return RuleInvalidClaimDisposalsError error" when {
      Seq("otherGains", "unlistedShares").foreach { arrayField =>
        s"passed a body with BAD and INV claimOrElectionCodes supplied for $arrayField" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "claimOrElectionCodes", Json.arr("BAD", "INV"))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleInvalidClaimDisposalsError.withPath(s"/$arrayField/0/claimOrElectionCodes")))
        }
      }
    }

    "return RuleAmountGainLossError error" when {
      Seq("cryptoassets", "otherGains").foreach { arrayField =>
        s"passed a body with both amountOfNetGain and amountOfNetLoss supplied for $arrayField" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "amountOfNetLoss", JsNumber(1.99))

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleAmountGainLossError.withPath(s"/$arrayField/0")))
        }

        s"passed a body without amountOfNetGain or amountOfNetLoss supplied for $arrayField" in {
          val invalidJson: JsValue = updateArrayField(arrayField, "amountOfNetGain", JsNull)

          val result: Either[ErrorWrapper, CreateAmendOtherCgtRequestData] = validator(body = invalidJson).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, RuleAmountGainLossError.withPath(s"/$arrayField/0")))
        }
      }
    }

  }

}
