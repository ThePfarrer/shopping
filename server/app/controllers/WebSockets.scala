package controllers

import actors.{BrowserActor, BrowserManagerActor}
import akka.actor.ActorSystem
import akka.stream.Materializer
import io.swagger.annotations._
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import play.api.Logging

import javax.inject.{Inject, Singleton}

@Singleton
@Api(value = "Product and Cart API")
class WebSockets @Inject() (implicit actorSystem: ActorSystem, materializer: Materializer, cc: ControllerComponents)
    extends AbstractController(cc)
    with Logging {

  val managerActor = actorSystem.actorOf(BrowserManagerActor.props(), "manager-actor")

  def cartEventWS = WebSocket.accept[String, String] { implicit request =>
    ActorFlow.actorRef { out =>
      logger.info(s"Got a new websocket connection from ${request.host}")
      managerActor ! BrowserManagerActor.AddBrowser(out)

      BrowserActor.props(managerActor)
    }
  }

}
