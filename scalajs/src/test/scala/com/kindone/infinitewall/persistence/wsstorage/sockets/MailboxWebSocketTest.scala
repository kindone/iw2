package com.kindone.infinitewall.persistence.wsstorage.sockets

import com.kindone.infinitewall.util.TestableTimer
import org.scalatest.FunSuite

import scala.scalajs.js.JavaScriptException

/**
 * Created by kindone on 2017. 3. 4..
 */
class MailboxWebSocketTest extends FunSuite with org.scalamock.scalatest.MockFactory {

  test("testSetMailbox") {
    val testTimer = new TestableTimer
    val mockPersistentSocket = mock[PersistentSocket]
    inSequence {
      (mockPersistentSocket.addOnReceiveListener _).expects(*)
      (mockPersistentSocket.send _).expects("aa")
      (mockPersistentSocket.send _).expects("bb")
      // retry
      (mockPersistentSocket.send _).expects("aa")
      (mockPersistentSocket.send _).expects("bb")
    }
    val mailboxSocket = new MailboxWebSocket(mockPersistentSocket, testTimer)

    mailboxSocket.setMailbox(List("aa", "bb"))
    testTimer.advance(MailboxWebSocket.RETRY_TIMEOUT)
    mailboxSocket.clearMailbox()
    testTimer.advance(MailboxWebSocket.RETRY_TIMEOUT)
  }

}
