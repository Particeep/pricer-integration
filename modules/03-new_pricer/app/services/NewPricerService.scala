package newpricer.services

import domain.PricerResponse.Offer
import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus

import javax.inject.{ Inject, Singleton }
import play.api.libs.json.{ JsError, JsSuccess, Json }
import play.api.libs.ws.{ WSClient, WSResponse }
import play.api.{ Configuration, Logging }
import scalaz.{ -\/, \/, \/- }
import utils.NumberUtils.amountFromDoubleToCentime
import newpricer.models.NewPricerResponse.{ FailureCase, SuccessCase }
import newpricer.models.{
  NewPricerJsonParser,
  NewPricerQuote,
  NewPricerQuoteConfig,
  NewPricerSelectConfig,
  NewPricerSubscribe
}

import scala.concurrent.{ ExecutionContext, Future }

@Singleton
private[newpricer] class NewPricerService @Inject() (
  val ws:          WSClient,
  val config:      Configuration
)(implicit val ec: ExecutionContext)
  extends JsonParser
    with Sorus
    with Logging
    with NewPricerJsonParser {

  private[this] val new_pricer_url = config.get[String]("new_pricer.url")

  private[newpricer] def parse_new_pricer_quote_response(response: SuccessCase): Offer = {
    Offer(
      Price(Amount(amountFromDoubleToCentime(PricerBaseCalculator.average_price(
        response.MontantTotalPrimeTTC.toDouble,
        12
      )))),
      external_data = Some(Json.obj("quote_reference" -> response.QuoteReference))
    )
  }

  /**
   * @param request : input for the webservice
   * @param config : broker authentication
   * @return
   */
  private[newpricer] def quote(
    request: NewPricerQuote,
    config:  NewPricerQuoteConfig
  ): Future[Fail \/ PricerResponse] = {
    for {
      response <- ws.url(s"$new_pricer_url/getPrice").addHttpHeaders("Ocp-Apim-Subscription-Key" -> config.key).post(
                    Json.toJson(request)(new_pricer_quote_write)
                  ) ?| ()
      result   <- response.json.validate[SuccessCase] ?| s"error in parsing response from api: ${response.body}"
    } yield {
      parse_new_pricer_quote_response(result)
    }
  }

  /**
   * @param request : input for the webservice
   * @param config : broker authentication
   * @param selected_quote : the result of the call to quote
   */
  private[newpricer] def select(
    request:        NewPricerSubscribe,
    config:         NewPricerSelectConfig,
    selected_quote: Quote
  ): Future[Fail \/ Quote] = {
    for {
      response <- ws.url(s"$new_pricer_url/subscribe").addHttpHeaders(
                    "Ocp-Apim-Subscription-Key" -> config.key,
                    "PartnershipCode"           -> config.partnership_code
                  ).post(
                    Json.toJson(request)(new_pricer_subscribe_write)
                  ) ?| ()
      _        <-
        (response.status == 200) ?| s"new pricer returned an error with status ${response.status} and body ${response.body}"
    } yield {
      selected_quote
    }
  }
}
