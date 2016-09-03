package com.kindone.infinitewall.versioncontrol

import com.kindone.infinitewall.data.action.{ _ }
import com.kindone.infinitewall.data.versioncontrol.{ Read, Change }
import com.kindone.infinitewall.facades.CryptoJS
import upickle.default._

/**
 * Created by kindone on 2016. 7. 27..
 */
class VersionControl {

  def createRead(action: ReadonlyAction, parentHash: Option[String] = None) = {
    Read(action, parentHash)
  }

  def createChange(action: WriteAction, parentHash: Option[String] = None) = {
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

    Change(hash, /* typeString,*/ action, parentHash)
  }

}
