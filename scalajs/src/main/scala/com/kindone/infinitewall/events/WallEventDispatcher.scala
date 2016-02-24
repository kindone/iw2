package com.kindone.infinitewall.events

/**
 * Created by kindone on 2016. 2. 24..
 */
trait WallEventDispatcher extends SheetAppendedEventDispatcher
  with SheetRemovedEventDispatcher
  with ViewChangeEventDispatcher
