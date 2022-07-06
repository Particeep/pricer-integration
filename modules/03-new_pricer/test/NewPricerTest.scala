package newpricer.test

import org.scalatestplus.play.PlaySpec

/**
 * Sometime insurer Api give you data and you just have to use them in this class.
 * But sometime you don't have any data test so you have to create it.
 */
class NewPricerTest extends PlaySpec {

  "NewPricer" should {
    "do a quote and succeed" in {
      1 mustBe 2
    }

    "parse business error in quote" in {
      1 mustBe 2
    }

    "parse technical error during quote" in {
      1 mustBe 2
    }

    "do a select and succeed" in {
      1 mustBe 2
    }

    "parse business error in select" in {
      1 mustBe 2
    }

    "parse technical during select" in {
      1 mustBe 2
    }
  }

}
