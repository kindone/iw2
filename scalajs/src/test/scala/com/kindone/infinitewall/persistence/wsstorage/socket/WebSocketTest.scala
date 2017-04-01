package com.kindone.infinitewall.persistence.wsstorage.socket

import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.wsstorage.events.{ MessageReceiveEvent, SocketErrorEvent, SocketOpenCloseEvent }
import org.scalajs.dom.raw.{ CloseEvent, MessageEvent, ErrorEvent, Event }
import org.scalatest._
import org.scalamock.scalatest.MockFactory

import scala.scalajs.js

/**
 * Created by kindone on 2017. 2. 14..
 */

class WebSocketTest extends FlatSpec with MockFactory with Matchers {

  "WebSocket" should "respond to events" in {

    val wsi = new MockWebSocketInterface()
    val ws = new WebSocket(wsi)

    var (opened, closed, errorOccurred, received) = (false, false, false, false)

    ws.addOnSocketOpenListener((e: SocketOpenCloseEvent) => {
      opened = true
    })

    ws.addOnSocketCloseListener((e: SocketOpenCloseEvent) => {
      closed = true
    })

    ws.addOnErrorListener((e: SocketErrorEvent) => {
      errorOccurred = true
    })

    ws.addOnReceiveListener((e: MessageReceiveEvent) => {
      if (e.str == "message")
        received = true
    })

    ws.send("hello world")
    wsi.onopen(js.Dynamic.literal().asInstanceOf[Event])
    wsi.onclose(js.Dynamic.literal().asInstanceOf[CloseEvent])
    wsi.onerror(js.Dynamic.literal(message = "error message").asInstanceOf[ErrorEvent])
    wsi.onmessage(js.Dynamic.literal(data = "message").asInstanceOf[MessageEvent])
    ws.close()

    opened should be(true)
    closed should be(true)
    errorOccurred should be(true)
    received should be(true)
    wsi.sendCalled should be(true)

  }

}
