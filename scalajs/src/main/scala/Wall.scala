package com.kindone.infinitewall

import org.scalajs.dom
import org.scalajs.dom._
import org.scalajs.jquery._

import scala.scalajs.js
import scalatags.JsDom.all._
import facades.JqWheel._

/**
 * Created by kindone on 2016. 1. 31..
 */
class Wall {

  private var scale = 1.0
  private var panX = 0.0
  private var panY = 0.0
  private var shiftX = 0.0
  private var shiftY = 0.0
  private var rotate = 0.0 //45.0

  val element = {
    val containerDiv = div(cls := "container")(
      div(id := "centerx")(),
      div(id := "centery")(),
      div(cls := "layer")(
        div(cls := "visual-dummy")()
      )
    )
    jQuery(containerDiv.render)
  }

  def setup(): Unit = {

    def width = jQuery(element).width()
    def height = jQuery(element).height()

    val layer = jQuery(element).find(".layer")

    layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX($scale) scaleY($scale) rotate(${rotate}deg)")

    jQuery(element).on("mousedown", (evt: JQueryEventObject) => {
      val (downX, downY) = (evt.pageX, evt.pageY)

      println("mousedown")

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
        panX = (totalX + (scale - 1.0) * width / 2) / scale
        panY = (totalY + (scale - 1.0) * height / 2) / scale
        shiftX = totalX - panX * scale
        shiftY = totalY - panY * scale

        layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX($scale) scaleY($scale) rotate(${rotate}deg)")
        jQuery("body")
          .off("mouseup", upHandler)
          .off("mousemove", moveHandler)
      }

      val wheelHandler: js.Function1[JQueryEventObject, js.Any] = (evt: JQueryEventObject) => {
        printf("%d %d %d\n", evt.deltaX, evt.deltaY, evt.deltaFactor)
        val prevScale = scale
        if (evt.deltaY > 0)
          scale = scale * (1.01)
        else if (evt.deltaY < 0)
          scale = scale * 0.99

        //          rotate += evt.deltaY

        shiftX = (1.0 - scale) * width / 2
        shiftY = (1.0 - scale) * height / 2

        layer.css("transform", s"translateX(${panX * scale + shiftX}px) translateY(${panY * scale + shiftY}px) scaleX(${scale}) scaleY(${scale}) rotate(${rotate}deg)")
      }

      jQuery("body").on("mousemove", moveHandler)
      jQuery("body").on("mouseup", upHandler)
      jQuery(dom.window).on("mousewheel", wheelHandler)

    })
  }

}