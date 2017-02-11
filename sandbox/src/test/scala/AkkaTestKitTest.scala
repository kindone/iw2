import minitest.SimpleTestSuite
import akka.testkit.{ TestActorRef }
/**
 * Created by kindone on 2016. 11. 28..
 */
object AkkaTestKitTest extends SimpleTestSuite {
  test("should be") {

    assert(1 + 1 != 3)
  }
}