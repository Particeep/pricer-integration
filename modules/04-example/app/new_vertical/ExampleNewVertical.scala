package example.new_vertical

import domain._
import example.new_vertical.models.{ ExampleConfig, ExampleRequest }
import example.services.ExampleService
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import play.api.Logging
import play.api.libs.json._
import scalaz.\/

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future

@Singleton
class ExampleNewVertical @Inject() (
  service: ExampleService
) extends PricerService
    with Sorus
    with Logging {

  def input_format(pricer_id: String): List[InputFormat] = {
    InputFormatFactoryExampleNewVertical.input_format
  }

  /**
   * Replace with your value, that is just an example
   */
  def quote(broker_config: Option[JsValue])(
    pricer_id:             String,
    quote_input:           QuoteInput
  ): Future[Fail \/ PricerResponse] = {
    logger.info(s"[example] [quote] The pricer request is  ${(quote_input.input_json)}")
    for {
      pricer_request <- quote_input.input_json.validate[ExampleRequest] ?| ()
      auth           <- broker_config                                   ?| "error.broker.login.required" // defined in I18n in the directory newpricer
      broker_config  <- auth.validate[ExampleConfig]                    ?| ()
      result         <- service.quote(pricer_request, broker_config)    ?| ()
    } yield {
      result
    }
  }

  /**
   * Replace with your value, that is just an example
   */
  def select(broker_config: Option[JsValue])(
    pricer_id:              String,
    subscription_input:     SelectSubscriptionInput
  ): Future[Fail \/ SelectSubscriptionOutput] = {
    logger.info(s"[example] [select] The pricer request is  ${(subscription_input.data)}")
    for {
      pricer_request <- subscription_input.data.validate[ExampleRequest] ?| ()
      select_data    <- pricer_request.select_data                       ?| "select data for new pricer newvertical is empty !"
      auth           <- broker_config                                    ?| "error.broker.login.required"
      broker_config  <- auth.validate[ExampleConfig]                     ?| ()
      updated_quote  <- service.select(
                          pricer_request,
                          broker_config,
                          select_data // put select directly avoid to carry option during service part
                        ) ?| ()
    } yield {
      SelectSubscriptionOutput("200", updated_quote)
    }
  }
}
