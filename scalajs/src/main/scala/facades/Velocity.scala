package facades

import org.scalajs.jquery.JQuery

import scala.scalajs.js

/**
 * Created by kindone on 2016. 1. 24..
 */
@js.native
class Velocity extends js.Object {
  def velocity(attrs: js.Any): Velocity = ???
  def velocity(attrs: js.Any, options: js.Any): Velocity = ???
}

object Velocity {
  implicit def jq2velocity(jq: JQuery): Velocity = jq.asInstanceOf[Velocity]
}

