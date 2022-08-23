package wakam.home.models

/**
 * This case class describes the json input you will receive for quote request
 */
private[wakam] final case class WakamQuoteRequest()

/**
 * This case class describes the json input you will receive for select request
 * It may contains fields in commmons with NewPricerQuoteRequest
 */
private[wakam] final case class WakamSelectRequest()
