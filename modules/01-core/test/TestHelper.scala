package test

import scala.concurrent._
import scala.concurrent.duration.Duration

trait TestHelper extends JsonComparison {
  def await[A](some_f: Future[A]): A = Await.result(some_f, Duration.Inf)

  def loadResource(filename: String): String = {
    val source = scala.io.Source.fromURL(getClass.getResource(filename))
    try source.mkString
    finally source.close()
  }

}
