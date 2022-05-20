import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.i18n.Lang
import utils.m

class I18nTest extends AnyWordSpec with PrivateMethodTester with Matchers {

  "I18nTest" should {

    "message is not a key" in {
      val message_error = m(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
      )(Lang("en"))

      message_error shouldBe ("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
    }

    "message with Special characters" in {
      val message_error = m(
        "Lorem ipsum dolor sit amet &é(-è_çà)={}~#^$*ù!:';',?./§µ%¨£*"
      )(Lang("en"))

      message_error shouldBe ("Lorem ipsum dolor sit amet &é(-è_çà)={}~#^$*ù!:';',?./§µ%¨£*")
    }
  }
}
