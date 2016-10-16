package actors

import actors.event.{ RemoveEventListener, AddEventListener }
import akka.actor.{ Actor, Props, ActorRef }
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

  def response(reqId: Long, result: (Long, Boolean)): String = {
    response(reqId, result._1, result._2)
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
    case UserGeneratedChange(out, userId, reqId, change @ Change(action: WallAlterAction, baseLogId, _)) =>

      var result: Tuple2[Long, Boolean] = (0, false)

      action match {

        case ChangePanAction(wallId, x, y) =>
          wallManager.setPan(wallId, x, y)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 2, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ChangeZoomAction(wallId, scale) =>
          wallManager.setZoom(wallId, scale)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 3, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ChangeViewAction(wallId, x, y, scale) =>
          wallManager.setView(wallId, x, y, scale)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 4, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ChangeTitleAction(wallId, title) =>
          wallManager.setTitle(wallId, title)(userId)
          val result = wallLogManager.create(WallLog(wallId, baseLogId, 5, Some(write(action))))(userId)
          out ! response(reqId, result)

        case CreateSheetAction(wallId, sheet) =>
          val sheetId = wallManager.createSheet(wallId, sheet)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 6, Some(write(action))))(userId)
          out ! response(reqId, result._1, sheet.copy(id = sheetId))
        case DeleteSheetAction(wallId, sheetId) =>
          wallManager.deleteSheet(wallId, sheetId)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 7, Some(write(action))))(userId)
          out ! response(reqId, result)
        case _ =>
          Logger.warn("This message type is not supported")
      }

      if (result._2)
        broadcast(result._1, change)
    case _ =>
  }
}
