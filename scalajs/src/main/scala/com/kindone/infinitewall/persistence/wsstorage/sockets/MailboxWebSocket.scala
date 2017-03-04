package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.data.action.Action
import com.kindone.infinitewall.data.versioncontrol.{ Change, Read }
import com.kindone.infinitewall.data.ws._
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.{ MessageReceiveEvent, SocketEventDispatcher, SocketOpenCloseEvent }
import upickle.default._

import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.timers.SetTimeoutHandle

/**
 * Created by kindone on 2016. 10. 24..
 *
 *
 */
class MailboxWebSocket(persistentSocket: PersistentSocket)
    extends MailboxSocket {
  persistentSocket.addOnReceiveListener(receive _)

  private var timeoutHandle: Option[SetTimeoutHandle] = None
  private var mailBox: List[String] = List()

  private def restartTimer() = {
    timeoutHandle = Some(js.timers.setTimeout(1000) {
      sendMailBox()
    })
  }

  private def stopTimer() = {
    timeoutHandle.foreach(js.timers.clearTimeout(_))
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
