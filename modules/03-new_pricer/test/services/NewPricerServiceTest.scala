package newpricer

import domain.PricerResponse.Offer
import domain.{ Amount, Price, PricerResponse, Quote }
import helpers.sorus.Fail
import play.api.Configuration
import play.api.libs.ws.WSClient
import scalaz.{ -\/, \/, \/- }
import test.TestHelper
import utils.TimeUtils
import newpricer.models._
import newpricer.services.NewPricerService

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import com.typesafe.config.{ Config, ConfigFactory }
import newpricer.models.NewPricerResponse.SuccessCase
import org.scalatest.PrivateMethodTester
import org.scalatestplus.mockito.MockitoSugar.mock

class NewPricerServiceTest
  extends PlaySpec
    with GuiceOneServerPerTest
    with TestHelper
    with NewPricerJsonParser
    with PrivateMethodTester {
  private[this] val new_pricer_quote: NewPricerQuote                = NewPricerQuote(
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
  private[this] val offer_data: Offer                               = Offer(Price(Amount(2013)))
  private[this] val config: Config                                  = ConfigFactory.load("reference.conf")
  private[this] val configuration: Configuration                    = Configuration(config)
  private[this] val new_pricer_quote_config: NewPricerQuoteConfig   =
    NewPricerQuoteConfig(key = "68392ddb6dab406aa0fd785459f0b298")
  private[this] val new_pricer_select_config: NewPricerSelectConfig =
    NewPricerSelectConfig(key = "f094572ee63a4622a4e8dc294a44f52c", partnership_code = "2666080601")
  val date_formatter: DateTimeFormatter                             = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  private[this] val new_pricer_select: NewPricerSubscribe = NewPricerSubscribe(
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
  private[this] val selected_quote: Quote                 = Quote("", TimeUtils.now(), "", "", "", "", "", "", "", offer_data)

  "NewPricerService" should {
    "return offer on sending valid quote" in {
      val ws: WSClient                         = app.injector.instanceOf[WSClient]
      val new_pricer_service: NewPricerService =
        new NewPricerService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse     = await(new_pricer_service.quote(new_pricer_quote, new_pricer_quote_config))
      response match {
        case \/-(Offer(price, _, _, _, _)) => price mustBe offer_data.price
        case error                         => fail(s"unexpected error: $error")
      }
    }

    "return failure with invalid quote data" in {
      val new_quote                            = new_pricer_quote.copy(postal_code = "1")
      val ws: WSClient                         = app.injector.instanceOf[WSClient]
      val new_pricer_service: NewPricerService =
        new NewPricerService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse     = await(new_pricer_service.quote(new_quote, new_pricer_quote_config))
      response.fold(
        fail => {
          fail.message contains "Les paramétres transmis ne permettent pas d'établir un tarif. Vérifier les modalités transmises."
        },
        result => fail(s"unexpected response: $result")
      )
    }

    "return failure with missing quote data" in {
      val new_quote                            = new_pricer_quote.copy(postal_code = "")
      val ws: WSClient                         = app.injector.instanceOf[WSClient]
      val new_pricer_service: NewPricerService =
        new NewPricerService(ws = ws, config = configuration)
      val response: Fail \/ PricerResponse     = await(new_pricer_service.quote(new_quote, new_pricer_quote_config))
      response.fold(
        fail => {
          fail.message contains "Unprocessable entity"
        },
        result => fail(s"unexpected response: $result")
      )
    }

    "return offer from parse new pricer quote with valid data" in {
      val ws: WSClient                         = mock[WSClient]
      val config                               = mock[Configuration]
      val new_pricer_service: NewPricerService = new NewPricerService(ws = ws, config = config)
      val parse_response                       = PrivateMethod[Offer](Symbol("parse_new_pricer_quote_response"))
      val api_response                         = SuccessCase("8722b2eb-9069-4e5d-a174-889ee5c7c06c", "OK", "241.59")
      val offer: Offer                         = new_pricer_service invokePrivate parse_response(api_response)
      offer.price mustBe offer_data.price
    }

    "return selected quote with valid selection" ignore {
      val ws: WSClient                         = app.injector.instanceOf[WSClient]
      val new_pricer_service: NewPricerService =
        new NewPricerService(ws = ws, config = configuration)
      val response                             = await(new_pricer_service.select(new_pricer_select, new_pricer_select_config, selected_quote))
      response.fold(
        error => fail(s"error in response: ${error.message}"),
        quote => quote.response.price mustBe offer_data.price
      )

    }

    "return selected quote with missing selection data" ignore {
      val ws: WSClient                         = app.injector.instanceOf[WSClient]
      val new_selection                        = new_pricer_select.copy(quote_reference = "")
      val new_pricer_service: NewPricerService =
        new NewPricerService(ws = ws, config = configuration)
      val response                             = await(new_pricer_service.select(new_selection, new_pricer_select_config, selected_quote))
      response.fold(
        fail => fail.message contains "Unprocessable entity",
        result => fail(s"unexpected response: $result")
      )

    }
  }

}
