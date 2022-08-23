package wakam.home.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import play.api.libs.json.{ JsValue, Json, OFormat, Writes }

private[wakam] final case class WakamQuote(
  postal_code:                         String,
  municipality:                        String,
  nature:                              String,
  occupation_status:                   String,
  type_of_residence:                   String,
  number_of_rooms:                     Int,
  stage:                               String,
  surface:                             String,
  movable_capital:                     String,
  capital_valuables:                   String,
  deductible:                          String,
  nomadic_business_option:             String,
  smartphone_option:                   Boolean,
  computer_option:                     String,
  electrical_damage_option:            String,
  ice_breaker_option:                  Boolean,
  nursery_assistant_rc_option:         Boolean,
  animal_rc_option:                    Boolean,
  room_rental_rc_option:               Boolean,
  seasonal_rental_rc_option:           Boolean,
  professional_rc_option:              Boolean,
  non_motorized_travel_rc_option:      Boolean,
  property_damage_option:              Boolean,
  physical_hard_knocks_capital_option: Boolean,
  legal_protection_option:             Boolean,
  hotel_sharing_damage_option:         Boolean,
  tenant_pno_rc_option:                Boolean,
  new_furniture_replacement_option:    Boolean,
  school_insurance_option:             Boolean,
  wine_cellar_option:                  Boolean,
  dependence:                          Boolean,
  commercial_latitude_requested:       String
)

private[wakam] object WakamQuote {
  private[this] def convert_boolean_to_yes_no(option: Boolean): String = {
    if(option) "Yes" else "No"
  }
  implicit val wakam_quote_write: Writes[WakamQuote]   = new Writes[WakamQuote] {
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
      "OptionSmartphone"                    -> convert_boolean_to_yes_no(wakam_quote.smartphone_option),
      "OptionOrdinateur"                    -> wakam_quote.computer_option,
      "OptionDommagesElectriques"           -> wakam_quote.electrical_damage_option,
      "OptionBrisDeGlace"                   -> convert_boolean_to_yes_no(wakam_quote.ice_breaker_option),
      "OptionRCAssistanceMaternelle"        -> convert_boolean_to_yes_no(wakam_quote.nursery_assistant_rc_option),
      "OptionRCAnimal"                      -> convert_boolean_to_yes_no(wakam_quote.animal_rc_option),
      "OptionRCLocationDeSalle"             -> convert_boolean_to_yes_no(wakam_quote.room_rental_rc_option),
      "OptionRCLocationSaisonniere"         -> convert_boolean_to_yes_no(wakam_quote.seasonal_rental_rc_option),
      "OptionRCProfessionnelle"             -> convert_boolean_to_yes_no(wakam_quote.professional_rc_option),
      "OptionRCMoyenDeplacementNonMotorise" -> convert_boolean_to_yes_no(
        wakam_quote.non_motorized_travel_rc_option
      ),
      "OptionDommagesMaterielsPro"          -> convert_boolean_to_yes_no(wakam_quote.property_damage_option),
      "OptionCapitalCoupsdursphysiques"     -> convert_boolean_to_yes_no(
        wakam_quote.physical_hard_knocks_capital_option
      ),
      "OptionProtectionJuridique"           -> convert_boolean_to_yes_no(wakam_quote.legal_protection_option),
      "OptionDommagesHoteSharing"           -> convert_boolean_to_yes_no(wakam_quote.hotel_sharing_damage_option),
      "OptionRCLocatairePNO"                -> convert_boolean_to_yes_no(wakam_quote.tenant_pno_rc_option),
      "OptionRemplacementANeufMobilier"     -> convert_boolean_to_yes_no(
        wakam_quote.new_furniture_replacement_option
      ),
      "OptionAssuranceScolaire"             -> convert_boolean_to_yes_no(wakam_quote.school_insurance_option),
      "OptionCaveAVin"                      -> convert_boolean_to_yes_no(wakam_quote.wine_cellar_option),
      "Dependance"                          -> convert_boolean_to_yes_no(wakam_quote.dependence),
      "LatitudeCommercialeDemandee"         -> wakam_quote.commercial_latitude_requested
    )
  }
  implicit val wakam_quote_format: OFormat[WakamQuote] = Jsonx.formatCaseClass[WakamQuote]
}
