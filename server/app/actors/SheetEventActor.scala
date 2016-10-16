package actors

import actors.event._
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
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

  def response(reqId: Long, result: Tuple2[Long, Boolean]): String = {
    response(reqId, result._1, result._2)
  }

  def notification(logId: Long, change: Change) = {
    write(Notification(logId, change))
  }

  def broadcast(logId: Long, change: Change) = {
    for (listener <- listeners) {
      listener ! notification(logId, change)
    }
  }

  def receive = {
    case AddEventListener(actorRef) =>
      Logger.info("listening sheet actor:" + actorRef.toString())
      listeners = listeners + actorRef
    case RemoveEventListener(actorRef) =>
      Logger.info("unlistening sheet actor:" + actorRef.toString())
      listeners = listeners - actorRef
    case UserGeneratedChange(out, userId, reqId, change @ Change(action: SheetAlterAction, baseLogId, _)) =>
      var result: (Long, Boolean) = (0, false)

      action match {
        case MoveSheetAction(id, x, y) =>
          sheetManager.setPosition(id, x, y)(userId)
          result = sheetLogManager.create(SheetLog(id, baseLogId, 0, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ResizeSheetAction(id, width, height) =>
          sheetManager.setSize(id, width, height)(userId)
          result = sheetLogManager.create(SheetLog(id, baseLogId, 1, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ChangeSheetDimensionAction(id, x, y, width, height) =>
          sheetManager.setDimension(id, x, y, width, height)(userId)
          result = sheetLogManager.create(SheetLog(id, baseLogId, 2, Some(write(action))))(userId)
          out ! response(reqId, result)
        case ChangeSheetContentAction(id, content, pos, length) =>
          sheetManager.setText(id, content)(userId) // TODO: pos, length
          result = sheetLogManager.create(SheetLog(id, baseLogId, 3, Some(write(action))))(userId)
          out ! response(reqId, result)
      }
      if (result._2)
        broadcast(result._1, change)
    case _ =>
  }
}