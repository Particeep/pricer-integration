package wakam

import domain.PricerResponse.Offer
import domain.{ Amount, Price, PricerResponse, Quote }

import helpers.sorus.Fail
import play.api.Configuration
import play.api.libs.ws.{ WSClient, WSResponse }
import scalaz.{ -\/, \/, \/- }
import test.TestHelper
import utils.TimeUtils
import wakam.home.models.{ WakamJsonParser, WakamQuote, WakamQuoteConfig, WakamSelectConfig, WakamSubscribe }
import wakam.home.services.WakamService

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

import scala.concurrent.ExecutionContext.Implicits.global

import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest

import com.typesafe.config.{ Config, ConfigFactory }

class WakamServiceTest extends PlaySpec with GuiceOneServerPerTest with TestHelper with WakamJsonParser {
  private[this] val wakam_quote: WakamQuote                = WakamQuote(
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
  private[this] val offer_data: Offer                      = Offer(Price(Amount(2013)))
  private[this] val config: Config                         = ConfigFactory.load("reference.conf")
  private[this] val configuration: Configuration           = Configuration(config)
  private[this] val wakam_quote_config: WakamQuoteConfig   = WakamQuoteConfig(key = "68392ddb6dab406aa0fd785459f0b298")
  private[this] val wakam_select_config: WakamSelectConfig =
    WakamSelectConfig(key = "f094572ee63a4622a4e8dc294a44f52c", partnership_code = "2666080601")
  val date_formatter: DateTimeFormatter                    = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  private[this] val wakam_select: WakamSubscribe     = WakamSubscribe(
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
  private[this] val selected_quote: Quote            = Quote("", TimeUtils.now(), "", "", "", "", "", "", "", offer_data)
  private[this] val offer_mock: Offer                = mock[Offer]
  private[this] val wakam_service_mock: WakamService = mock[WakamService]
  private[this] val response: WSResponse             = mock[WSResponse]
  private[this] val quote: Quote                     = mock[Quote]

  "WakamService" should {
    "return offer on sending valid quote" in {
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(wakam_quote, wakam_quote_config))
      response.fold(
        error => fail(s"error in wakam response: ${error.message}"),
        {
          case offer: Offer   => offer.price mustBe offer_data.price
          case other_response => fail(s"unexpected response: $other_response ")
        }
      )
    }

    "return failure with invalid quote data" in {
      val new_quote                        = wakam_quote.copy(postal_code = "1")
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(new_quote, wakam_quote_config))
      response.fold(
        fail => {
          fail.message contains "Les paramétres transmis ne permettent pas d'établir un tarif. Vérifier les modalités transmises."
        },
        _ => succeed
      )
    }

    "return failure with missing quote data" in {
      val new_quote                        = wakam_quote.copy(postal_code = "")
      val ws: WSClient                     = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService      =
        new WakamService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse = await(wakam_service.quote(new_quote, wakam_quote_config))
      response.fold(
        fail => {
          fail.message contains "Unprocessable entity"
        },
        _ => succeed
      )
    }

    "parse wakam quote api response and return an offer" in {
      when(wakam_service_mock.parse_wakam_quote_response(response)).thenReturn(\/-(offer_mock))
      val offer_returned = wakam_service_mock.parse_wakam_quote_response(response)
      offer_returned.fold(
        error => fail(s"error in parsing the response: ${error.message}"),
        offer => offer mustBe offer_mock
      )
    }

    "parse wakam quote api response and return an error" in {
      when(wakam_service_mock.parse_wakam_quote_response(response)).thenReturn(-\/(Fail("Unprocessable Entity")))
      val offer_returned = wakam_service_mock.parse_wakam_quote_response(response)
      offer_returned.fold(
        error => error.message mustBe "Unprocessable Entity",
        _ => succeed
      )
    }

    "parse wakam select api response and return selected quote" in {
      when(wakam_service_mock.parse_wakam_select_response(response, quote)).thenReturn(\/-(quote))
      val result = wakam_service_mock.parse_wakam_select_response(response, quote)
      result.fold(
        error => fail(s"error in parsing wakam select api response: ${error.message}"),
        returned_quote => returned_quote mustBe quote
      )
    }

    "parse wakam select api response and return an error" in {
      when(wakam_service_mock.parse_wakam_select_response(response, quote)).thenReturn(
        -\/(Fail("Unprocessable Entity"))
      )
      val result = wakam_service_mock.parse_wakam_select_response(response, quote)
      result.fold(
        error => error.message mustBe "Unprocessable Entity",
        _ => succeed
      )
    }

    "return selected quote with valid selection" ignore {
      val ws: WSClient                = app.injector.instanceOf[WSClient]
      val wakam_service: WakamService =
        new WakamService(ws = ws, config = configuration)
      val response                    = await(wakam_service.select(wakam_select, wakam_select_config, selected_quote))
      response.fold(
        error => fail(s"error in response: ${error.message}"),
        quote => quote.response.price mustBe offer_data.price
      )

    }

    "return selected quote with missing selection data" ignore {
      val ws: WSClient                = app.injector.instanceOf[WSClient]
      val new_selection               = wakam_select.copy(quote_reference = "")
      val wakam_service: WakamService =
        new WakamService(ws = ws, config = configuration)
      val response                    = await(wakam_service.select(new_selection, wakam_select_config, selected_quote))
      response.fold(
        fail => fail.message mustBe "Unprocessable entity",
        _ => succeed
      )

    }
  }

}
