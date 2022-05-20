package oauth

import play.api.libs.ws.{ WSRequest, WSResponse }

import scala.concurrent.Future

case class Authentication(
  token_url:     String,
  token_headers: Seq[(String, String)],
  token_result:  WSRequest => Future[WSResponse]
)
