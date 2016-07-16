package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.events.Event

/**
 * Created by kindone on 2016. 2. 24..
 */
case class SheetContentChangeEvent(content: String, pos: Int = 0)
  extends Event
