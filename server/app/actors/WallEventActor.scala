package actors

import actors.event.{ RemoveEventListener, AddEventListener }
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.Wall
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.ws.{ Notification, Response }
import models._
import play.api.Logger
import upickle.default._

/**
 * Created by kindone on 2016. 4. 24..
 */

object WallEventActor {
  def props() = Props(new WallEventActor)
}

class WallEventActor extends Actor {
  var listeners = Set[ActorRef]()
  lazy val wallManager = new WallManager
  lazy val wallLogManager = new WallLogManager

  def response[T: Writer](reqId: Long, logId: Long, msg: T) = {
    write(Response(reqId, logId, write[T](msg)))
  }

  def notification(logId: Long, change: Change) = {
    write(Notification(logId, change))
  }

  def broadcast(logId: Long, change: Change) = {
    Logger.debug("broadcasting wall event: " + change.toString)
    for (listener <- listeners) {
      listener ! notification(logId, change)
    }
  }

  def receive = {
    case AddEventListener(actorRef) =>
      Logger.info("listening wall actor:" + actorRef.toString())
      listeners = listeners + actorRef
    case RemoveEventListener(actorRef) =>
      Logger.info("unlistening wall actor:" + actorRef.toString())
      listeners = listeners - actorRef
    case UserGeneratedChange(out, userId, reqId, change @ Change(_, action: WallAlterAction, _)) =>

      var logId: Long = 0

      action match {

        case ChangePanAction(wallId, x, y) =>
          wallManager.setPan(wallId, x, y)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 2, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ChangeZoomAction(wallId, scale) =>
          wallManager.setZoom(wallId, scale)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 3, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ChangeViewAction(wallId, x, y, scale) =>
          wallManager.setView(wallId, x, y, scale)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 4, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ChangeTitleAction(wallId, title) =>
          wallManager.setTitle(wallId, title)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 5, Some(write(action))))(userId)
          out ! response(reqId, logId, true)

        case CreateSheetAction(wallId, sheet) =>
          val sheetId = wallManager.createSheet(wallId, sheet)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 6, Some(write(action))))(userId)
          out ! response(reqId, logId, sheet.copy(id = sheetId))
        case DeleteSheetAction(wallId, sheetId) =>
          wallManager.deleteSheet(wallId, sheetId)(userId)
          logId = wallLogManager.create(WallLog(wallId, 0, 7, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case _ =>
          Logger.warn("This message type is not supported")
      }
      broadcast(logId, change)
    case _ =>
  }
}
