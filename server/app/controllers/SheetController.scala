package controllers

import com.kindone.infinitewall.data.state.Sheet
import models.SheetManager
import models.WallManager
import play.api._
import play.api.mvc._
import upickle.default._

/**
 * Created by kindone on 2016. 3. 20..
 */
class SheetController extends Controller {
  lazy val sheetManager = new SheetManager

  def get(id: Long) = UserAction { request =>
    implicit val userId = request.userId
    sheetManager.find(id) match {
      case Some(sheet) => Ok(write[Sheet](sheet)).as(JSON_TYPE)
      case None        => NotFound
    }
  }

  def setPosition(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val sheet = read[Sheet](bodyText)
    sheetManager.setPosition(id, sheet.x, sheet.y)
    Ok("")
  }

  def setSize(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val sheet = read[Sheet](bodyText)
    sheetManager.setSize(id, sheet.width, sheet.height)
    Ok("")
  }

  def setDimension(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val sheet = read[Sheet](bodyText)
    sheetManager.setDimension(id, sheet.x, sheet.y, sheet.width, sheet.height)
    Ok("")
  }

  def setText(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val sheet = read[Sheet](bodyText)
    sheetManager.setText(id, sheet.text)
    Ok("")
  }

}
