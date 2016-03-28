package com.kindone.infinitewall.persistence.localstorage

import com.kindone.infinitewall.persistence.api.Persistence

/**
 * Created by kindone on 2016. 3. 5..
 */
class LocalPersistence extends Persistence {
  private val localStorage: LocalStorage = new LocalStorage

  val sheetManager = new SheetManager(localStorage)
  val wallManager = new WallManager(localStorage, sheetManager)

  def clear() = localStorage.clear()
}
