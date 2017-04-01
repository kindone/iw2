package com.kindone.infinitewall.event

import org.scalajs.dom

/**
 * Created by kindone on 2016. 2. 21..
 */
class EventDispatcher[T <: Event] {
  private var eventListeners: List[(String, EventListener[T])] = List()

  def addEventListener(key: String, listener: EventListener[T]) = {
    eventListeners :+= (key, listener)
    dom.console.info("added event:" + this.toString + "," + key)
  }

  def removeEventListener(key: String, listener: EventListener[T]) = {
    eventListeners = eventListeners.dropWhile(_ == (key, listener))
    dom.console.info("removed event:" + eventListeners.size)
  }

  def dispatchEvent(key: String, evt: T) = {
    if (!eventListeners.isEmpty)
      dom.console.info("dispatching event: " + this.toString + "," + eventListeners(0).toString())
    else
      dom.console.info("dispatching event: " + this.toString + ", nothing")
    for (listener <- eventListeners) {
      if (listener._1 == key) {
        listener._2.apply(evt)
      }
    }
  }

  def isEmpty() = eventListeners.isEmpty

  def clear() = {
    eventListeners = List()
  }

  def clear(key: String) = {
    eventListeners = eventListeners.filterNot(_._1 == key)
  }

  def numEventListeners(key: String) = {
    eventListeners.filter(_._1 == key).size
  }
}
