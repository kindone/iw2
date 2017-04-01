package com.kindone.infinitewall.data.state.transform

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.{WallWithSheets, SheetsInWall}

/**
 * Created by kindone on 2017. 4. 1..
 */
object WallWithSheetsTransformer {
  def transform(wws:WallWithSheets, action:Action):WallWithSheets = {
    action match {
      case action: SheetAlterAction =>
        val newSheets = wws.sheetsInWall.sheets.map { case (id, sheet) =>
          if(action.sheetId == id)
            (id, sheet.applyAction(action))
          else
            (id, sheet)
        }
        wws.copy(sheetsInWall = wws.sheetsInWall.copy(sheets = newSheets))
      case CreateSheetAction(_, sheet) =>
        wws.copy(sheetsInWall = SheetsInWall(wws.wall.id, (wws.sheetsInWall.sheets + (sheet.id -> sheet))))
      case DeleteSheetAction(_, sheetId) =>
        wws.copy(sheetsInWall = SheetsInWall(wws.wall.id, (wws.sheetsInWall.sheets - sheetId)))
      case action: WallAlterAction =>
        wws.copy(wall = wws.wall.applyAction(action))
      case _ =>
        /// unsupported action
        assert(false)
        wws
    }
  }
}
