package oauth

import play.api.libs.json._

case class OAuthToken(access_token: String)

object OAuthToken {

  implicit val reads: Reads[OAuthToken] = {
    case JsObject(underlying) =>
      underlying.map {
        case ("access_token", JsString(value)) => JsSuccess(OAuthToken(value))
        case ("AccessToken", JsString(value))  => JsSuccess(OAuthToken(value))
        case ("accessToken", JsString(value))  => JsSuccess(OAuthToken(value))
        case _                                 => JsError()
      }
        .find(_.isSuccess) match {
        case Some(jsResult) => jsResult
        case None           => JsError("unknown access token format")
      }
    case _                    =>
      JsError("access token format not found")
  }

}
