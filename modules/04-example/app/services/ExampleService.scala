package example.services

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import example.new_vertical.{ ExampleNewVerticalBusinessRequirement, TransformData }
import example.new_vertical.models.{ ExampleConfig, ExampleRequest, QuoteData, SelectData }

import javax.inject.{ Inject, Singleton }
import play.api.{ Configuration, Logging }

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import play.api.libs.ws.WSClient

@Singleton
private[example] final class ExampleService @Inject() (
  val ws:     WSClient,
  val config: Configuration
)(implicit
  val ec:     ExecutionContext
) extends JsonParser
    with Sorus
    with Logging {

  private[this] val url = config.get[String]("example.url")

  /**
   * For the quote we can imagine this is a simple post and you do not need to check business requirement.
   */
  private[example] def quote(request: ExampleRequest, config: ExampleConfig): Future[Fail \/ PricerResponse] = {
    for {
      data_transformed        <- TransformData.merge[QuoteData](request.quote_data, config) ?| ()
      request                 <- ws.url(url).post(data_transformed)                         ?| "impossible to get price"
      response: PricerResponse = ???
    } yield response // <- you can have an object which parse response from request and return Fail \/ PricerResponse
  }

  /**
   * Sometime you need data from quote and sometime no. If you do not need it, you can delete quote data in this method.
   */
  private[example] def select(
    data:           ExampleRequest,
    config:         ExampleConfig,
    selected_quote: SelectData
  ): Future[Fail \/ Quote] = {
    for {
      _                <- ExampleNewVerticalBusinessRequirement.check_insee(selected_quote.insee) ?| "error during check requirement"
      data_transformed <- TransformData.merge[SelectData](selected_quote, config)                 ?| ()
      request          <- ws.url(url).post(data_transformed)                                      ?| "impossible to do select"
      response: Quote   = ??? // <- you can have an object which parse response from request and return Fail \/ Quote.
    } yield response
  }
}
