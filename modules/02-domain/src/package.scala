package domain {
  case class URL(value: String)
  case class Percentage(value: Double)

  /**
   * All `Amount` are in cents.
   * There are functions to help you convert the amount from euros to cents in `NumberUtils`
   */
  case class Amount(value: Int)
}
