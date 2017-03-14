package com.kindone.infinitewall.persistence.wsstorage.sockets

import java.util.UUID

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.data.versioncontrol.{ Change, Read }
import com.kindone.infinitewall.data.ws._
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.{ MessageReceiveEvent, SocketEventDispatcher, SocketOpenCloseEvent }
import com.kindone.infinitewall.util.Timer
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js

/**
 * Created by kindone on 2016. 10. 24..
 *
 *
 */

object MailboxWebSocket {
  val RETRY_TIMEOUT = 1000
}

class MailboxWebSocket(persistentSocket: PersistentSocket, timer: Timer)
    extends MailboxSocket {
  import MailboxWebSocket._

  persistentSocket.addOnReceiveListener(receive _)

  private var timeoutHandle: Option[UUID] = None
  private var mailBox: List[String] = List()

  private def restartTimer() = {

    timeoutHandle = Some(timer.setTimeout(RETRY_TIMEOUT) {
      sendMailBox()
    })
  }

  private def stopTimer() = {
    timeoutHandle.foreach(timer.clearTimeout(_))
    timeoutHandle = None
  }

  private def sendMailBox(): Unit = {
    for (msg <- mailBox)
      persistentSocket.send(msg)
    restartTimer()
  }

  def setMailbox(messages: List[String]): Unit = {
    mailBox = messages
    sendMailBox()
  }

  def clearMailbox(): Unit = {
    mailBox = List()
    stopTimer()
  }

  private def receive(event: MessageReceiveEvent) = {
    dispatchReceiveEvent(event.str)
  }

}
