package new_pricer.new_vertical

import domain.InputFormat

private[new_pricer] object InputFormatFactoryNewPricerNewVertical {

  def input_format: List[InputFormat] = {
    List(input_format_quote, input_format_select)
  }

  private[this] def input_format_quote: InputFormat = {
    ???
  }
  private[this] def input_format_select: InputFormat = {
    ???
  }
}
