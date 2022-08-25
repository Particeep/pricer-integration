package wakam.home.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[wakam] sealed trait ComputerOption extends Product with Serializable with Enum

private[wakam] object ComputerOption extends EnumHelper[ComputerOption] {

  final case object YES       extends ComputerOption { override def label(): String = "Oui"       }
  final case object NO        extends ComputerOption { override def label(): String = "Non"       }
  final case object INCLUSION extends ComputerOption { override def label(): String = "Inclusion" }

  override val values: Set[ComputerOption] = Set(YES, NO, INCLUSION)

  override implicit def enumWrites: Writes[ComputerOption] = new Writes[ComputerOption] {
    def writes(v: ComputerOption): JsValue = JsString(v.label())
  }
}
