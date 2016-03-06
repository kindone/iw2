package com.kindone.infinitewall

import com.kindone.infinitewall.elements._
import com.kindone.infinitewall.events._
import com.kindone.infinitewall.facades.ShowdownConverter
import com.kindone.infinitewall.persistence.{ Sheet => SheetModel, _ }
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 1. 23..
 */

@JSExport
object WallApp {

  @JSExport
  def setup(wallId: Int): Unit = {

    // load data
    val localStorageManager = new LocalStorage()
    val persistence = new Persistence(localStorageManager)

    val stage = jQuery("#stage")
    val wallView = new WallView(wallId, persistence)

    stage.append(wallView.element)
    wallView.setup()

  }
}
