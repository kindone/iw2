package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.data.action.ChangePanAction
import com.kindone.infinitewall.data.versioncontrol.{ Change, Branch }
import com.kindone.infinitewall.data.ws.{ Response, Notification }
import com.kindone.infinitewall.events.EventListener
import com.kindone.infinitewall.persistence.api.events.PersistenceUpdateEvent
import com.kindone.infinitewall.persistence.wsstorage.events.MessageReceiveEvent
import com.kindone.infinitewall.persistence.wsstorage.socket.{ MailboxSocket, PersistentWebSocket }
import org.scalatest.{ Matchers, FunSuite }
import upickle.default._
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2017. 3. 11..
 */

trait MockMailboxSocket extends MailboxSocket {

}

class MessageProcessorTest extends FunSuite with org.scalamock.scalatest.MockFactory with Matchers {
  val branch = Branch(Branch.genHash)

  test("testSend") {

    val mockSocket = mock[MailboxSocket]
    inSequence {
      (mockSocket.addOnReceiveListener _).expects(*)
      (mockSocket.setMailbox _).expects(*)
    }
    val processor = new MessageProcessor(branch, mockSocket)
    processor.send[Boolean](ChangePanAction(0, 10.0, 0.0))

  }

  test("testResponseReceive") {
    val mockSocket = mock[MailboxSocket]
    var theHandler: EventListener[MessageReceiveEvent] = { (e: MessageReceiveEvent) =>
      throw new RuntimeException("nothing assigned")
    }

    inSequence {
      (mockSocket.addOnReceiveListener _).expects(*) onCall { (handler: MessageReceiveEvent => Unit) =>
        theHandler = handler
      }

      (mockSocket.setMailbox _).expects(*)

      (mockSocket.setMailbox _).expects(*)

      (mockSocket.setMailbox _).expects(*)

      (mockSocket.dispatchReceiveEvent _).expects(*) onCall { (str: String) =>
        theHandler(MessageReceiveEvent(str))
      }

      (mockSocket.setMailbox _).expects(*)

      (mockSocket.dispatchReceiveEvent _).expects(*) onCall { (str: String) =>
        theHandler(MessageReceiveEvent(str))
      }

      (mockSocket.setMailbox _).expects(*)

      (mockSocket.dispatchReceiveEvent _).expects(*) onCall { (str: String) =>
        theHandler(MessageReceiveEvent(str))
      }

      (mockSocket.setMailbox _).expects(*)
    }

    val processor = new MessageProcessor(branch, mockSocket)

    // generate change
    val responseFuture1 = processor.send[Boolean](ChangePanAction(0, 0.0, 0.0))
    val responseFuture2 = processor.send[Boolean](ChangePanAction(0, 0.0, 0.0))
    val responseFuture3 = processor.send[Int](ChangePanAction(0, 0.0, 0.0))

    val response1 = Response(1L /*reqId*/ , 0 /*logid*/ , "true")
    val response2 = Response(2L /*reqId*/ , 0 /*logid*/ , "false")
    val response3 = Response(3L /*reqId*/ , 0 /*logid*/ , "5")

    processor.size should be(3)
    mockSocket.dispatchReceiveEvent(write(response1))
    processor.size should be(2)
    mockSocket.dispatchReceiveEvent(write(response2))
    processor.size should be(1)
    mockSocket.dispatchReceiveEvent(write(response3))
    processor.size should be(0)

    responseFuture1.foreach(value =>
      value should be(true)
    )
    responseFuture2.foreach(value =>
      value should be(false)
    )
    responseFuture3.foreach(value =>
      value should be(5)
    )

    // TODO: unmatched request id pair

  }

  test("testNotificationReceive") {

    val mockSocket = mock[MailboxSocket]
    var theHandler: EventListener[MessageReceiveEvent] = { (e: MessageReceiveEvent) =>
      throw new RuntimeException("nothing assigned")
    }

    val mockListener = mock[EventListener[PersistenceUpdateEvent]]

    inSequence {
      (mockSocket.addOnReceiveListener _).expects(*) onCall { (handler: MessageReceiveEvent => Unit) =>
        theHandler = handler
      }

      (mockSocket.dispatchReceiveEvent _).expects(*) onCall { (str: String) =>
        theHandler(MessageReceiveEvent(str))
      }

      (mockListener.apply _).expects(*) onCall { _: PersistenceUpdateEvent =>
        println("event handler called")
      }
    }

    val processor = new MessageProcessor(branch, mockSocket)

    processor.addOnWallNotificationListener(0, mockListener)
    // generate change
    val noti = Notification(0 /*logid*/ , Change(ChangePanAction(100L /*wallId*/ , 10.0, 0.0), 0 /*logid*/ , branch))
    mockSocket.dispatchReceiveEvent(write(noti))

  }

}
