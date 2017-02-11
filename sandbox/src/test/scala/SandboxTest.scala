import minitest.SimpleTestSuite
import sandbox.{ EventListener, ExampleObservable }
import sandbox.ObservableTest._

/**
 * Created by kindone on 2016. 12. 7..
 */

object SandboxTest extends SimpleTestSuite {
  test("function as type") {
    val eventListener: EventListener[Int] = { a: Int =>
      println("run: " + a.toString)
    }

    eventListener.apply(5)
    assert(1 + 1 != 3)
  }
}