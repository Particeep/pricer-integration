package domain

import play.api.i18n.Lang
import play.api.libs.json.JsObject
import utils.m

import java.time.OffsetDateTime

import com.ibm.icu.util.Currency

sealed trait PricerResponse extends Product with Serializable {
  def translate()(implicit lang: Lang): PricerResponse
}
object PricerResponse {

  case class PricerError(message: String, args: List[String] = List.empty) extends PricerResponse {
    def translate()(implicit lang: Lang): PricerError = {
      PricerError(m(message, args))
    }
  }

  case class Decline(url: URL, meta: Option[Meta] = None) extends PricerResponse {
    def translate()(implicit lang: Lang): Decline = {
      Decline(url, meta.map(_.translate()))
    }
  }

  case class NeedMoreData(url: URL, meta: Option[Meta] = None) extends PricerResponse {
    def translate()(implicit lang: Lang): NeedMoreData = {
      NeedMoreData(url, meta.map(_.translate()))
    }
  }

  /**
   * @param price : the price returned by the insurer
   * @param detail : a list of items related to insurance coverage
   * @param internal_data : custom & complex data to display, such as payment schedule
   * @param external_data : data to carry to other requests on external pricer
   * @param meta : marketing data about the offer
   */
  case class Offer(
    price:         Price,
    detail:        List[OfferItem]      = List(),
    internal_data: Option[InternalData] = None,
    external_data: Option[JsObject]     = None,
    meta:          Option[Meta]         = None
  ) extends PricerResponse {
    def translate()(implicit lang: Lang): Offer = {
      Offer(
        price,
        detail.map(_.translate()),
        internal_data,
        external_data,
        meta.map(_.translate())
      )
    }
  }

  /**
   * Store offer details like TAEG / options / rate / contract / documentation / etc...
   * This information would be provided in the documentation
   *
   * @param kind: image | text | pdf | link | etc...
   */
  case class OfferItem(label: String, value: String, kind: String, args: List[String] = List.empty) {
    def translate()(implicit lang: Lang): OfferItem = kind match {
      case "text" => OfferItem(m(label, args), m(value), kind)
      case _      => OfferItem(m(label, args), value, kind)
    }
  }
}

case class Price(
  amount_ht:   Amount,
  owner_fees:  Amount    = Amount(0),
  broker_fees: Amount    = Amount(0),
  taxes:       Amount    = Amount(0),
  currency:    Currency  = Currency.getInstance("EUR"),
  frequency:   Frequency = Frequency.ONCE
)
case class PaymentData(
  amount_to_pay: Option[Amount]         = None,
  frequency:     Option[Frequency]      = None,
  start_at:      Option[OffsetDateTime] = None,
  end_at:        Option[OffsetDateTime] = None
)
case class InternalData(
  payment_data: Option[PaymentData] = None,
  display_data: Option[JsObject]    = None,
  pricer_id:    Option[String]      = None
)
