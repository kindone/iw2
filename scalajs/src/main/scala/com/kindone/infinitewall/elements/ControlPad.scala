package com.kindone.infinitewall.elements

import org.scalajs.jquery._

import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 2. 2..
 */
class ControlPad extends Element {

  private var onAddButtonClickListener: Option[() => Unit] = None
  private var onClearDBButtonClickListener: Option[() => Unit] = None

  val element = {
    val html = div(cls := "controlpad")(
      button(id := "btn-addsheet", cls := "btn btn-default button-add", `type` := "submit")("Add"),
      button(id := "btn-cleardb", cls := "btn btn-default button-add", `type` := "submit")("ClearDB")
    )
    jQuery(html.render)
  }

  def setOnAddButtonClickListener(listener: () => Unit) = {
    onAddButtonClickListener = Some(listener)
  }

  def setOnClearDBButtonClickListener(listener: () => Unit) = {
    onClearDBButtonClickListener = Some(listener)
  }

  def setup(): Unit = {
    jQuery(element).find("#btn-addsheet").on("click", (evt: JQueryEventObject) => {
      onAddButtonClickListener.map(_.apply())
    })

    jQuery(element).find("#btn-cleardb").on("click", (evt: JQueryEventObject) => {
      onClearDBButtonClickListener.map(_.apply())
    })
  }
}
