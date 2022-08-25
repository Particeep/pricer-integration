package wakam.home.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[wakam] sealed trait Stage extends Product with Serializable with Enum

private[wakam] object Stage extends EnumHelper[Stage] {
  final case object GROUND_FLOOR extends Stage { override def label(): String = "Rez de Chaussée" }
  final case object INTERMEDIATE extends Stage { override def label(): String = "Intermédiaire"   }
  final case object LAST_STAGE   extends Stage { override def label(): String = "Dernier Etage"   }

  override val values: Set[Stage]                 = Set(GROUND_FLOOR, INTERMEDIATE, LAST_STAGE)
  override implicit def enumWrites: Writes[Stage] = new Writes[Stage] {
    def writes(v: Stage): JsValue = JsString(v.label())
  }
}
