package wakam

import com.typesafe.config.{ Config, ConfigFactory }
import domain.{ Amount, Price, PricerResponse, Quote }
import domain.PricerResponse.Offer
import helpers.sorus.Fail
import org.scalatestplus.play.PlaySpec
import wakam.home.models.{ WakamConfig, WakamQuote, WakamSelectConfig, WakamSubscribe }
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Configuration
import play.api.libs.ws.WSClient
import scalaz.{ \/, \/- }
import test.TestHelper
import wakam.home.services.WakamService

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global

class WakamServiceTest extends PlaySpec with GuiceOneServerPerTest with TestHelper {
  private[this] val wakam_quote: WakamQuote      = WakamQuote(
    "75002",
    "PARIS-2E-ARRONDISSEMENT",
    "Appartement",
    "Locataire",
    "Principale",
    2,
    "Intermédiaire",
    "30",
    "25000",
    "5000",
    "Franchise de Base",
    "Non",
    smartphone_option                   = false,
    "Non",
    "Non",
    ice_breaker_option                  = false,
    nursery_assistant_rc_option         = false,
    animal_rc_option                    = false,
    room_rental_rc_option               = false,
    seasonal_rental_rc_option           = false,
    professional_rc_option              = false,
    non_motorized_travel_rc_option      = false,
    property_damage_option              = false,
    physical_hard_knocks_capital_option = false,
    legal_protection_option             = false,
    hotel_sharing_damage_option         = false,
    tenant_pno_rc_option                = false,
    new_furniture_replacement_option    = false,
    school_insurance_option             = false,
    wine_cellar_option                  = false,
    dependence                          = false,
    "0%"
  )
  private[this] val offer_data: Offer            = Offer(Price(Amount(2013)))
  private[this] val config: Config               = ConfigFactory.load("reference.conf")
  private[this] val configuration: Configuration = Configuration(config)
  private[this] val wakam_config: WakamConfig    = WakamConfig(key = "68392ddb6dab406aa0fd785459f0b298")
  private[this] val wakam_select_config          =
    WakamSelectConfig(key = "f094572ee63a4622a4e8dc294a44f52c", partnership_code = "2666080601")
  val date_formatter: DateTimeFormatter          = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  private[this] val wakam_select   = WakamSubscribe(
    "C000000001",
    "Pas de référence client externe",
    OffsetDateTime.parse("2018-01-01T00:00:00+01:00", date_formatter),
    OffsetDateTime.parse("2018-01-01T00:00:00+01:00", date_formatter),
    "M",
    OffsetDateTime.parse("2018-01-01T00:00:00+01:00", date_formatter),
    "Dupont",
    "Pierre",
    "M.",
    "pierre.dupont@hotmail.fr",
    "FR7630006000011234567890189",
    "CRCAFRPP",
    "12345",
    "Nom de la banque",
    "NOM ASSURÉ",
    "06 01 01 01 01",
    "120 rue Reaumur",
    "Paris",
    "75000",
    List.empty,
    OffsetDateTime.parse("1970-01-01T00:00:00+01:00", date_formatter),
    "Orteaux",
    "rue de France",
    7,
    "1",
    "P0001",
    100,
    "1234",
    "ALLZ04938127",
    OffsetDateTime.parse("2015-01-01T00:00:00+01:00", date_formatter)
  )
  private[this] val selected_quote = Quote("", OffsetDateTime.now(), "", "", "", "", "", "", "", offer_data)

  "WakamService" should {
    "return offer on sending valid quote" in {
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(wakam_quote, wakam_config))
      response.fold(
        fail => fail,
        {
          case offer: Offer   => offer.price mustBe (offer_data.price)
          case other_response => fail(s"unexpected response: $other_response ")
        }
      )
    }

    "return failure with invalid quote data" in {
      val new_quote                        = wakam_quote.copy(postal_code = "1")
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(new_quote, wakam_config))
      response.fold(
        fail => {
          fail.message mustBe ("Les paramétres transmis ne permettent pas d'établir un tarif. Vérifier les modalités transmises.")
        },
        result => result
      )
    }

    "return failure with missing quote data" in {
      val new_quote                        = wakam_quote.copy(postal_code = "")
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(new_quote, wakam_config))
      response.fold(
        fail => {
          fail.message mustBe ("Unprocessable entity")
        },
        result => result
      )
    }
    // ignore
    "return selected quote with valid selection" in {
      val ws: WSClient                = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService =
        new WakamService(ws = ws, config = configuration)
      val response                    = await(wakam_service.select(wakam_select, wakam_select_config, selected_quote))
      response.fold(
        fail => println(s"error in response: ${fail.message}"),
        quote => quote.response.price mustBe (offer_data.price)
      )

    }
    // ignore
    "return selected quote with missing selection data" in {
      val ws: WSClient                = app.injector.instanceOf[WSClient]
      val new_selection               = wakam_select.copy(quote_reference = "")
      val wakam_service: WakamService =
        new WakamService(ws = ws, config = configuration)
      val response                    = await(wakam_service.select(wakam_select, wakam_select_config, selected_quote))
      response.fold(
        fail => fail.message mustBe ("Unprocessable entity"),
        quote => quote
      )

    }
  }

}
