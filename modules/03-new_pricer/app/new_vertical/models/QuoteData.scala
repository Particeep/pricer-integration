package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.OFormat

/**
 * put data in the case class
 */
private[new_pricer] final case class QuoteData()

private[new_pricer] object QuoteData {
  implicit val format: OFormat[QuoteData] = Jsonx.formatCaseClass[QuoteData]
}
