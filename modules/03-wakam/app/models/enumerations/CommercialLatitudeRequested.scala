package wakam.home.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[wakam] sealed trait CommercialLatitudeRequested extends Product with Serializable with Enum

private[wakam] object CommercialLatitudeRequested extends EnumHelper[CommercialLatitudeRequested] {
  final case object `-15%` extends CommercialLatitudeRequested { override def label(): String = "-15%" }
  final case object `-10%` extends CommercialLatitudeRequested { override def label(): String = "-10%" }
  final case object `-5%`  extends CommercialLatitudeRequested { override def label(): String = "-5%"  }
  final case object `0%`   extends CommercialLatitudeRequested { override def label(): String = "0%"   }
  final case object `5%`   extends CommercialLatitudeRequested { override def label(): String = "5%"   }
  final case object `10%`  extends CommercialLatitudeRequested { override def label(): String = "10%"  }
  final case object `15%`  extends CommercialLatitudeRequested { override def label(): String = "15%"  }

  override val values: Set[CommercialLatitudeRequested] =
    Set(`-15%`, `-10%`, `-5%`, `0%`, `5%`, `10%`, `15%`)

  override implicit def enumWrites: Writes[CommercialLatitudeRequested] = new Writes[CommercialLatitudeRequested] {
    def writes(v: CommercialLatitudeRequested): JsValue = JsString(v.label())
  }
}
