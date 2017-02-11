package com.kindone.infinitewall.events

/**
 * Created by kindone on 2016. 2. 21..
 */
class EventDispatcher[T <: Event] {
  private var eventListeners: List[(String, EventListener[T])] = List()

  def addEventListener(key: String, listener: EventListener[T]) = {
    eventListeners = eventListeners :+ (key, listener)
  }

  def removeEventListener(key: String, listener: EventListener[T]) = {
    eventListeners = eventListeners.dropWhile(_ == (key, listener))
  }

  def dispatchEvent(key: String, evt: T) = {
    for (listener <- eventListeners) {
      if (listener._1 == key)
        listener._2.apply(evt)
    }
  }

  def isEmpty() = eventListeners.isEmpty

  def clear() = {
    eventListeners = List()
  }

  def clear(key: String) = {
    eventListeners = eventListeners.filterNot(_._1 == key)
  }
}
