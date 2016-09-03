package com.kindone.infinitewall.data

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithState}

/**
 * Created by kindone on 2016. 2. 13..
 */

sealed abstract class State {
  def applyAction(action:Action):State
}

case class Sheet(id: Long, stateId: Long, x: Double, y: Double,
                 width: Double, height: Double, text: String) extends State
{

  val oid = "sheet_" + id

  def applyAction(action:Action):Sheet = {
    action match {
      case action:SheetAlterAction =>
        assert(action.sheetId == id)
        action match {
          case MoveSheetAction(_, x, y) =>
            this.copy(x = x, y = y)
          case ResizeSheetAction(_, width, height) =>
            this.copy(width = width, height = height)
          case ChangeSheetDimensionAction(_, x, y, width, height) =>
            this.copy(x = x, y = y, width = width, height = height)
          case ChangeSheetContentAction(_, content, pos, length) =>
            val ss = new StringWithState(text)
            ss.apply(new TextOperation(pos, length, content), 0)
            this.copy(text = ss.text)
          case _ =>
            this.copy()
        }
      case _ =>
        this.copy()
    }
  }
}

case class SheetInWall(wallId: Long, sheetId: Long)


case class SheetsInWall(wallId: Long, sheets:Set[Sheet]) extends State
{
  val oid = "sheetsInWall_" + wallId

  def applyAction(action:Action):SheetsInWall = {
    action match {
      case action:WallAlterAction =>
        assert(action.wallId == wallId)
        action match {
          case CreateSheetAction(_, sheet) =>
            this.copy(sheets = (sheets + sheet))
          case DeleteSheetAction(_, sheetId) =>
            val newSet = sheets.filter(_.id != sheetId)
            this.copy(sheets = newSet)
          case _ =>
            this.copy()
        }
      case _ =>
        this.copy()
    }
  }
}

case class Wall(id: Long, stateId:Long, x: Double, y: Double, scale: Double, title: String = "") extends State
{
  val oid = "wall_" + id

  def applyAction(action:Action):Wall = {
    action match {
      case action:WallAlterAction =>
        assert(action.wallId == id)
        action match {
          case ChangePanAction(_, x, y) =>
            this.copy(x = x, y = y)
          case ChangeZoomAction(_, scale) =>
            this.copy(scale = scale)
          case ChangeViewAction(_, x, y, scale) =>
            this.copy(x = x, y = y, scale = scale)
          case ChangeTitleAction(_, title) =>
            this.copy(title = title)
          case _ =>
            this.copy()
        }
      case _ =>
        this.copy()
    }
  }
}


//case class WallSnapshot(wall:Wall, sheetsInWall:List[SheetInWall], sheets:List[Sheet]) extends State