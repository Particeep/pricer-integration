package example.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[example] final case class ExampleConfig(
  id:       String,
  password: Int
)

private[example] object ExampleConfig {
  implicit val format: OFormat[ExampleConfig] = Jsonx.formatCaseClass[ExampleConfig]
}
