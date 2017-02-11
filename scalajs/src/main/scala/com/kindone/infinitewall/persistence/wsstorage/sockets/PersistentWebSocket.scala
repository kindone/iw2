package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.wsstorage.events._
import upickle.default._
import org.scalajs.dom
import scala.scalajs.js.JavaScriptException
import scala.scalajs.js.timers._

/**
 * Created by kindone on 2016. 4. 17..
 */
object PersistentWebSocket {
  val BACKOFF_BASE_MS = 500
}

class PersistentWebSocket(baseUrl: String) extends PersistentSocket {

  private var socket: Option[Socket] = connect(baseUrl)
  private var backOff = 0

  def send(str: String): Unit = {
    socket.foreach(_.send(str))
  }

  def onReceive(e: MessageReceiveEvent) = {
    dispatchReceiveEvent(e.str)
  }

  private def connect(baseUrl: String): Option[WebSocket] = {
    dom.console.info(s"Trying WebSocket reconnection(${backOff}) ... ")

    try {
      val ws = new WebSocket(baseUrl)
      ws.addOnReceiveListener(onReceive _)
      ws.addOnSocketOpenListener({ e: SocketOpenCloseEvent =>
        resetBackOff()
      })

      ws.addOnSocketCloseListener({ e: SocketOpenCloseEvent =>
        reconnect()
        increaseBackOff()
      })

      dom.console.info("WebSocket connected")
      Some(ws)
    } catch {
      case err: JavaScriptException =>
        dom.console.error("Error occurred in creating WebSocket object: " + err.toString())
        increaseBackOff()
        reconnect()
        increaseBackOff()
        None
    }
  }

  private def reconnect(): Unit = {
    socket.foreach(_.close())
    socket = None

    runWithBackOff {
      socket = connect(baseUrl)
    }
  }

  private def runWithBackOff(block: => Unit): Unit = {
    if (backOff == 0) {
      block
    } else {
      setTimeout(Math.pow(2.0, backOff) * PersistentWebSocket.BACKOFF_BASE_MS) {
        block
      }
    }
  }

  private def increaseBackOff(): Unit = {
    if (backOff < 5)
      backOff += 1
  }

  private def resetBackOff(): Unit = {
    backOff = 0
  }

}
