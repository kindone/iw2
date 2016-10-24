package com.kindone.infinitewall.elements

import com.kindone.infinitewall.elements.events.{ WallEventDispatcher, ViewChangeEvent, SheetRemovedEvent, SheetAppendedEvent }
import com.kindone.infinitewall.events._
import com.kindone.infinitewall.facades.JqWheel._
import com.kindone.infinitewall.data.{ Wall => WallModel }
import org.scalajs.dom
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._

class Wall(initial: WallModel) extends Element with WallEventDispatcher {

  val id = initial.id

  private var stateId = 0L
  private var scale = 1.0
  private var panX = 0.0
  private var panY = 0.0
  private var shiftX = 0.0
  private var shiftY = 0.0
  private var rotate = 0.0 //45.0
  private var downX: Int = 0
  private var downY: Int = 0

  private def width = jQuery(element).width()
  private def height = jQuery(element).height()

  private def recalculateTranslation(totalX: Double, totalY: Double) = {
    panX = (totalX + (scale - 1.0) * width / 2) / scale
    panY = (totalY + (scale - 1.0) * height / 2) / scale
    shiftX = totalX - panX * scale
    shiftY = totalY - panY * scale
  }

  val element = {
    val containerDiv = div(cls := "wall")(
      //      div(id := "centerx")(),
      //      div(id := "centery")(),
      div(cls := "layer")( //        div(cls := "visual-dummy")()
      )
    )
    jQuery(containerDiv.render)
  }

  val layer = element.find(".layer")

  def appendSheet(sheet: Sheet) = {
    layer.append(sheet.element)
    sheet.setup(scaler)
    dispatchSheetAppendedEvent(SheetAppendedEvent(sheet.id))
  }

  def removeSheet(sheet: Sheet) = {
    sheet.element.remove()
    dispatchSheetRemovedEvent(new SheetRemovedEvent(sheet.id))
  }

  def scaler(distance: Double) = {
    distance / scale
  }

  def center = (panX, panY)

  def setup(): Unit = {

    stateId = initial.stateId
    scale = initial.scale
    recalculateTranslation(initial.x, initial.y)

    val layer = jQuery(element).find(".layer")

    layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX($scale) scaleY($scale) rotate(${rotate}deg)")

    lazy val moveHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      val diffX = evt.pageX - downX
      val diffY = evt.pageY - downY
      layer.css("transform", s"translateX(${panX * scale + diffX + shiftX}px) translateY(${panY * scale + diffY + shiftY}px) scaleX($scale) scaleY($scale) rotate(${rotate}deg)")
    }

    lazy val upHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      val diffX = evt.pageX - downX
      val diffY = evt.pageY - downY

      val totalX = panX * scale + shiftX + diffX
      val totalY = panY * scale + shiftY + diffY

      // recalculate components from diff
      recalculateTranslation(totalX, totalY)

      layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX($scale) scaleY($scale) rotate(${rotate}deg)")
      jQuery("body")
        .off("mouseup", upHandler)
        .off("mousemove", moveHandler)

      dispatchViewChangedEvent(new ViewChangeEvent(panX * scale + shiftX, panY * scale + shiftY, scale))

      element
    }

    val wheelHandler: js.Function1[JQueryEventObject, Boolean] = (evt: JQueryEventObject) => {
      printf("%d %d %d\n", evt.deltaX, evt.deltaY, evt.deltaFactor)
      val prevScale = scale
      if (evt.deltaY > 0)
        scale = scale * (1.01)
      else if (evt.deltaY < 0)
        scale = scale * 0.99

      shiftX = (1.0 - scale) * width / 2
      shiftY = (1.0 - scale) * height / 2

      layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX(${scale}) scaleY(${scale}) rotate(${rotate}deg)")
      dispatchViewChangedEvent(new ViewChangeEvent(panX * scale + shiftX, panY * scale + shiftY, scale))
      false
    }

    val downHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
      downX = evt.pageX
      downY = evt.pageY

      println("mousedown - pan")
      jQuery("body").on("mousemove", moveHandler)
      jQuery("body").on("mouseup", upHandler)
    }

    jQuery(element).on("mousedown", downHandler)
    jQuery(dom.window).on("mousewheel", wheelHandler)
  }

}