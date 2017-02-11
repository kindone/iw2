package actors

import actors.event.{ RemoveEventListener, AddEventListener }
import akka.actor.{ Actor, ActorRef }
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.data.ws.{ Notification, Response }
import models.{ LogCreationResult, ModelManager }
import play.api.Logger
import upickle.default._

/**
 * Created by kindone on 2016. 12. 3..
 */
trait EventProcessor extends Actor {
  lazy val modelManager = new ModelManager
  var listeners = Set[ActorRef]()

  def addListener(listener: ActorRef) = {
    Logger.info("listening actor:" + listener.toString())
    listeners = listeners + listener
  }

  def removeListener(listener: ActorRef) = {
    Logger.info("unlistening actor:" + listener.toString())
    listeners = listeners - listener
  }

  def response[T: Writer](reqId: Long, logId: Long, msg: T) = {
    write(Response(reqId, logId, write[T](msg)))
  }

  def response(reqId: Long, result: LogCreationResult): String = {
    response(reqId, result.logId, result.success)
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
    case AddEventListener(listener) =>
      addListener(listener)
    case RemoveEventListener(listener) =>
      removeListener(listener)
    case change: ChangeOnWebSocket =>
      applyChange(change)
    case _ =>
  }

  def applyChange(change: ChangeOnWebSocket): Unit
}
