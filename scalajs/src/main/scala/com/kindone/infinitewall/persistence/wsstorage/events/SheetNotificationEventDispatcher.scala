package com.kindone.infinitewall.persistence.wsstorage.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

/**
 * Created by kindone on 2016. 5. 30..
 */

trait SheetNotificationEventDispatcher {
  private var dispatchers: Map[Long, EventDispatcher[PersistenceUpdateEvent]] = Map()

  val SHEET_NOTIFICATION = "sheetNotification"

  def addOnSheetNotificationListener(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]) = {
    if (!dispatchers.isDefinedAt(sheetId))
      dispatchers = dispatchers + (sheetId -> new EventDispatcher[PersistenceUpdateEvent])
    for (dispatcher <- dispatchers.get(sheetId))
      dispatcher.addEventListener(this.SHEET_NOTIFICATION, handler)
  }

  def removeOnSheetNotificationListener(sheetId: Long, handler: EventListener[PersistenceUpdateEvent]) = {
    if (dispatchers.isDefinedAt(sheetId)) {
      for (dispatcher <- dispatchers.get(sheetId)) {
        dispatcher.removeEventListener(this.SHEET_NOTIFICATION, handler)
        if (dispatcher.isEmpty)
          dispatchers = dispatchers - sheetId
      }
    }
  }

  def dispatchSheetNotificationEvent(sheetId: Long, evt: PersistenceUpdateEvent) = {
    for (dispatcher <- dispatchers.get(sheetId)) {
      dispatcher.dispatchEvent(this.SHEET_NOTIFICATION, evt)
    }
  }

}
