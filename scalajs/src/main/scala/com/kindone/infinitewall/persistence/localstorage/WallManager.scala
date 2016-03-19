package com.kindone.infinitewall.persistence.localstorage

import com.kindone.infinitewall.persistence.Wall
import com.kindone.infinitewall.persistence.api.{ WallManager => WallManagerAPI }

import scala.concurrent.Future
import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue

/**
 * Created by kindone on 2016. 2. 13..
 */
class WallManager(localStorage: LocalStorage) extends WallManagerAPI {

  private val objectManager = new ObjectManager[Wall](localStorage, "wall")
  private val sheetsManager = new SheetInWallManager(localStorage)

  def create(title: String, x: Double = 0, y: Double = 0, scale: Double = 1.0): Future[Wall] = Future {
    val id = objectManager.nextId()
    val wall = new Wall(id, x, y, scale, title)
    objectManager.save(id, wall)
    wall
  }

  def delete(id: Long) = Future {
    objectManager.delete(id)
    true
  }

  def get(id: Long) = Future { objectManager.getWall(id) }

  def getWalls() = Future { objectManager.getWalls() }

  def pan(id: Long, x: Double, y: Double) = Future {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, x, y, wall.scale)
    objectManager.save(id, newWall)
    true
  }

  def zoom(id: Long, scale: Double) = Future {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, wall.x, wall.y, scale)
    objectManager.save(id, newWall)
    true
  }

  def setView(id: Long, x: Double, y: Double, scale: Double) = Future {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, x, y, scale)
    objectManager.save(id, newWall)
    true
  }

  def getSheets(wallId: Long) = Future {
    sheetsManager.getSheetsInWall(wallId)
  }

  def appendSheet(id: Long, sheetId: Long) = Future {
    sheetsManager.addSheetToWall(id, sheetId)
    true
  }

  def removeSheet(id: Long, sheetId: Long) = Future {
    sheetsManager.removeSheetFromWall(id, sheetId)
    true
  }

}