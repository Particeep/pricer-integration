package domain

import ai.x.play.json.Encoders._
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

case class File(
  name:      String,
  permalink: String
)

object File {
  implicit lazy val format: OFormat[File] = Jsonx.formatCaseClassUseDefaults[File]
}
