package sandbox

import minitest._
import monix.execution.Scheduler.Implicits.global

object ObservableTest extends SimpleTestSuite {
  test("should be") {
    val ob = new ExampleObservable
    val sub = ob.observable.map { item =>
      println(item)
    }.subscribe()
    assert(1 + 1 != 3)
  }

}