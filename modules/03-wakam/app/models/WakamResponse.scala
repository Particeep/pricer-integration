package wakam.home.models

import play.api.libs.json.{ Json, OFormat }

private[wakam] sealed trait WakamResponse

private[wakam] object WakamResponse {

  case class SuccessCase(QuoteReference: String, message: String, MontantTotalPrimeTTC: Double) extends WakamResponse

  case class FailureCase(message: String) extends WakamResponse

  implicit val success_case_format: OFormat[SuccessCase] = Json.format[SuccessCase]
  implicit val failure_case_format: OFormat[FailureCase] = Json.format[FailureCase]
}
