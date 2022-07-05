package newpricer.services

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import newpricer.models.{ NewPricerConfig, NewPricerRequest, SelectData }
import play.api.libs.ws.WSClient
import play.api.{ Configuration, Logging }
import scalaz.\/

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
private[new_pricer] class NewPricerService @Inject() (
  val ws:          WSClient,
  val config:      Configuration
)(implicit val ec: ExecutionContext)
  extends JsonParser
    with Sorus
    with Logging {

  private[new_pricer] def quote(request: NewPricerRequest, config: NewPricerConfig): Future[Fail \/ PricerResponse] = {
    ???
  }

  private[new_pricer] def select(
    request:        NewPricerRequest,
    config:         NewPricerConfig,
    selected_quote: SelectData
  ): Future[Fail \/ Quote] = {
    ???
  }
}
