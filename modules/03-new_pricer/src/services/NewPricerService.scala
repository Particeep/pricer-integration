package newpricer.services

import newpricer.models._
import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus

import javax.inject.{ Inject, Singleton }
import play.api.{ Configuration, Logging }
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import play.api.libs.ws.WSClient

@Singleton
class NewPricerService @Inject() (
  val ws:     WSClient,
  val config: Configuration
)(implicit
  val ec:     ExecutionContext
) extends JsonParser
    with Sorus
    with Logging {

  private[newpricer] def quote(
    request: NewPricerRequest,
    config:  NewPricerConfig
  ): Future[Fail \/ PricerResponse] = {
    ???
  }

  private[newpricer] def select(
    request:        NewPricerRequest,
    config:         NewPricerConfig,
    selected_quote: Quote
  ): Future[Fail \/ Quote] = {
    ???
  }
}
