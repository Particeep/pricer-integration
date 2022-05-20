package domain

import domain.PricerResponse.Offer
import ai.x.play.json.Encoders._
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat
import utils.{ StringUtils, TimeUtils }

import java.time.OffsetDateTime

case class Quote(
  id:               String         = StringUtils.generateUuid(),
  created_at:       OffsetDateTime = TimeUtils.now(),
  broker_owner_id:  String,
  broker_user_id:   String,
  pricer_id:        String,
  subscription_id:  String,
  product_name:     String,
  product_category: String,
  input_hash:       String,
  response:         Offer
)

object Quote {
  import JsonParser._
  implicit lazy val format: OFormat[Quote] = Jsonx.formatCaseClassUseDefaults[Quote]
}
