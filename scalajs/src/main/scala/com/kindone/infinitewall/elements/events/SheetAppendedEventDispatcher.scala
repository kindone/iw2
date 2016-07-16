package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetAppendedEventDispatcher {
  private val dispatcher: EventDispatcher[SheetAppendedEvent] = new EventDispatcher
  val SHEET_APPENDED = "sheetAppended"

  def addOnSheetAppendedListener(handler: EventListener[SheetAppendedEvent]) =
    dispatcher.addEventListener(SHEET_APPENDED, handler)

  def removeOnSheetAppendedListener(handler: EventListener[SheetAppendedEvent]) =
    dispatcher.removeEventListener(SHEET_APPENDED, handler)

  def dispatchSheetAppendedEvent(evt: SheetAppendedEvent) =
    dispatcher.dispatchEvent(SHEET_APPENDED, evt)
}
