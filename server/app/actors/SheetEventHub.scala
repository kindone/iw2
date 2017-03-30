package actors

import actors.event.{ RemoveEventListener, AddEventListener }
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.ws.Response
import models.{ ModelManager, SheetManager }
import play.api.Logger
import upickle.default._

/**
 * Created by kindone on 2016. 4. 17..
 */
object SheetEventHub {
  def props() = Props(new SheetEventHub())
}

class SheetEventHub extends Actor {
  lazy val modelManager = new ModelManager

  def response[T: Writer](reqId: Long, logId: Long, msg: T) = {
    write(Response(reqId, logId, write[T](msg)))
  }

  var openSheets = Map[Long, ActorRef]()

  def sendToSheetEventActor(sheetId: Long, out: ActorRef, msg: Any) = {
    // create event actor if not running
    if (!openSheets.contains(sheetId)) {
      val actor = context.actorOf(SheetEventProcessor.props(), name = "sheetEventActor_" + sheetId)
      openSheets = openSheets + (sheetId -> actor)
      Logger.info("created sheet event actor of id: " + sheetId)
    }

    for (actor <- openSheets.get(sheetId))
      actor ! msg
  }

  def sendToOpenEventActor(sheetId: Long, msg: Any) = {
    for (actor <- openSheets.get(sheetId))
      actor ! msg
  }

  def receive = {
    case change @ ChangeOnWebSocket(context @ WebSocketContext(out, userId, msgId), Change(action: SheetAction, _, _)) =>
      action match {
        case action @ SubscribeSheetEventAction(sheetId) =>
          sendToSheetEventActor(sheetId, out, AddEventListener(out, context))

        // read-only shortcut
        case GetSheetAction(id) =>
          out ! response(msgId, 0, modelManager.findSheet(id)(userId).get)

        case action: SheetAlterAction =>
          sendToSheetEventActor(action.sheetId, out, change)

        case _ =>
          Logger.error("Unsupported action type to Sheet Actor received")
      }
    case ConnectionClosed(out, _, sheets) =>
      Logger.info("connection closed by: " + out.toString)
      for (sheetId <- sheets) {
        sendToOpenEventActor(sheetId, RemoveEventListener(out))
      }
    case _ =>
      Logger.error("Unsupported message type to Sheet Actor received")
  }
}