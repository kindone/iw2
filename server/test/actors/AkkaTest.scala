package actors

import com.typesafe.config.ConfigFactory
import org.scalatest.{ WordSpecLike, Matchers, FunSuite }
import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import akka.testkit.{ TestActors, DefaultTimeout, ImplicitSender, TestKit }
import scala.concurrent.duration._
import scala.collection.immutable

/**
 * Created by kindone on 2017. 3. 15..
 */

class ForwardingActor(next: ActorRef) extends Actor {
  def receive = {
    case msg => next ! msg
  }
}

class AkkaTest extends TestKit(ActorSystem(
  "TestKitUsageSpec"))
    with DefaultTimeout with ImplicitSender with WordSpecLike with org.scalamock.scalatest.MockFactory with Matchers {

  "An EchoActor" should {
    "Respond with the same message it receives" in {
      val echoRef = system.actorOf(TestActors.echoActorProps)
      echoRef ! "test"
      expectMsg("test")
    }
  }

  val forwardRef = system.actorOf(Props(classOf[ForwardingActor], testActor))

  "A ForwardingActor" should {
    "Forward a message it receives" in {
      within(500 millis) {
        forwardRef ! "test"
        expectMsg("test")
      }
    }
  }

}
