package newpricer.models

/**
 * This case class describes the credentials needed to call the insurer's api
 * It could be an api token, user / password, etc...
 */
private[newpricer] final case class NewPricerQuoteConfig(key: String)
private[newpricer] case class NewPricerSelectConfig(key: String, partnership_code: String)
