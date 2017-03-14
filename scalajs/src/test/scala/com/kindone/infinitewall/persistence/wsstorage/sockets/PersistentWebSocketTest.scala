package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.persistence.wsstorage.WebSocketFactory
import com.kindone.infinitewall.util.TestableTimer
import org.scalajs.dom.raw.{ CloseEvent, MessageEvent, ErrorEvent, Event }
import org.scalatest.FlatSpec
import org.scalatest._

import scala.scalajs.js
import scala.scalajs.js.JavaScriptException

/**
 * Created by kindone on 2017. 2. 18..
 */

class MockWebSocketInterface {
  def send(str: String): Unit = {
    sendCalled = true
  }
  def close(code: Int, reason: String): Unit = {}
  var onopen: js.Function1[Event, _] = null
  var onerror: js.Function1[ErrorEvent, _] = null
  var onmessage: js.Function1[MessageEvent, _] = null
  var onclose: js.Function1[CloseEvent, _] = null
  var sendCalled: Boolean = false
}

trait MockWebSocketFactory extends WebSocketFactory {
  def create(url: String): WebSocket
  val ws = new MockWebSocketInterface()
}

class PersistentWebSocketTest extends FlatSpec with org.scalamock.scalatest.MockFactory with Matchers with GivenWhenThen {

  "PersistentWebSocket" should "connect can fail" in {
    val factory = mock[MockWebSocketFactory]
    val ws = factory.ws
    (factory.create _).expects(*).throws(new JavaScriptException("synthetic error message"))

    val timer = new TestableTimer
    val pws = new PersistentWebSocket("", factory, timer)
  }

  "PersistentWebSocket" should "wait for reconnect on failed connect attempt" in {
    val factory = mock[MockWebSocketFactory]

    (factory.create _).expects(*).throws(new JavaScriptException("synthetic error message"))

    val timer = new TestableTimer
    val pws = new PersistentWebSocket("", factory, timer)

    timer.advance(PersistentWebSocket.BACKOFF_BASE_MS - 1)

    //    (factory.create _).expects(*).throws(new JavaScriptException("synthetic error message"))
    timer.firedEntries.size should be(0)

    println("")
  }

  "PersistentWebSocket" should "reconnect on failed connect attempt after wait" in {
    val factory = mock[MockWebSocketFactory]

    //reconnection will be tried and succeed after third attempt
    inSequence {
      (factory.create _).expects(*).throws(new JavaScriptException("synthetic error message 1"))
      (factory.create _).expects(*).throws(new JavaScriptException("synthetic error message 2"))
      (factory.create _).expects(*).returns(new WebSocket(new MockWebSocketInterface))
      (factory.create _).expects(*).returns(new WebSocket(new MockWebSocketInterface))
    }

    // there should be one timer for retry (fail 1)
    val timer = new TestableTimer
    val ws = factory.ws
    Given("a new instance")
    val pws = new PersistentWebSocket("", factory, timer)
    Then("timer should be scheduled as it failed to create websocket")
    timer.firedEntries.size should be(0)
    timer.scheduledEntries.size should be(1)

    println("time advanced by 500ms ")
    // timer will fire at first backoff and reschedule another (fail 2)
    timer.advance(PersistentWebSocket.BACKOFF_BASE_MS)

    println("timer should fire and reschedule another since it fails again")
    timer.firedEntries.size should be(1)
    timer.scheduledEntries.size should be(1)

    println("time advanced by another 1000ms")
    timer.advance(PersistentWebSocket.BACKOFF_BASE_MS * 2)

    println("timer was fired and creating websocket succeeds, but timer remains to be fired")
    timer.firedEntries.size should be(2)
    timer.scheduledEntries.size should be(1)

    println("time advanced by another 2000ms")
    //
    timer.advance(PersistentWebSocket.BACKOFF_BASE_MS * 4)
    timer.firedEntries.size should be(2)
    timer.scheduledEntries.size should be(1)
    timer.canceledEntries.size should be(0)

    timer.advance(PersistentWebSocket.CONNECTION_TIMEOUT_MS)
    timer.firedEntries.size should be(3)
    timer.advance(PersistentWebSocket.BACKOFF_BASE_MS * 8)
    println("socket emits open event")
    println("ws opening: " + ws.toString)
    pws.open()
    //ws.onopen(js.Dynamic.literal().asInstanceOf[Event]) // force onopen event

    println("onopen timer is canceled")
    timer.firedEntries.size should be(4)
    timer.scheduledEntries.size should be(0)
    timer.canceledEntries.size should be(1)

    When("nothing to fire and time flows")
    timer.advance(PersistentWebSocket.CONNECTION_TIMEOUT_MS * 100)

    Then("nothing happens")
    timer.firedEntries.size should be(4)
    timer.scheduledEntries.size should be(0)
    timer.canceledEntries.size should be(1)
  }

}
