package controllers
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._
import play.api.mvc._

import scala.concurrent.Future

class Application extends Controller {

  def index = Action { request =>
    request.session.get(LOGGED_IN_AS) match {
      case Some(_) =>
        Redirect(routes.WallController.index)
      case None =>
        Ok(views.html.index("Your new application is ready."))
    }
  }
}
