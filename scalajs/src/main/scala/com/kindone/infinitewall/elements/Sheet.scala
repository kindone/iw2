package com.kindone.infinitewall.elements

import com.kindone.infinitewall.elements.events.{ SheetEventDispatcher, SheetDimensionChangeEvent, SheetContentChangeEvent, SheetCloseEvent }
import com.kindone.infinitewall.event._
import com.kindone.infinitewall.facades.ShowdownConverter
import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

class Sheet(model: Sheet, converter: ShowdownConverter) extends Element with SheetEventDispatcher {

  val id = model.id

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
      div(cls := "sheet-resizehandle sheet-resizehandle-bottomright")(),
      div(cls := "sheet-closehandle")(
        span(cls := "glyphicon glyphicon-remove-sign")()
      )

    )
    jQuery(html.render)
  }

  private var stateId = 0L
  private var x: Double = 0.0
  private var y: Double = 0.0
  private var width: Double = 100
  private var height: Double = 100.0
  private var text: String = ""

  private var downX: Int = 0
  private var downY: Int = 0

  private var resizeDownX: Int = 0
  private var resizeDownY: Int = 0

  private var onDoubleClickListener: Option[(Sheet) => Unit] = None

  private val sheetTextElement = element.find(".sheet-text")

  def setOnDoubleClickListener(listener: (Sheet) => Unit) = {
    onDoubleClickListener = Some(listener)
  }

  def updateText(text: String) = {

  }

  def updateDimension() = {

  }

  def setText(text: String) = {
    this.text = text
    sheetTextElement.html(converter.makeHtml(text))
    dispatchContentChangeEvent(new SheetContentChangeEvent(text))
  }

  def getText(): String = text

  def setup() = setup((x) => 1.0)

  def setup(scaler: (Double) => Double) = {

    stateId = model.stateId
    x = model.x
    y = model.y
    width = model.width
    height = model.height
    text = model.text
    sheetTextElement.html(converter.makeHtml(text))

    element.css(js.Dictionary("left" -> s"${x}px", "top" -> s"${y}px",
      "width" -> s"${width}px", "height" -> s"${height}px"))

    val resizeHandle = element.find(".sheet-resizehandle-bottomright")
    val closeHandle = element.find(".sheet-closehandle")

    lazy val moveHandler: js.Function1[JQueryEventObject, js.Any] =
      (evt: JQueryEventObject) => {
        val diffX = evt.pageX - downX
        val diffY = evt.pageY - downY
        element.css(js.Dictionary(
          "left" -> s"${x + scaler(diffX)}px",
          "top" -> s"${y + scaler(diffY)}px"))
      }

    lazy val endMoveHandler: js.Function1[JQueryEventObject, js.Any] =
      (evt: JQueryEventObject) => {
        val diffX = evt.pageX - downX
        val diffY = evt.pageY - downY

        x += scaler(diffX)
        y += scaler(diffY)

        element.css(js.Dictionary("left" -> s"${x}px", "top" -> s"${y}px"))
        jQuery("body")
          .off("mouseup", endMoveHandler)
          .off("mousemove", moveHandler)

        dispatchDimensionChangeEvent(
          new SheetDimensionChangeEvent(x, y, width, height))
        element
      }

    jQuery(element).on("mousedown", (evt: JQueryEventObject) => {
      downX = evt.pageX
      downY = evt.pageY

      jQuery("body").on("mousemove", moveHandler)
      jQuery("body").on("mouseup", endMoveHandler)
      dom.console.info("mousedown - move")
      false
    })

    val registerResizeHandle: js.Function2[Int, dom.Element, js.Any] =
      (_: Int, handle: dom.Element) => {
        lazy val resizeHandler: js.Function1[JQueryEventObject, js.Any] =
          (evt: JQueryEventObject) => {
            val diffX = evt.pageX - resizeDownX
            val diffY = evt.pageY - resizeDownY
            jQuery(element).css(js.Dictionary(
              "width" -> s"${width + scaler(diffX)}px",
              "height" -> s"${height + scaler(diffY)}px"))
          }

        lazy val endResizeHandler: js.Function1[JQueryEventObject, js.Any] =
          (evt: JQueryEventObject) => {
            val diffX = evt.pageX - resizeDownX
            val diffY = evt.pageY - resizeDownY

            width += scaler(diffX)
            height += scaler(diffY)

            jQuery(element).css(js.Dictionary(
              "width" -> s"${width}px",
              "height" -> s"${height}px"))
            jQuery("body")
              .off("mouseup", endResizeHandler)
              .off("mousemove", resizeHandler)

            dispatchDimensionChangeEvent(
              new SheetDimensionChangeEvent(x, y, width, height))
            element
          }

        jQuery(handle).on("mousedown", (evt: JQueryEventObject) => {
          resizeDownX = evt.pageX
          resizeDownY = evt.pageY
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

    val self = this

    closeHandle.on("click", (evt: JQueryEventObject) => {
      dispatchSheetCloseEvent(new SheetCloseEvent(id, self))
    })
  }

  def cleanup() = {

  }
}
