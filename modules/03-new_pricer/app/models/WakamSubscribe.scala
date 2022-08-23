package newpricer.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{ JsValue, Json, OFormat, Writes }

import java.time.OffsetDateTime

final case class WakamSubscribe(
  quote_reference:                   String,
  reference_client:                  String,
  start_date_effect:                 OffsetDateTime,
  end_date_effect:                   OffsetDateTime,
  split_payment:                     String,
  first_due_date:                    OffsetDateTime,
  last_name:                         String,
  first_name:                        String,
  title:                             String,
  email:                             String,
  iban:                              String,
  bic:                               String,
  bank_code:                         String,
  bank_name:                         String,
  holder:                            String,
  mobile_number:                     String,
  address:                           String,
  municipality:                      String,
  postal_code:                       String,
  beneficiaries:                     String,
  date_of_birth:                     OffsetDateTime,
  said_place:                        String,
  channel_name:                      String,
  channel_number:                    Int,
  quality:                           String,
  payment_number:                    String,
  payment_amount:                    Int,
  previous_insurer:                  String,
  previous_policy_number:            String,
  previous_policy_subscription_date: OffsetDateTime
)

object WakamSubscribe {
  implicit val wakam_subscribe_write: Writes[WakamSubscribe] = new Writes[WakamSubscribe] {
    override def writes(wakam_subscribe: WakamSubscribe): JsValue = Json.obj(
      "QuoteReference"                   -> wakam_subscribe.quote_reference,
      "ReferenceClient"                  -> wakam_subscribe.reference_client,
      "DateDebutEffet"                   -> wakam_subscribe.start_date_effect,
      "DateFinEffet"                     -> wakam_subscribe.end_date_effect,
      "Fractionnement"                   -> wakam_subscribe.split_payment,
      "DatePremiereEcheance"             -> wakam_subscribe.first_due_date,
      "Nom"                              -> wakam_subscribe.last_name,
      "Prenom"                           -> wakam_subscribe.first_name,
      "Titre"                            -> wakam_subscribe.title,
      "Email"                            -> wakam_subscribe.email,
      "Iban"                             -> wakam_subscribe.iban,
      "Bic"                              -> wakam_subscribe.bic,
      "CodeBanque"                       -> wakam_subscribe.bank_code,
      "NomBanque"                        -> wakam_subscribe.bank_name,
      "Titulaire"                        -> wakam_subscribe.holder,
      "NumeroMobile"                     -> wakam_subscribe.mobile_number,
      "Adresse"                          -> wakam_subscribe.address,
      "Commune"                          -> wakam_subscribe.municipality,
      "CodePostal"                       -> wakam_subscribe.postal_code,
      "Beneficiaires"                    -> wakam_subscribe.beneficiaries,
      "DateDeNaissance"                  -> wakam_subscribe.date_of_birth,
      "LieuDit"                          -> wakam_subscribe.said_place,
      "NomVoie"                          -> wakam_subscribe.channel_name,
      "NumeroVoie"                       -> wakam_subscribe.channel_number,
      "Qualite"                          -> wakam_subscribe.quality,
      "NumeroPaiement"                   -> wakam_subscribe.payment_number,
      "MontantPaiement"                  -> wakam_subscribe.payment_amount,
      "AssureurPrecedent"                -> wakam_subscribe.previous_insurer,
      "NumeroPolicePrecedent"            -> wakam_subscribe.previous_policy_number,
      "DateSouscriptionPolicePrecedente" -> wakam_subscribe.previous_policy_subscription_date
    )
  }
  implicit val wakam_subscribe_read: OFormat[WakamSubscribe] = Jsonx.formatCaseClass[WakamSubscribe]
}
