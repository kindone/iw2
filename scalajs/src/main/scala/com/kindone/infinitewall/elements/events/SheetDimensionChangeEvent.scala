package com.kindone.infinitewall.elements.events

import com.kindone.infinitewall.event.Event

/**
 * Created by kindone on 2016. 2. 24..
 */
case class SheetDimensionChangeEvent(x: Double, y: Double, w: Double, h: Double)
  extends Event
