package actors.event

import actors.WebSocketContext
import akka.actor.ActorRef

/**
 * Created by kindone on 2016. 4. 24..
 */
sealed trait Event

case class AddEventListener(actorRef: ActorRef, context: WebSocketContext)
case class RemoveEventListener(actorRef: ActorRef)

case class SheetEvent() extends Event
case class WallEvent() extends Event

