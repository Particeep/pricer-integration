package controllers

import javax.inject.Singleton
import play.twirl.api.Html

@Singleton
class ApplicationController extends BaseController {

  def ping = Action {
    Ok("ok")
  }

  def index = Action {
    Ok(Html(s"""
    <h1>New Pricer Implementation</h1>
    <ul>
      <li>Input Format : GET <a href="${routes.QuoteController.format(
      "new_pricer_9837778b-46b8-412b-a0c8-c3c478c0fda5"
    ).url}">${routes.QuoteController.format("new_pricer_9837778b-46b8-412b-a0c8-c3c478c0fda5").url}</a></li>
      <li>Quote : POST ${routes.QuoteController.quote("new_pricer_9837778b-46b8-412b-a0c8-c3c478c0fda5").url}</li>
      <li>Select : POST ${routes.QuoteController.select("new_pricer_9837778b-46b8-412b-a0c8-c3c478c0fda5").url}</li>
    </ul>
    """))
  }
}
