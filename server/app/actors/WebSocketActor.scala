package actors

import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.ws.ChangeRequest
import play.api.Logger
import upickle.default._

import scala.util.Try

/**
 * Created by kindone on 2016. 4. 17..
 */

case class UserGeneratedChange(outActor: ActorRef, userId: Long, reqId: Long, change: Change)
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
      val reqTry = Try { read[ChangeRequest](str) }

      for (req <- reqTry) {
        req match {
          case ChangeRequest(reqId, change @ Change(_, action: SubscribeWallEventAction, _)) =>
            subscribeWall(action.wallId)
            wallActor ! UserGeneratedChange(outActor, userId, reqId, change)
          case ChangeRequest(reqId, change @ Change(_, action: SubscribeSheetEventAction, _)) =>
            subscribeSheet(action.sheetId)
            sheetActor ! UserGeneratedChange(outActor, userId, reqId, change)
          case ChangeRequest(reqId, change @ Change(_, wallAction: WallAction, _)) =>
            wallActor ! UserGeneratedChange(outActor, userId, reqId, change)
          case ChangeRequest(reqId, change @ Change(_, sheetAction: SheetAction, _)) =>
            sheetActor ! UserGeneratedChange(outActor, userId, reqId, change)
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