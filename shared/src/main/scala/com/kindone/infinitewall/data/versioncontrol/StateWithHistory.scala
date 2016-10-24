package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.action.{WallAlterAction, SheetAlterAction, ChangeSheetContentAction}
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

  def create(sheet:Sheet):SheetWithHistory =
    new SheetWithHistory(sheet)

  def create(wall:WallWithSheets):StateWithHistory = {
    val sheets = wall.sheetsInWall.sheets.map { case (id, sheet) =>
      (id, create(sheet))
    }
    new WallWithSheetsWithHistory(wall.wall, sheets)
  }
}

class SheetWithHistory(val sheet:Sheet, ss:StringWithHistory) extends StateWithHistory with SheetLike{

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
        val newSheetWithHistory = new SheetWithHistory(sheet.applyAction(change.action), ss)
        (newSheetWithHistory, change)
    }
  }

  override def toString = {
    s"{id: $id, x: $x, y: $y, width: $width, height: $height, text: '$text'}"
  }
}


class WallWithSheetsWithHistory(val wall:Wall, sheets:Map[Long, SheetWithHistory]) extends StateWithHistory with WallWithSheetsLike {
  def sheetsInWall:SheetsInWall = {
    val newSheets = sheets.map { case (id, sheet) =>
      (id, sheet.asInstanceOf[SheetWithHistory].sheet)
    }

    SheetsInWall(wall.id, newSheets)
  }


  def applyChange(change:Change):(StateWithHistory, Change) = {
    change.action match {
      case a:SheetAlterAction =>
        val sheet = sheets.get(a.sheetId).get.asInstanceOf[SheetWithHistory]
        val (newSheet, newChange) = sheet.applyChange(change)
        val newSheets = sheets + (a.sheetId -> newSheet.asInstanceOf[SheetWithHistory]) // replace
        (new WallWithSheetsWithHistory(wall, newSheets), newChange)
      case a:WallAlterAction =>
        (new WallWithSheetsWithHistory(wall.applyAction(a), sheets), change)
      case _ =>
        (this, change)
    }
  }
}