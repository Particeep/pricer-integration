package new_pricer.new_vertical

import helpers.sorus.Fail
import scalaz.{ -\/, \/, \/- }

/**
 * THIS IS AN EXAMPLE, REPLACE WITH YOUR VALUE IF NEEDED.
 * This object check some fields with constraint asked by insured API.
 * Sometimes insurer will hide (literally) some constraint.
 *
 * Another way to check business requirement is to use type on field. Indeed, when insurer write for a field
 * a list of possible value, this is a business requirement and you can manage that with enumeration.
 * See package enumeration to know more.
 */
private[new_pricer] object NewPricerNewVerticalBusinessRequirement {

  private[this] val fail: String => -\/[Fail] =
    (message: String) => -\/(Fail(s"[NewPricerNewVertical]’[check business requirement] error: $message"))

  private[this] val success: \/-[Unit]        = \/-(())

  /**
   * We can imagine insurer API demand that field 'insee' must be modulo 9 AND length must be between 12 and 24.
   * This is an business requirement so we can check it.
   *
   * You can see insurer did not precise if value can be positive of negative. So either you ask that to us, or you find
   * on internet that the field is always positive.
   *
   * Do not hesitate to source your code.
   *
   * why I used logarithm to check length instead of value.toString.length:
   *
   * https://stackoverflow.com/questions/11922686/is-there-a-scala-way-to-get-the-length-of-a-number
   *
   * https://www.techno-science.net/definition/4925.html
   */
  def check_insee(value: Int): Fail \/ Unit = {
    val value_length = BigDecimal(value).precision
    if(value > 0) {
      (value % 9 == 0, value_length >= 12 || value_length <= 24) match {
        case (true, true)   => success
        case (true, false)  => fail(s"insee length is not between 12 and 24. Value : $value and length $value_length")
        case (false, true)  => fail(s"insee is not modulo 9. Value : $value")
        case (false, false) => fail(
            s"insee is not modulo 9 AND length is not between 12 and 24.  Value : $value and length $value_length"
          )
      }
    } else { fail(s"insee value is negative ! Value : $value") }
  }
}
