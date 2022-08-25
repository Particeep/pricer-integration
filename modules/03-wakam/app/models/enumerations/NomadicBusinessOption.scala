package wakam.home.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[wakam] sealed trait NomadicBusinessOption extends Product with Serializable with Enum

private[wakam] object NomadicBusinessOption extends EnumHelper[NomadicBusinessOption] {

  final case object NO         extends NomadicBusinessOption { override def label(): String = "Non"        }
  final case object INDIVIDUAL extends NomadicBusinessOption { override def label(): String = "Individuel" }
  final case object FAMILY     extends NomadicBusinessOption { override def label(): String = "Famille"    }

  override val values: Set[NomadicBusinessOption] = Set(NO, INDIVIDUAL, FAMILY)

  override implicit def enumWrites: Writes[NomadicBusinessOption] = new Writes[NomadicBusinessOption] {
    def writes(v: NomadicBusinessOption): JsValue = JsString(v.label())
  }
}
