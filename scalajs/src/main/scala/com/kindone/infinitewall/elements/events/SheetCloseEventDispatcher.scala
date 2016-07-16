package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetCloseEventDispatcher {
  private val dispatcher: EventDispatcher[SheetCloseEvent] = new EventDispatcher
  val SHEET_CLOSE = "sheetClose"

  def addOnSheetCloseListener(handler: EventListener[SheetCloseEvent]) =
    dispatcher.addEventListener(SHEET_CLOSE, handler)

  def removeOnSheetCloseListener(handler: EventListener[SheetCloseEvent]) =
    dispatcher.removeEventListener(SHEET_CLOSE, handler)

  def dispatchSheetCloseEvent(evt: SheetCloseEvent) =
    dispatcher.dispatchEvent(SHEET_CLOSE, evt)
}
