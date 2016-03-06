package com.kindone.infinitewall.elements

import org.scalajs.jquery._

/**
 * Created by kindone on 2016. 3. 5..
 */
trait Element {
  val element: JQuery

  def setup(): Unit
}
