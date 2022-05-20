package domain

import org.scalatestplus.play.PlaySpec

class PricerBaseCalculatorTest extends PlaySpec {

  "PricerBaseCalculator" should {
    "calculate the average " in {
      PricerBaseCalculator.average(Seq(1.2, 1.01)) mustEqual 1.11
      PricerBaseCalculator.average(Seq(2, 6, 2)) mustEqual 3.33
      PricerBaseCalculator.average(Seq(0, 0, 0)) mustEqual 0
    }

    "calculate the percentage" in {
      PricerBaseCalculator.percentage(100, 20) mustEqual 20
      PricerBaseCalculator.percentage(150, 20) mustEqual 30
      PricerBaseCalculator.percentage(141, 20) mustEqual 28.2
      PricerBaseCalculator.percentage(0, 20) mustEqual 0
      PricerBaseCalculator.percentage(20, 0) mustEqual 0
    }

    "calculate the average price" in {
      PricerBaseCalculator.average_price(120, 12) mustEqual 10
      PricerBaseCalculator.average_price(0, 12) mustEqual 0
      intercept[ArithmeticException] {
        PricerBaseCalculator.average_price(100, 0)
      }
    }

    "calculate the average with remainder" in {
      PricerBaseCalculator.average_with_remainder(120, 12) mustEqual ((10, 0))
      PricerBaseCalculator.average_with_remainder(100, 12) mustEqual ((8, 4))
      intercept[ArithmeticException] {
        PricerBaseCalculator.average_with_remainder(100, 0)
      }
    }

    "calculate the subtraction" in {
      PricerBaseCalculator.subtract(2.0, 1.0) mustEqual 1.0
      PricerBaseCalculator.subtract(1.5, 2.0) mustEqual -0.5
      PricerBaseCalculator.subtract(0, 0) mustEqual 0
    }
  }
}
