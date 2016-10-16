package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.action.ChangeSheetContentAction
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithHistory}
import com.kindone.infinitewall.data._
import com.kindone.util.Hasher
import upickle.default._


sealed trait StateWithHistory
{
  def applyChange(change:Change):(StateWithHistory, Change)
}

object StateWithHistory {
  def create(anonymous:State):StateWithHistory = {
    anonymous match {
      case sheet:Sheet =>
        create(sheet)
      case sheetsInWall:SheetsInWall =>
        create(sheetsInWall)
      case wall:Wall =>
        create(wall)
      case wall:WallWithSheets =>
        create(wall)
    }
  }

  def create(sheet:Sheet):StateWithHistory =
    new SheetWithHistory(sheet)

  def create(sheetsInWall:SheetsInWall) =
    new SheetsInWallWithHistory(sheetsInWall)

  def create(wall:Wall) =
    new WallWithHistory(wall)

  def create(wall:WallWithSheets) =
    new WallWithSheets(wall.wall, wall.sheetsInWall)
}

class SheetWithHistory(sheet:Sheet, ss:StringWithHistory) extends StateWithHistory with SheetLike{

  def this(sheet:Sheet) {
    this(sheet, new StringWithHistory(sheet.text))
  }

  def id = sheet.id
  def stateId = sheet.stateId
  def x = sheet.x
  def y = sheet.y
  def width = sheet.width
  def height = sheet.height
  def text = sheet.text

  def applyChange(change:Change):(StateWithHistory, Change) = {
    change match {
      case Change(action:ChangeSheetContentAction,  _, _) =>
        val (newSs, op) = ss.applyTextOperation(new TextOperation(action.content, action.from, action.numDeleted), change.branch.hash)
        val newSheetWithHistory = new SheetWithHistory(sheet.applyAction(change.action), newSs)
        (newSheetWithHistory, change.copy(action = action.copy(content = op.content, from = op.from, numDeleted = op.numDeleted)))
      case _ =>
        val newSheetWithHistory = new SheetWithHistory(sheet.applyAction(change.action))
        (newSheetWithHistory, change)
    }
  }

  override def toString = {
    s"{id: $id, x: $x, y: $y, width: $width, height: $height, text: '$text'}"
  }
}

class SheetsInWallWithHistory(sheetsInWall:SheetsInWall) extends StateWithHistory with SheetsInWallLike {

  def wallId = sheetsInWall.wallId
  def sheets = sheetsInWall.sheets

  def applyChange(change:Change):(StateWithHistory, Change) = {
    // trivial changes. No history
    (new SheetsInWallWithHistory(sheetsInWall.applyAction(change.action)), change)
  }
}

class WallWithHistory(wall:Wall) extends StateWithHistory with WallLike {
  def id: Long = wall.id
  def stateId:Long = wall.stateId
  def x: Double = wall.x
  def y: Double = wall.y
  def scale: Double = wall.scale
  def title: String = wall.title

  def applyChange(change:Change):(StateWithHistory, Change) = {
    // trivial changes. No history
    (new WallWithHistory(wall.applyAction(change.action)), change)
  }
}