Pricer Integration
===========================================

# Intro

The main goal of the application is to display insurance's offers and allow the user to select one of them.

The application allow

* to query insurance's webservice that return a price and some data about the insurance. We call this endpoint `quote`
* to fulfill a contract with an insurer by providing data (name, address, etc...). We call this endpoint `select` because the end-user have selected an insurance

# Setup dev environment

## Requirement

* Java 11
* sbt 1.6.1
* scala 2.13.8

## Setup

You need to clone the repository. With https method you need a PAT (Personal Access Token)
```
git clone https://github.com/Particeep/pricer-integration.git
```
You can clone the repository with ssh method, but you need to create key
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

* `01-core` is commons code that is available for convenience. You will probably use StringUtils or DateUtils
* `02-domain` is the domain of the application. It defines input and output type for the part you need to implement
* `03-new-pricer` : this is the module you need to implement.
* `04-new-pricer` an example in order to understand in order you to understand the structure.

# Your Goal

The goal is to complete the module of `/modules/03-new_pricer` with code that implement the pricer you have been assigned.
new_pricer contains example in order to guide you but you need to delete all comment or example code and replace by your implementation/

Especially this parts that contains un-implemented method

* `InputFormatFactory.input_format_quote`
* `InputFormatFactory.input_format_select`
* `NewPricerService.quote`
* `NewPricerService.select`

# InputFormat

You need to implement 2 InputFormat

* `InputFormatFactory.input_format_quote`
* `InputFormatFactory.input_format_select`

InputFormat is a schema that describes json in a way that suit our need.
The building bloc are defined in the domain module

* `input_format_quote` describe the json input of the `quote` endpoint
* `input_format_select` describe the json input of the `select` endpoint, minus every fields that are already defined in `input_format_quote`

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

NB 1: if `is_array` is true then the json type is an array and items in the array are of type `kind`

NB 2: if `is_array` is false and `multiple` is true then we are in the case where you have checkbox and field to check
is defined by what you put in option.

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

You should implement the method in `NewPricerService.quote`. In the code you will have a dummy example.
```scala
private[new_pricer] def quote(
    request: NewPricerRequest,
    config:  NewPricerConfig
  ): Future[Fail \/ PricerResponse] = {
    ???
  }
```

## Input

The input type `NewPricerRequest` and `NewPricerConfig` should be defined by you according to the requirement of the webservice you're working on.
They should reflect the input format you define.

You should document what difficulty did you encounter during developpement and what did you do. For instance if a pricer have a wsdl but it is too old for play, you have to explain that and
explain what did you do to resolve this problem.

## Output

