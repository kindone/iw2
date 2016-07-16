package actors.util

import akka.actor.ActorRef

import scala.collection.immutable.BitSet
import scala.util.Try

/**
 * Created by kindone on 2016. 5. 23..
 */

class SubscriberSet {
  private var subscribers: Map[Int, ActorRef] = Map()
  private var set: BitSet = BitSet()

  def subscribe(actorRef: ActorRef) = {
    val newKey: Int = Try { set.lastKey }.getOrElse(-1) + 1
    set = set + newKey
    subscribers = subscribers + (newKey -> actorRef)
  }
  def unsubscribe(id: Int): Unit = {
    set = set - id
    subscribers - id
  }

  def map[T](block: (ActorRef) => T) = {
    subscribers.map { kv =>
      block(kv._2)
    }
  }
}
