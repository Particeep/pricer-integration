package filters

import javax.inject.Inject
import play.api.http.HttpFilters
import play.filters.cors.CORSFilter
import play.filters.csrf.CSRFFilter
import play.filters.gzip.GzipFilter

class AppliedFilters @Inject() (
  gzip:          GzipFilter,
  cors:          CORSFilter,
  csrf:          CSRFFilter,
  broker_filter: BrokerFilter
) extends HttpFilters {

  val filters = Seq(
    cors,
    csrf,
    gzip,
    broker_filter
  )
}
