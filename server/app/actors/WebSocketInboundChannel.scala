package actors

import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.communication.{ ChangeRequest }
import play.api.Logger
import upickle.default._

import scala.util.Try

/**
 * Created by kindone on 2016. 4. 17..
 */

case class WebSocketContext(outChannel: ActorRef, userId: Long, reqId: Long)

case class ChangeOnWebSocket(context: WebSocketContext, change: Change)
case class ConnectionClosed(outActor: ActorRef, subscribingWalls: Set[Long], subscribingSheets: Set[Long])
case class SubscribeEvent(outActor: ActorRef, userId: Long, wallId: Long)

object WebSocketInboundChannel {
  def props(wallActor: ActorRef, sheetActor: ActorRef, userId: Long)(outboundChannel: ActorRef) = Props(new WebSocketInboundChannel(wallActor, sheetActor, outboundChannel, userId))
}

class WebSocketInboundChannel(wallActor: ActorRef, sheetActor: ActorRef, outboundChannel: ActorRef, userId: Long) extends Actor {

  var subscribingSheets = Set[Long]()
  var subscribingWalls = Set[Long]()

  def subscribeSheet(id: Long) =
    subscribingSheets += id

  def unsubscribeSheet(id: Long) =
    subscribingSheets -= id

  def subscribeWall(id: Long) =
    subscribingWalls += id

  def unsubscribeWall(id: Long) =
    subscribingWalls -= id

  def unsubscribeAll() = {
    subscribingSheets = Set()
    subscribingWalls = Set()
  }

  def processRequest(req: ChangeRequest): Unit = {
    req match {
      case ChangeRequest(reqId, change @ Change(action: SubscribeWallEventAction, _, _)) =>
        subscribeWall(action.wallId)
        wallActor ! ChangeOnWebSocket(WebSocketContext(outboundChannel, userId, reqId), change)
      case ChangeRequest(reqId, change @ Change(action: SubscribeSheetEventAction, _, _)) =>
        subscribeSheet(action.sheetId)
        sheetActor ! ChangeOnWebSocket(WebSocketContext(outboundChannel, userId, reqId), change)
      case ChangeRequest(reqId, change @ Change(wallAction: WallAction, _, _)) =>
        wallActor ! ChangeOnWebSocket(WebSocketContext(outboundChannel, userId, reqId), change)
      case ChangeRequest(reqId, change @ Change(sheetAction: SheetAction, _, _)) =>
        sheetActor ! ChangeOnWebSocket(WebSocketContext(outboundChannel, userId, reqId), change)
      case _ =>
        Logger.error("Unexpected ActionMessage type")
    }
  }

  def receive = {
    case str: String =>
      Logger.debug("WebSocket raw message came: " + str)
      val reqTry = Try { read[ChangeRequest](str) }

      for (req <- reqTry) {
        processRequest(req)
      }
      reqTry.recover {
        case e: upickle.Invalid.Json =>
          Logger.error("Invalid string that cannot be parsed to json was received: " + e.getMessage)
        case e: upickle.Invalid.Data =>
          Logger.error("Unable to parse ActionMessage from json received: " + e.getMessage)
        case e: Exception =>
          Logger.error("Unknown error: " + e.getMessage)
      }
    case _ =>
      Logger.error("Unexpected message type")
  }

  override def preStart(): Unit = {
    Logger.info("preStart: " + self.path)
  }

  override def postStop(): Unit = {
    // websocket is closed

    Logger.info("postStop: " + self.path)
    wallActor ! ConnectionClosed(outboundChannel, subscribingWalls, subscribingSheets)
    sheetActor ! ConnectionClosed(outboundChannel, subscribingWalls, subscribingSheets)

  }

}