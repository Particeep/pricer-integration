package newpricer.models

import domain.PricerResponse.OfferItem

case class Warranty(
  CodeGarantie:            String,
  MontantPrimeHT:          String,
  MontantTotalCommissions: String,
  MontantTotalTaxes:       String,
  MontantPrimeTTC:         String,
  Plafond:                 String,
  Franchise:               String
) {
  def transform_to_offer_item: Seq[OfferItem] = {
    List(
      OfferItem("newpricer.code_garantie", CodeGarantie, "text"),
      OfferItem("newpricer.montant_prime_ht", MontantPrimeHT, "amount"),
      OfferItem("newpricer.montant_total_commission", MontantTotalCommissions, "amount"),
      OfferItem("newpricer.montant_total_taxes", MontantTotalTaxes, "amount"),
      OfferItem("newpricer.montant_prime_ttc", MontantPrimeTTC, "amount"),
      OfferItem("newpricer.plafond", Plafond, "amount"),
      OfferItem("newpricer.franchise", Franchise, "amount")
    )
  }
}
