package controllers

import play.api.libs.typedmap.TypedKey

object SecurityConstant {

  val BROKER_CONFIG: String        = "broker-config"
  val BROKER: TypedKey[String]     = TypedKey.apply[String](BROKER_CONFIG)
  val IS_SECURE: TypedKey[Boolean] = TypedKey.apply[Boolean]("IS_SECURE")

}
