package example.new_vertical

import helpers.sorus.Fail
import example.new_vertical.models.ExampleConfig
import play.api.libs.json.{ JsObject, Json, OFormat }
import scalaz.{ -\/, \/, \/- }

import scala.util.Try

/**
 * We can imagine before to send data we need to transform them.
 * For our case we decide to separate config to user data so our transformation is to merge all.
 */
private[example] object TransformData {

  def merge[T](request: T, config: ExampleConfig)(implicit format: OFormat[T]): Fail \/ JsObject = {
    Try {
      Json.toJson(request).as[JsObject].deepMerge(Json.toJson(config).as[JsObject])
    }.fold(
      ex => -\/(Fail("[ExampleNewVertical][transform data] error : impossible to transform data").withEx(ex)),
      su => \/-(su)
    )
  }

}
