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

object WallEventHubActor {
  def props() = Props(new WallEventHubActor)
}

class WallEventHubActor extends Actor {
  lazy val wallManager = new WallManager
  lazy val wallLogManager = new WallLogManager

  def response[T: Writer](msgId: Long, logId: Long, msg: T) = {
    write(Response(msgId, logId, write[T](msg)))
  }

  def response(msgId: Long, result: (Long, Boolean)): String = {
    response(msgId, result._1, result._2)
  }

  var openWalls = Map[Long, ActorRef]()

  def sendToWallEventActor(wallId: Long, out: ActorRef, msg: Any) = {
    // create event actor if not running
    if (!openWalls.contains(wallId)) {
      val actor = context.actorOf(WallEventActor.props, name = "wallEventActor_" + wallId)
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

  def receive = {

    case userChange @ UserGeneratedChange(out, userId, reqId, Change(action: WallAction, baseLogId, _)) =>
      var result = (0L, false)

      action match {

        case action @ SubscribeWallEventAction(wallId) =>
          sendToWallEventActor(wallId, out, AddEventListener(out))

        case CreateWallAction(title, x, y, scale) =>
          val wallId = wallManager.create(Wall(0, 0, x, y, scale, title))(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 0, Some(write(action))))(userId)
          out ! response(reqId, result._1, Wall(wallId, 0, x, y, scale, title))
        case DeleteWallAction(wallId) =>
          wallManager.delete(wallId)(userId)
          result = wallLogManager.create(WallLog(wallId, baseLogId, 1, Some(write(action))))(userId)
          out ! response(reqId, result)

        case ListWallAction() =>
          out ! response(reqId, 0, wallManager.findAll()(userId))
        case GetWallAction(id) =>
          val wall = wallManager.find(id)(userId)
          out ! response(reqId, 0, wall)
        case action: WallAlterAction =>
          sendToWallEventActor(action.wallId, out, userChange)

        case ListSheetAction(wallId) =>
          out ! response(reqId, 0, wallManager.getSheets(wallId)(userId).map(_.id))
        case _ =>
          Logger.error("Unsupported action type to Wall Actor received")
      }
    // broadcast(logId, action) # noting to broadcast

    case ConnectionClosed(out, _, walls) =>
      Logger.info("connection closed by: " + out.toString)
      for (wallId <- walls) {
        sendToOpenEventActor(wallId, RemoveEventListener(out))
      }
    case a @ _ =>
      Logger.error("Unsupported message type to Wall Actor received" + a.toString)

  }
}