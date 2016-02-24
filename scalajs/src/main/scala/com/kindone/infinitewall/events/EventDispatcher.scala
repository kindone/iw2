package com.kindone.infinitewall.events

/**
 * Created by kindone on 2016. 2. 21..
 */
class EventDispatcher[T <: Event] {
  private var eventListeners: List[EventListener[T]] = List()

  def addEventListener(key: String, listener: EventListener[T]) = {
    eventListeners = eventListeners :+ listener
  }

  def removeEventListener(key: String, listener: EventListener[T]) = {
    eventListeners = eventListeners.dropWhile(_ == listener)
  }

  def dispatchEvent(key: String, evt: T) = {
    for (listener <- eventListeners)
      listener.apply(evt)
  }
}
