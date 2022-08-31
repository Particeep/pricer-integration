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
    "wakam_home_3c25657e-2952-11ed-a261-0242ac120002" -> wakam_home
  )
}
