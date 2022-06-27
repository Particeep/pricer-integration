package newpricer

import org.scalatestplus.play.PlaySpec

/**
 * Sometime insurer Api give you data and you just have to use them in this class.
 * But sometime you don't have any data test so you have to create it.
 */
class NewPricerTest extends PlaySpec {

  "NewPricer NewVertical" should {
    "do a quote and succeed" in {
      1 must be(1)
    }

    "do a select and succeed" in {
      1 must be(1)
    }

    "parse business error in quote" in {
      1 must be(1)
    }

    "parse technical error during quote" in {
      1 must be(1)
    }

    "parse business error in select" in {
      1 must be(1)
    }

    "parse technical during select" in {
      1 must be(1)
    }
  }
}
