package com.kindone.infinitewall

import org.scalajs.dom
import org.scalajs.jquery._
import scalatags.JsDom.all._
import scala.scalajs.js

/**
 * Created by kindone on 2016. 2. 2..
 */
class ControlPad {

  private var onAddButtonClickListener: Option[() => Unit] = None

  val element = {
    val html = div(cls := "controlpad")(
      button(cls := "btn btn-default button-add", `type` := "submit")("Add")
    )
    jQuery(html.render)
  }

  def setOnAddButtonClickListener(listener: () => Unit) = {
    onAddButtonClickListener = Some(listener)
  }

  def setup(): Unit = {
    jQuery(element).on("click", (evt: JQueryEventObject) => {
      onAddButtonClickListener.map(_.apply())
    })
  }
}
