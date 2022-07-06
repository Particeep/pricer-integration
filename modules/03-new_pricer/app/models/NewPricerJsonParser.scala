package newpricer.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[newpricer] trait NewPricerJsonParser {
  implicit val config_format: OFormat[NewPricerConfig]       = Jsonx.formatCaseClassUseDefaults[NewPricerConfig]
  implicit val req_q_format: OFormat[NewPricerQuoteRequest]  = Jsonx.formatCaseClassUseDefaults[NewPricerQuoteRequest]
  implicit val req_s_format: OFormat[NewPricerSelectRequest] = Jsonx.formatCaseClassUseDefaults[NewPricerSelectRequest]

}
