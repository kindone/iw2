package com.kindone.infinitewall

/**
 * Created by kindone on 2016. 12. 7..
 */
package object events {
  type EventListener[T] = Function1[T, Unit]
}