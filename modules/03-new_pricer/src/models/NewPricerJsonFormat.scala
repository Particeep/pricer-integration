package newpricer.models

import ai.x.play.json.Encoders._
import ai.x.play.json.Jsonx

private[newpricer] trait NewPricerJsonParser {
  implicit val config_format = Jsonx.formatCaseClassUseDefaults[NewPricerConfig]
  implicit val req_format    = Jsonx.formatCaseClassUseDefaults[NewPricerRequest]
}
