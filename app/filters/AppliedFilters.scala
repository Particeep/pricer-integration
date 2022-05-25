package filters

import javax.inject.Inject
import play.filters.gzip.GzipFilter

class AppliedFilters @Inject() (
  gzip: GzipFilter
) {

  val filters = Seq(
    gzip
  )
}
