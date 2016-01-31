package com.kindone.infinitewall

import org.scalajs.dom._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scalatags.JsDom.all._
import org.scalajs.dom
import org.scalajs.jquery.{ JQueryEventObject, jQuery, JQuery }
import facades.Velocity._

/**
 * Created by kindone on 2016. 1. 23..
 */

object WallApp extends JSApp {
  def main(): Unit = {

    val wall = new Wall()

    jQuery("#container-wrap").append(wall.element)
    wall.setup()

  }
}
