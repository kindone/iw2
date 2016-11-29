package com.kindone.infinitewall.persistence.httpstorage

import com.kindone.infinitewall.persistence.api.{ SimplePersistence }

/**
 * Created by kindone on 2016. 3. 28..
 */
class HttpPersistence(baseUrl: String) extends SimplePersistence {
  val sheetManager = new SheetManager(baseUrl)
  val wallManager = new WallManager(baseUrl)

  def clear() = ???
}
