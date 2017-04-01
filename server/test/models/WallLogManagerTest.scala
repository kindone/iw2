package models

import com.kindone.infinitewall.data.action.ChangePanAction
import com.kindone.infinitewall.data.state.Wall
import org.scalatest.MustMatchers
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import upickle.default._

/**
 * Created by kindone on 2017. 3. 18..
 */
class WallLogManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {
  lazy val userManager = new UserManager
  lazy val wallManager = new WallManager
  lazy val wallLogManager = new WallLogManager

  "wallLogManager" should {
    "properly find admin user's wallLog" in {
      wallLogManager.find(0)(0L).size must be(0)
    }

    "properly throw error on illegal wall log" in {
      val actionStr = write(ChangePanAction(0, 0.0, 0.0))
      an[NoSuchElementException] should be thrownBy
        wallLogManager.create(WallLog(0L, 0L, ModelManager.CHANGE_WALL_PAN, Some(actionStr)))(0L)

    }

    "properly create admin user's wallLog" in {
      val wallId = wallManager.create(Wall(0, 0, 0.0, 0.0, 1.0))(0L).get
      val actionStr = write(ChangePanAction(wallId, 0.0, 0.0))
      val result1 = wallLogManager.create(WallLog(wallId, 0L, ModelManager.CHANGE_WALL_PAN, Some(actionStr)))(0L)
      result1.success must be(true)
      result1.logId must be(1)
      wallLogManager.find(wallId)(0L).size must be(1)
      wallLogManager.find(0L)(0L).size must be(0)

      // log id increases
      val result2 = wallLogManager.create(WallLog(wallId, 1L, ModelManager.CHANGE_WALL_PAN, Some(actionStr)))(0L)
      result2.success must be(true)
      result2.logId must be(2)

      // should fail on outdated walllog id
      val result3 = wallLogManager.create(WallLog(wallId, 1L, ModelManager.CHANGE_WALL_PAN, Some(actionStr)))(0L)
      result3.success must be(false)
      result3.logId must be(2)
    }

  }
}
