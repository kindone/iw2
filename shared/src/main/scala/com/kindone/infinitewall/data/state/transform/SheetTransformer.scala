package com.kindone.infinitewall.data.state.transform

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.Sheet
import com.kindone.infinitewall.data.versioncontrol.util.TextOperation

/**
 * Created by kindone on 2017. 4. 1..
 */
object SheetTransformer {

  def transform(sheet:Sheet, action:SheetAlterAction):Sheet = {
      assert(action.sheetId == sheet.id)
      action match {
        case MoveSheetAction(_, x, y) =>
          sheet.copy(x = x, y = y)
        case ResizeSheetAction(_, width, height) =>
          sheet.copy(width = width, height = height)
        case ChangeSheetDimensionAction(_, x, y, width, height) =>
          sheet.copy(x = x, y = y, width = width, height = height)
        case ChangeSheetContentAction(_, content, pos, length) =>
          sheet.copy(text = new TextOperation(content, pos, length).transform(sheet.text))
        case _ =>
          sheet
      }
  }
}
