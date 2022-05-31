package new_pricer.services

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import new_pricer.new_vertical.{ NewPricerNewVerticalBusinessRequirement, TransformData }
import new_pricer.new_vertical.models.{ NewPricerConfig, NewPricerRequest, QuoteData, SelectData }

import javax.inject.{ Inject, Singleton }
import play.api.{ Configuration, Logging }

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import play.api.libs.ws.WSClient

@Singleton
private[new_pricer] final class NewPricerService @Inject() (
  val ws:     WSClient,
  val config: Configuration
)(implicit
  val ec:     ExecutionContext
) extends JsonParser
    with Sorus
    with Logging {

  private[this] val url = config.get[String]("new_pricer.url")

  /**
   * THIS IS AN EXAMPLE, REPLACE WITH YOUR VALUE IF NEEDED.
   * For the quote we can imagine this is a simple post and you do not need to check business requirement.
   */
  private[new_pricer] def quote(request: NewPricerRequest, config: NewPricerConfig): Future[Fail \/ PricerResponse] = {
    for {
      data_transformed        <- TransformData.merge[QuoteData](request.quote_data, config) ?| ()
      request                 <- ws.url(url).post(data_transformed)                         ?| "impossible to get price"
      response: PricerResponse = ???
    } yield response // <- you have to create an object which parse response from request and return Fail \/ PricerResponse
  }

  /**
   * THIS IS AN EXAMPLE, REPLACE WITH YOUR VALUE IF NEEDED.
   * Sometime you need data from quote and sometime no. If you do not need it, you can delete quote data in this method.
   */
  private[new_pricer] def select(
    data:           NewPricerRequest,
    config:         NewPricerConfig,
    selected_quote: SelectData
  ): Future[Fail \/ Quote] = {
    for {
      _                <- NewPricerNewVerticalBusinessRequirement.check_insee(selected_quote.insee) ?| "error during check requirement"
      data_transformed <- TransformData.merge[SelectData](selected_quote, config)                   ?| ()
      request          <- ws.url(url).post(data_transformed)                                        ?| "impossible to get price"
      response: Quote   = ???
    } yield response // <- you have to create an object which parse response from request and return Fail \/ Quote.
  }
}
