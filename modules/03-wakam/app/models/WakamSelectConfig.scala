package wakam.home.models

import play.api.libs.json.{Json, OFormat}

private[wakam] case class WakamSelectConfig(key: String, partnership_code: String)

private[wakam] object WakamSelectConfig {
  implicit val wakam_select_format: OFormat[WakamSelectConfig] = Json.format[WakamSelectConfig]
}
