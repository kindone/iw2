package com.kindone.infinitewall

import facades.ShowdownConverter
import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 2. 2..
 */
class Sheet(private var x: Double = 0.0, private var y: Double = 0.0, private var width: Double = 100, private var height: Double = 100.0) {

  val element = {
    val html = div(cls := "sheet")(
      div(cls := "sheet-text")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-top")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-left")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-right")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-bottom")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-topleft")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-topright")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-bottomleft")(),
      div(cls := "sheet-resizehandle sheet-resizehandle-bottomright")()
    )
    jQuery(html.render)
  }

  private var downX: Int = 0
  private var downY: Int = 0

  private var resizeDownX: Int = 0
  private var resizeDownY: Int = 0

  private var onDoubleClickListener: Option[(Sheet) => Unit] = None

  private val sheetTextElement = element.find(".sheet-text")
  private var sheetText = ""

  def setOnDoubleClickListener(listener: (Sheet) => Unit) = {
    onDoubleClickListener = Some(listener)
  }

  def setText(text: String, converter: ShowdownConverter) = {
    sheetText = text
    sheetTextElement.html(converter.makeHtml(text))
  }

  def getText(): String = {
    sheetText
  }

  def setup(scaler: (Double) => Double) = {

    element.css(js.Dictionary("left" -> s"${x}px", "top" -> s"${y}px",
      "width" -> s"${width}px", "height" -> s"${height}px"))

    val resizeHandle = element.find(".sheet-resizehandle-bottomright")

    lazy val moveHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      val diffX = evt.pageX - downX
      val diffY = evt.pageY - downY
      element.css(js.Dictionary("left" -> s"${x + scaler(diffX)}px", "top" -> s"${y + scaler(diffY)}px"))
    }

    lazy val endMoveHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      val diffX = evt.pageX - downX
      val diffY = evt.pageY - downY

      x += scaler(diffX)
      y += scaler(diffY)

      element.css(js.Dictionary("left" -> s"${x}px", "top" -> s"${y}px"))
      jQuery("body")
        .off("mouseup", endMoveHandler)
        .off("mousemove", moveHandler)
    }

    jQuery(element).on("mousedown", (evt: JQueryEventObject) => {
      downX = evt.pageX
      downY = evt.pageY

      jQuery("body").on("mousemove", moveHandler)
      jQuery("body").on("mouseup", endMoveHandler)
      println("mousedown - move")
      false
    })

    val registerResizeHandle: js.Function2[js.Any, dom.Element, js.Any] = (_: js.Any, handle: dom.Element) => {
      lazy val resizeHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
        val diffX = evt.pageX - resizeDownX
        val diffY = evt.pageY - resizeDownY
        jQuery(element).css(js.Dictionary("width" -> s"${width + scaler(diffX)}px", "height" -> s"${height + scaler(diffY)}px"))
      }

      lazy val endResizeHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
        val diffX = evt.pageX - resizeDownX
        val diffY = evt.pageY - resizeDownY

        width += scaler(diffX)
        height += scaler(diffY)

        jQuery(element).css(js.Dictionary("width" -> s"${width}px", "height" -> s"${height}px"))
        jQuery("body")
          .off("mouseup", endResizeHandler)
          .off("mousemove", resizeHandler)
      }

      jQuery(handle).on("mousedown", (evt: JQueryEventObject) => {
        resizeDownX = evt.pageX
        resizeDownY = evt.pageY
        println("mousedown - resize")
        jQuery("body").on("mousemove", resizeHandler)
        jQuery("body").on("mouseup", endResizeHandler)
        false
      })
    }

    resizeHandle.each(registerResizeHandle)

    jQuery(element).on("dblclick", (evt: JQueryEventObject) => {
      val sheet: Sheet = this
      onDoubleClickListener.map(_.apply(sheet))
    })
  }
}
