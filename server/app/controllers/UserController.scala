package controllers

import com.kindone.infinitewall.data.{ ChangePasswordForm, UserForm }
import models.{ User, UserManager }
import play.api.mvc._
import upickle.default._
import org.mindrot.jbcrypt.BCrypt;

/**
 * Created by kindone on 2016. 3. 20..
 */
class UserController extends Controller {

  lazy val userManager = new UserManager

  def list() = AdminAction {
    Ok(write[List[User]](userManager.findAll())).as(JSON_TYPE)
  }

  def get(id: Long) = UserAction { request =>
    if (id == request.userId)
      userManager.find(id) match {
        case Some(user) =>
          Ok(write[User](user)).as(JSON_TYPE)
        case None =>
          NotFound
      }
    else
      Unauthorized
  }

  def create() = EmptyUserAction { implicit request =>
    val userForm = read[UserForm](bodyText)
    val user = User(0, userForm.email, BCrypt.hashpw(userForm.password, BCrypt.gensalt))
    val userId = userManager.create(user)
    Ok("").as(JSON_TYPE)
  }

  def delete(id: Long) = AdminAction {
    if (userManager.delete(id))
      Ok("")
    else
      BadRequest
  }

  def changePassword(id: Long) = UserAction { implicit request =>
    val changePasswordForm = read[ChangePasswordForm](bodyText)
    if (userManager.changePassword(id, changePasswordForm.oldPassword, changePasswordForm.newPassword))
      Ok("")
    else
      BadRequest
  }

}
