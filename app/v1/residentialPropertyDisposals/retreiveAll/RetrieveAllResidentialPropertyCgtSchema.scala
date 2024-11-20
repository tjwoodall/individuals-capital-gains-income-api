package v1.residentialPropertyDisposals.retreiveAll

import api.schema.DownstreamReadable
import play.api.libs.json.Reads
import v1.residentialPropertyDisposals.retreiveAll.def1.model.response.Def1_RetrieveAllResidentialPropertyCgtResponse
import v1.residentialPropertyDisposals.retreiveAll.model.response.RetrieveAllResidentialPropertyCgtResponse

sealed trait RetrieveAllResidentialPropertyCgtSchema extends DownstreamReadable[RetrieveAllResidentialPropertyCgtResponse]

object RetrieveAllResidentialPropertyCgtSchema {

  case object Def1 extends RetrieveAllResidentialPropertyCgtSchema {
    type DownstreamResp = Def1_RetrieveAllResidentialPropertyCgtResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveAllResidentialPropertyCgtResponse.reads
  }

  val schema: RetrieveAllResidentialPropertyCgtSchema = Def1

}