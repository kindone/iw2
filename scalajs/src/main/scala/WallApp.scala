package com.kindone.infinitewall

import facades.CodeMirror
import org.scalajs.dom._

import scala.collection.mutable
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
    val controlPad = new ControlPad

    jQuery("#container-wrap").append(wall.element)
    wall.setup()
    jQuery("#container-wrap").append(controlPad.element)
    controlPad.setup()
    controlPad.setOnAddButtonClickListener({ () =>
      wall.createEmptySheet()
    })

    //    val options = js.Dictionary("rtlMoveVisually" -> false, "mode" -> js.Dictionary("name" -> "gfm", "highlightFormatting" -> true), "lineWrapping" -> true)
    //val cm = CodeMirror(jQuery(wall.element).find(".layer").get(0).asInstanceOf[Element], options)

  }
}
