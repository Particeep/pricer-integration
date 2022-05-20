package domain

case class ExternalService(
  name:     String,
  provider: Option[String] = None
)
