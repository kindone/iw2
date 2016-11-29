package sandbox

import monix.execution.Cancelable
import monix.reactive.Observable
import monix.reactive.observers.Subscriber

/**
 * Created by kindone on 2016. 11. 20..
 */
class ExampleObservable {
  val observable: Observable[Int] = Observable.fromIterable(Seq(1, 23, 3))
}
