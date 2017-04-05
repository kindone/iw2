package com.kindone.infinitewall.data.versioncontrol

import com.kindone.infinitewall.data.action.{WallAlterAction, SheetAlterAction, ChangeSheetContentAction}
import com.kindone.infinitewall.data.state._
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, JournaledString}
import com.kindone.infinitewall.data._
import com.kindone.util.Hasher
import upickle.default._


trait SheetLike {
  def id:Long
  def stateId:Long
  def x:Double
  def y:Double
  def width:Double
  def height:Double
  def text:String
}

trait WallLike {
  def id: Long
  def stateId:Long
  def x: Double
  def y: Double
  def scale: Double
  def title: String
}

trait WallWithSheetsLike {
  def wall:Wall
  def sheetsInWall:SheetsInWall
}

sealed trait JournaledState
{
  type BaseType
  def stateId:Long
  def applyChange(change:Change):(JournaledState, Change)
}

object JournaledState {
  def create(anonymous:State):JournaledState = {
    anonymous match {
      case sheet:Sheet =>
        new JournaledSheet(sheet)
      case wall:Wall =>
        new JournaledWall(wall)
      case wws:WallWithSheets =>
        new JournaledWallWithSheets(wws)
    }
  }
}

class JournaledSheet(val sheet:Sheet, ss:JournaledString) extends JournaledState with SheetLike{

  type BaseType = Sheet

  // initial state
  def this(sheet:Sheet) {
    this(sheet, new JournaledString(sheet.text))
  }

  def id = sheet.id
  def stateId = sheet.stateId
  def x = sheet.x
  def y = sheet.y
  def width = sheet.width
  def height = sheet.height
  def text = sheet.text

  def applyChange(change:Change):(JournaledState, Change) = {
    change match {
      case Change(action:ChangeSheetContentAction,  _, _) =>
        val (newSs, op) = ss.applyTextOperation(new TextOperation(action.content, action.from, action.numDeleted), change.branch.hash)
        val newSheetWithHistory = new JournaledSheet(sheet.applyAction(change.action), newSs)
        (newSheetWithHistory, change.copy(action = action.copy(content = op.content, from = op.from, numDeleted = op.numDeleted)))
      case _ =>
        val newSheetWithHistory = new JournaledSheet(sheet.applyAction(change.action), ss)
        (newSheetWithHistory, change)
    }
  }

  override def toString = {
    s"{id: $id, x: $x, y: $y, width: $width, height: $height, text: '$text'}"
  }
}


class JournaledWall(val wall:Wall) extends JournaledState with WallLike {
  type BaseType = Wall

  def id: Long = wall.id
  def stateId:Long = wall.stateId
  def x: Double = wall.x
  def y: Double = wall.y
  def scale: Double = wall.scale
  def title: String = wall.title

  def applyChange(change:Change):(JournaledState, Change) = {
    change.action match {
      case a:WallAlterAction =>
        (new JournaledWall(wall.applyAction(a)), change)
      case _ =>
        (this, change)
    }
  }

  override def toString = {
    s"{id: $id, x: $x, y: $y, scale: $scale, title: '$title'}"
  }
}

class JournaledWallWithSheets(val wws:WallWithSheets) extends JournaledState with WallWithSheetsLike {
  type BaseType = WallWithSheets

  def stateId: Long = wws.stateId

  def wall: Wall = wws.wall

  def sheetsInWall: SheetsInWall = wws.sheetsInWall

  def applyChange(change: Change): (JournaledState, Change) = {
    change.action match {
      case a:WallAlterAction =>
        (new JournaledWallWithSheets(WallWithSheets(wws.wall.applyAction(a), wws.sheetsInWall)), change)
      case _ =>
        (this, change)
    }
  }

}