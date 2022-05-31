package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import new_vertical.models.enumeration.DriverLicence
import play.api.libs.json.OFormat

private[new_pricer] final case class QuoteData(
  driver_licence:        DriverLicence,
  number_of_infractions: Int
)

private[new_pricer] object QuoteData {
  implicit val format: OFormat[QuoteData] = Jsonx.formatCaseClass[QuoteData]
}
