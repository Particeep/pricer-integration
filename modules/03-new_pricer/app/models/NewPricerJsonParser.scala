package newpricer.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[newpricer] trait NewPricerJsonParser {
  implicit val format_select: OFormat[SelectData]      = Jsonx.formatCaseClass[SelectData]
  implicit val format_quote: OFormat[QuoteData]        = Jsonx.formatCaseClass[QuoteData]
  implicit val config_format: OFormat[NewPricerConfig] = Jsonx.formatCaseClassUseDefaults[NewPricerConfig]
  implicit val req_format: OFormat[NewPricerRequest]   = Jsonx.formatCaseClassUseDefaults[NewPricerRequest]

}
