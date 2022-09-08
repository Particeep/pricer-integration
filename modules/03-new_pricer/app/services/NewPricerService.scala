package newpricer.services

import domain.PricerResponse.Offer
import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus

import javax.inject.{ Inject, Singleton }
import play.api.libs.json.{ JsValue, Json }
import play.api.libs.ws.WSClient
import play.api.{ Configuration, Logging }
import scalaz.{ -\/, \/, \/- }
import utils.NumberUtils.amountFromDoubleToCentime
import newpricer.models.NewPricerResponse.SuccessCase
import newpricer.models.{
  NewPricerJsonParser,
  NewPricerQuote,
  NewPricerQuoteConfig,
  NewPricerSelectConfig,
  NewPricerSubscribe,
  Warranty
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

  private[this] def transform_to_offer_items(warranties: List[Warranty]): List[PricerResponse.OfferItem] = {
    warranties.flatMap(_.transform_to_offer_item)
  }

  private[this] def parse_new_pricer_quote_response(response: JsValue): Fail \/ Offer = {
    response.validate[SuccessCase].fold(
      error => -\/(Fail(s"Error in parsing response: ${error.map(_._2.mkString(" "))}")),
      data =>
        \/-(Offer(
          Price(Amount(amountFromDoubleToCentime(PricerBaseCalculator.average_price(
            data.MontantTotalPrimeHT.toDouble,
            12
          )))),
          detail        = transform_to_offer_items(data.Garanties),
          external_data = Some(Json.obj("quote_reference" -> data.QuoteReference))
        ))
    )
  }

  private[this] def check_response_status(status: Int, body: JsValue): Fail \/ Offer = {
    status match {
      case 200             => parse_new_pricer_quote_response(body)
      case 400 | 401 | 524 => -\/(Fail(s"Error from wakam api with status: $status and body: $body"))
      case _               => -\/(Fail(s"Unexpected Error with status: $status and body: $body"))
    }
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
      offer    <- check_response_status(response.status, response.json) ?| ()
    } yield {
      offer
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
