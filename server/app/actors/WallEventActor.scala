package actors

import actors.event.{ RemoveEventListener, AddEventListener }
import actors.util.SubscriberSet
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.Wall
import com.kindone.infinitewall.data.action._
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

  def notification(logId: Long, action: Action) = {
    write(Notification(logId, action))
  }

  def broadcast(logId: Long, action: Action) = {
    for (listener <- listeners) {
      listener ! notification(logId, action)
    }
  }

  def receive = {
    case AddEventListener(actorRef) =>
      Logger.info("listening actor:" + actorRef.toString())
      listeners = listeners + actorRef
    case RemoveEventListener(actorRef) =>
      Logger.info("unlistening actor:" + actorRef.toString())
      listeners = listeners - actorRef
    case UserRequestedAction(out, userId, reqId, action: WallAlterAction) =>

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
      broadcast(logId, action)
    case _ =>
  }
}
