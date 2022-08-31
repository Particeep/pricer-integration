package domain

import helpers.sorus.Fail
import play.api.libs.json.JsValue
import scalaz.\/

import scala.concurrent.Future

trait PricerService {

  def input_format(pricer_id: String): List[InputFormat]

  def quote(wakam_quote_config: Option[JsValue])(
    pricer_id:                  String,
    quote_input:                QuoteInput
  ): Future[Fail \/ PricerResponse]

  def input_select_format(pricer_id: String): List[InputFormat]

  def select(wakam_select_config: Option[JsValue])(
    pricer_id:                    String,
    subscription_input:           SelectSubscriptionInput
  ): Future[Fail \/ SelectSubscriptionOutput]
}
