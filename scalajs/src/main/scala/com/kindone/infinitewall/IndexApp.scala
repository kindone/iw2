package com.kindone.infinitewall

import com.kindone.infinitewall.persistence.localstorage.{ WallManager, LocalStorage }
import upickle.default._
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2016. 3. 6..
 */
@JSExport
class IndexApp {
  @JSExport
  def walls(onComplete: js.Function1[js.Array[js.Dictionary[Any]], Unit]): Unit = {
    import js.JSConverters._

    val localStorage = new LocalStorage()
    val wallManager = new WallManager(localStorage)
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
    val localStorage = new LocalStorage()
    val wallManager = new WallManager(localStorage)
    val wallFuture = wallManager.create(title)
    wallFuture.foreach { wall =>
      val result = JSON.parse(write(wall)).asInstanceOf[js.Dictionary[Any]]
      onComplete.apply(result)
    }
  }

  @JSExport
  def deleteWall(id: Int, onComplete: js.Function1[Boolean, Unit]): Unit = {
    val localStorage = new LocalStorage()
    val wallManager = new WallManager(localStorage)
    wallManager.delete(id).map { status =>
      onComplete.apply(status)
    }
  }
}
