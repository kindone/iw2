package facades

import org.scalajs.jquery.{ JQueryEventObject, JQuery }

import scala.scalajs.js

/**
 * Created by kindone on 2016. 1. 24..
 */
@js.native
class JqWheel extends js.Object {
  def deltaX: Double = js.native
  def deltaY: Double = js.native
  def deltaFactor: Double = js.native
}

object JqWheel {
  implicit def jqevt2JqWheel(jqevt: JQueryEventObject): JqWheel = jqevt.asInstanceOf[JqWheel]
}

