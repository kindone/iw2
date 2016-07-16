package com.kindone.infinitewall

import com.kindone.infinitewall.persistence.api.Persistence
import com.kindone.infinitewall.persistence.localstorage.{ LocalPersistence, WallManager, LocalStorage }
import com.kindone.infinitewall.persistence.httpstorage.HttpPersistence
import com.kindone.infinitewall.persistence.wsstorage.WebSocketPersistence
import upickle.default._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2016. 3. 6..
 */
@JSExport
class IndexApp(val useLocalStorage: Boolean = true, val useWS: Boolean = false) {

  val persistence: Persistence = {
    if (useLocalStorage)
      new LocalPersistence()
    else if (useWS)
      new WebSocketPersistence("")
    else
      new HttpPersistence("")
  }

  @JSExport
  def walls(onComplete: js.Function1[js.Array[js.Dictionary[Any]], Unit]): Unit = {
    import js.JSConverters._

    val wallManager = persistence.wallManager
    val wallsFuture = wallManager.getWalls().map { walls =>
      walls.map { wall =>
        JSON.parse(write(wall)).asInstanceOf[js.Dictionary[Any]]
      }.toJSArray
    }

    wallsFuture.foreach { walls =>
      onComplete.apply(walls)
    }
  }

  @JSExport
  def createWall(title: String, onComplete: js.Function1[js.Dictionary[Any], Unit]): Unit = {
    val wallManager = persistence.wallManager
    val wallFuture = wallManager.create(title)
    wallFuture.foreach { wall =>
      val result = JSON.parse(write(wall)).asInstanceOf[js.Dictionary[Any]]
      onComplete.apply(result)
    }
  }

  @JSExport
  def deleteWall(id: Int, onComplete: js.Function1[Boolean, Unit]): Unit = {
    val wallManager = persistence.wallManager
    wallManager.delete(id).map { status =>
      onComplete.apply(status)
    }
  }
}
