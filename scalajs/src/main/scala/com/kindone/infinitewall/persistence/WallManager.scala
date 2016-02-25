package com.kindone.infinitewall.persistence

/**
 * Created by kindone on 2016. 2. 13..
 */
class WallManager(localStorageManager: LocalStorageManager) {

  private val objectManager = new ObjectManager[Wall](localStorageManager, "wall")
  private val sheetsManager = new SheetInWallManager(localStorageManager)

  def create(x: Double = 0, y: Double = 0, scale: Double = 1.0): Wall = {
    val id = objectManager.nextId()
    val wall = new Wall(id, x, y, scale)
    objectManager.save(id, wall)
    wall
  }

  def delete(id: Long) =
    objectManager.delete(id)

  def get(id: Long) = objectManager.getWall(id)

  def pan(id: Long, x: Double, y: Double) = {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, x, y, wall.scale)
    objectManager.save(id, newWall)
  }

  def zoom(id: Long, scale: Double) = {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, wall.x, wall.y, scale)
    objectManager.save(id, newWall)
  }

  def setView(id: Long, x: Double, y: Double, scale: Double) = {
    val wall = objectManager.getWall(id).get
    val newWall = new Wall(id, x, y, scale)
    objectManager.save(id, newWall)
  }

  def getSheets(wallId: Long) = sheetsManager.getSheetsInWall(wallId)

  def appendSheet(id: Long, sheetId: Long) =
    sheetsManager.addSheetToWall(id, sheetId)

  def removeSheet(id: Long, sheetId: Long) =
    sheetsManager.removeSheetFromWall(id, sheetId)

}