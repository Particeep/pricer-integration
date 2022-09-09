package newpricer.models.enumerations

import helpers.{ Enum, EnumHelper }
import play.api.libs.json.{ JsString, JsValue, Writes }

private[newpricer] sealed trait Deductible extends Product with Serializable with Enum

private[newpricer] object Deductible extends EnumHelper[Deductible] {

  final case object BASE_DEDUCTIBLE               extends Deductible { val label: String = "Franchise de Base" }
  final case object INCREASE_IN_DEDUCTIBLE        extends Deductible {
    val label: String = "Augmentation de la franchise"
  }
  final case object DOUBLING_THE_DEDUCTIBLE       extends Deductible {
    val label: String = "Doublement de la franchise"
  }
  final case object PARTIAL_DEDUCTIBLE_REDEMPTION extends Deductible {
    val label: String = "Rachat de franchise partiel"
  }

  override val values: Set[Deductible] =
    Set(BASE_DEDUCTIBLE, INCREASE_IN_DEDUCTIBLE, DOUBLING_THE_DEDUCTIBLE, PARTIAL_DEDUCTIBLE_REDEMPTION)

  override implicit def enumWrites: Writes[Deductible] = new Writes[Deductible] {
    def writes(v: Deductible): JsValue = JsString(v.label())
  }
}
