package com.kindone.infinitewall

import com.kindone.infinitewall.facades.DomTimer
import com.kindone.infinitewall.persistence.wsstorage.WebSocketPersistence
import com.kindone.infinitewall.persistence.wsstorage.socket._

import scala.scalajs.js

class BrowserModule(val url: String) {
  import com.softwaremill.macwire._

  lazy val wsFactory = wire[DomWebSocketFactory]

  lazy val domTimer = wire[DomTimer]

  def persistentWebSocket = wire[PersistentWebSocket]

  lazy val mailboxSocket = wire[MailboxWebSocket]

  lazy val websocketPersistence = wire[WebSocketPersistence]
}

// currently works as dummy entry class
object Environment extends js.JSApp {
  def main(): Unit = {

  }
}

