package com.kindone.infinitewall

import com.kindone.infinitewall.persistence.{ LocalStorage, WallManager }
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js

/**
 * Created by kindone on 2016. 3. 6..
 */
@JSExport
class IndexApp {
  @JSExport
  def walls(): js.Array[js.Dictionary[Any]] = {

    import js.JSConverters._
    import upickle.default._

    val localStorage = new LocalStorage()
    val wallManager = new WallManager(localStorage)
    wallManager.getWalls().map { wall =>
      JSON.parse(write(wall)).asInstanceOf[js.Dictionary[Any]]
    }.toJSArray
  }
}
