package utils

import helpers.sorus.Fail
import play.api.Logging
import scalaz.\/

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NonFatal
import play.api.Logger

object LoggerUtils extends Logging with LoggerUtils
trait LoggerUtils {

  protected def logger: Logger

  val not_handled: String = "warning.event.not.handled"

  private[this] def is_handled(fail: Fail): Boolean =
    fail.message != not_handled || !(fail.userMessage()).startsWith(not_handled)

  /**
   * You can choose your logger and log level like this
   *
   * private[this] val log = LoggerFactory.getLogger(this.getClass)
   * LoggerUtils.log_error(fail, log.info, log.info)
   */
  def log_error(fail: Fail, log_with_error: (String, Throwable) => Unit, log: String => Unit): Unit = {
    if(is_handled(fail)) {
      fail.getRootException()
        .map(ex => log_with_error(fail.userMessage(), ex))
        .getOrElse(log(fail.userMessage()))
    }
  }

  def log_light(fail: Fail): Unit = {
    if(is_handled(fail)) {
      fail.getRootException()
        .map(ex => logger.error(fail.userMessage(), ex))
        .getOrElse(logger.warn(fail.userMessage()))
    }
  }

  def log_error(fail: Fail): Unit = {
    if(is_handled(fail)) {
      fail.getRootException()
        .map(ex => logger.error(fail.userMessage(), ex))
        .getOrElse(logger.error(fail.userMessage()))
    }
  }

  def log_error(v: Fail \/ _): Unit = {
    v.swap.map(log_error)
    ()
  }

  def log_errors(v: Seq[Fail \/ _]): Unit = v.foreach(log_error)

  def log_errors[A](f: Future[Seq[Fail \/ A]], msg: Option[String])(implicit
    ec:                ExecutionContext
  ): Future[Seq[Fail \/ A]] = {
    f.map(log_errors)
    f.recoverWith {
      case NonFatal(e) => {
        logger.error(msg.getOrElse("Error in Future"), e)
        f
      }
    }
  }

  def log_error[A](f: Future[Fail \/ A], msg: String)(implicit ec: ExecutionContext): Future[Fail \/ A] =
    log_error(f, Some(msg))

  def log_error[A](f: Future[Fail \/ A], msg: Option[String] = None)(implicit
    ec:               ExecutionContext
  ): Future[Fail \/ A] = {
    f.map(log_error)

    f.recoverWith {
      case NonFatal(e) => {
        logger.error(msg.getOrElse("Error in Future"), e)
        f
      }
    }
  }
}
