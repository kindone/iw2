package models

import org.scalatest._
import org.scalatestplus.play._
import play.api.test.Helpers._

/**
 * Created by kindone on 2017. 3. 18..
 */
class WallManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {

  val wallManager = new WallManager

  "wallManager" should {
    "work well when getSheets" in {
      val ids = wallManager.getSheetIds(0)(0)

      ids.size must be(0)
    }
  }
}
