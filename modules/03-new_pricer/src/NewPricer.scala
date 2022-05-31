package newpricer

import newpricer.models._
import newpricer.services.NewPricerService
import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import play.api.Logging
import play.api.libs.json._
import scalaz.\/

import javax.inject.{ Inject, Singleton }
import scala.concurrent.Future

@Singleton
class NewPricer @Inject() (
  service: NewPricerService
) extends PricerService
    with Sorus
    with Logging
    with NewPricerJsonParser {

  def input_format(pricer_id: String): List[InputFormat] = {
    InputFormatFactory.input_format_quote
  }

  def quote(broker_config: Option[JsValue])(
    pricer_id:             String,
    quote_input:           QuoteInput
  ): Future[Fail \/ PricerResponse] = {
    logger.info(s"[newPricer] [quote] The pricer request is  ${(quote_input.input_json)}")
    for {
      pricer_request <- quote_input.input_json.validate[NewPricerRequest] ?| ()
      auth           <- broker_config                                     ?| "error.broker.login.required" // defined in I18n
      //auth           <- broker_config                                     ?| "broker config is empty for pricer newPricer" // error custom
      broker_config  <- auth.validate[NewPricerConfig]                    ?| ()
      result         <- service.quote(pricer_request, broker_config)      ?| ()
    } yield {
      result
    }
  }

  def input_format_select(pricer_id: String): List[InputFormat] = {
    InputFormatFactory.input_format_select
  }

  def select(broker_config: Option[JsValue])(
    pricer_id:              String,
    subscription_input:     SelectSubscriptionInput
  ): Future[Fail \/ SelectSubscriptionOutput] = {
    for {
      pricer_request <- subscription_input.data.validate[NewPricerRequest] ?| ()
      auth           <- broker_config                                      ?| "error.broker.login.required"
      broker_config  <- auth.validate[NewPricerConfig]                     ?| ()
      updated_quote  <- service.select(
                          pricer_request,
                          broker_config,
                          subscription_input.selected_quote
                        ) ?| ()
    } yield {
      SelectSubscriptionOutput("200", updated_quote)
    }
  }
}
