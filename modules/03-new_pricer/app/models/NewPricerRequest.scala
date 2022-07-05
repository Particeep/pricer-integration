package newpricer.models

private[new_pricer] final case class NewPricerRequest(
  quote_data:  QuoteData,
  select_data: Option[SelectData] = None
)
