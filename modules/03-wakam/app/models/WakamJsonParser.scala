package wakam.home.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

private[wakam] trait WakamJsonParser {
  implicit val config_format: OFormat[WakamConfig]       = Jsonx.formatCaseClassUseDefaults[WakamConfig]
  implicit val req_q_format: OFormat[WakamQuoteRequest]  = Jsonx.formatCaseClassUseDefaults[WakamQuoteRequest]
  implicit val req_s_format: OFormat[WakamSelectRequest] = Jsonx.formatCaseClassUseDefaults[WakamSelectRequest]

}
