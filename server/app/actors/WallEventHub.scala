package actors

import actors.event.{ AddEventListener, RemoveEventListener }
import akka.actor.{ Actor, Props, ActorRef }
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.ws.Response
import com.kindone.infinitewall.data.Wall
import com.kindone.infinitewall.data.action._
import models.{ WallLog, WallLogManager, WallManager }
import play.api.Logger
import upickle.default._

import scala.collection.immutable.BitSet
import scala.util.Try

/**
 * Created by kindone on 2016. 4. 17..
 */

object WallEventHub {
  def props() = Props(new WallEventHub)
}

class WallEventHub extends EventProcessor {

  var openWalls = Map[Long, ActorRef]()

  def sendToWallEventActor(wallId: Long, out: ActorRef, msg: Any) = {
    // create event actor if not running
    if (!openWalls.contains(wallId)) {
      val actor = context.actorOf(WallEventProcessor.props, name = "WallEventProcessor_" + wallId)
      openWalls = openWalls + (wallId -> actor)
      Logger.info("created wall event actor of id: " + wallId)
    }

    for (actor <- openWalls.get(wallId))
      actor ! msg
  }

  def sendToOpenEventActor(wallId: Long, msg: Any) = {
    for (actor <- openWalls.get(wallId))
      actor ! msg
  }

  // life cycle management: create actor -> do action (+delete) poisonpill

  override def receive = {
    case change: ChangeOnWebSocket =>
      applyChange(change)
    case ConnectionClosed(out, _, walls) =>
      Logger.info("connection closed by: " + out.toString)
      for (wallId <- walls) {
        sendToOpenEventActor(wallId, RemoveEventListener(out))
      }
    case a @ _ =>
      super.receive(a)
      Logger.error("Unsupported message type to Wall Actor received" + a.toString)
  }

  def applyChange(change: ChangeOnWebSocket): Unit = {
    change match {
      case userChange @ ChangeOnWebSocket(context @ WebSocketContext(out, userId, reqId), Change(action: WallAction, baseLogId, _)) =>

        action match {

          case action @ SubscribeWallEventAction(wallId) =>
            sendToWallEventActor(wallId, out, AddEventListener(out, context))

          case a @ CreateWallAction(title, x, y, scale) =>
            val result = modelManager.createWall(0, a)(userId)
            out ! response(reqId, result.logId, Wall(result.id, 0, x, y, scale, title))

          case a @ DeleteWallAction(wallId) =>
            val result = modelManager.deleteWall(baseLogId, a)(userId)
            out ! response(reqId, result)
          case ListWallAction() =>
            out ! response(reqId, 0, modelManager.findAllWalls()(userId))
          case GetWallAction(id) =>
            val wall = modelManager.findWall(id)(userId)
            out ! response(reqId, 0, wall)
          case action: WallAlterAction =>
            sendToWallEventActor(action.wallId, out, userChange)

          case ListSheetAction(wallId) =>
            out ! response(reqId, 0, modelManager.getSheetsInWall(wallId)(userId).map(_.id))
          case _ =>
            Logger.error("Unsupported action type to Wall Actor received")
        }
    }
    // broadcast(logId, action) # noting to broadcast
  }
}