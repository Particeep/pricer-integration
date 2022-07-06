package newpricer.services

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import newpricer.models.{ NewPricerConfig, NewPricerQuoteRequest, NewPricerSelectRequest }
import play.api.libs.ws.WSClient
import play.api.{ Configuration, Logging }
import scalaz.\/

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
private[newpricer] class NewPricerService @Inject() (
  val ws:          WSClient,
  val config:      Configuration
)(implicit val ec: ExecutionContext)
  extends JsonParser
    with Sorus
    with Logging {

  /**
   * @param request : input for the webservice
   * @param config : broker authentication
   * @return
   */
  private[newpricer] def quote(
    request: NewPricerQuoteRequest,
    config:  NewPricerConfig
  ): Future[Fail \/ PricerResponse] = {
    ???
  }

  /**
   * @param request : same structure as request: NewPricerQuoteRequest but with more data
   * @param config : broker authentication
   * @param selected_quote : the result of the call to quote
   */
  private[newpricer] def select(
    request:        NewPricerSelectRequest,
    config:         NewPricerConfig,
    selected_quote: Quote
  ): Future[Fail \/ Quote] = {
    ???
  }
}
