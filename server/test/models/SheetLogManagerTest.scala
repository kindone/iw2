package models

import com.kindone.infinitewall.data.Wall
import com.kindone.infinitewall.data.action._
import com.kindone.infinitewall.data.state.{Wall, Sheet}
import org.scalatest.MustMatchers
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }
import upickle.default._

/**
 * Created by kindone on 2017. 3. 18..
 */
class SheetLogManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {
  lazy val userManager = new UserManager
  lazy val wallManager = new WallManager
  lazy val sheetManager = new SheetManager
  lazy val sheetLogManager = new SheetLogManager

  "sheetLogManager" should {

    implicit val userId: Long = 0L

    "properly find admin user's sheet log" in {
      sheetLogManager.find(0).size must be(0)
    }

    "properly throw error on illegal sheet log" in {
      val actionStr = write(ChangePanAction(0, 0.0, 0.0))
      an[NoSuchElementException] should be thrownBy
        sheetLogManager.create(SheetLog(0L, 0L, ModelManager.MOVE_SHEET, Some(actionStr)))

    }

    "properly create admin user's sheet log" in {
      val wallId = wallManager.create(Wall(0, 0, 0.0, 0.0, 1.0))(0L).get
      val sheetId = wallManager.createSheet(wallId, Sheet(0, 0, 0.0, 0.0, 1.0, 1.0, "")).get
      val actionStr = write(MoveSheetAction(sheetId, 0.0, 0.0))
      val result1 = sheetLogManager.create(SheetLog(sheetId, 0L, ModelManager.MOVE_SHEET, Some(actionStr)))
      result1.success must be(true)
      result1.logId must be(1)
      sheetLogManager.find(sheetId).size must be(1)
      sheetLogManager.find(0L).size must be(0)

      // log id increases
      val result2 = sheetLogManager.create(SheetLog(sheetId, 1L, ModelManager.MOVE_SHEET, Some(actionStr)))
      result2.success must be(true)
      result2.logId must be(2)

      // should fail on outdated log id
      val result3 = sheetLogManager.create(SheetLog(sheetId, 1L, ModelManager.MOVE_SHEET, Some(actionStr)))
      result3.success must be(false)
      result3.logId must be(2)
    }

  }
}
