package controllers

import helpers.sorus.Fail
import javax.inject._
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{ Lang, MessagesApi }
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc._
import play.api.routing.Router
import utils.PlayUtils

import scala.concurrent._

@Singleton
class ErrorHandler @Inject() (
  env:          Environment,
  config:       Configuration,
  sourceMapper: OptionalSourceMapper,
  router:       Provider[Router],
  msg_api:      MessagesApi
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
    with JsonError {

  private[this] val default_lang = Lang("en")

  override def onProdServerError(request: RequestHeader, exception:  UsefulException) = Future.successful {
    InternalServerError(error2json(request, 500))
  }

  def clientError(request: RequestHeader, statusCode: Int, message: String): Result = {
    new Status(statusCode)(error2json(request, statusCode))
  }

  private[this] def error2json(request: RequestHeader, statusCode: Int): JsValue = {
    val fail = Fail(utils.m(s"error.page.${statusCode}.message")(default_lang) + " : " + PlayUtils.absolute_url(
      config,
      request.path
    ))
    fail2json(fail)(default_lang)
  }
  override def onClientError(request:     RequestHeader, statusCode: Int, message: String): Future[Result] =
    Future.successful(
      clientError(request, statusCode, message)
    )
}
