package controllers

import helpers.sorus.Fail
import play.api.i18n.Lang
import play.api.libs.json._
import utils.m

import scala.util.Try

trait JsonError {

  private[this] case class Errors(errors: List[String]) {
    def toFail(): Fail = errors.map(e => Fail(e)).reduceOption((f1, f2) => f1.withEx(f2)).getOrElse(Fail("error.empty"))
  }
  private[this] val err_format = Json.format[Errors]

  def fail2json(fail: Fail)(implicit lang: Lang): JsValue = Json.toJson(Errors(formatFailMessage(fail)))(err_format)

  def json2fail(json: JsValue): Option[Fail] = {
    json.validate[Errors](err_format) match {
      case JsSuccess(result, _) => Some(result.toFail())
      case _: JsError           => None
    }
  }

  def formatFailMessage(fail: Fail)(implicit lang: Lang): List[String] = {
    fail.userMessages().map(translate)
  }

  private[this] def translate(msg: String)(implicit lang: Lang): String = Try {
    m(msg)
  }.getOrElse(msg)
}
object JsonError extends JsonError
