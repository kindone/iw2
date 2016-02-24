package com.kindone.infinitewall.events

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetRemovedEventDispatcher {
  private val dispatcher: EventDispatcher[SheetRemovedEvent] = new EventDispatcher
  val SHEET_REMOVED = "sheetRemoved"

  def addOnSheetRemovedListener(handler: EventListener[SheetRemovedEvent]) =
    dispatcher.addEventListener(SHEET_REMOVED, handler)

  def removeOnSheetRemovedListener(handler: EventListener[SheetRemovedEvent]) =
    dispatcher.removeEventListener(SHEET_REMOVED, handler)

  def dispatchSheetRemovedEvent(evt: SheetRemovedEvent) =
    dispatcher.dispatchEvent(SHEET_REMOVED, evt)
}
