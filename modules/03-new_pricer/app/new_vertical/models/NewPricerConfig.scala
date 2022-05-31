package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[new_pricer] final case class NewPricerConfig(
  id:       String,
  password: Int
)

private[new_pricer] object NewPricerConfig {
  implicit val format: OFormat[NewPricerConfig] = Jsonx.formatCaseClass[NewPricerConfig]
}
