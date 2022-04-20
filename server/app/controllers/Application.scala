package controllers

import io.fscala.shopping.client.shared.SharedMessages
import javax.inject._
import play.api.mvc._

@Singleton
class Application @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def index: Action[AnyContent] = Action {
    Ok(views.html.index(SharedMessages.itWorks))
  }

}
