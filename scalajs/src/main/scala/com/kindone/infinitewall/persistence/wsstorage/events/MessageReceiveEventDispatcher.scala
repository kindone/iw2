package com.kindone.infinitewall.persistence.wsstorage.events

import com.kindone.infinitewall.events.{ EventListener, EventDispatcher }

/**
 * Created by kindone on 2016. 5. 31..
 */

class MessageReceiveEvent(str: String) extends WebSocketEvent

trait MessageReceiveEventEventDispatcher {
  private val dispatcher: EventDispatcher[MessageReceiveEvent] = new EventDispatcher

  val RECEIVE = "receive"

  def addOnReceiveListener(handler: EventListener[MessageReceiveEvent]) =
    dispatcher.addEventListener(RECEIVE, handler)

  def removeOnReceiveListener(handler: EventListener[MessageReceiveEvent]) =
    dispatcher.removeEventListener(RECEIVE, handler)

  def dispatchReceiveEvent(str: String) =
    dispatcher.dispatchEvent(RECEIVE, new MessageReceiveEvent(str))

}

