package utils

import akka.util.ByteString
import com.google.common.io.BaseEncoding

object EncoderUtils {

  def toBase64(data: String): String = toBase64(data.getBytes("utf-8"))

  def toBase64(data: ByteString): String = toBase64(data.toArray)

  def toBase64(data: Array[Byte]): String = BaseEncoding.base64().encode(data)

}
