package domain

import play.api.libs.json.JsValue

case class SelectSubscriptionInput(data: JsValue, selected_quote: Quote)

case class SelectSubscriptionOutput(status: String, output: Quote)
