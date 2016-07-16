package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetContentChangeEventDispatcher {

  private val dispatcher: EventDispatcher[SheetContentChangeEvent] = new EventDispatcher
  private val CONTENT_CHANGE = "contentChange"

  def addOnContentChangeListener(handler: EventListener[SheetContentChangeEvent]) =
    dispatcher.addEventListener(CONTENT_CHANGE, handler)

  def removeOnContentChangeListener(handler: EventListener[SheetContentChangeEvent]) =
    dispatcher.removeEventListener(CONTENT_CHANGE, handler)

  def dispatchContentChangeEvent(evt: SheetContentChangeEvent) =
    dispatcher.dispatchEvent(CONTENT_CHANGE, evt)
}
