/**
 * Created by kindone on 2016. 12. 7..
 */
package object sandbox {
  type EventListener[T] = Function1[T, Unit]

  type DuckType = {
    def quack(): Unit
    def setter: Int
    def setter_=(value: Int): Unit
  }
}
