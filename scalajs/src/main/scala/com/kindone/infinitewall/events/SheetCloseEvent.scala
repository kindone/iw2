package com.kindone.infinitewall.events

import com.kindone.infinitewall.elements.Sheet

/**
 * Created by kindone on 2016. 2. 24..
 */
case class SheetCloseEvent(sheetId: Long, sheet: Sheet)
  extends Event