* `\/` is the [scalaz disjunction](https://eed3si9n.com/learning-scalaz/Either.html), it works almost like the standard `Either`
* `Fail` is an error type, defined in the project. It's a wrapper on a String and a stacktrace.

### PricerResponse 

This is the type that you have to return to the method quote. There is many class for this object, and we will see each of them. 
But to resume, eiher your code return a price given by insurer with extra data specified in the doc or by us or you return an error divised in two types. 

#### PricerError

```scala
case class PricerError(message: String, args: List[String] = List.empty) extends PricerResponse
```


`PricerError` is use when insurer API return a business error.
A business error is an error which explicit the insurer refuse user because there is no coherence in the data.

For instance driver age is inferior to 18. Another example you can not say you have a good vision but at the same time you are blind.

Unfortunately, api insurer can make difficult how to separate technical problem from business problem. In this case you can return a sentence like "you are not eligible"
and put on a fail what the api insured return.


A technical problem is an error on data. For instance, they are missing data or data is not understood by the api insurer.
That implied you made a mistake in the code, and you have to correct it.

Insurer API can send respond in english or in french. You don't have to translate them.

#### Decline

```scala
case class Decline(url: URL, meta: Option[Meta] = None) extends PricerResponse
```
Decline is use when insurer API refuse to give you a price because your profile is not eligible. 
All data from user are good and consistent, but Insurer API does not want to insure this user profile.
At this moment you have to put the reason and use this class. It can be for instance :
```
There are no offer for you in our database accoring to your profile : you did too much infraction this year.
```
As you can see, there is the type `Meta` and `URL`.

`URL` is rarely use, put an empty string only if we do not say what to put in the spec :
```scala
val url : URL = URL("")
```
`Meta` is a type which structure text.
```scala
case class Meta(
  title:       Option[String]             = None,
  sub_title:   Option[String]             = None,
  description: Option[String]             = None,
  documents:   Option[List[MetaDocument]] = None,
  args:        Map[String, List[String]]  = Map.empty
)
```
In general, you will use title and description. You will use the rest of the attribute if we specific to you this requirement.
Attributes title, sub_title and description are subject to I18n, so you need to declare in `message.en.conf` and `message.fr.conf` in new_pricer directory fields and value and call them.

Example : 

In `message.en.conf` we have this :
````
new_pricer.title.decline = "Quote denied"
new_pricer.title.description = "you are not eligible"
````
And in `message.fr.conf` we have this :
````
new_pricer.title.decline = "Tarification refusée"
new_pricer.title.description = "Vous n'êtes pas éligble."
````
your `Decline` will be : 
```scala
val meta : Meta = Meta(title = "new_pricer.title.decline".some, description = "new_pricer.title.description".some )
```

#### Offer

```scala
case class Offer(
                  price:         Price,
                  detail:        List[OfferItem]      = List(),
                  internal_data: Option[InternalData] = None,
                  external_data: Option[JsObject]     = None,
                  meta:          Option[Meta]         = None
                ) extends PricerResponse
```
The type `Offer` is use when insurer API give you a price. So this is the positive case. This type is always with the type `Price`, but we will see it later.
Overall, you have to put data according to what we demand. You will have to put :

##### Price

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
For us, a price that insurer API give you is of type `Amount`.
```scala
case class Amount(value: Int)
```
You have to convert your price into `Int`, but you have two restrictions : 
- first, you don't have to use `toInt` directly because we work with type `BigDecimal`.
- second, take always two numbers after comma because all monetary amounts are in cents. 
You can use this already defined in `PricerBaseCalculator`:
```scala
 def amountFromDoubleToCentime(amount: Double): Int =
  (BigDecimal(amount) * BigDecimal(100)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toInt
```
if you have to do operation on price use object `PricerBaseCalculator` and do not hesitate to add new operation with `Int`, `double` or `Long` for your need in this object.

Example : 
Imagine insurer API return a price and the price 123,34, you have to do :
```scala
import utils.NumberUtils
val result_request : Double = 123.34
val amount : Amount = Amount(NumberUtils.amountFromDoubleToCentime(result_request)) // res0 : Amount(12334)
```
Now we know what is `Amount`, we can return to the type `price`
```scala
// I put here, don't need to scroll (don't thanks me)
case class Price(
                  amount_ht:   Amount,
                  owner_fees:  Amount    = Amount(0),
                  broker_fees: Amount    = Amount(0),
                  taxes:       Amount    = Amount(0),
                  currency:    Currency  = Currency.getInstance("EUR"),
                  frequency:   Frequency = Frequency.ONCE
                )
```
You will have to feed each attribute according to what we demand.

Example:

Imagine you have the doc and we write that
```scala
currency : EUR
owner_fees : 0€
taxes : 1337,21€
broker_fees :  234€
```
And the price that insurer API give you, is 12€. So you need to feed your type price like that
```scala
val price : Price = 
  Price(
    amount_ht= Amount(1200),
    taxes = Amount(NumberUtils.amountFromDoubleToCentime(1337.21)),
    broker_fees = Amount(234)
  )
```
##### OfferItem

```scala
case class OfferItem(label: String, value: String, kind: String, args: List[String] = List.empty)
```
`OfferItem` is additional data which come with the price. What put in this is an information that we will give you if needed.

Parameter kind is the type of your data which can be defined by us. 

This case class is subject to I18n and like `Meta` so if needed, you have to define in message.en.newpricer.conf and 
message.fr.newpricer.conf label and value.

There are something new compared to `Meta` :  the parameter args. Indeed, you can create in message.en/fr.newpricer.conf
a message with a symbol in order to be replaced. For instance in message.en.conf you can have : 
```
message_with_custom_things = "You have {0} in your wallet and {1} in your hand
```
```scala
val custom_args = List("pistol", "chewing-um")
val offer_item = OfferItem(label = "message_with_custom_things", kind = "text" ,value = "something else", args = custom_args)
// the message will be "You have pistol in your wallet and chewing-um in your hand"
```
Now get back to type `Offer` 

##### external_data

External is data that you need in order to complete select part. You are free to decide JSON structure.
If during the select insurer return you a link, you have to create an external_data.

##### InternalData

// TODO : I have no idea what it is --> need help for that

##### PaymentData

// TODO : I have no idea what it is --> need help for that

# Select endpoint

You should implement the method in `NewPricerService.select`

```scala
private[newpricer] def select(
    request:        NewPricerRequest,
    config:         NewPricerConfig,
    selected_quote: Quote
  ): Future[Fail \/ Quote] = {
    ???
  }
```
## Input

This method take in input new data to send in select part. Depending on insurer you have to send again data quote but in another endpoint, 
and with that new data like name, first name, phone number etc..

## Output

Depending on insurer you can have many cases : 
- what you send is data for user sign up, so you don't have to in more
- insurance send you back (so after you send to it data) a link in order to allow user to continue the process on the insurance site and in this case you have to put
the link in external data

# Fail and ?|

## ?| aka Sorus

Sorus take inspiration from [Play Monadic Action](https://github.com/Driox/play-monadic-actions) to extends the DSL outside of Actions.
It provides some syntactic sugar that allows boilerplate-free combination of "classical" type such as `Future[Option[A]]`, `Future[Either[A, B]]`, `Future[A]` using for-comprehensions. It also provide a simple and powerful way to handle error via `Fail`

This [article](https://medium.com/@adriencrovetto/130034b21b37) explain in greater detail the problem that this project addresses, and how to use the solution in your own projects.

## Usage

The DSL adds the `?|` operator to most of the types one could normally encounter (such as `Future[A]`, `Future[Option[A]]`, `Either[B,A]`, Future[Fail \/ A], etc...). The `?|` operator will transform the "error" part of the type to `Fail` (ie. None for Option, Left for Either, etc...) returning an `EitherT[Future, Fail, A]` (which is aliased to `Step[A]` for convenience)
It enables the writing of the whole action as a single for-comprehension.
Implicit convertion allows us to retrive a `Future[Fail \/ A]` as a result of the for-comprehension

~~~scala
package exemples

import helpers._
import helpers.SorusDSL._
import scala.concurrent.Future
import scalaz._

class BasicExemple extends Sorus {

  // Sample User class
  final case class User(id:Option[Long], email:String, validate:Boolean)

  def do_something(): Future[Fail \/ User] = {
    for {
      user <- load_User(12L)       ?| "Error while loading user"     // <- you don't create Fail yourself but the ?| operator do it for you
      _    <- user.validate       ?| "Account need to be validated"
      _    <- log_user_action(user) ?| ()                             // <- You can just forward underlying Fail without adding a message
    } yield {
      user
    }
  }

  def load_user(id:Long):Future[Option[User]] = {
    // Load it from DB / API / Services ...
    Future.successful(Some(User(Some(id), "foo@bar.com", false)))
  }

  def log_user_action(user:User):Future[Fail \/ Unit] = {
    for {
      id <- user.id ?| "Can't log action of user wihtout id"
    } yield {
      println(s"user $id access the resource")
    }
  }
}
~~~

## Fail

`Fail` will accumulate error and if you compose multiple Fails. It has convenient method to retrieve information about the error :

~~~scala
import helpers.sorus.Fail

val exception_1:Throwable = new IllegalArgumentException

// this return a type Fail with a message next to the exeception.
val fail = Fail("Error during something important in the code").withEx(exception_1)

// This code give just the message of the fail
val lastErrorMessage:String = fail.message // return : "Error during something important in the code"

val exception_2:Option[Throwable] = fail.getRootException()

val allMessagesInOneString:String = fail.userMessage()
~~~


# General rules

## Renaming

rename new_pricer with the real name of the pricer you will work on

Ex: you work on a home insurance for Axa
You should do this kins of renaming

* `03-new_prier` -> `03-axa`
* `package newpricer.xxx` -> `package axa.home.xxx`
* `NewPricer` in class / function name would become `AxaHome`

# Coding standard

* no `var`, no `null`
* do not add library without consulting us first
* use [OffsetDateTime](https://docs.oracle.com/javase/8/docs/api/java/time/OffsetDateTime.html) for date
* don't throw exception : use `Fail \/ A` to catch error
* be non-blocking : never do blocking in the main execution context or in the play's execution context cf. [play's doc](https://www.playframework.com/documentation/2.8.x/ThreadPools)
* clean compilation error and warning (it exists warning that you can't delete. Delete warning only present on your pricer integration.)
* code must be written in snake_case
* code must be written in english
* code in type level and respect functional programming (no side effect, use monad etc..)
* encapsulate each of your class in private[new_pricer] except for the three methods implemented by `PricerService`
* We use generally private[this] in the front of a val or def.
* run `sbt scalastyle` and clean warning
* run `sbt fmt`

# error management and log

- All technical error must be contained on a Fail.
- All business error must be contained on a `PricerError` (when it is possible).
- use logger.info only if it is important that is to say when received data in input and output.

# Testing

It is important to test what you did. The main aims is to test program behavior

You will have to test many cases :
- test case when you received a price
- test case when your select is ok
- test case when pricer return technical error
- test case when pricer return business error

You do not need to test library you use 
(for instance do not create a test which examine if ws client send data, format json from jsonx.formatCaseClass).
Again You do not need to re test your tools, test only what you created.

When you have to test quote part, do not write a test which compare a price in the code with what api insurer give you.
Because an insurer does not have fix price it can change when he wants. You can compare then the AST that you have defined
and see if during the test you receive the good type which carry price.

For instance, we can consider this AST
~~~scala
sealed trait NewPricerNewResponse

final case class NewPricerPrice(value : Double) extends NewPricerNewResponse
final case class NewPricerError(code_error : Int, reason  : String) extends NewPricerNewResponse
~~~

So the test can be 
~~~scala
"NewPriceService test" should {
  "Success, because it return a price" in {
    val new_pricer_service = app.injector.instanceOf[NewPricerService]
    val result      = await(new_pricer_service.get_price(data, config, TypeOfFormula.BASIC))

    (result match {
      case \/-(NewPricerPrice(_))       => true
      case not_expected                => 
        logger.error(s"Error during get price -> $not_expected")
        false
    }) must be(true)
  }
~~~

If you create method that transform data you have to write test. For instance, we have this object.
Insurer want to get date into timestamps unix

~~~scala
import java.time.OffsetDateTime

private[new_pricer] object NewPricerUtils {
  def to_timestamps_unix(date: OffsetDateTime): String = date.toInstant.getEpochSecond
}
~~~
So you need to test this method. In directory test you have to write a new class called for instance "NewPricerMethodTest.scala" and the method with an expected result

~~~scala
"NewPricer new vertical format_date method" should {
  "succeed whdata_nowen formatting date" in {
    val date_test = OffsetDateTime.now(ZoneOffset.UTC).withNano(0)
    val date_to_timestamp: Long = to_timestamps_unix(date_test)
    
   val  revert_date_test_to_offsetdatetime = new Date(date_to_timestamp * 1000).toInstant.atOffset(ZoneOffset.UTC)
    
    date_test.compareTo(date_to_offsetdatetime) must be(0)
  }
}
~~~
# Use case Rest / Json

Doc for play json [here](https://www.playframework.com/documentation/2.8.x/ScalaJson)

With scala, it is easier to manage json data. You have to define in the companion object a format.
For instance :

~~~scala
package new_pricer.new_vertical.models

import ai.x.play.json.Encoders.encoder
import ai.x.play.json.Jsonx
import new_vertical.models.enumeration.Civility
import play.api.libs.json.OFormat

import java.time.OffsetDateTime

private[new_pricer] final case class Assures(
                                              civility: Civility,
                                              first_name: String,
                                              last_name: String,
                                              birthday: OffsetDateTime,
                                              is_smoking: Boolean
                                            )

object Assures {
  implicit val format: OFormat[Assures] =
    Jsonx.formatCaseClass[
      Assures
    ] // this line is important, this is that which define for you json format for your class
}

~~~

If you have to transform your data on Json and send it in the body for you request, this can be done easier.
~~~scala
package new_pricer.services

import domain._
import helpers.sorus.Fail
import helpers.sorus.SorusDSL.Sorus
import javax.inject.{ Inject, Singleton }
import play.api.{ Configuration, Logging }

import scala.concurrent.{ ExecutionContext, Future }
import scalaz.\/
import play.api.libs.ws.WSClient

@Singleton
private[new_pricer] final class NewPricerService @Inject() (val ws: WSClient, val config: Configuration)(implicit val ec: ExecutionContext) extends JsonParser with Sorus with Logging {

  private[this] val url = config.get[String]("new_pricer.url")

  private[newpricer] def send_quote(data_to_send: Assures): Future[Fail \/ PricerResponse] = {
    for {
      request <- ws.url(url).post(Json.toJson(Assures)) ?| "impossible to get price for pricer newpricer newvertical"
    } yield ???
  }
}
~~~
# Use case SOAP

They are two way to handle SOAP and it depends on the insurer.

First case, the easier, the insurer give you a WSDL, use sbt to compile it use it methods to construct the data and send them to the api pricer.

Second case, less easy, you can not use the wsdl (too old for play) or pricer does not give you that and you have to write manually the xml and call manually the api pricer.
In this case you can use [XML confect](https://github.com/mthaler/xmlconfect).

# Build sbt
 
Do not forget to put your pricer in build sbt like for new_pricer.