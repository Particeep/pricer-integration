package newpricer.models

import newpricer.models.enumerations._

private[newpricer] final case class NewPricerQuote(
  postal_code:                         String,
  municipality:                        String,
  nature:                              String,
  occupation_status:                   OccupationStatus,
  type_of_residence:                   String,
  number_of_rooms:                     Int,
  stage:                               Stage,
  surface:                             Int,
  movable_capital:                     Int,
  capital_valuables:                   Int,
  deductible:                          Deductible,
  nomadic_business_option:             NomadicBusinessOption,
  smartphone_option:                   Boolean,
  computer_option:                     ComputerOption,
  electrical_damage_option:            Boolean,
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
  commercial_latitude_requested:       CommercialLatitudeRequested
)

private[newpricer] object NewPricerQuote {
  private[newpricer] def convert_boolean_to_yes_no(option: Boolean): String = {
    if(option) "Oui" else "Non"
  }
}
