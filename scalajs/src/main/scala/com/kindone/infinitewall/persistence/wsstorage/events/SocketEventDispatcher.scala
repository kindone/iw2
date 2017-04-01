package com.kindone.infinitewall.persistence.wsstorage.events

import com.kindone.infinitewall.event.{ EventDispatcher, EventListener }

/**
 * Created by kindone on 2016. 5. 30..
 */
class SocketEventDispatcher extends SocketOpenCloseEventDispatcher
  with MessageReceiveEventEventDispatcher with SocketErrorEventDispatcher