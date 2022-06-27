package new_pricer.services

import new_pricer.new_vertical.models._
import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus

import javax.inject.{ Inject, Singleton }
import play.api.{ Configuration, Logging }
import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import play.api.libs.ws.WSClient

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
