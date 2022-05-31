package new_pricer.new_vertical

import helpers.sorus.Fail
import new_pricer.new_vertical.models.NewPricerConfig
import play.api.libs.json.{ JsObject, Json, OFormat }
import scalaz.{ -\/, \/, \/- }

import scala.util.{ Failure, Success, Try }

/**
 * THIS IS AN EXAMPLE, REPLACE WITH YOUR VALUE IF NEEDED.
 *
 * We can imagine before to send data we need to transform them.
 * For our case we decide to separate config to user data so our transformation is to merge all.
 *
 * But like write in the readme, insurer API can ask you to send data in a particular format like PHP tab.
 */
private[new_pricer] object TransformData {

  def merge[T](request: T, config: NewPricerConfig)(implicit format: OFormat[T]): Fail \/ JsObject = {
    Try {
      Json.toJson(request).as[JsObject].deepMerge(Json.toJson(config).as[JsObject])
    } match {
      case Failure(exception) =>
        -\/(Fail("[NewPricerNewVertical][transform data] error : impossible to transform data").withEx(exception))
      case Success(value)     => \/-(value)
    }
  }

}
