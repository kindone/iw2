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

object WallEventProcessor {
  def props() = Props(new WallEventProcessor)
}

class WallEventProcessor extends EventProcessor {
  def applyChange(change: ChangeOnWebSocket): Unit = {
    change match {
      case ChangeOnWebSocket(WebSocketContext(outChannel, userId, reqId), change @ Change(alterAction: WallAlterAction, baseLogId, _)) =>

        alterAction match {
          case createAction @ CreateSheetAction(wallId, sheet) =>
            val actionResult = modelManager.createSheetInWall(baseLogId, createAction)(userId)
            outChannel ! response(reqId, actionResult.logId, actionResult.id)
            if (actionResult.success) {
              val newChange = change.copy(action = createAction.copy(actionResult.id))
              broadcast(actionResult.logId, newChange)
            }
          case nonCreateAction @ _ =>
            val actionResult: LogCreationResult =
              nonCreateAction match {
                case action @ ChangePanAction(wallId, x, y) =>
                  modelManager.setWallPan(baseLogId, action)(userId)
                case action @ ChangeZoomAction(wallId, scale) =>
                  modelManager.setWallZoom(baseLogId, action)(userId)
                case action @ ChangeViewAction(wallId, x, y, scale) =>
                  modelManager.setWallView(baseLogId, action)(userId)
                case action @ ChangeTitleAction(wallId, title) =>
                  modelManager.setWallTitle(baseLogId, action)(userId)
                case action @ DeleteSheetAction(wallId, sheetId) =>
                  modelManager.deleteSheetInWall(baseLogId, action)(userId)
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

}
