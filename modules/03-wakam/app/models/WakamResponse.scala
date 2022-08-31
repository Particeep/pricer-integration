package wakam.home.models

private[wakam] sealed trait WakamResponse

private[wakam] object WakamResponse {

  case class SuccessCase(QuoteReference: String, message: String, MontantTotalPrimeTTC: String) extends WakamResponse

  case class FailureCase(message: String) extends WakamResponse
}
