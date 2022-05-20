package filters

import akka.stream.Materializer
import controllers.SecurityConstant
import javax.inject.Inject
import play.api.mvc.{ Filter, RequestHeader, Result }

import scala.concurrent.Future

class BrokerFilter @Inject() (implicit val mat: Materializer) extends Filter {
  def apply(nextFilter: RequestHeader => Future[Result])(request_header: RequestHeader): Future[Result] = {
    request_header.headers.get(SecurityConstant.BROKER_CONFIG) match {
      case Some(broker_auth) => nextFilter(request_header.addAttr(SecurityConstant.BROKER, broker_auth))
      case None              => nextFilter(request_header)
    }
  }
}
