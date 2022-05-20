package utils

import java.util.Base64

import play.api.libs.json.{ JsValue, Json }

object Crypto {

  private[this] final val UTF_8 = "UTF-8"

  def encodeBase64(s: String): String = {
    val e       = Base64.getEncoder
    val b_array = e.encode(s.getBytes(UTF_8))
    new String(b_array, UTF_8)
  }

  def decodeBase64(s: String): String = {
    val e       = Base64.getDecoder
    val b_array = e.decode(s)
    new String(b_array, UTF_8)
  }

  def decodeBase64ToJson(s: String): JsValue = {
    val string = decodeBase64(s)
    Json.parse(string)
  }
}
