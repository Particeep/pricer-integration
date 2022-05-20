package domain {
  //TODO : check why AnyVal mess up with serialization (Json / toString / etc...)
  // especially with contract generation
  case class URL(value: String) // extends AnyVal
  case class Percentage(value: Double) // extends AnyVal
  case class Amount(value: Int) // extends AnyVal

}
