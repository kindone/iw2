package controllers

import com.kindone.infinitewall.data.Sheet
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

  def get(id: Long) = Action {
    Ok(write[Option[Sheet]](sheetManager.find(id))).as("application/json")
  }

  def delete(id: Long) = Action {
    sheetManager.delete(id)
    Ok("")
  }

  def setPosition(id: Long) = Action { implicit request =>
    val sheet = read[Sheet](bodyText)
    sheetManager.setPosition(id, sheet.x, sheet.y)
    Ok("")
  }

  def setSize(id: Long) = Action { implicit request =>
    val sheet = read[Sheet](bodyText)
    sheetManager.setSize(id, sheet.width, sheet.height)
    Ok("")
  }

  def setDimension(id: Long) = Action { implicit request =>
    val sheet = read[Sheet](bodyText)
    sheetManager.setDimension(id, sheet.x, sheet.y, sheet.width, sheet.height)
    Ok("")
  }

  def setText(id: Long) = Action { implicit request =>
    val sheet = read[Sheet](bodyText)
    sheetManager.setText(id, sheet.text)
    Ok("")
  }

}
