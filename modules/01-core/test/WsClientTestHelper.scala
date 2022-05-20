package test

import play.api.test.WsTestClient
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.shaded.ahc.org.asynchttpclient.AsyncHttpClient

import akka.actor.ActorSystem

trait WsClientTestHelper {

  protected implicit val ec = scala.concurrent.ExecutionContext.global

  protected implicit val sys = ActorSystem("WsClientTestHelper")

  def withStandaloneClient[T](
    block: StandaloneWSClient => T
  ): T = {
    WsTestClient.withClient { client =>
      val ws_standalone: StandaloneWSClient = new StandaloneAhcWSClient(client.underlying[AsyncHttpClient])
      block(ws_standalone)
    }
  }
}
