package com.kindone.infinitewall.persistence.api

/**
 * Created by kindone on 2016. 3. 28..
 */
trait Persistence {
  val sheetManager: SheetManager
  val wallManager: WallManager

  def clear(): Unit
}
