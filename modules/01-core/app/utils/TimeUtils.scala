package utils

import play.api.Logging
import play.api.libs.json._

import java.time.format.DateTimeFormatter
import java.time.{ LocalDate, Month, OffsetDateTime, ZoneOffset }
import scala.util.Try

object TimeUtils extends Logging {

  private[this] val pattern: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneOffset.UTC)

  def start_of_today(): OffsetDateTime = now().withHour(0).withMinute(0).withSecond(0).withNano(0)

  def from(year: Int, month: Month, day: Int): OffsetDateTime = {
    start_of_today().withYear(year).withMonth(month.getValue).withDayOfMonth(day)
  }

  def parse(date: String): Option[OffsetDateTime] = Try {
    OffsetDateTime.parse(date, pattern)
  }.toOption

  def parse(date: String, format: String): Option[OffsetDateTime] =
    Try {
      val pattern_custom = DateTimeFormatter.ofPattern(format)
      Some(LocalDate.parse(date, pattern_custom).atStartOfDay().atZone(ZoneOffset.UTC).toOffsetDateTime)
    }.getOrElse(parse(date))

  def isIso(date: String): Boolean = parse(date).isDefined

  def toIso(date: OffsetDateTime): String = pattern.format(date.withNano(0))

  def toStringWithFormatter(date: OffsetDateTime, format: String): String =
    date.format(DateTimeFormatter.ofPattern(format))

  def now(): OffsetDateTime = OffsetDateTime.now(ZoneOffset.UTC).withNano(0)

  def now_iso(): String = toIso(now())

  def now_day(): String = to_now_day(now())

  def to_now_day(date: OffsetDateTime): String = {
    val month     = date.getMonth.getValue
    val month_str = if(month < 10) s"0$month" else month.toString
    val day       = date.getDayOfMonth
    val day_str   = if(day < 10) s"0$day" else day.toString
    s"${date.getYear}-${month_str}-${day_str}"
  }

  def format(date: OffsetDateTime, format: String): String = {
    DateTimeFormatter.ofPattern(format).format(date)
  }

  val format: Format[OffsetDateTime] = new Format[OffsetDateTime] {
    def reads(json: JsValue): JsResult[OffsetDateTime] = json match {
      case JsString(s) => parse(s).map(JsSuccess(_)).getOrElse(JsError(s"can't parse $json as ISO date"))
      case _           => JsError(s"type of $json is unknown, impossible to pase it as ISO date.")
    }
    def writes(t:   OffsetDateTime): JsValue           = JsString(toIso(t))
  }
}
