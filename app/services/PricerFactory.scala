package services

import domain.PricerService
import helpers.sorus.Fail
import scalaz.{ -\/, \/, \/- }
import wakam.home.WakamHome

import javax.inject.{ Inject, Singleton }

@Singleton
class PricerFactory @Inject() (
  wakam_home: WakamHome
) {

  def build(pricer_id: String): Fail \/ PricerService = {
    all_pricers
      .get(pricer_id)
      .fold[Fail \/ PricerService](
        -\/(Fail(s"Not found a pricer service with this id $pricer_id"))
      )(service => \/-(service))
  }

  private[this] val all_pricers = Map(
    "new_pricer_9837778b-46b8-412b-a0c8-c3c478c0fda5" -> wakam_home
  )
}
