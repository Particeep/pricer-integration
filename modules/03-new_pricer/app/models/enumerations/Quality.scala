package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait Quality extends Product with Serializable with Enum

private[newpricer] object Quality extends EnumHelper[Quality] {

  final case object `1` extends Quality { override def label(): String = "1" }
  final case object `2` extends Quality { override def label(): String = "2" }
  final case object `3` extends Quality { override def label(): String = "3" }
  final case object `4` extends Quality { override def label(): String = "4" }

  override val values: Set[Quality] = Set(`1`, `2`, `3`, `4`)

  override implicit def enumWrites: Writes[Quality] = new Writes[Quality] {
    def writes(v: Quality): JsValue = JsString(v.label())
  }
}
