package com.kindone.infinitewall.persistence.wsstorage

import com.kindone.infinitewall.persistence.api.Persistence

/**
 * Created by kindone on 2016. 4. 17..
 */
class WebSocketPersistence(baseUrl: String) extends Persistence {
  private val socket = new Socket(baseUrl)
  val sheetManager = new SheetManager(socket)
  val wallManager = new WallManager(socket)

  def clear(): Unit = {}
}
