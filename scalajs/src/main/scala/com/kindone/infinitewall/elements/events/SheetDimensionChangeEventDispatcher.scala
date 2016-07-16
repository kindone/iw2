package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetDimensionChangeEventDispatcher {

  private val dispatcher: EventDispatcher[SheetDimensionChangeEvent] = new EventDispatcher
  private val DIMENSION_CHANGE = "dimensionChange"

  def addOnDimensionChangeListener(handler: EventListener[SheetDimensionChangeEvent]) =
    dispatcher.addEventListener(DIMENSION_CHANGE, handler)

  def removeOnDimensionChangeListener(handler: EventListener[SheetDimensionChangeEvent]) =
    dispatcher.removeEventListener(DIMENSION_CHANGE, handler)

  def dispatchDimensionChangeEvent(evt: SheetDimensionChangeEvent) =
    dispatcher.dispatchEvent(DIMENSION_CHANGE, evt)
}
