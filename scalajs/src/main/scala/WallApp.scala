package com.kindone.infinitewall

import facades.{ ShowdownConverter, CodeMirror }
import org.scalajs.dom._

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.JSApp
import scalatags.JsDom.all._
import org.scalajs.dom
import org.scalajs.jquery._
import facades.Velocity._

/**
 * Created by kindone on 2016. 1. 23..
 */

object WallApp extends JSApp {

  val overlay = {
    val html = div(cls := "overlay-transparent")()
    jQuery(html.render)
  }

  def main(): Unit = {

    val root = jQuery("#container-wrap")

    val wall = new Wall()
    val controlPad = new ControlPad

    root.append(wall.element)
    wall.setup()

    root.append(controlPad.element)
    controlPad.setup()

    root.append(overlay)

    val showdownConverter = new ShowdownConverter()

    val editor = new Editor(showdownConverter)
    root.append(editor.element)
    editor.setup()

    lazy val editorClose: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      editor.close()
      overlay.hide()
      overlay.off("mousedown", editorClose)
    }

    controlPad.setOnAddButtonClickListener({ () =>
      // create random sheet
      val sheet = new Sheet(js.Math.random() * 800, js.Math.random() * 800)
      wall.appendSheet(sheet)
      sheet.setOnDoubleClickListener((sheet: Sheet) => {
        editor.open(sheet)
        overlay.show()
        overlay.on("mousedown", editorClose)
      })
    })
  }
}
