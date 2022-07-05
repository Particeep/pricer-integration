package newpricer.models

private[newpricer] final case class NewPricerRequest(
  quote_data:  QuoteData,
  select_data: Option[SelectData] = None
)
