package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[new_pricer] final case class SelectData(
  assures: Assures,
  insee:   Int
)

private[new_pricer] object SelectData {
  implicit val format: OFormat[SelectData] = Jsonx.formatCaseClass[SelectData]
}
