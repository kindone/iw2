package com.kindone.infinitewall.events

/**
 * Created by kindone on 2016. 2. 24..
 */
trait SheetEventDispatcher extends SheetContentChangeEventDispatcher
  with SheetDimensionChangeEventDispatcher
  with SheetCloseEventDispatcher
