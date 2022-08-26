package wakam.home

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus

import javax.inject.{ Inject, Singleton }
import models.{ WakamSubscribe, _ }
import wakam.home.services.WakamService
import play.api.Logging
import play.api.libs.json._
import scalaz.\/

import scala.concurrent.Future

@Singleton
class WakamHome @Inject() (
  service: WakamService
) extends PricerService
    with Sorus
    with Logging
    with WakamJsonParser {

  override def input_format(pricer_id: String): List[InputFormat] = {
    InputFormatFactory.input_format_quote
  }

  override def quote(broker_config: Option[JsValue])(
    pricer_id:                      String,
    quote_input:                    QuoteInput
  ): Future[Fail \/ PricerResponse] = {
    for {
      pricer_request <- quote_input.input_json.validate[WakamQuote]  ?| ()
      auth           <- broker_config                                ?| "error.broker.login.required"
      broker_config  <- auth.validate[WakamConfig]                   ?| ()
      result         <- service.quote(pricer_request, broker_config) ?| ()
    } yield {
      result
    }
  }

  override def input_select_format(pricer_id: String): List[InputFormat] = {
    InputFormatFactory.input_format_select
  }

  override def select(broker_config: Option[JsValue])(
    pricer_id:                       String,
    subscription_input:              SelectSubscriptionInput
  ): Future[Fail \/ SelectSubscriptionOutput] = {
    for {
      pricer_request <- subscription_input.data.validate[WakamSubscribe]                                 ?| ()
      auth           <- broker_config                                                                    ?| "error.broker.login.required"
      broker_config  <- auth.validate[WakamConfig]                                                       ?| ()
      updated_quote  <- service.select(pricer_request, broker_config, subscription_input.selected_quote) ?| ()
    } yield {
      SelectSubscriptionOutput("200", updated_quote)
    }
  }
}