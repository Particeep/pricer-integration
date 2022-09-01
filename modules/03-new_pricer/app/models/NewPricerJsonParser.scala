package newpricer.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{ JsValue, Json, OFormat, Reads, Writes }
import newpricer.models.NewPricerResponse.{ FailureCase, SuccessCase }

private[newpricer] trait NewPricerJsonParser {
  implicit val new_pricer_quote_config_format: OFormat[NewPricerQuoteConfig]   =
    Jsonx.formatCaseClassUseDefaults[NewPricerQuoteConfig]
  implicit val new_pricer_quote_write: Writes[NewPricerQuote]                  = new Writes[NewPricerQuote] {
    override def writes(new_pricer_quote: NewPricerQuote): JsValue = Json.obj(
      "CodePostal"                          -> new_pricer_quote.postal_code,
      "Commune"                             -> new_pricer_quote.municipality,
      "Nature"                              -> new_pricer_quote.nature,
      "StatutOccupation"                    -> new_pricer_quote.occupation_status,
      "TypeDeResidence"                     -> new_pricer_quote.type_of_residence,
      "NombreDePieces"                      -> new_pricer_quote.number_of_rooms,
      "Etage"                               -> new_pricer_quote.stage,
      "Surface"                             -> new_pricer_quote.surface,
      "CapitalMobilier"                     -> new_pricer_quote.movable_capital,
      "CapitalObjetsdeValeur"               -> new_pricer_quote.capital_valuables,
      "Franchise"                           -> new_pricer_quote.deductible,
      "OptionAffairesNomades"               -> new_pricer_quote.nomadic_business_option,
      "OptionSmartphone"                    -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.smartphone_option),
      "OptionOrdinateur"                    -> new_pricer_quote.computer_option,
      "OptionDommagesElectriques"           -> new_pricer_quote.electrical_damage_option,
      "OptionBrisDeGlace"                   -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.ice_breaker_option),
      "OptionRCAssistanceMaternelle"        -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.nursery_assistant_rc_option
      ),
      "OptionRCAnimal"                      -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.animal_rc_option),
      "OptionRCLocationDeSalle"             -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.room_rental_rc_option),
      "OptionRCLocationSaisonniere"         -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.seasonal_rental_rc_option
      ),
      "OptionRCProfessionnelle"             -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.professional_rc_option),
      "OptionRCMoyenDeplacementNonMotorise" -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.non_motorized_travel_rc_option
      ),
      "OptionDommagesMaterielsPro"          -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.property_damage_option),
      "OptionCapitalCoupsdursphysiques"     -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.physical_hard_knocks_capital_option
      ),
      "OptionProtectionJuridique"           -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.legal_protection_option),
      "OptionDommagesHoteSharing"           -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.hotel_sharing_damage_option
      ),
      "OptionRCLocatairePNO"                -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.tenant_pno_rc_option),
      "OptionRemplacementANeufMobilier"     -> NewPricerQuote.convert_boolean_to_yes_no(
        new_pricer_quote.new_furniture_replacement_option
      ),
      "OptionAssuranceScolaire"             -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.school_insurance_option),
      "OptionCaveAVin"                      -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.wine_cellar_option),
      "Dependance"                          -> NewPricerQuote.convert_boolean_to_yes_no(new_pricer_quote.dependence),
      "LatitudeCommercialeDemandee"         -> new_pricer_quote.commercial_latitude_requested
    )
  }
  implicit val new_pricer_quote_format: OFormat[NewPricerQuote]                = Jsonx.formatCaseClass[NewPricerQuote]
  implicit val success_case_format: OFormat[SuccessCase]                       = Json.format[SuccessCase]
  implicit val failure_case_format: OFormat[FailureCase]                       = Json.format[FailureCase]
  implicit val new_pricer_select_config_format: OFormat[NewPricerSelectConfig] = Json.format[NewPricerSelectConfig]
  implicit val new_pricer_subscribe_write: Writes[NewPricerSubscribe]          = new Writes[NewPricerSubscribe] {
    override def writes(new_pricer_subscribe: NewPricerSubscribe): JsValue = Json.obj(
      "QuoteReference"                   -> new_pricer_subscribe.quote_reference,
      "ReferenceClient"                  -> new_pricer_subscribe.reference_client,
      "DateDebutEffet"                   -> new_pricer_subscribe.start_date_effect,
      "DateFinEffet"                     -> new_pricer_subscribe.end_date_effect,
      "Fractionnement"                   -> new_pricer_subscribe.split_payment,
      "DatePremiereEcheance"             -> new_pricer_subscribe.first_due_date,
      "Nom"                              -> new_pricer_subscribe.last_name,
      "Prenom"                           -> new_pricer_subscribe.first_name,
      "Titre"                            -> new_pricer_subscribe.title,
      "Email"                            -> new_pricer_subscribe.email,
      "Iban"                             -> new_pricer_subscribe.iban,
      "Bic"                              -> new_pricer_subscribe.bic,
      "CodeBanque"                       -> new_pricer_subscribe.bank_code,
      "NomBanque"                        -> new_pricer_subscribe.bank_name,
      "Titulaire"                        -> new_pricer_subscribe.holder,
      "NumeroMobile"                     -> new_pricer_subscribe.mobile_number,
      "Adresse"                          -> new_pricer_subscribe.address,
      "Commune"                          -> new_pricer_subscribe.municipality,
      "CodePostal"                       -> new_pricer_subscribe.postal_code,
      "Beneficiaires"                    -> Json.toJson(new_pricer_subscribe.beneficiaries),
      "DateDeNaissance"                  -> new_pricer_subscribe.date_of_birth,
      "LieuDit"                          -> new_pricer_subscribe.said_place,
      "NomVoie"                          -> new_pricer_subscribe.channel_name,
      "NumeroVoie"                       -> new_pricer_subscribe.channel_number,
      "Qualite"                          -> new_pricer_subscribe.quality,
      "NumeroPaiement"                   -> new_pricer_subscribe.payment_number,
      "MontantPaiement"                  -> new_pricer_subscribe.payment_amount,
      "AssureurPrecedent"                -> new_pricer_subscribe.previous_insurer,
      "NumeroPolicePrecedent"            -> new_pricer_subscribe.previous_policy_number,
      "DateSouscriptionPolicePrecedente" -> new_pricer_subscribe.previous_policy_subscription_date
    )
  }
  implicit val new_pricer_subscribe_read: OFormat[NewPricerSubscribe]          = Jsonx.formatCaseClass[NewPricerSubscribe]
  implicit val beneficiaries_read: Reads[Beneficiaries]                        = Json.reads[Beneficiaries]
  implicit val beneficiaries_write: Writes[Beneficiaries]                      = new Writes[Beneficiaries] {
    override def writes(o: Beneficiaries): JsValue = Json.obj(
      "Type"            -> o.kind,
      "Titre"           -> o.title,
      "Nom"             -> o.last_name,
      "Prenom"          -> o.first_name,
      "DateDeNaissance" -> o.date_of_birth.toString
    )
  }

}
