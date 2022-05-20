package domain

sealed trait Frequency extends Product with Serializable

object Frequency {

  final case object ONCE       extends Frequency
  final case object YEARLY     extends Frequency
  final case object HALFYEARLY extends Frequency
  final case object QUARTERLY  extends Frequency
  final case object MONTHLY    extends Frequency

  val values = Set(ONCE, YEARLY, HALFYEARLY, QUARTERLY, MONTHLY)
  def parse(s: String): Option[Frequency] = values.filter(_.productPrefix == s).headOption
}
