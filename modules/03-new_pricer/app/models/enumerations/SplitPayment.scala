package models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

sealed trait SplitPayment extends Product with Serializable with Enum

object SplitPayment extends EnumHelper[SplitPayment] {

  final case object M extends SplitPayment { override def label(): String = "M" }
  final case object A extends SplitPayment { override def label(): String = "A" }

  override val values: Set[SplitPayment] = Set(M, A)

  override implicit def enumWrites: Writes[SplitPayment] = new Writes[SplitPayment] {
    def writes(v: SplitPayment): JsValue = JsString(v.label())
  }
}
