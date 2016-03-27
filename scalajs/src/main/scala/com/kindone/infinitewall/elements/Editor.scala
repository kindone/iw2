package com.kindone.infinitewall.elements

import com.kindone.infinitewall.facades.{ CodeMirror, ShowdownConverter }
import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.dom.{ Element => DomElement }
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 2. 10..
 */
class Editor(showdown: ShowdownConverter) extends Element {
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
    cmOpt = Some(CodeMirror(editorElement.get(0).asInstanceOf[DomElement],
      js.Dictionary("rtlMoveVisually" -> false,
        "mode" -> js.Dictionary(
          "name" -> "markdown",
          "highlightFormatting" -> true),
        "lineWrapping" -> true)))

    val wheelHandler: js.Function1[JQueryEventObject, Unit] = (evt: JQueryEventObject) => {
      evt.stopPropagation() // prevent wall to be scaled when wheel-scrolled inside editor
    }

    jQuery(editorElement).on("mousewheel", wheelHandler)
  }

  private var intervalHandle = 0

  def open(sheet: Sheet) = {
    sheetOpt = Some(sheet)

    jQuery(editorElement).show()
    cmOpt.foreach(_.setValue(sheet.getText()))
    intervalHandle = dom.setInterval(() => {
      for (sheet <- sheetOpt; cm <- cmOpt)
        sheet.setText(cm.getValue())
    }, 500)

  }

  def close() = {
    for (sheet <- sheetOpt; cm <- cmOpt)
      sheet.setText(cm.getValue())

    jQuery(editorElement).hide()
    dom.clearInterval(intervalHandle)
  }

}
