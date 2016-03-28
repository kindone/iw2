package com.kindone.infinitewall.persistence.remotestorage

import com.kindone.infinitewall.persistence.api.Persistence

/**
 * Created by kindone on 2016. 3. 28..
 */
class RemotePersistence(baseUrl: String) extends Persistence {
  val sheetManager = new SheetManager(baseUrl)
  val wallManager = new WallManager(baseUrl)

  def clear() = ???
}
