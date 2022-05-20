package domain

import helpers.EnumHelper

sealed trait FieldType extends Product with Serializable

object FieldType extends EnumHelper[FieldType] {

  final case object TEXT      extends FieldType
  final case object DATE      extends FieldType
  final case object NUMBER    extends FieldType
  final case object ADDRESS   extends FieldType
  final case object PHONE     extends FieldType
  final case object FILE      extends FieldType
  final case object ENUM      extends FieldType
  final case object OBJECT    extends FieldType
  final case object BOOLEAN   extends FieldType
  final case object SIGNATURE extends FieldType

  val values = Set(TEXT, DATE, NUMBER, ADDRESS, PHONE, FILE, ENUM, OBJECT, BOOLEAN, SIGNATURE)
}
