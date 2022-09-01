package newpricer.models

private[newpricer] final case class NewPricerQuote(
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

private[newpricer] object NewPricerQuote {
  private[newpricer] def convert_boolean_to_yes_no(option: Boolean): String = {
    if(option) "Oui" else "Non"
  }
}
