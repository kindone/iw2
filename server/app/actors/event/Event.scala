package actors.event

import akka.actor.ActorRef

/**
 * Created by kindone on 2016. 4. 24..
 */
sealed trait Event

case class AddEventListener(actorRef: ActorRef)
case class RemoveEventListener(actorRef: ActorRef)

case class SheetEvent() extends Event
case class WallEvent() extends Event

