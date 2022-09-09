package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait ComputerOption extends Product with Serializable with Enum

private[newpricer] object ComputerOption extends EnumHelper[ComputerOption] {

  final case object YES       extends ComputerOption { val label: String = "Oui"       }
  final case object NO        extends ComputerOption { val label: String = "Non"       }
  final case object INCLUSION extends ComputerOption { val label: String = "Inclusion" }

  override val values: Set[ComputerOption] = Set(YES, NO, INCLUSION)

  override implicit def enumWrites: Writes[ComputerOption] = new Writes[ComputerOption] {
    def writes(v: ComputerOption): JsValue = JsString(v.label())
  }
}
