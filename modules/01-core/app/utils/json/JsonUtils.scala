package utils.json

import play.api.libs.json._

object JsonUtils {

  def removeNulls(jsObject: JsObject): JsObject = {
    JsObject(jsObject.fields.collect {
      case (s, j: JsObject)            =>
        (s, removeNulls(j))
      case other if other._2 != JsNull =>
        other
    })
  }

  def optionOrJsNull[T](option: Option[T])(implicit writer: Writes[T]): JsValue = {
    option match {
      case Some(value) => Json.toJson(value)
      case None        => JsNull
    }
  }

  def formatJsonError(errors: scala.collection.Seq[(JsPath, scala.collection.Seq[JsonValidationError])]): String = {
    errors.map(err => err._1.path.mkString("/") + " -> " + err._2.map(_.message).mkString(", ")).mkString("\n")
  }
}
