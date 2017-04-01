package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.event.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 2. 24..
 */
trait ViewChangeEventDispatcher {

  private val dispatcher: EventDispatcher[ViewChangeEvent] = new EventDispatcher
  val VIEW_CHANGED = "viewChanged"

  def addOnViewChangedListener(handler: EventListener[ViewChangeEvent]) =
    dispatcher.addEventListener(VIEW_CHANGED, handler)

  def removeOnViewChangedListener(handler: EventListener[ViewChangeEvent]) =
    dispatcher.removeEventListener(VIEW_CHANGED, handler)

  def dispatchViewChangedEvent(evt: ViewChangeEvent) =
    dispatcher.dispatchEvent(VIEW_CHANGED, evt)
}
