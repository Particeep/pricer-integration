package example.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import example.new_vertical.models.enumeration.Civility
import play.api.libs.json.OFormat

import java.time.OffsetDateTime

private[example] final case class Assures(
  civility:   Civility,
  first_name: String,
  last_name:  String,
  birthday:   OffsetDateTime,
  is_smoking: Boolean
)

private[example] object Assures {
  implicit val format: OFormat[Assures] = Jsonx.formatCaseClass[Assures]
}
