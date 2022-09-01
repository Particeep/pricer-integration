package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait Title extends Product with Serializable with Enum

private[newpricer] object Title extends EnumHelper[Title] {

  final case object MRS extends Title { override def label(): String = "MME" }
  final case object MR  extends Title { override def label(): String = "M."  }

  override val values: Set[Title] = Set(MRS, MR)

  override implicit def enumWrites: Writes[Title] = new Writes[Title] {
    def writes(v: Title): JsValue = JsString(v.label())
  }
}
