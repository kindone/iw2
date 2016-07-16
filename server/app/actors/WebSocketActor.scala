package actors

import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.ws.Request
import play.api.Logger
import upickle.default._

import scala.util.Try

/**
 * Created by kindone on 2016. 4. 17..
 */

case class UserRequestedAction(outActor: ActorRef, userId: Long, reqId: Long, action: Action)
case class ConnectionClosed(outActor: ActorRef, subscribingWalls: Set[Long], subscribingSheets: Set[Long])
case class SubscribeEvent(outActor: ActorRef, userId: Long, wallId: Long)

object WebSocketActor {
  def props(wallActor: ActorRef, sheetActor: ActorRef, userId: Long)(outActor: ActorRef) = Props(new WebSocketActor(wallActor, sheetActor, outActor, userId))
}

class WebSocketActor(wallActor: ActorRef, sheetActor: ActorRef, outActor: ActorRef, userId: Long) extends Actor {
  //  val sheetActor = context.actorOf(SheetEventDistributeActor.props(out), name = "sheetEventDistributeActor")

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

  def receive = {
    case str: String =>
      Logger.debug("ws event: " + str)
      val reqTry = Try { read[Request](str) }

      for (req <- reqTry) {
        req match {
          case Request(reqId, action: SubscribeWallEventAction) =>
            subscribeWall(action.wallId)
            wallActor ! UserRequestedAction(outActor, userId, reqId, action)
          case Request(reqId, action: SubscribeSheetEventAction) =>
            subscribeSheet(action.sheetId)
            sheetActor ! UserRequestedAction(outActor, userId, reqId, action)
          case Request(reqId, wallAction: WallAction) =>
            wallActor ! UserRequestedAction(outActor, userId, reqId, wallAction)
          case Request(reqId, sheetAction: SheetAction) =>
            sheetActor ! UserRequestedAction(outActor, userId, reqId, sheetAction)
          case _ =>
            Logger.error("Unexpected ActionMessage type")
        }
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
    wallActor ! ConnectionClosed(outActor, subscribingWalls, subscribingSheets)
    sheetActor ! ConnectionClosed(outActor, subscribingWalls, subscribingSheets)

  }

}