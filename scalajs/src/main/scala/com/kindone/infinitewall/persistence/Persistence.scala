package com.kindone.infinitewall.persistence

import com.kindone.infinitewall.persistence.localstorage.{ WallManager, SheetManager, LocalStorage }

/**
 * Created by kindone on 2016. 3. 5..
 */
class Persistence(localStorage: LocalStorage) {

  val sheetManager: SheetManager = new SheetManager(localStorage)
  val wallManager: WallManager = new WallManager(localStorage, sheetManager)

  def clear() = localStorage.clear()
}
