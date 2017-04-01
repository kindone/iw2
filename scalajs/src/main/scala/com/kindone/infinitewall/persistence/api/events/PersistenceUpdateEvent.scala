package com.kindone.infinitewall.persistence.api.events

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.data.versioncontrol.Change
import com.kindone.infinitewall.event.Event

/**
 * Created by kindone on 2016. 5. 30..
 */
case class PersistenceUpdateEvent(logId: Long, change: Change) extends Event
