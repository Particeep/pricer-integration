package wakam

import com.typesafe.config.ConfigFactory
import domain.PricerResponse
import domain.PricerResponse.Offer
import helpers.sorus.Fail
import org.scalatestplus.play.PlaySpec
import wakam.home.models.{WakamConfig, WakamQuote}
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Configuration
import play.api.libs.ws.WSClient
import scalaz.\/
import test.TestHelper
import wakam.home.services.WakamService

import scala.concurrent.ExecutionContext.Implicits.global

class WakamServiceTest extends PlaySpec with GuiceOneServerPerTest with TestHelper {
  val wakam_quote: WakamQuote = WakamQuote(
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
    smartphone_option = false,
    "Non",
    "Non",
    ice_breaker_option = false,
    nursery_assistant_rc_option = false,
    animal_rc_option = false,
    room_rental_rc_option = false,
    seasonal_rental_rc_option = false,
    professional_rc_option = false,
    non_motorized_travel_rc_option = false,
    property_damage_option = false,
    physical_hard_knocks_capital_option = false,
    legal_protection_option = false,
    hotel_sharing_damage_option = false,
    tenant_pno_rc_option = false,
    new_furniture_replacement_option = false,
    school_insurance_option = false,
    wine_cellar_option = false,
    dependence = false,
   "0%")
  private[this] val config_file = ConfigFactory.load("reference.conf")
  private[this] val configuration = Configuration(config_file)

  "WakamService" should {
    "return offer on sending valid quote" in {
      val ws = app.injector.instanceOf[WSClient]

      val wakam_service = new WakamService(ws = ws, config = configuration)

      val config: WakamConfig = app.injector.asInstanceOf[WakamConfig]
      val offer: Offer = app.injector.asInstanceOf[Offer]
      val response: Fail \/ PricerResponse = await(wakam_service.quote(wakam_quote, config))
    }
  }

}
