package actors

import actors.event._
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.ws.{ Notification, Response }
import models.{ SheetLog, SheetLogManager, SheetManager }
import play.api.Logger
import upickle.default._

/**
 * Created by kindone on 2016. 4. 24..
 */

object SheetEventActor {
  def props() = Props(new SheetEventActor)
}

class SheetEventActor extends Actor {
  var listeners = Set[ActorRef]()
  lazy val sheetManager = new SheetManager
  lazy val sheetLogManager = new SheetLogManager

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
      Logger.info("listening sheet actor:" + actorRef.toString())
      listeners = listeners + actorRef
    case RemoveEventListener(actorRef) =>
      Logger.info("unlistening sheet actor:" + actorRef.toString())
      listeners = listeners - actorRef
    case UserRequestedAction(out, userId, reqId, action: SheetAlterAction) =>

      var logId: Long = 0

      action match {
        case MoveSheetAction(id, x, y) =>
          sheetManager.setPosition(id, x, y)(userId)
          logId = sheetLogManager.create(SheetLog(id, 0, 0, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ResizeSheetAction(id, width, height) =>
          sheetManager.setSize(id, width, height)(userId)
          logId = sheetLogManager.create(SheetLog(id, 0, 1, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ChangeSheetDimensionAction(id, x, y, width, height) =>
          sheetManager.setDimension(id, x, y, width, height)(userId)
          logId = sheetLogManager.create(SheetLog(id, 0, 2, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
        case ChangeSheetContentAction(id, content, pos) =>
          sheetManager.setText(id, content)(userId) // TODO: pos
          logId = sheetLogManager.create(SheetLog(id, 0, 3, Some(write(action))))(userId)
          out ! response(reqId, logId, true)
      }
      broadcast(logId, action)
    case _ =>
  }
}