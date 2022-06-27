package example.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[example] final case class ExampleRequest(
  quote_data:  QuoteData,
  select_data: Option[SelectData] = None
)

private[example] object ExampleRequest {
  implicit val format: OFormat[ExampleRequest] = Jsonx.formatCaseClassUseDefaults[ExampleRequest]
}
