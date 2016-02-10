package com.kindone.infinitewall

import facades.{ ShowdownConverter, CodeMirror }
import org.scalajs.dom._
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 2. 10..
 */
class Editor(showdown: ShowdownConverter) {
  val element = {
    val html = div(cls := "editor-wrapper")(
      div(cls := "editor")()
    )
    jQuery(html.render)
  }

  val editorElement = element.children()

  private var cmOpt: Option[CodeMirror] = None
  private var sheetOpt: Option[Sheet] = None

  def setup() = {
    cmOpt = Some(CodeMirror(editorElement.get(0).asInstanceOf[Element], js.Dictionary("rtlMoveVisually" -> false, "mode" -> js.Dictionary("name" -> "gfm", "highlightFormatting" -> true), "lineWrapping" -> true)))
  }

  def open(sheet: Sheet) = {
    sheetOpt = Some(sheet)

    jQuery(editorElement).show()
    cmOpt.foreach(_.setValue(sheet.getText()))
  }

  def close() = {
    for (sheet <- sheetOpt; cm <- cmOpt)
      sheet.setText(cm.getValue(), showdown)

    jQuery(editorElement).hide()
  }

}
