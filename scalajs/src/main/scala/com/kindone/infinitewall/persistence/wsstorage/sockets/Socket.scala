package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.persistence.wsstorage.events.{ MessageReceiveEventEventDispatcher, SocketEventDispatcher }

/**
 * Created by kindone on 2016. 12. 5..
 */
object Socket {
  type Protocol = Int
}

trait Socket extends SocketEventDispatcher {
  def close(): Unit
  def send(str: String): Unit
}

trait PersistentSocket extends MessageReceiveEventEventDispatcher {
  def send(str: String): Unit
}

trait MailboxSocket extends MessageReceiveEventEventDispatcher {
  def setMailbox(messages: List[String]): Unit
  def clearMailbox(): Unit
}