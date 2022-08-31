package wakam.home.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{ JsValue, Json, OFormat, Reads, Writes }
import wakam.home.models.WakamResponse.{ FailureCase, SuccessCase }

private[wakam] trait WakamJsonParser {
  implicit val config_format: OFormat[WakamQuoteConfig]        = Jsonx.formatCaseClassUseDefaults[WakamQuoteConfig]
  implicit val wakam_quote_write: Writes[WakamQuote]           = new Writes[WakamQuote] {
    override def writes(wakam_quote: WakamQuote): JsValue = Json.obj(
      "CodePostal"                          -> wakam_quote.postal_code,
      "Commune"                             -> wakam_quote.municipality,
      "Nature"                              -> wakam_quote.nature,
      "StatutOccupation"                    -> wakam_quote.occupation_status,
      "TypeDeResidence"                     -> wakam_quote.type_of_residence,
      "NombreDePieces"                      -> wakam_quote.number_of_rooms,
      "Etage"                               -> wakam_quote.stage,
      "Surface"                             -> wakam_quote.surface,
      "CapitalMobilier"                     -> wakam_quote.movable_capital,
      "CapitalObjetsdeValeur"               -> wakam_quote.capital_valuables,
      "Franchise"                           -> wakam_quote.deductible,
      "OptionAffairesNomades"               -> wakam_quote.nomadic_business_option,
      "OptionSmartphone"                    -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.smartphone_option),
      "OptionOrdinateur"                    -> wakam_quote.computer_option,
      "OptionDommagesElectriques"           -> wakam_quote.electrical_damage_option,
      "OptionBrisDeGlace"                   -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.ice_breaker_option),
      "OptionRCAssistanceMaternelle"        -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.nursery_assistant_rc_option),
      "OptionRCAnimal"                      -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.animal_rc_option),
      "OptionRCLocationDeSalle"             -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.room_rental_rc_option),
      "OptionRCLocationSaisonniere"         -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.seasonal_rental_rc_option),
      "OptionRCProfessionnelle"             -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.professional_rc_option),
      "OptionRCMoyenDeplacementNonMotorise" -> WakamQuote.convert_boolean_to_yes_no(
        wakam_quote.non_motorized_travel_rc_option
      ),
      "OptionDommagesMaterielsPro"          -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.property_damage_option),
      "OptionCapitalCoupsdursphysiques"     -> WakamQuote.convert_boolean_to_yes_no(
        wakam_quote.physical_hard_knocks_capital_option
      ),
      "OptionProtectionJuridique"           -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.legal_protection_option),
      "OptionDommagesHoteSharing"           -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.hotel_sharing_damage_option),
      "OptionRCLocatairePNO"                -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.tenant_pno_rc_option),
      "OptionRemplacementANeufMobilier"     -> WakamQuote.convert_boolean_to_yes_no(
        wakam_quote.new_furniture_replacement_option
      ),
      "OptionAssuranceScolaire"             -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.school_insurance_option),
      "OptionCaveAVin"                      -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.wine_cellar_option),
      "Dependance"                          -> WakamQuote.convert_boolean_to_yes_no(wakam_quote.dependence),
      "LatitudeCommercialeDemandee"         -> wakam_quote.commercial_latitude_requested
    )
  }
  implicit val wakam_quote_format: OFormat[WakamQuote]         = Jsonx.formatCaseClass[WakamQuote]
  implicit val success_case_format: OFormat[SuccessCase]       = Json.format[SuccessCase]
  implicit val failure_case_format: OFormat[FailureCase]       = Json.format[FailureCase]
  implicit val wakam_select_format: OFormat[WakamSelectConfig] = Json.format[WakamSelectConfig]
  implicit val wakam_subscribe_write: Writes[WakamSubscribe]   = new Writes[WakamSubscribe] {
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
      "Beneficiaires"                    -> Json.toJson(wakam_subscribe.beneficiaries),
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
  implicit val wakam_subscribe_read: OFormat[WakamSubscribe]   = Jsonx.formatCaseClass[WakamSubscribe]
  implicit val beneficiaries_read: Reads[Beneficiaries]        = Json.reads[Beneficiaries]
  implicit val beneficiaries_write: Writes[Beneficiaries]      = new Writes[Beneficiaries] {
    override def writes(o: Beneficiaries): JsValue = Json.obj(
      "Type"            -> o.kind,
      "Titre"           -> o.title,
      "Nom"             -> o.last_name,
      "Prenom"          -> o.first_name,
      "DateDeNaissance" -> o.date_of_birth.toString
    )
  }

}
