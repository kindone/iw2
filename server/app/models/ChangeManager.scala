package models

import com.kindone.infinitewall.data.action.MoveSheetAction
import upickle.default._

/**
 * Created by kindone on 2016. 12. 2..
 */
class ChangeManager {
  lazy val sheetManager = new SheetManager
  lazy val sheetLogManager = new SheetLogManager

}
