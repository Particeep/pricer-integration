package newpricer.models

private[newpricer] sealed trait NewPricerResponse

private[newpricer] object NewPricerResponse {

  case class SuccessCase(QuoteReference: String, message: String, MontantTotalPrimeTTC: String)
    extends NewPricerResponse

  case class FailureCase(message: String) extends NewPricerResponse
}
