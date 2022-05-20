package utils

import play.api.Logging
import play.api.i18n.Lang

import scala.collection.mutable
import scala.jdk.CollectionConverters._
import scala.reflect.ClassTag

import com.ibm.icu.text.MessageFormat

import scala.util.Try

/**
 * i18n made with icu : http://site.icu-project.org/
 *
 * Java doc : http://icu-project.org/apiref/icu4j/com/ibm/icu/text/MessageFormat.html
 */
object m extends Logging {
  private[this] val messagesCache = mutable.Map[Lang, Resource]()
  private[this] def messages(lang: Lang) =
    messagesCache.getOrElseUpdate(lang, Resource("i18n/messages." + lang.code + ".conf"))

  private[this] val formatCache = mutable.Map[(String, Lang), MessageFormat]()
  private[this] def format(key: String)(implicit lang: Lang) =
    formatCache.getOrElseUpdate((key, lang), new MessageFormat(m(key), lang.toLocale))

  //TODO Remove after fix the translate with parameter
  val return_me = "return_me"

  def apply(key: String)(implicit lang: Lang): String = key match {
    case ""                           => key
    case s if s.startsWith(return_me) => s.replace(return_me, "")
    case s if s.contains(",")         => s.split(",").map(get_form_messages_safe(_)).mkString(",")
    case k                            => get_form_messages_safe(k)
  }

  private[this] def get_form_messages_safe(k: String)(implicit lang: Lang): String = Try {
    get_from_messages(k)
  }.toOption.getOrElse(k)

  private[this] def get_from_messages(k: String)(implicit lang: Lang): String = {
    messages(lang)(k).getOrElse {
      logger.warn(s"Invalid i18n key '$k', locale '${lang.code}'")
      k
    }
  }

  def apply[X: ClassTag](key: String, args: (String, Any)*)(implicit lang: Lang): String =
    format(key).format(args.toMap.asJava)

  /*
   * ClassManifest come from : http://stackoverflow.com/questions/3307427/scala-double-definition-2-methods-have-the-same-type-erasure
   */
  def apply(key: String, args: (Any)*)(implicit lang: Lang): String = {
    format(key).format(args.toArray)
  }
}
