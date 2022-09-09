package newpricer.models

case class NewPricerQuoteResponse(
  QuoteReference:      String,
  message:             String,
  MontantTotalPrimeHT: String,
  Garanties:           List[Warranty]
)
