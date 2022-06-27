package example.new_vertical.models.enumeration

import helpers.{ Enum, EnumHelper }

/**
 * This is important to keep this structure
 * for more information : https://nrinaudo.github.io/scala-best-practices/adts/
 */
private[example] sealed trait DriverLicence extends Product with Serializable with Enum

private[example] object DriverLicence extends EnumHelper[DriverLicence] {
  final case object TYPE_A        extends DriverLicence { def label(): String = "Permis A"           }
  final case object TYPE_B        extends DriverLicence { def label(): String = "Permis B"           }
  final case object TRUNK_LICENCE extends DriverLicence { def label(): String = "Permis poids lourd" }

  val values: Set[DriverLicence] = Set(TYPE_A, TYPE_B, TRUNK_LICENCE)
}
