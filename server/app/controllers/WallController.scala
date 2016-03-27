package controllers

import com.kindone.infinitewall.data.{ Wall, Sheet }
import models.WallManager
import play.api._
import play.api.mvc._
import upickle.default._

/**
 * Created by kindone on 2016. 3. 20..
 */
class WallController extends Controller {

  lazy val wallManager = new WallManager

  def getAll() = Action {
    Ok(write[List[Wall]](wallManager.findAll())).as("application/json")
  }

  def get(id: Long) = Action {
    wallManager.find(id) match {
      case Some(wall) => Ok(write[Wall](wall)).as("application/json")
      case None       => NotFound
    }
  }

  def create() = Action { implicit request =>
    val wall = read[Wall](bodyText)
    wallManager.create(wall)
    Ok("")
  }

  def delete(id: Long) = Action {
    wallManager.delete(id)
    Ok("")
  }

  def setPan(id: Long) = Action { implicit request =>
    val wall = read[Wall](bodyText)
    wallManager.setPan(id, wall.x, wall.y)
    Ok("")
  }

  def setZoom(id: Long) = Action { implicit request =>
    val wall = read[Wall](bodyText)
    wallManager.setZoom(id, wall.scale)
    Ok("")
  }

  def setView(id: Long) = Action { implicit request =>
    val wall = read[Wall](bodyText)
    wallManager.setView(id, wall.x, wall.y, wall.scale)
    Ok("")
  }

  def getSheets(id: Long) = Action {
    Ok(write[List[Sheet]](wallManager.getSheets(id))).as("application/json")
  }

  def createSheet(id: Long) = Action { implicit request =>
    val sheet = read[Sheet](bodyText)
    val sheetId = wallManager.createSheet(id, sheet)
    Ok("{id: " + sheetId + "}").as("application/json")
  }

  def deleteSheet(id: Long, sheetId: Long) = Action {
    wallManager.deleteSheet(id, sheetId)
    Ok("")
  }
}
