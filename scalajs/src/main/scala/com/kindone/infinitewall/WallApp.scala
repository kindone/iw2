package com.kindone.infinitewall

import com.kindone.infinitewall.elements._
import com.kindone.infinitewall.events._
import com.kindone.infinitewall.facades.ShowdownConverter
import com.kindone.infinitewall.persistence.api.Persistence
import com.kindone.infinitewall.persistence.localstorage.{ LocalPersistence, LocalStorage }
import com.kindone.infinitewall.data.{ Sheet => SheetModel, _ }
import com.kindone.infinitewall.persistence.remotestorage.RemotePersistence
import org.scalajs.jquery._

import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

/**
 * Created by kindone on 2016. 1. 23..
 */

@JSExport
class WallApp(val useLocalStorage: Boolean = true) {

  val persistence: Persistence = {
    if (useLocalStorage)
      new LocalPersistence()
    else
      new RemotePersistence("")
  }

  @JSExport
  def setup(wallId: Int): Unit = {

    val stage = jQuery("#stage")
    val wallView = new WallView(wallId, persistence)

    stage.append(wallView.element)
    wallView.setup()

  }
}
