package com.kindone.infinitewall.data.action

import com.kindone.infinitewall.data.Sheet

/**
 * Created by kindone on 2016. 4. 16..
 */
sealed trait Action

sealed trait WallAction extends Action
sealed trait WallAlterAction extends WallAction {
  def wallId:Long
}
sealed trait SheetAction extends Action
sealed trait SheetAlterAction extends SheetAction {
  def sheetId:Long
}

case class CreateWallAction(title:String,  x:Double, y:Double, scale:Double) extends WallAction
case class DeleteWallAction(wallId:Long) extends WallAlterAction
case class GetWallAction(wallId:Long) extends WallAction
case class ListWallAction() extends WallAction

case class SubscribeWallEventAction(wallId:Long) extends WallAction
case class SubscribeSheetEventAction(sheetId:Long) extends SheetAction

case class ChangePanAction(wallId: Long, x: Double, y:Double) extends WallAlterAction
case class ChangeZoomAction(wallId: Long, scale:Double) extends WallAlterAction
case class ChangeViewAction(wallId:Long, x: Double, y: Double, scale: Double) extends WallAlterAction
case class ChangeTitleAction(wallId:Long, title:String) extends WallAlterAction

case class ListSheetAction(wallId:Long) extends WallAction
case class GetSheetAction(sheetId:Long) extends SheetAction

case class CreateSheetAction(wallId:Long, sheet:Sheet) extends WallAlterAction
case class DeleteSheetAction(wallId:Long, sheetId:Long) extends WallAlterAction

case class MoveSheetAction(sheetId:Long, x: Double, y:Double) extends SheetAlterAction
case class ResizeSheetAction(sheetId:Long, width:Double, height:Double) extends SheetAlterAction
case class ChangeSheetDimensionAction(sheetId:Long, x: Double, y:Double, width:Double, height: Double) extends SheetAlterAction
case class ChangeSheetContentAction(sheetId:Long, content:String, pos:Int = 0) extends SheetAlterAction



