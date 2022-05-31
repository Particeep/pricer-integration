package new_vertical.models.enumeration

import helpers.{ Enum, EnumHelper }

/**
 * This is import to keep this structure
 * for more information : https://nrinaudo.github.io/scala-best-practices/adts/
 */
sealed trait Civility extends Product with Serializable with Enum

object Civility extends EnumHelper[Civility] {
  final case object LADY   extends Civility { def label(): String = "MISS" }
  final case object MADAM  extends Civility { def label(): String = "MME"  }
  final case object MISTER extends Civility { def label(): String = "MR"   }

  val values = Set(LADY, MADAM, MISTER)
}
