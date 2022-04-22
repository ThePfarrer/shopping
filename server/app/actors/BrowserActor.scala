package actors

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import io.circe.parser.decode
import io.circe.generic.auto._
import io.fscala.shopping.shared.CartEvent

object BrowserActor {
  def props(browserManager: ActorRef) =
    Props(new BrowserActor(browserManager))

}

class BrowserActor(browserManager: ActorRef) extends Actor with ActorLogging {
  def receive = { case msg: String =>
    log.info(s"Received JSON message: {}")
    decode[CartEvent](msg) match {
      case Right(cartEvent) =>
        log.info(s"Got $cartEvent message")
        browserManager forward cartEvent

      case Left(error) => log.info(s"Unhandled message: $error")
    }
  }
}
