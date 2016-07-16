package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.elements.Sheet
import com.kindone.infinitewall.events.Event

/**
 * Created by kindone on 2016. 2. 24..
 */
case class SheetCloseEvent(sheetId: Long, sheet: Sheet)
  extends Event
