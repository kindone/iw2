package actors

import actors.event._
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.communication.{ Notification, Response }
import models._
import play.api.Logger
import upickle.default._

/**
 * Created by kindone on 2016. 4. 24..
 */

object SheetEventProcessor {
  def props() = Props(new SheetEventProcessor)
}

class SheetEventProcessor extends EventProcessor {

  def applyChange(change: ChangeOnWebSocket): Unit = {
    change match {
      case ChangeOnWebSocket(WebSocketContext(outChannel, userId, reqId), change @ Change(alterAction: SheetAlterAction, baseLogId, _)) =>
        val actionResult: LogCreationResult =
          alterAction match {
            case action @ MoveSheetAction(id, x, y) =>
              modelManager.moveSheet(baseLogId, action)(userId)
            case action @ ResizeSheetAction(id, width, height) =>
              modelManager.resizeSheet(baseLogId, action)(userId)
            case action @ ChangeSheetDimensionAction(id, x, y, width, height) =>
              modelManager.setSheetDimension(baseLogId, action)(userId)
            case action @ ChangeSheetContentAction(id, content, pos, length) =>
              modelManager.setSheetText(baseLogId, action)(userId)
            case _ =>
              Logger.warn("This message type is not supported")
              throw new RuntimeException("Unexpected sheet alter action")
          }

        outChannel ! response(reqId, actionResult)

        if (actionResult.success)
          broadcast(actionResult.logId, change)
    }
  }
}