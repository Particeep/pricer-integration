package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[new_pricer] final case class NewPricerRequest(
  quote_data:  QuoteData,
  select_data: Option[SelectData] = None
)

private[new_pricer] object NewPricerRequest {
  implicit val format: OFormat[NewPricerRequest] = Jsonx.formatCaseClassUseDefaults[NewPricerRequest]
}
