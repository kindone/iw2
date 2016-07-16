package controllers

import javax.inject.{ Singleton, Inject }

import actors.{ SheetEventHubActor, WallEventHubActor, WebSocketActor }
import akka.actor._
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.{ Wall, Sheet }
import models.WallManager
import play.api._
import play.api.libs.json.JsValue
import play.api.mvc._
import upickle.default._

import scala.concurrent.Future
import play.api.Play.current
import upickle.default._

import scala.util.Try

/**
 * Created by kindone on 2016. 3. 20..
 */
// defining singleton necessary to keep actor reference
@Singleton
class WallController @Inject() (system: ActorSystem) extends Controller {

  lazy val wallManager = new WallManager

  lazy val wallActor = system.actorOf(WallEventHubActor.props(), name = "wallEventHubActor")
  lazy val sheetActor = system.actorOf(SheetEventHubActor.props(), name = "sheetEventHubActor")

  def index = UserAction {
    Ok(views.html.wall.index("Your new application is ready."))
  }

  def wall(id: Long) = UserAction {
    Ok(views.html.wall.wall("Your new application is ready.", id))
  }

  def list() = UserAction { request =>
    implicit val userId = request.userId
    Ok(write[List[Wall]](wallManager.findAll())).as(JSON_TYPE)
  }

  def get(id: Long) = UserAction { request =>
    implicit val userId = request.userId
    wallManager.find(id) match {
      case Some(wall) => Ok(write[Wall](wall)).as(JSON_TYPE)
      case None       => NotFound
    }
  }

  def create() = UserAction { implicit request =>
    implicit val userId = request.userId
    val wall = read[Wall](bodyText)
    val wallId = wallManager.create(wall)
    Ok(write[Wall](wall.copy(id = wallId))).as(JSON_TYPE)
  }

  def delete(id: Long) = UserAction { request =>
    implicit val userId = request.userId
    wallManager.delete(id)
    Ok("")
  }

  def setPan(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val wall = read[Wall](bodyText)
    wallManager.setPan(id, wall.x, wall.y)
    Ok("")
  }

  def setZoom(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val wall = read[Wall](bodyText)
    wallManager.setZoom(id, wall.scale)
    Ok("")
  }

  def setView(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val wall = read[Wall](bodyText)
    wallManager.setView(id, wall.x, wall.y, wall.scale)
    Ok("")
  }

  def setTitle(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val wall = read[Wall](bodyText)
    wallManager.setTitle(id, wall.title)
    Ok("")
  }

  def getSheets(id: Long) = UserAction { request =>
    implicit val userId = request.userId
    Ok(write[Set[Long]](wallManager.getSheetIds(id))).as(JSON_TYPE)
  }

  def createSheet(id: Long) = UserAction { implicit request =>
    implicit val userId = request.userId
    val sheet = read[Sheet](bodyText)
    val sheetId = wallManager.createSheet(id, sheet)
    Ok(write[Sheet](sheet.copy(id = sheetId))).as(JSON_TYPE)
  }

  def deleteSheet(id: Long, sheetId: Long) = UserAction { request =>
    implicit val userId = request.userId
    wallManager.deleteSheet(id, sheetId)
    Ok("")
  }

  def websocket = WebSocket.tryAcceptWithActor[String, String] { request =>
    Future.successful(request.session.get(LOGGED_IN_AS) match {
      case None         => Left(Forbidden)
      case Some(userId) => Right(WebSocketActor.props(wallActor, sheetActor, userId.toLong))
    })
  }
}
