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

package api.hateoas

import api.models.hateoas.Link
import api.models.hateoas.Method._
import api.models.hateoas.RelType._
import config.AppConfig

//noinspection ScalaStyle
trait HateoasLinks {

  private def employmentUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/employments/$nino/$taxYear"

  private def employmentUriWithId(appConfig: AppConfig, nino: String, taxYear: String, employmentId: String) =
    s"/${appConfig.apiGatewayContext}/employments/$nino/$taxYear/$employmentId"

  private def otherCgtAndDisposalsUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/disposals/other-gains/$nino/$taxYear"

  private def cgtPpdOverridesUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/disposals/residential-property/$nino/$taxYear/ppd"

  private def cgtResidentialPropertyDisposalsAndOverridesUri(appConfig: AppConfig, nino: String, taxYear: String) =
    s"/${appConfig.apiGatewayContext}/disposals/residential-property/$nino/$taxYear"

  // API resource links

  // Employments
  def listEmployment(appConfig: AppConfig, nino: String, taxYear: String, isSelf: Boolean): Link =
    Link(
      href = employmentUri(appConfig, nino, taxYear),
      method = GET,
      rel = if (isSelf) SELF else LIST_EMPLOYMENTS
    )

  def retrieveEmployment(appConfig: AppConfig, nino: String, taxYear: String, employmentId: String): Link =
    Link(
      href = employmentUriWithId(appConfig, nino, taxYear, employmentId),
      method = GET,
      rel = SELF
    )

  // Other CGT and Disposals
  def retrieveOtherCgt(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = otherCgtAndDisposalsUri(appConfig, nino, taxYear),
      method = GET,
      rel = SELF
    )

  def createAmendOtherCgt(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = otherCgtAndDisposalsUri(appConfig, nino, taxYear),
      method = PUT,
      rel = CREATE_AND_AMEND_OTHER_CGT_AND_DISPOSALS
    )

  def deleteOtherCgt(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = otherCgtAndDisposalsUri(appConfig, nino, taxYear),
      method = DELETE,
      rel = DELETE_OTHER_CGT_AND_DISPOSALS
    )

  // Retrieve All CGT Residential Property Disposals and Overrides
  def retrieveAllCgtPpdDisposalsOverrides(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = cgtResidentialPropertyDisposalsAndOverridesUri(appConfig, nino, taxYear),
      method = GET,
      rel = SELF
    )

  def createAmendNonPpdCgt(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = cgtResidentialPropertyDisposalsAndOverridesUri(appConfig, nino, taxYear),
      method = PUT,
      rel = CREATE_AND_AMEND_NON_PPD_CGT_AND_DISPOSALS
    )

  def deleteNonPpdCgt(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = cgtResidentialPropertyDisposalsAndOverridesUri(appConfig, nino, taxYear),
      method = DELETE,
      rel = DELETE_NON_PPD_CGT_AND_DISPOSALS
    )

  // 'Report and Pay Capital Gains Tax on Property' Overrides
  def createAmendCgtPpdOverrides(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = cgtPpdOverridesUri(appConfig, nino, taxYear),
      method = PUT,
      rel = CREATE_AND_AMEND_CGT_PPD_OVERRIDES
    )

  def deleteCgtPpdOverrides(appConfig: AppConfig, nino: String, taxYear: String): Link =
    Link(
      href = cgtPpdOverridesUri(appConfig, nino, taxYear),
      method = DELETE,
      rel = DELETE_CGT_PPD_OVERRIDES
    )

}
