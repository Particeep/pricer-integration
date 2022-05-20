package helpers

import play.api.libs.json._

trait Enum {
  def label(): String
}

trait EnumHelper[E <: Product with Serializable] {
  val values: Set[E]
  def parse(s: String): Option[E] = values.find(_.productPrefix == s)

  implicit def enumReads: Reads[E] = new Reads[E] {
    def reads(json: JsValue): JsResult[E] = json match {
      case JsString(s) => parse(s) match {
          case Some(enum) => JsSuccess(enum)
          case None       => JsError(
              s"[error] enum value $s is not in the enum possible value ${values.map(_.productPrefix).mkString(", ")}"
            )
        }
      case _           => JsError(s"[error] unknown error while parsing value from json $json")
    }
  }

  implicit def enumWrites: Writes[E] = new Writes[E] {
    def writes(v: E): JsValue = JsString(v.productPrefix)
  }
}

trait EnumPlug { self: Product =>
  def name: String = this.productPrefix
}

trait EnumHelperPlug[E <: EnumPlug] {
  def values: Set[E]

  def get(value: String): Option[E] = values.find(t => t.name == value)

  def get(valueOpt: Option[String]): Option[E] = valueOpt.flatMap(get)

  def isAvailable(value: String): Boolean = get(value).isDefined

  def toString(value: E): String = value.name

  implicit def ordering: Ordering[E] = Ordering.by(_.name)

  implicit def enumReads: Reads[E] = new Reads[E] {
    def reads(json: JsValue): JsResult[E] = json match {
      case JsString(s) => get(s) match {
          case Some(enum) => JsSuccess(enum)
          case None       =>
            JsError(s"[error] enum value $s is not in the enum possible value ${values.map(_.name).mkString(", ")}")
        }
      case _           => JsError(s"[error] unknown error while parsing value from json $json")
    }
  }

  implicit def enumWrites: Writes[E] = new Writes[E] {
    def writes(v: E): JsValue = JsString(v.name)
  }
}
