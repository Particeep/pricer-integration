package example.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[example] final case class SelectData(
  assures: Assures,
  insee:   Int
)

private[example] object SelectData {
  implicit val format: OFormat[SelectData] = Jsonx.formatCaseClass[SelectData]
}
