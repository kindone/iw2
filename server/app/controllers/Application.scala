package controllers

import play.api._
import play.api.mvc._

class Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def wall(id: Long) = Action {
    Ok(views.html.wall("Your new application is ready.", id))
  }

}
