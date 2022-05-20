package domain

import domain.PricerResponse._
import ai.x.play.json.Encoders._
import ai.x.play.json.Jsonx
import play.api.libs.functional.syntax._
import play.api.libs.json._
import com.ibm.icu.util.Currency
import play.api.libs.json.Reads._

trait JsonParser {

  implicit val amount_format     = Jsonx.formatInline[Amount]
  implicit val url_format        = Jsonx.formatInline[URL]
  implicit val percentage_format = Jsonx.formatInline[Percentage]
  implicit val currency_format   = new Format[Currency] {
    def writes(currency: Currency): JsValue = JsString(currency.getCurrencyCode)
    def reads(json:      JsValue)           = json match {
      case JsString(currency) => JsSuccess(Currency.getInstance(currency))
      case _                  => JsError(s"${json} is not a Currency")
    }
  }
  implicit val frequency_parser  = new Format[Frequency] {
    def writes(field_type: Frequency) = JsString(field_type.productPrefix)
    def reads(json: JsValue) = {
      Frequency.values.filter(v => JsString(v.productPrefix) == json).headOption match {
        case Some(value) => JsSuccess(value)
        case None        => JsError(s"${json} is not a Frequency")
      }
    }
  }

  implicit val meta_document_parser = Json.format[MetaDocument]

  implicit val meta_reads: Reads[Meta] = (
    (JsPath \ "title").readNullable[String] and
      (JsPath \ "sub_title").readNullable[String] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "documents").readNullable[List[MetaDocument]]
  )((title, sub_title, description, documents) => Meta(title, sub_title, description, documents))

  implicit val meta_writes: Writes[Meta] = { meta =>
    Json.obj(
      "title"       -> meta.title,
      "sub_title"   -> meta.sub_title,
      "description" -> meta.description,
      "documents"   -> meta.documents
    )
  }

  implicit val meta_parser: Format[Meta] = Format[Meta](meta_reads, meta_writes)

  implicit val price_format = Jsonx.formatCaseClassUseDefaults[Price]

  implicit val price_decline_format = Jsonx.formatCaseClassUseDefaults[Decline]

  implicit val price_error_reads: Reads[PricerError] =
    (__ \ "message").read[String].map(message => PricerError(message, List.empty))

  implicit val price_error_writes: Writes[PricerError] = { error =>
    Json.obj(
      "message" -> error.message
    )
  }

  implicit val price_error_format: Format[PricerError] = Format[PricerError](price_error_reads, price_error_writes)

  implicit val price_more_data_format  = Jsonx.formatCaseClassUseDefaults[NeedMoreData]
  implicit val payment_data_format     = Jsonx.formatCaseClass[PaymentData]
  implicit val internal_data_format    = Jsonx.formatCaseClass[InternalData]
  implicit val price_offer_item_format = Jsonx.formatCaseClassUseDefaults[OfferItem]
  implicit val price_offer_format      = Jsonx.formatCaseClassUseDefaults[Offer]

  implicit val price_response_format = Jsonx.formatSealed[PricerResponse]

  implicit val quoteInputFormat               = Json.format[QuoteInput]
  implicit val selectSubscriptionInputFormat  = Json.format[SelectSubscriptionInput]
  implicit val selectSubscriptionOutputFormat = Json.format[SelectSubscriptionOutput]

  implicit val externalServiceFormat = Json.format[ExternalService]

  implicit val field_type_parser                              = new Format[FieldType] {
    def writes(field_type: FieldType) = JsString(field_type.productPrefix)
    def reads(json: JsValue) = {
      FieldType.values.filter(v => JsString(v.productPrefix) == json).headOption match {
        case Some(value) => JsSuccess(value)
        case None        => JsError(s"${json} is not a FieldType")
      }
    }
  }
  implicit val pricer_input_field_format: Format[InputFormat] = (
    (__ \ Symbol("name")).format[String] and
      (__ \ Symbol("kind")).format[FieldType] and
      (__ \ Symbol("mandatory")).format[Boolean] and
      (__ \ Symbol("options")).formatWithDefault[List[String]](List()) and
      (__ \ Symbol("multiple")).formatWithDefault[Boolean](false) and
      (__ \ Symbol("is_array")).formatWithDefault[Boolean](false) and
      (__ \ Symbol("fields")).lazyFormatNullable(implicitly[Format[List[InputFormat]]]) and
      (__ \ Symbol("tags")).formatNullable[List[String]] and
      (__ \ Symbol("external_service")).formatNullable[ExternalService]
  )(InputFormat.apply, unlift(InputFormat.unapply))

  def extract_signature_from_data(data: JsObject, field: String): Option[Signature] = {
    val json = field match {
      case f if f.nonEmpty => data.fields.find(_._1.endsWith(f)).map(_._2)
      case _               => None
    }
    json.flatMap(_.asOpt[Signature])
  }
}

object JsonParser extends JsonParser
