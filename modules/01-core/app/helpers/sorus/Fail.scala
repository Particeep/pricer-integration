/*
 * Copyright 2014
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package helpers.sorus

import scalaz._

import org.apache.commons.lang3.exception.ExceptionUtils

class Fail(val message: String, val cause: Option[Throwable \/ Fail] = None) {

  def withEx(s: String): Fail = new Fail(s, Some(\/-(this)))

  def withEx(ex: Throwable): Fail = new Fail(this.message, Some(-\/(ex)))

  def withEx(fail: Fail): Fail = {
    if(this.cause.isEmpty) {
      new Fail(this.message, Some(\/-(fail)))
    } else {
      this.withEx(fail.message)
    }
  }

  private def messages(): NonEmptyList[String] = cause match {
    case None              => NonEmptyList(message)
    case Some(-\/(exp))    => message <:: NonEmptyList(s"${exp.getMessage} ${getStackTrace(exp)}")
    case Some(\/-(parent)) => message <:: parent.messages()
  }

  def userMessages(): List[String] = (cause match {
    case None              => NonEmptyList(message)
    case Some(-\/(_))      => NonEmptyList(message)
    case Some(\/-(parent)) => message <:: parent.messages()
  }).list.toList

  def userMessage(): String = userMessages().mkString(". ")

  def getRootException(): Option[Throwable] = cause flatMap {
    _ match {
      case -\/(exp)    => Some(exp)
      case \/-(parent) => parent.getRootException()
    }
  }

  private[this] def getStackTrace(e: Throwable): String = {
    ExceptionUtils.getStackTrace(e)
  }

  override def toString(): String = {
    message + cause.map(_.toString).getOrElse("")
  }

  override def equals(obj: Any): Boolean = {
    obj match {
      case f: Fail => f.message == this.message && f.cause == this.cause
      case _       => false
    }
  }

  override def hashCode(): Int = {
    this.message.hashCode + this.cause.hashCode()
  }
}

object Fail {
  def apply(message: String, cause: Option[Throwable \/ Fail] = None): Fail = new Fail(message, cause)

  def apply(jsError: SorusDSL.JsErrorContent): Fail = {
    jsError.foldLeft(Fail("Error parsing json")) { (fail, error) =>
      val msg = s"${error._1.toJsonString} -> ${error._2.map(_.toString()).reduce((s1, s2) => s"$s1, $s2")}"
      fail.withEx(msg)
    }
  }
}
