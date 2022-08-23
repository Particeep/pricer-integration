package models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

sealed trait Stage extends Product with Serializable with Enum

object Stage extends EnumHelper[Stage] {

  // all name of enumeration are in uppercase
  // name of object will be what you send to another API which is not Wakam
  // 'label' is then the value waited by Wakam
  final case object GROUND_FLOOR extends Stage { override def label(): String = "Rez de Chaussée" }
  final case object INTERMEDIATE extends Stage { override def label(): String = "Intermédiaire"   }
  final case object LAST_STAGE   extends Stage { override def label(): String = "Dernier Etage"   }

  override val values: Set[Stage] = Set(GROUND_FLOOR, INTERMEDIATE, LAST_STAGE)

  // when you want to send data to wakam, you need to call 'label'
  // in order to be automatic (and for your case we can do this automaticly) we can overrides writes from enumeration

  override implicit def enumWrites: Writes[Stage] = new Writes[Stage] {
    def writes(v: Stage): JsValue = JsString(v.label())
  }
}
