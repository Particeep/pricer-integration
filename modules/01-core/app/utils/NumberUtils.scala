package utils

import java.util.Locale
import play.api.i18n.Lang

object NumberUtils {

  def validePrecision(value: Double, precision: Int): Boolean = {
    val valueS = value.toString
    if(valueS.contains(".")) valueS.substring(valueS.indexOf(".") + 1).length <= precision else true
  }

  // in amount with receive for 100.50€ = 10050
  // for handle centime we divide him by 100 = 100.50
  def amountFromCentimeToDouble(amount: Int): Double = amountFromCentimeToDouble(amount.toLong)

  def amountFromCentimeToDouble(amount: Long): Double =
    (BigDecimal(amount) / BigDecimal(100)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

  def amountFromDoubleToCentime(amount: Double): Int =
    (BigDecimal(amount) * BigDecimal(100)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toInt

  def formatAmount(amount: BigDecimal): Int = {
    amount.*(100).setScale(0, BigDecimal.RoundingMode.HALF_UP).toInt
  }

  def displayAmountFromCentime(amount: Long, lang: Lang): String = {
    displayAmountFromCentime(amount, lang.locale)
  }

  def displayAmountFromCentime(amount: Long, locale: Locale): String = {
    val formatter = java.text.NumberFormat.getCurrencyInstance(locale)
    formatter.format(amountFromCentimeToDouble(amount))
  }
}
