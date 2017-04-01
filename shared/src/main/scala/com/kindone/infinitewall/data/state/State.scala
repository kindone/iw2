package com.kindone.infinitewall.data.state

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.transform.{WallWithSheetsTransformer, WallTransformer, SheetTransformer}
import com.kindone.infinitewall.data.versioncontrol.util.TextOperation

/**
 * Created by kindone on 2016. 2. 13..
 */

sealed trait State {
  def oid:String
  def stateId:Long
  def applyAction(action:Action):State
}

case class Sheet(id: Long, stateId: Long, x: Double, y: Double,
                 width: Double, height: Double, text: String) extends State //with SheetLike
{
  val oid = "sheet_" + id

  def applyAction(action:Action):Sheet = {
    SheetTransformer.transform(this, action.asInstanceOf[SheetAlterAction])
  }
}

case class Wall(id: Long, stateId:Long, x: Double, y: Double, scale: Double, title: String = "") extends State
{
  val oid = "wall_" + id

  def applyAction(action:Action):Wall = {
    WallTransformer.transform(this, action.asInstanceOf[WallAlterAction])
  }
}

case class SheetsInWall(wallId: Long, sheets:Map[Long, Sheet])

case class WallWithSheets(wall:Wall, sheetsInWall:SheetsInWall) extends State
{
  val oid = "wws_" + wall.id

  def stateId:Long = wall.stateId

  def applyAction(action:Action):WallWithSheets = {
    WallWithSheetsTransformer.transform(this, action)
  }
}