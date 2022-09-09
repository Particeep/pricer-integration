package newpricer.models

import domain.FieldType.{ BOOLEAN, DATE, ENUM, NUMBER, OBJECT, TEXT }
import domain.InputFormat

import newpricer.models.enumerations._

private[newpricer] object InputFormatFactory {

  /**
   * This should be the format of the input required by the quote endpoint
   */
  def input_format_quote: List[InputFormat] = {
    List(
      InputFormat(name = "postal_code", kind       = TEXT, mandatory   = true),
      InputFormat(name = "municipality", kind      = TEXT, mandatory   = true),
      InputFormat(name = "nature", kind            = TEXT, mandatory   = true),
      InputFormat(
        name           = "occupation_status",
        kind           = ENUM,
        mandatory      = true,
        options        = OccupationStatus.values.map(_.toString).toList
      ),
      InputFormat(name = "type_of_residence", kind = TEXT, mandatory   = true),
      InputFormat(name = "number_of_rooms", kind   = NUMBER, mandatory = true),
      InputFormat(name = "stage", kind             = ENUM, mandatory   = true, options = Stage.values.map(_.toString).toList),
      InputFormat(name = "surface", kind           = NUMBER, mandatory = true),
      InputFormat(name = "movable_capital", kind   = NUMBER, mandatory = true),
      InputFormat(name = "capital_valuables", kind = NUMBER, mandatory = true),
      InputFormat(
        name           = "deductible",
        kind           = ENUM,
        mandatory      = true,
        options        = Deductible.values.map(_.toString).toList
      ),
      InputFormat(
        name           = "nomadic_business_option",
        kind           = ENUM,
        mandatory      = true,
        options        = NomadicBusinessOption.values.map(_.toString).toList
      ),
      InputFormat(
        name           = "smartphone_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "computer_option",
        kind           = ENUM,
        mandatory      = true,
        options        = ComputerOption.values.map(_.toString).toList
      ),
      InputFormat(
        name           = "electrical_damage_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "ice_breaker_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "nursery_assistant_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "animal_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "room_rental_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "seasonal_rental_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "professional_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "non_motorized_travel_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "property_damage_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "physical_hard_knocks_capital_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "legal_protection_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "hotel_sharing_damage_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "tenant_pno_rc_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "new_furniture_replacement_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "school_insurance_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "wine_cellar_option",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "dependence",
        kind           = BOOLEAN,
        mandatory      = true
      ),
      InputFormat(
        name           = "commercial_latitude_requested",
        kind           = ENUM,
        mandatory      = true,
        options        = CommercialLatitudeRequested.values.map(_.toString).toList
      )
    )
  }

  /**
   * This should be the format of the input required by the select endpoint
   */
  def input_format_select: List[InputFormat] = {
    List(
      InputFormat(name = "reference_client", kind                  = TEXT, mandatory   = true),
      InputFormat(name = "start_date_effect", kind                 = DATE, mandatory   = true),
      InputFormat(name = "end_date_effect", kind                   = DATE, mandatory   = true),
      InputFormat(
        name           = "split_payment",
        kind           = ENUM,
        mandatory      = true,
        options        = SplitPayment.values.map(_.toString).toList
      ),
      InputFormat(name = "first_due_date", kind                    = DATE, mandatory   = true),
      InputFormat(name = "last_name", kind                         = TEXT, mandatory   = true),
      InputFormat(name = "first_name", kind                        = TEXT, mandatory   = true),
      InputFormat(name = "title", kind                             = ENUM, mandatory   = true, options  = Title.values.map(_.toString).toList),
      InputFormat(name = "email", kind                             = TEXT, mandatory   = true),
      InputFormat(name = "iban", kind                              = TEXT, mandatory   = true),
      InputFormat(name = "bic", kind                               = TEXT, mandatory   = true),
      InputFormat(name = "bank_code", kind                         = TEXT, mandatory   = true),
      InputFormat(name = "bank_name", kind                         = TEXT, mandatory   = true),
      InputFormat(name = "holder", kind                            = TEXT, mandatory   = true),
      InputFormat(name = "mobile_number", kind                     = TEXT, mandatory   = true),
      InputFormat(name = "address", kind                           = TEXT, mandatory   = true),
      InputFormat(name = "municipality", kind                      = TEXT, mandatory   = true),
      InputFormat(name = "postal_code", kind                       = TEXT, mandatory   = true),
      InputFormat(name = "beneficiaries", kind                     = OBJECT, mandatory = true, is_array = true),
      InputFormat(name = "date_of_birth", kind                     = DATE, mandatory   = true),
      InputFormat(name = "said_place", kind                        = TEXT, mandatory   = false),
      InputFormat(name = "channel_name", kind                      = TEXT, mandatory   = true),
      InputFormat(name = "channel_number", kind                    = NUMBER, mandatory = true),
      InputFormat(name = "quality", kind                           = ENUM, mandatory   = true, options  = Quality.values.map(_.toString).toList),
      InputFormat(name = "payment_number", kind                    = TEXT, mandatory   = true),
      InputFormat(name = "payment_amount", kind                    = NUMBER, mandatory = false),
      InputFormat(name = "previous_insurer", kind                  = TEXT, mandatory   = false),
      InputFormat(name = "previous_policy_number", kind            = TEXT, mandatory   = false),
      InputFormat(name = "previous_policy_subscription_date", kind = DATE, mandatory   = false)
    )
  }
}
