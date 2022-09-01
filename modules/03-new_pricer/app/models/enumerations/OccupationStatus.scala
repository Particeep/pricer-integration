package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait OccupationStatus extends Product with Serializable with Enum

private[newpricer] object OccupationStatus extends EnumHelper[OccupationStatus] {

  final case object TENANT         extends OccupationStatus { override def label(): String = "Locataire"             }
  final case object OCCUPANT_OWNER extends OccupationStatus { override def label(): String = "Propriétaire occupant" }

  override val values: Set[OccupationStatus] = Set(TENANT, OCCUPANT_OWNER)

  override implicit def enumWrites: Writes[OccupationStatus] = new Writes[OccupationStatus] {
    def writes(v: OccupationStatus): JsValue = JsString(v.label())
  }
}
