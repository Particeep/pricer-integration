import org.scalatest.matchers.must.Matchers.convertToAnyMustWrapper
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Logging
import utils.DocumentUtils

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class DocumentUtilsTest extends AnyWordSpec with GuiceOneServerPerTest with Logging {

  "DocumentUtilsTest" ignore {

    val doc_pdf_url = "http://document.particeep.com/8f411945-ed53-4f0b-857d-511d9c1a32a8/download"
    val doc_zip_url = "https://test-api.particeep.com/document/f41dcab0-c050-457d-b897-0dbdc2796f94"

    "download pdf signature file" in {
      val signatureUtils   = app.injector.instanceOf[DocumentUtils]
      val docs_f           = signatureUtils.signatureFiles(doc_pdf_url)
      val docs             = Await.result(docs_f, 10.seconds)
      val docs_and_content = docs.toOption.getOrElse(Map.empty)
      docs_and_content.size                                       mustBe 1
      docs_and_content.values.headOption.map(_.size).getOrElse(0) mustBe 111028
    }

    "download pdf signature file to base64" in {
      val signatureUtils   = app.injector.instanceOf[DocumentUtils]
      val docs_f           = signatureUtils.signatureBase64(doc_pdf_url)
      val docs             = Await.result(docs_f, 10.seconds)
      val docs_and_content = docs.toOption.getOrElse(Map.empty)
      docs_and_content.size                                       mustBe 1
      docs_and_content.values.headOption.map(_.size).getOrElse(0) mustBe 148040
    }

    "download and unzip signature files" in {
      val signatureUtils   = app.injector.instanceOf[DocumentUtils]
      val docs_f           = signatureUtils.signatureFiles(doc_zip_url)
      val docs             = Await.result(docs_f, 10.seconds)
      val docs_and_content = docs.toOption.getOrElse(Map.empty)
      docs_and_content.size                                     mustBe 3
      docs_and_content.get("doc1.pdf").map(_.size).getOrElse(0) mustBe 140844
      docs_and_content.get("doc2.pdf").map(_.size).getOrElse(0) mustBe 154941
      docs_and_content.get("doc3.pdf").map(_.size).getOrElse(0) mustBe 363619
    }

    "download and unzip signature files to base64" in {
      val zipUtils         = app.injector.instanceOf[DocumentUtils]
      val docs_f           = zipUtils.signatureBase64(doc_zip_url)
      val docs             = Await.result(docs_f, 10.seconds)
      val docs_and_content = docs.toOption.getOrElse(Map.empty)
      docs_and_content.size                                     mustBe 3
      docs_and_content.get("doc1.pdf").map(_.size).getOrElse(0) mustBe 187792
      docs_and_content.get("doc2.pdf").map(_.size).getOrElse(0) mustBe 206588
      docs_and_content.get("doc3.pdf").map(_.size).getOrElse(0) mustBe 484828
    }
  }
}
