package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.{ _ }
import com.kindone.infinitewall.data.versioncontrol.{ Branch, Read, Change }
import com.kindone.infinitewall.facades.CryptoJS
import upickle.default._

/**
 * Created by kindone on 2016. 7. 27..
 */
class VersionControl {

  lazy val branch: Branch = new Branch(Branch.genHash)

  def createRead(action: ReadonlyAction, baseLogId: Long = 0) = {
    Read(action, baseLogId)
  }

  def createChange(action: WriteAction, baseLogId: Long = 0) = {
    val serializedString = write(action)
    val hash = (new CryptoJS).SHA1(serializedString)
    //    val typeString: String = action match {
    //      case _: ChangeSheetContentAction   => "changeSheetContent"
    //      case _: ChangePanAction            => "changePan"
    //      case _: ChangeZoomAction           => "changeZoom"
    //      case _: ChangeViewAction           => "changeView"
    //      case _: ChangeTitleAction          => "changeTitle"
    //      case _: CreateSheetAction          => "createSheet"
    //      case _: DeleteSheetAction          => "deleteSheet"
    //      case _: MoveSheetAction            => "moveSheet"
    //      case _: ResizeSheetAction          => "resizeSheet"
    //      case _: ChangeSheetDimensionAction => "changeSheetDimension"
    //      case _                             => "unknown"
    //    }

    Change(action, baseLogId, branch)
  }

}
