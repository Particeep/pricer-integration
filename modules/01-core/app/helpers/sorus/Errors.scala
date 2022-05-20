package helpers.sorus

import play.api.libs.json.{ JsValue, Json }

/**
 * Standard error format for Particeep's API
 */
case class Error(technicalCode: String, message: String, code: Option[String] = None, stack: Option[String] = None)

trait ErrorResult
case class Errors(hasError: Boolean, errors: List[Error]) extends ErrorResult
case class ParsingError(hasError: Boolean, errors: List[JsValue]) extends ErrorResult

object Errors {
  implicit val error_format         = Json.format[Error]
  implicit val errors_format        = Json.format[Errors]
  implicit val parsing_error_format = Json.format[ParsingError]

  def format(err: ErrorResult): String = err match {
    case Errors(_, errors)       => errors.map(_.message).mkString("\n")
    case ParsingError(_, errors) => errors.map(_.toString()).mkString("\n")
  }
}
