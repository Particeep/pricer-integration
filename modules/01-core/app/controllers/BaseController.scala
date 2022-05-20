package controllers

import helpers.sorus._
import javax.inject.Inject
import play.api.i18n.{ I18nSupport, Lang }
import play.api.mvc._
import scalaz.\/-
import scala.annotation.nowarn
import utils.LoggerUtils

trait BaseController
  extends InjectedController
    with SorusPlay[Request[_]]
    with FormatErrorResult[Request[_]]
    with I18nSupport
    with JsonError {

  @nowarn
  @Inject() private[this] var injected_error_handler: ErrorHandler = _
  lazy val error_handler: ErrorHandler                             = injected_error_handler

  override protected def failToResult(request: RequestHeader)(fail: Fail): Result = {
    fail match {
      case fail: FailWithErrorDetail => fail.withResult(BadRequest)(request2lang(request))
      case fail: Fail                => BadRequest(fail2json(fail)(request2lang(request)))
    }
  }

  protected def not_found[A](x: A)(implicit req: RequestHeader): FailWithResult = x match {
    case fail: Fail      =>
      FailWithResult.apply(
        NOT_FOUND.toString,
        error_handler.clientError(req, NOT_FOUND, fail.userMessage())
      ).withEx(fail)
    case message: String => not_found(Fail(message))
    case _               => not_found(Fail(NOT_FOUND.toString))
  }

  protected def forbidden[A](x: A)(implicit req: RequestHeader): FailWithResult = x match {
    case fail: Fail      =>
      FailWithResult.apply(
        FORBIDDEN.toString,
        error_handler.clientError(req, FORBIDDEN, fail.userMessage())
      ).withEx(fail)
    case message: String => forbidden(Fail(message))
    case _               => forbidden(Fail(FORBIDDEN.toString))
  }

  override protected def log(fail: Fail): Unit = {
    LoggerUtils.log_light(fail)
  }

  protected def redirect(call: Call)(fail: Fail)(implicit req: Request[_]): Result = fail match {
    case fail: FailWithErrorDetail => fail.withResult(Redirect(call))
    case fail                      => Redirect(call).flashing("error" -> formatFailMessage(fail).mkString(" "))
  }

  implicit def request2lang(implicit request: RequestHeader): Lang = {
    request.lang
  }

  def translateError(msg: String)(implicit lang: Lang): String = {
    utils.m(msg)
  }

  implicit def status2FailFunction2(status: Status): Fail => FailWithResult = {
    fail: Fail => FailWithResult("result from ctrl", status, Some(\/-(fail)))
  }
}
