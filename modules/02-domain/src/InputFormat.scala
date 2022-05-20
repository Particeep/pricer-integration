package domain

import domain.FieldType.SIGNATURE

case class InputFormat(
  name:             String,
  kind:             FieldType,
  mandatory:        Boolean,
  options:          List[String]              = List(),
  multiple:         Boolean                   = false,
  is_array:         Boolean                   = false,
  fields:           Option[List[InputFormat]] = None,
  tags:             Option[List[String]]      = None,
  external_service: Option[ExternalService]   = None
)

object InputFormat {

  def input_signature(name: String, docs: List[(String, String)], provider: String = "universign"): InputFormat = {
    InputFormat(
      name             = name,
      kind             = SIGNATURE,
      mandatory        = false,
      options          = docs.map(doc => s"${doc._1}=${doc._2}"),
      external_service = Some(ExternalService("signature", Some(provider)))
    )
  }
}
