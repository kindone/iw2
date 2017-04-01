package com.kindone.infinitewall.data.state.transform

import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.Wall

/**
 * Created by kindone on 2017. 4. 1..
 */
object WallTransformer {
  def transform(wall:Wall, action:WallAlterAction):Wall = {
    assert(action.wallId == wall.id)
    action match {
      case ChangePanAction(_, x, y) =>
        wall.copy(x = x, y = y)
      case ChangeZoomAction(_, scale) =>
        wall.copy(scale = scale)
      case ChangeViewAction(_, x, y, scale) =>
        wall.copy(x = x, y = y, scale = scale)
      case ChangeTitleAction(_, title) =>
        wall.copy(title = title)
      case _ =>
        wall
    }
  }

}
