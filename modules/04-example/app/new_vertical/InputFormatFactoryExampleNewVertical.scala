package example.new_vertical

import domain.FieldType._
import domain.InputFormat
import example.new_vertical.models.enumeration.{ Civility, DriverLicence }

private[example] object InputFormatFactoryExampleNewVertical {

  def input_format: List[InputFormat] = {
    List(input_format_quote, input_format_select)
  }

  private[this] def input_format_quote: InputFormat = {
    InputFormat(
      name      = "quote_data",
      kind      = OBJECT,
      mandatory = true,
      fields    = Some(
        List(
          InputFormat(
            name           = "driving_licence",
            kind           = ENUM,
            mandatory      = true,
            options        = DriverLicence.values.map(_.toString).toList
          ),
          InputFormat(name = "number_of_infractions", kind = NUMBER, mandatory = true)
        )
      )
    )
  }

  /**
   * Our structure oblige to put at te same time input for quote and select. In order to have choice for us to have only
   * data for price, we put select in not mandatory but we check if it is defined during select part.
   */
  private[this] def input_format_select: InputFormat = {
    InputFormat(
      name      = "select_data",
      kind      = OBJECT,
      mandatory = false,
      fields    = Some(
        List(
          format_assures,
          InputFormat(name = "insee", kind = NUMBER, mandatory = true)
        )
      )
    )
  }

  private[this] def format_assures: InputFormat = {
    InputFormat(
      name      = "assures",
      kind      = OBJECT,
      mandatory = true,
      is_array  = true,
      fields    = Some(
        List(
          civility_format,
          firstname_format,
          lastname_format,
          birthdate_format,
          smoker_format
        )
      )
    )
  }

  private[this] def civility_format: InputFormat = {
    InputFormat(name = "civility", kind = ENUM, mandatory = true, options = Civility.values.map(_.toString).toList)
  }

  private[this] def firstname_format: InputFormat = {
    InputFormat(name = "first_name", kind = TEXT, mandatory = true)
  }

  private[this] def lastname_format: InputFormat = {
    InputFormat(name = "last_name", kind = TEXT, mandatory = true)
  }

  private[this] def birthdate_format: InputFormat = {
    InputFormat(name = "birthdate", kind = DATE, mandatory = true)
  }

  private[this] def smoker_format: InputFormat = {
    InputFormat(name = "is_smoking", kind = BOOLEAN, mandatory = false)
  }

}
