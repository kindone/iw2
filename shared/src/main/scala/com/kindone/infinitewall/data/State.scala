package com.kindone.infinitewall.data

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.versioncontrol.util.{StringWithHistory, TextOperation}

/**
 * Created by kindone on 2016. 2. 13..
 */

sealed abstract class State {
  def oid:String
  def applyAction(action:Action):State
}

trait SheetLike {
  def id:Long
  def stateId:Long
  def x:Double
  def y:Double
  def width:Double
  def height:Double
  def text:String
}

trait SheetsInWallLike {
  def wallId: Long
  def sheets:Set[Sheet]
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

case class Sheet(id: Long, stateId: Long, x: Double, y: Double,
                 width: Double, height: Double, text: String) extends State //with SheetLike
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
            this.copy(text = new TextOperation(content, pos, length).transform(text))
          case _ =>
            this
        }
      case _ =>
        this
    }
  }
}

case class SheetInWall(wallId: Long, sheetId: Long)


case class SheetsInWall(wallId: Long, sheets:Map[Long, Sheet]) extends State
{
  val oid = "sheetsInWall_" + wallId

  def applyAction(action:Action):SheetsInWall = {
    action match {
      case action:WallAlterAction =>
        assert(action.wallId == wallId)
        action match {
          case CreateSheetAction(_, sheet) =>
            this.copy(sheets = (sheets + (sheet.id -> sheet)))
          case DeleteSheetAction(_, sheetId) =>
            this.copy(sheets = sheets - sheetId)
          case _ =>
            this
        }
      case _ =>
        this
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
            this
        }
      case _ =>
        this
    }
  }
}

case class WallWithSheets(wall:Wall, sheetsInWall:SheetsInWall) extends State
{
  val oid = "wws_" + wall.id

  def applyAction(action:Action):WallWithSheets = {
    action match {
      case action: SheetAlterAction =>
        val newSheets = sheetsInWall.sheets.map { case (id, sheet) =>
          if(action.sheetId == id)
            (id, sheet.applyAction(action))
          else
            (id, sheet)
        }
        this.copy(sheetsInWall = sheetsInWall.copy(sheets = newSheets))
      case CreateSheetAction(_, sheet) =>
        this.copy(sheetsInWall = sheetsInWall.applyAction(action))
      case DeleteSheetAction(_, sheetId) =>
        this.copy(sheetsInWall = sheetsInWall.applyAction(action))
      case action: WallAlterAction =>
        this.copy(wall = wall.applyAction(action))
      case _ =>
        /// unsupported action
        assert(false)
        this
    }
  }
}