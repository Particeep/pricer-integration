package oauth

import helpers.ClientParser
import helpers.sorus.Fail
import play.api.http.Status
import play.api.Configuration
import play.api.libs.json.Format
import play.api.libs.ws.{ WSClient, WSRequest, WSResponse }
import scalaz.{ -\/, \/ }
import utils.StringUtils

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import helpers.sorus.SorusDSL._

class OAuthTokenCache {
  private[this] val all_tokens = scala.collection.mutable.HashMap[String, OAuthToken]()

  private[this] def extract_token_key(request: WSRequest) = request.uri.getHost()

  def put(request: WSRequest, token: OAuthToken) = all_tokens.put(extract_token_key(request), token)
  def get(request: WSRequest) = all_tokens.get(extract_token_key(request))
}

trait OAuthService extends ClientParser with Sorus {
  def ws: WSClient
  def config: Configuration
  def authentication: Authentication
  implicit val ec: ExecutionContext

  private[this] val token_cache = new OAuthTokenCache()

  def handleResponse[T](request: WSRequest, f: WSRequest => Future[WSResponse])(
    implicit format:             Format[T]
  ): Future[Fail \/ T] = {
    token_cache.get(request).map { token =>
      handleResponseWithToken[T](request, f, token)
    }.getOrElse(
      handleResponseNoToken[T](request, f)
    )
      .run
  }

  private[this] def handleResponseNoToken[T](request: WSRequest, f: WSRequest => Future[WSResponse])(
    implicit format:                                  Format[T]
  ): Step[T] = {
    for {
      token  <- retrieveAccessToken() ?| "Can't load access token"
      _       = token_cache.put(request, token)
      result <- handleResponseWithToken(request, f, token)
    } yield {
      result
    }
  }

  private[this] def handleResponseWithToken[T](
    request:         WSRequest,
    f:               WSRequest => Future[WSResponse],
    token:           OAuthToken
  )(
    implicit format: Format[T]
  ): Step[T] = {
    val request_with_token = f(requestWithHeaders(request, token)) ?| ()

    request_with_token.flatMap { response =>
      response.status match {
        case s if s >= Status.OK && s < Status.MULTIPLE_CHOICES => parseSuccess[T](response) ?| ()
        case Status.UNAUTHORIZED                                => handleResponseNoToken(request, f)
        case _                                                  =>
          val code = StringUtils.randomAlphanumericString(8)
          logger.warn(s"[$code] Failed response with a status ${response.status} and a body ${response.body}")
          Sorus.fail[T](
            Fail(s"[$code] Response's status ${response.status} and response's body ${response.body}")
          )
      }
    }
  }

  private[this] def retrieveAccessToken(): Future[Fail \/ OAuthToken] = {
    val (token_url, token_headers, token_result) = (
      authentication.token_url,
      authentication.token_headers,
      authentication.token_result
    )
    val request                                  = ws.url(token_url).addHttpHeaders(token_headers: _*)
    token_result(request)
      .map(parseResponseAs[OAuthToken])
      .recover {
        case NonFatal(e) =>
          -\/(Fail(
            s"Exception occurred @OAuthService.retrieveAccessToken for POST token on $token_url"
          ).withEx(e))
      }
  }

  private[this] def requestWithHeaders(request: WSRequest, token: OAuthToken): WSRequest = {
    request.addHttpHeaders(
      "Authorization" -> s"Bearer ${token.access_token}"
    )
  }
}
