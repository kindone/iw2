package com.kindone.infinitewall.persistence.api.events

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.events.Event

/**
 * Created by kindone on 2016. 5. 30..
 */
case class PersistenceUpdateEvent(logId: Long, action: Action) extends Event
