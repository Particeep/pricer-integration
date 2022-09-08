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
import newpricer.models.enumerations._
import org.scalatest.PrivateMethodTester
import org.scalatestplus.mockito.MockitoSugar.mock
import play.api.libs.json.Json

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
    OccupationStatus.TENANT,
    "Principale",
    2,
    Stage.INTERMEDIATE,
    "30",
    "25000",
    "5000",
    Deductible.BASE_DEDUCTIBLE,
    NomadicBusinessOption.NO,
    smartphone_option                   = false,
    ComputerOption.NO,
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
    CommercialLatitudeRequested.`0%`
  )
  private[this] val offer_data: Offer                               = Offer(Price(Amount(1776)))
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
    Title.MR,
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
    Quality.`1`,
    "P0001",
    100,
    "1234",
    "ALLZ04938127",
    OffsetDateTime.parse("2015-01-01T00:00:00+01:00", date_formatter)
  )
  private[this] val selected_quote: Quote                 = Quote("", TimeUtils.now(), "", "", "", "", "", "", "", offer_data)
  private[this] val get_price_api_success_response        = Json.parse(
    """{
      |  "statusCode": 200,
      |  "message": "OK",
      |  "MontantTotalPrimeHT": "213.16",
      |  "MontantTotalCommissions": "42.31",
      |  "MontantTotalTaxes": "28.43",
      |  "MontantTotalPrimeTTC": "241.59",
      |  "CoefficientLatitude": "0.00",
      |  "TauxCommissionnementPartenariat": "0.21",
      |  "Garanties": [
      |    {
      |      "CodeGarantie": "INC",
      |      "MontantPrimeHT": "15.93",
      |      "MontantTotalCommissions": "3.35",
      |      "MontantTotalTaxes": "4.78",
      |      "MontantPrimeTTC": "20.71",
      |      "Plafond": "25000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "TEMP",
      |      "MontantPrimeHT": "5.36",
      |      "MontantTotalCommissions": "1.13",
      |      "MontantTotalTaxes": "0.48",
      |      "MontantPrimeTTC": "5.84",
      |      "Plafond": "25000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "DDE",
      |      "MontantPrimeHT": "90.80",
      |      "MontantTotalCommissions": "19.07",
      |      "MontantTotalTaxes": "8.17",
      |      "MontantPrimeTTC": "98.97",
      |      "Plafond": "25000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "VLX",
      |      "MontantPrimeHT": "32.41",
      |      "MontantTotalCommissions": "6.81",
      |      "MontantTotalTaxes": "2.92",
      |      "MontantPrimeTTC": "35.33",
      |      "Plafond": "8500.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "RCX",
      |      "MontantPrimeHT": "8.32",
      |      "MontantTotalCommissions": "1.75",
      |      "MontantTotalTaxes": "0.75",
      |      "MontantPrimeTTC": "9.07",
      |      "Plafond": "6 300 000€ par sinistre dans la limite de 900 000€ pour les dommages matériels et immatériels consécutifs",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "CATNAT",
      |      "MontantPrimeHT": "13.98",
      |      "MontantTotalCommissions": "1.12",
      |      "MontantTotalTaxes": "1.26",
      |      "MontantPrimeTTC": "15.24",
      |      "Plafond": "25000.00",
      |      "Franchise": "Franchise légale"
      |    },
      |    {
      |      "CodeGarantie": "CATECH",
      |      "MontantPrimeHT": "3.76",
      |      "MontantTotalCommissions": "0.79",
      |      "MontantTotalTaxes": "0.34",
      |      "MontantPrimeTTC": "4.10",
      |      "Plafond": "25000.00",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "DRX",
      |      "MontantPrimeHT": "2.78",
      |      "MontantTotalCommissions": "0.58",
      |      "MontantTotalTaxes": "0.25",
      |      "MontantPrimeTTC": "3.03",
      |      "Plafond": "20 000€ dans les limites et conditions prévues aux Conditions générales",
      |      "Franchise": "Seuil d'intervention de 300€ HT par litiges"
      |    },
      |    {
      |      "CodeGarantie": "ASS",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Selon convention",
      |      "Franchise": "Selon convention"
      |    },
      |    {
      |      "CodeGarantie": "GAREAT",
      |      "MontantPrimeHT": "3.12",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.28",
      |      "MontantPrimeTTC": "3.40",
      |      "Plafond": "25000.00",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "ATTEN",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "5.90",
      |      "MontantPrimeTTC": "5.90",
      |      "Plafond": "15000.00",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "ANO",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "2500.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "PHONE",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "1000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "ORDI",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "1200.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "DOMX",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "15000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "BDG",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Valeur du matériel de remplacement",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "ASSMAT",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "6 300 000€ par sinistre \ndans la limite de \n900 000€ pour les dommages matériels et immatériels consécutifs",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "RCLOCSAL",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "6 300 000€ par sinistre \ndans la limite de \n900 000€ pour les dommages matériels et immatériels consécutifs",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "RCLOCSAIS",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "6 300 000€ par sinistre \ndans la limite de \n900 000€ pour les dommages matériels et immatériels consécutifs",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "RCP",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "DOM",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "RCMOB",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "6 300 000€ par sinistre dans la limite de 900 000€ pour les dommages matériels et immatériels consécutifs",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "REMPMOB",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "8500.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "RCS",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Limites et conditions prévues aux Conditions générales",
      |      "Franchise": "Franchises prévues aux Conditions générales"
      |    },
      |    {
      |      "CodeGarantie": "CAVIN",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Valeur de remplacement",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "CYB",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "DPER",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "STOCK",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "OBJVAL",
      |      "MontantPrimeHT": "36.70",
      |      "MontantTotalCommissions": "7.71",
      |      "MontantTotalTaxes": "3.30",
      |      "MontantPrimeTTC": "40.00",
      |      "Plafond": "5000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "ANIDOM",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "230000.00",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "IA",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "PJ",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "20 000€ dans les limites et conditions prévues à la Convention Protection juridique",
      |      "Franchise": "Seuil d'intervention 350€ TTC par litiges dans les limites et conditions prévues à la Convention Protection juridique"
      |    },
      |    {
      |      "CodeGarantie": "DEMRC",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "SHAR",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Valeur de remplacement",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "PNOLOC",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "A venir",
      |      "Franchise": "A venir"
      |    },
      |    {
      |      "CodeGarantie": "DEP",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "Matériel contenu : 1 500€",
      |      "Franchise": "150.00"
      |    },
      |    {
      |      "CodeGarantie": "FRPAIE",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "0.00",
      |      "Franchise": "0.00"
      |    },
      |    {
      |      "CodeGarantie": "FRSIGN",
      |      "MontantPrimeHT": "0.00",
      |      "MontantTotalCommissions": "0.00",
      |      "MontantTotalTaxes": "0.00",
      |      "MontantPrimeTTC": "0.00",
      |      "Plafond": "0.00",
      |      "Franchise": "0.00"
      |    }
      |  ],
      |  "QuoteReference": "00000000-0000-0000-0000-000000000000"
      |}""".stripMargin
  )
  private[this] val get_price_api_bad_request__response   = Json.parse(
    """{"statusCode":400,"message":"Les paramétres transmis ne permettent pas d'établir un tarif. Vérifier les modalités transmises."}"""
  )
  private[this] val unauthorized_response                 = Json.parse(
    """{ "statusCode": 401, "message": "Access denied due to missing subscription key. Make sure to include subscription key when making requests to an API." }"""
  )
  private[this] val unexpected_error_response             =
    Json.parse("""{"MissingParameters":["CodePostal"],"statusCode":422,"message":"Unprocessable entity"}""")

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
          fail.message must include(
            "Les paramétres transmis ne permettent pas d'établir un tarif. Vérifier les modalités transmises."
          )
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
          fail.message must include("Unprocessable entity")
        },
        result => fail(s"unexpected response: $result")
      )
    }

    "return offer with valid json response" in {
      val ws: WSClient                         = mock[WSClient]
      val config                               = mock[Configuration]
      val new_pricer_service: NewPricerService = new NewPricerService(ws = ws, config = config)
      val parse_response                       = PrivateMethod[\/-[Offer]](Symbol("check_response_status"))
      val offer: \/-[Offer]                    = new_pricer_service invokePrivate parse_response(200, get_price_api_success_response)
      offer.b.price mustBe offer_data.price
    }

    "return bad request error with 400 status code " in {
      val ws: WSClient                         = mock[WSClient]
      val config                               = mock[Configuration]
      val new_pricer_service: NewPricerService = new NewPricerService(ws = ws, config = config)
      val parse_response                       = PrivateMethod[-\/[Fail]](Symbol("check_response_status"))
      val error                                = new_pricer_service invokePrivate parse_response(400, get_price_api_bad_request__response)
      error.a.message must include(get_price_api_bad_request__response.toString())
    }

    "return unauthorized error with 401 status code " in {
      val ws: WSClient                         = mock[WSClient]
      val config                               = mock[Configuration]
      val new_pricer_service: NewPricerService = new NewPricerService(ws = ws, config = config)
      val parse_response                       = PrivateMethod[-\/[Fail]](Symbol("check_response_status"))
      val error                                = new_pricer_service invokePrivate parse_response(400, unauthorized_response)
      error.a.message must include(unauthorized_response.toString())
    }

    "return unexpected error response" in {
      val ws: WSClient                         = mock[WSClient]
      val config                               = mock[Configuration]
      val new_pricer_service: NewPricerService = new NewPricerService(ws = ws, config = config)
      val parse_response                       = PrivateMethod[-\/[Fail]](Symbol("check_response_status"))
      val error                                = new_pricer_service invokePrivate parse_response(422, unexpected_error_response)
      error.a.message must include(unexpected_error_response.toString())
    }

    //   Test ignored due to invalid api authorization key
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

    //   Test ignored due to invalid api authorization key
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
