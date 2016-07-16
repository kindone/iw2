package com.kindone.infinitewall.persistence.wsstorage.events

import com.kindone.infinitewall.events.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 5. 30..
 */
class WebSocketEventDispatcher extends SocketOpenCloseEventDispatcher
  with WallNotificationEventDispatcher with SheetNotificationEventDispatcher