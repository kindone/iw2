package controllers

import com.kindone.infinitewall.data.UserForm
import models.{ User, UserManager }
import play.api.Logger
import play.api.mvc.Action
import upickle.default._

/**
 * Created by kindone on 2016. 3. 20..
 */
class SessionController extends Controller {

  lazy val userManager = new UserManager

  def get() = UserAction { request =>
    Ok(s"""{"id":"${request.userId}"}""").as(JSON_TYPE)
  }

  // login
  def create() = EmptyUserAction { implicit request =>
    val userForm = read[UserForm](bodyText)
    userManager.find(userForm.email, userForm.password) match {
      case Some(id) =>
        Logger.info(s"${userForm.email} has logged in")
        Ok("").as(JSON_TYPE).withSession(LOGGED_IN_AS -> id.toString)
      case None =>
        NotFound
    }
  }

  // logout
  def delete = UserAction { implicit request =>
    Ok("").withNewSession
  }

}
