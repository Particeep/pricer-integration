package wakam.home.models

/**
 * This case class describes the credentials needed to call the insurer's api
 * It could be an api token, user / password, etc...
 */
private[wakam] final case class WakamQuoteConfig(key: String)
