package domain

object PricerBaseCalculator {

  def average(rates: Seq[Double]): Double = {
    (BigDecimal(rates.sum) / BigDecimal(rates.size)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def percentage(total: Double, value: Double): Double = {
    average_price(total * value, 100)
  }

  def average_price(total: Double, value: Int): Double = {
    (BigDecimal(total) / BigDecimal(value)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }

  def average_with_remainder(value_1: Double, value_2: Double): (Int, Int) = {
    val (result, remainder) = BigDecimal(value_1) /% BigDecimal(value_2)
    (result.intValue, remainder.intValue)
  }

  def subtract(v1: Double, v2: Double): Double = {
    (BigDecimal(v1) - BigDecimal(v2)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
  }
}
