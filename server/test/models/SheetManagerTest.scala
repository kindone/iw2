package models

import com.kindone.infinitewall.data.state.{Wall, Sheet}
import org.scalatest.MustMatchers
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }

/**
 * Created by kindone on 2017. 3. 19..
 */
class SheetManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {
  val wallManager = new WallManager
  val sheetManager = new SheetManager
  var wallId = 0L
  var sheetId = 0L

  "sheetManager" should {
    implicit val userId: Long = 0L // admin
    var sheetId = 0L

    "initially create a sheet to test" in {
      // creating a sheet on non-existent wall fails
      val wallId = wallManager.create(Wall(0, 0, 0.0, 0.0, 1.0)).get
      sheetId = wallManager.createSheet(wallId, Sheet(0, 5, 1.0, 2.0, 3.0, 14.0, "text")).get
      val sheets = wallManager.getSheets(wallId)
      sheets.size must be(1)
      sheets.head must be(Sheet(sheetId, 0, 1.0, 2.0, 3.0, 14.0, "text"))
    }

    "properly alter a sheet" in {
      val sheet = sheetManager.find(sheetId).get
      sheetManager.setPosition(sheetId, 10.0, 20.0) must be(true)
      sheetManager.find(sheetId).get must be(sheet.copy(x = 10.0, y = 20.0))
      sheetManager.setSize(sheetId, 30.0, 40.0) must be(true)
      sheetManager.find(sheetId).get must be(sheet.copy(x = 10.0, y = 20.0, width = 30.0, height = 40.0))
      sheetManager.setText(sheetId, "texttext") must be(true)
      sheetManager.find(sheetId).get must be(Sheet(sheetId, 0, 10.0, 20.0, 30.0, 40.0, "texttext"))
      sheetManager.setDimension(sheetId, 0.0, 0.0, 0.0, 0.0)
      sheetManager.find(sheetId).get must be(Sheet(sheetId, 0, 0.0, 0.0, 0.0, 0.0, "texttext"))
      sheetManager.updateText(sheetId, "blah", 2, 2)
      sheetManager.find(sheetId).get must be(Sheet(sheetId, 0, 0.0, 0.0, 0.0, 0.0, "teblahtext"))
      sheetManager.updateText(sheetId, "xf", 0, 2)
      sheetManager.find(sheetId).get must be(Sheet(sheetId, 0, 0.0, 0.0, 0.0, 0.0, "xfblahtext"))
      sheetManager.updateText(sheetId, "out", 10, 2)
      sheetManager.find(sheetId).get must be(Sheet(sheetId, 0, 0.0, 0.0, 0.0, 0.0, "xfblahtextout"))
    }

    "properly delete a sheet" in {
      sheetManager.find(sheetId).isDefined must be(true)
      sheetManager.delete(sheetId)
      sheetManager.find(sheetId).isDefined must be(false)
    }
  }
}
