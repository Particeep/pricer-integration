package controllers

import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.{ Configuration, Logging }
import utils.Crypto.decodeBase64ToJson

import scala.concurrent.Future

class SecureRequest[A](
  request: Request[A]
) extends WrappedRequest[A](request)

class BrokerRequest[A](
  val broker_auth: Option[JsValue],
  secure_request:  SecureRequest[A]
) extends SecureRequest[A](secure_request)

private[controllers] trait Security extends Logging { self: controllers.BaseController =>

  def configuration: Configuration

  def secureAction = new ActionRefiner[Request, SecureRequest] {
    def executionContext = self.executionContext
    def refine[A](request: Request[A]): Future[Either[Result, SecureRequest[A]]] = Future.successful {
      Right(new SecureRequest(request))
    }
  }

  def brokerAction: ActionRefiner[SecureRequest, BrokerRequest] = new ActionRefiner[SecureRequest, BrokerRequest] {
    def executionContext = self.executionContext
    def refine[A](request: SecureRequest[A]): Future[Either[Result, BrokerRequest[A]]] = Future.successful {
      Right(new BrokerRequest(request.attrs.get(SecurityConstant.BROKER).map(decodeBase64ToJson), request))
    }
  }

  def SecureAction: ActionBuilder[SecureRequest, AnyContent] = Action andThen secureAction
  def BrokerAction: ActionBuilder[BrokerRequest, AnyContent] = SecureAction andThen brokerAction

}
