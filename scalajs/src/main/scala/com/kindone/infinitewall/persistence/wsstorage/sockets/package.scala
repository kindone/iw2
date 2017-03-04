package com.kindone.infinitewall.persistence.wsstorage

import org.scalajs.dom.raw.{ CloseEvent, ErrorEvent, MessageEvent, Event }

import scala.scalajs.js

/**
 * Created by kindone on 2017. 2. 12..
 */
package object sockets {

  // system interface
  type WebSocketInterface = {
    def onopen: scalajs.js.Function1[Event, _]
    def onopen_=(func: scalajs.js.Function1[Event, _]): Unit

    def onerror: scalajs.js.Function1[ErrorEvent, _]
    def onerror_=(func: scalajs.js.Function1[ErrorEvent, _]): Unit

    def onmessage: scalajs.js.Function1[MessageEvent, _]
    def onmessage_=(func: scalajs.js.Function1[MessageEvent, _]): Unit

    def onclose: scalajs.js.Function1[CloseEvent, _]
    def onclose_=(func: scalajs.js.Function1[CloseEvent, _]): Unit

    def close(code: Int, reason: String): Unit
    def send(str: String): Unit

    def toString(): String
  }
}
