package actors

import com.kindone.infinitewall.data.action.{ MoveSheetAction, SheetAction }
import com.kindone.infinitewall.data.versioncontrol.{ Branch, Change }
import com.typesafe.config.ConfigFactory
import org.scalatest.{ WordSpecLike, Matchers, FunSuite }
import akka.testkit.{ TestActors, DefaultTimeout, ImplicitSender, TestKit }
import scala.concurrent.duration._
import scala.collection.immutable
import akka.actor._
import akka.testkit.TestProbe

/**
 * Created by kindone on 2017. 3. 15..
 */

class SheetEventHubTest extends TestKit(ActorSystem(
  "TestKitUsageSpec"))
    with DefaultTimeout with ImplicitSender with WordSpecLike with org.scalamock.scalatest.MockFactory with Matchers {

  val actorRef = system.actorOf(SheetEventHub.props())

  "SheetEventHub" should {
    "do nothing on unknown message type" in {
      actorRef ! "unhandled message"
      expectNoMsg()
    }

    "handle changeOnWebSocket" in {
      val outActor = TestProbe()
      actorRef ! ChangeOnWebSocket(WebSocketContext(outActor.ref, 0, 0), Change(MoveSheetAction(0, 0, 0), 0, Branch.create()))

    }

    "handle ConnectionClosed" in {
      val outActor = TestProbe()
      actorRef ! ConnectionClosed(outActor.ref, Set(), Set())
      expectNoMsg()
      outActor.expectNoMsg()
    }
  }

}
