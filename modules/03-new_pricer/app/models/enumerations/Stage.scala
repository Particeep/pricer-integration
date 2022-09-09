package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait Stage extends Product with Serializable with Enum

private[newpricer] object Stage extends EnumHelper[Stage] {
  final case object GROUND_FLOOR extends Stage { val label: String = "Rez de Chaussée" }
  final case object INTERMEDIATE extends Stage { val label: String = "Intermédiaire"   }
  final case object LAST_STAGE   extends Stage { val label: String = "Dernier Etage"   }

  override val values: Set[Stage]                 = Set(GROUND_FLOOR, INTERMEDIATE, LAST_STAGE)
  override implicit def enumWrites: Writes[Stage] = new Writes[Stage] {
    def writes(v: Stage): JsValue = JsString(v.label())
  }
}
