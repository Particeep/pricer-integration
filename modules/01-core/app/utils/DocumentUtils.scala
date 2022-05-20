package utils

import akka.util.ByteString
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import play.api.libs.ws.{ WSClient, WSResponse }
import scalaz.\/

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future

@Singleton
class DocumentUtils @Inject() (ws: WSClient) extends Sorus {

  def toBase64(url: String): Future[Fail \/ String] = {
    for {
      response <- ws.url(url).get()        ?| s"error.signature.on.download.url $url"
      _        <- (response.status == 200) ?| s"error.signature.on.download.url.status $url ${response.status}"
    } yield {
      EncoderUtils.toBase64(response.bodyAsBytes)
    }
  }

  def signatureBase64(url: String): Future[Fail \/ Map[String, String]] = {
    for {
      files <- signatureFiles(url) ?| ()
    } yield {
      files.view.mapValues(EncoderUtils.toBase64).toMap
    }
  }

  def signatureFiles(url: String): Future[Fail \/ Map[String, ByteString]] = {
    for {
      response <- ws.url(url).get()        ?| s"error.signature.on.download.url $url"
      _        <- (response.status == 200) ?| s"error.signature.on.download.url.status $url ${response.status}"
    } yield {
      handle_result(response)
    }
  }

  private[this] def handle_result(response: WSResponse): Map[String, ByteString] = {
    response.contentType match {
      case "application/pdf" => Map(extractPdfName(response) -> response.bodyAsBytes)
      case "application/zip" => ArchiveHelper.unzip(response.bodyAsBytes)
    }
  }

  private[this] def extractPdfName(response: WSResponse): String = {
    response.header("Content-Disposition").flatMap(_.split("filename=").lift(1)).getOrElse("document.pdf")
  }
}
