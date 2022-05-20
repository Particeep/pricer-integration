package helpers

import helpers.sorus.Fail
import play.api.Logging
import play.api.http.Status
import play.api.libs.json._
import play.api.libs.ws.WSResponse
import scalaz.{ -\/, \/, \/- }
import utils.StringUtils

trait ClientParser extends Logging {

  def parseResponseAs[T](response: WSResponse)(implicit reads: Reads[T]): Fail \/ T = {
    response.status match {
      case s if s >= Status.OK && s < Status.MULTIPLE_CHOICES => parseSuccess(response)
      case _                                                  => {
        val code = StringUtils.randomAlphanumericString(8)
        logger.warn(s"[$code] Failed response with a status ${response.status} and a body ${response.body}")
        -\/(Fail(s"[$code] Response's status ${response.status} and response's body ${response.body}"))
      }
    }
  }

  def parseSuccess[T](response: WSResponse)(implicit reads: Reads[T]): Fail \/ T = {
    val json = if(response.body.isEmpty) Json.toJson("") else response.json
    parseAs(json)
  }

  def parseAs[T](value: JsValue)(implicit reads: Reads[T]): Fail \/ T = {
    value.validate[T] match {
      case JsSuccess(value, _) => \/-(value)
      case JsError(err)        => -\/(Fail(s"Json validation Errors: ${err.toString}"))
    }
  }
}
