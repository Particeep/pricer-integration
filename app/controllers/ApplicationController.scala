package controllers

import javax.inject.Singleton
import play.twirl.api.Html

@Singleton
class ApplicationController extends BaseController {

  def ping  = Action {
    Ok("ok")
  }
  def index = Action {
    Ok(Html(s"""
    <h1>New Pricer Implementation</h1>
    <ul>
      <li>Input Format : GET <a href="${routes.QuoteController.format(
      "new_pricer_3c25657e-2952-11ed-a261-0242ac120002"
    ).url}">${routes.QuoteController.format("new_pricer_3c25657e-2952-11ed-a261-0242ac120002").url}</a></li>
      <li>Quote : POST ${routes.QuoteController.quote("new_pricer_3c25657e-2952-11ed-a261-0242ac120002").url}</li>
      <li>Select : POST ${routes.QuoteController.select("new_pricer_3c25657e-2952-11ed-a261-0242ac120002").url}</li>
    </ul>
    """))
  }
}
