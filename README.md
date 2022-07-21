Pricer Integration
===========================================

# Intro

The main purpose of the application is to display insurance offers and allow the user to select one of them.

The application allows

* to query the insurance webservice which returns a price and some insurance data. We call this endpoint `quote`
* to fulfill a contract with an insurer by providing data (name, address, etc...). We call this endpoint `select` because the end user has selected an insurance

# Setup dev environment

## Requirement

* Java 11
* sbt 1.6.2
* scala 2.13.8

## Setup

You need to clone the repository.
```
git clone https://github.com/Particeep/pricer-integration.git
```
Or in ssh :
```
git clone git@github.com:Particeep/pricer-integration.git
```
Then it's a standard sbt project
```
cd pricer-integration
sbt clean update compile
```

# Sbt Structure of the project

The base application is a standard [Play](https://www.playframework.com/) application

There are 3 sbt modules

* `01-core` is common code available for convenience. You will probably use StringUtils or DateUtils
* `02-domain` is the domain of the application. It defines the input and output type for the part you need to implement
* `03-new-pricer` is the module you need to implement.

# Your Goal

The main goal is to complete the module `/modules/03-newpricer` with code that implements the pricer assigned to you.

Especially this part which contains an unimplemented method, i.e. with `???` as a body

* `InputFormatFactory.input_format_quote`
* `InputFormatFactory.input_format_select`
* `NewPricerService.quote`
* `NewPricerService.select`

And empty case class

* `NewPricerQuoteRequest`
* `NewPricerSelectRequest`
* `NewPricerConfig`

# InputFormat

InputFormat is a schema that describes json in a way that suits our needs.
The building blocks are defined in the domain module

* `input_format_quote` describes the json input of the `quote` endpoint
* `input_format_select` describes the json input of the `select` endpoint, minus any fields already defined in `input_format_quote`

## Basic structure

```scala

case class InputFormat(
  // The name of the field, like the name of a field in an HTML form. It's the unique identifier of the field
  name:             String,

  // Type of the field : TEXT, DATE, NUMBER etc...
  kind:             FieldType,

  // Is the field required ?
  mandatory:        Boolean,

  // If kind == ENUM then this list the values that are allowed
  options:          List[String]              = List(),

  // Can we select multiple values
  multiple:         Boolean                   = false,

  // Is the field's value an array ?
  is_array:         Boolean                   = false,

  // If kind == OBJECT, we have a nested structure with sub-fields
  fields:           Option[List[InputFormat]] = None,

  // General tagging
  tags:             Option[List[String]]      = None,

  // Internal use, you don't need this
  external_service: Option[ExternalService]   = None
)

```

NB 1: if `is_array` is true, then the json type is an array and the elements of the array are of type `kind`

NB 2: if `is_array` is false and `multiple` is true then this is the case where it creates a checkbox, and fields defined by data in the "option" attribute.

## Sample Code

```scala

val insureds_format = {
  InputFormat(
    name      = "assures",
    kind      = OBJECT,
    mandatory = true,
    is_array  = true,
    fields    = Some(
      List(
        civility_format,
        firstname_format,
        lastname_format,
        birthdate_format,
        smoker_format
      )
    )
  )
}

private[this] def civility_format: InputFormat = {
  InputFormat(name = "civility", kind = ENUM, mandatory = true, options = List("MR", "MME", "MISS"))
}

private[this] def firstname_format: InputFormat = {
  InputFormat(name = "first_name", kind = TEXT, mandatory = true)
}

private[this] def lastname_format: InputFormat = {
  InputFormat(name = "last_name", kind = TEXT, mandatory = true)
}

private[this] def birthdate_format: InputFormat = {
  InputFormat(name = "birthdate", kind = DATE, mandatory = true)
}

private[this] def smoker_format: InputFormat = {
  InputFormat(name = "is_smoking", kind = BOOLEAN, mandatory = false)
}

```

# Quote endpoint

You have to implement the method in `NewPricerService.quote`.

```scala

def quote(
  request: NewPricerQuoteRequest,
  config:  NewPricerConfig
): Future[Fail \/ PricerResponse] = {
  ???
}

```

## Input

The `NewPricerQuoteRequest` and `NewPricerConfig` input type should be defined by you according to the requirements of the web service you are working on.
They should reflect the input format you defined.

## Output

* `\/` is the [scalaz disjunction](https://eed3si9n.com/learning-scalaz/Either.html), it works almost like the standard `Either`
* `Fail` is a type of error defined in the project. It's a wrapper over a String and a stacktrace.

## Case Class Hierarchy

### PricerResponse

This is an AST which can be one of

* `PricerError`  : used for business errors
* `NeedMoreData` : used when the insurer needs more data. It's very rare
* `Decline`      : used when no price is available
* `Offer`        : used if successful

### PricerError

```scala
case class PricerError(message: String, args: List[String] = List.empty) extends PricerResponse
```

`PricerError` is used when the insurer API returns a business error:

- when the insurer refuses the user due to a data inconsistency. For example, the driver's age is under 18
- when the error case is documented in the API documentation. For example, an error code list

A technical problem is everything else. For instance
- missing data or data not understood by the insurer api.
- parsing error
- network error like a Future that timeout
- server error like http error 500
- etc...

Technical errors are handled via a `Fail`

Unfortunately, the API documentation is not always clear. If you are not sure whether the error is business one nor technical one then classify it as a technical error

NB: The Insurer API can send a response in English or in French. You don't need to translate the answer.

### Decline

```scala
case class Decline(url: URL, meta: Option[Meta] = None) extends PricerResponse
```

`Decline` is used when the insurer's API refuses to give you a price because your profile is not eligible.
All user data is good and consistent, but the insurer does not want to insure this user.

In this case, you must put the reason in a `Decline` class. It can be for example:
```
Decline(
  URL("http://not_used.com"),
  Some(Meta(title = Some("There are no offer for you in our database accoring to your profile.")))
)
```

As you can see there is `Meta` and `URL` type
You can ignore `URL`

`Meta` is a type that structures text.

```scala
case class Meta(
  title:       Option[String]             = None,
  sub_title:   Option[String]             = None,
  description: Option[String]             = None,
  documents:   Option[List[MetaDocument]] = None,
  args:        Map[String, List[String]]  = Map.empty
)
```

Typically, you will use the title and description. You will use the rest of the attributes if we tell you to.
`title`, `sub_title` and `description` are subject to i18n, so you must declare an entry in `message.en.conf` and `message.fr.conf` in `conf/i18n/newpricer`

Example :

In `message.en.conf` we have this:

````
newpricer.title.decline = "Quote denied"
newpricer.title.description = "you are not eligible"
````

And in `message.fr.conf` we have this :

````
newpricer.title.decline = "Tarification refusée"
newpricer.title.description = "Vous n'êtes pas éligble."
````

your `Meta` will be :

```scala

val meta : Meta = Meta(
  title       = Some("newpricer.title.decline"),
  description = Some("newpricer.title.description")
)

```

### Offer

```scala

case class Offer(
  // the price returned by the insurer
  price:         Price,

  // a list of items related to insurance coverage
  detail:        List[OfferItem]      = List(),

  // custom & complex data to display, such as payment schedule
  // you don't need it, just use default value
  internal_data: Option[InternalData] = None,

  // data to carry to other requests on external pricer, explained below
  external_data: Option[JsObject]     = None,

  // marketing data about the offer
  meta:          Option[Meta]         = None
) extends PricerResponse

```

The `Offer` type is used when the insurer's API gives you a price. It's a success case.

`external_data` should contain the data you will need when you call the `select` endpoint. For example an ID generated by the insurer.
You are free to decide on the JSON structure. `external_data` will be passed identically between `quote` and `select`.


### Price

```scala

case class Price(
  amount_ht:   Amount,
  owner_fees:  Amount    = Amount(0),
  broker_fees: Amount    = Amount(0),
  taxes:       Amount    = Amount(0),
  currency:    Currency  = Currency.getInstance("EUR"),
  frequency:   Frequency = Frequency.ONCE
)

```

`Amount` is a wrapper on Int.
All `Amount` are in cents. There are functions to help you convert the amount from euros to cents in `NumberUtils`

If the web service does not give you all the fields, use the default values.


### OfferItem

```scala

case class OfferItem(
  label: String,
  value: String,
  kind: String,
  args: List[String] = List.empty
)

```

`OfferItem` store offer details like TAEG / options / rate / contract / documentation / etc...
This information will be provided in the documentation

This case class is subject to i18n so as with `Meta`, you must use a message key

There is something new compared to `Meta`: the `args` parameter. It will be used as an argument in the message value

```
message_with_custom_things = "You have {0} in your wallet and {1} in your hand
```

```scala
val custom_args = List("pistol", "chewing-um")
val offer_item = OfferItem(label = "message_with_custom_things", kind = "text" ,value = "something else", args = custom_args)

// the message will be "You have pistol in your wallet and chewing-um in your hand"
```

# Select endpoint

You have to implement the method in `NewPricerService.select`

```scala

private[newpricer] def select(
  // same structure as request: NewPricerQuoteRequest but with more data
  request:        NewPricerSelectRequest,

  // broker authentication
  config:         NewPricerConfig,

  // the result of the quote endpoint
  selected_quote: Quote
): Future[Fail \/ Quote] = {
  ???
}

```

## Input

This function can only be called after a successful call to the `quote` function.
This function receives the same input as the `quote` function with the addition of the `Quote` returned earlier.

All fields included in `NewPricerQuoteRequest` will also be in `NewPricerSelectRequest`.
`NewPricerSelectRequest` can have additional fields


## Output

Just return the `selected_quote` you receive as input.
In some cases, you may need to update some fields in the `Quote` if the web service returned an updated value for those fields.

# Fail and `?|` operator

For more detail, see this article : https://medium.com/@adriencrovetto/error-handling-in-scala-with-play-framework-130034b21b37

# General rules

# Coding standard

* no `var`, no `null`
* do not add a library without consulting us first
* use [OffsetDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html) for dates
* don't throw an exception: use `Fail \/ A` to catch errors
* do not use try/catch
* be non-blocking : never do blocking code in the main execution context or in play's execution context cf. [play framework documentation](https://www.playframework.com/documentation/2.8.x/ThreadPools)
* clean compilation error and warning (There are some warnings you cannot remove. Only  suppress warnings that are present in your own code)
* the code must be written in snake_case
* the code must be written in English
* respect the principle of functional programming as much as possible
* enforce encapsulation as much as possible, e.g. use private[newpricer] except for the three methods implemented by `PricerService`. Use private[this] if needed.
* run `sbt scalastyle` and clean up the warning
* run `sbt fmt` and format the code

# error management and log

- All technical error must be contained on a Fail.
- All business error must be contained on a `PricerError` (when it is possible).

# Testing

It is important to test your code

You will have to test many cases :

- test case when you do a quote and succeed
- test case when you do a quote and fail with business error
- test case when you do a quote and fail with other error
- test case when you do a select and succeed
- test case when you do a select and fail with business error
- test case when you do a select and fail with other error
- test all complex data transformation

Do not test the library:
- don't test Play json macro
- don't test WSClient
