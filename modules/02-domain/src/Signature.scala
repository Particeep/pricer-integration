package domain

import ai.x.play.json.Encoders._
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

case class Signature(
  signedFileUrl: Option[String] = None
)

object Signature {
  implicit lazy val format: OFormat[Signature] = Jsonx.formatCaseClassUseDefaults[Signature]
}
