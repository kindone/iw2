package com.kindone.infinitewall.persistence.wsstorage.events

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.events.{ EventListener, EventDispatcher }
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent

/**
 * Created by kindone on 2016. 5. 30..
 */

trait WallNotificationEventDispatcher {
  private var dispatchers: Map[Long, EventDispatcher[PersistenceUpdateEvent]] = Map()

  val WALL_NOTIFICATION = "wallNotification"

  def addOnWallNotificationListener(wallId: Long, handler: EventListener[PersistenceUpdateEvent]) = {
    if (!dispatchers.isDefinedAt(wallId))
      dispatchers = dispatchers + (wallId -> new EventDispatcher[PersistenceUpdateEvent])
    for (dispatcher <- dispatchers.get(wallId))
      dispatcher.addEventListener(this.WALL_NOTIFICATION, handler)
  }

  def removeOnWallNotificationListener(wallId: Long, handler: EventListener[PersistenceUpdateEvent]) = {
    if (dispatchers.isDefinedAt(wallId)) {
      for (dispatcher <- dispatchers.get(wallId)) {
        dispatcher.removeEventListener(this.WALL_NOTIFICATION, handler)
        if (dispatcher.isEmpty)
          dispatchers = dispatchers - wallId
      }
    }
  }

  def dispatchWallNotificationEvent(wallId: Long, evt: PersistenceUpdateEvent) = {
    for (dispatcher <- dispatchers.get(wallId)) {
      dispatcher.dispatchEvent(this.WALL_NOTIFICATION, evt)
    }
  }

}
