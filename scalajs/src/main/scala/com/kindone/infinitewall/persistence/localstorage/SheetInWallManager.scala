package com.kindone.infinitewall.persistence.localstorage

/**
 * Created by kindone on 2016. 2. 20..
 */
private class SheetInWallManager(localStorage: LocalStorage) {
  private val relationManager = new RelationManager(localStorage, "sheetInWall")

  def addSheetToWall(wallId: Long, sheetId: Long) = {
    relationManager.create(wallId, sheetId)
  }

  def getSheetsInWall(wallId: Long) = relationManager.get(wallId)

  def removeSheetFromWall(wallId: Long, sheetId: Long) = {
    relationManager.delete(wallId, sheetId)
  }
}
