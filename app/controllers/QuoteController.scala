package controllers

import domain._
import newpricer.services.PricerFactory

import javax.inject.{ Inject, Singleton }
import play.api.Configuration
import play.api.i18n.Lang
import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.{ Action, AnyContent, PlayBodyParsers }

import scala.concurrent.ExecutionContext

@Singleton
class QuoteController @Inject() (
  parse:             PlayBodyParsers,
  val configuration: Configuration,
  pricer_factory:    PricerFactory
)(implicit val ec:   ExecutionContext)
  extends BaseController
    with JsonParser {

  def format(pricer_id: String): Action[AnyContent] = Action.sorus { _ =>
    for {
      pricer <- pricer_factory.build(pricer_id) ?| ()
    } yield {
      Ok(Json.toJson(pricer.input_format(pricer_id)))
    }
  }

  def quote(pricer_id: String): Action[JsValue] = Action.sorus(parse.json) { implicit request =>
    val input = QuoteInput(request.body)
    val lang  = request.headers.get("lang").getOrElse("fr")
    for {
      pricer <- pricer_factory.build(pricer_id)               ?| ()
      result <- pricer.quote(broker_config)(pricer_id, input) ?| ()
    } yield {
      val translated_result = result.translate()(Lang(lang))
      Ok(Json.toJson(translated_result))
    }
  }

  def select(pricer_id: String): Action[JsValue] = Action.sorus(parse.json) { implicit request =>
    for {
      input  <- request.body.validate[SelectSubscriptionInput] ?| ()
      pricer <- pricer_factory.build(pricer_id)                ?| ()
      result <- pricer.select(broker_config)(pricer_id, input) ?| ()
    } yield {
      Ok(Json.toJson(result))
    }
  }

  /**
   * This is custom json used to store broker configuration
   * Like API key / secret
   */
  private[this] val broker_config = Some(Json.parse("""{}"""))
}
