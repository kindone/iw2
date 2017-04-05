package models

import java.util.NoSuchElementException

import com.kindone.infinitewall.data.state.{Wall, Sheet}
import org.scalatest._
import org.scalatestplus.play._
import play.api.test.Helpers._

/**
 * Created by kindone on 2017. 3. 18..
 */
class WallManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {

  val wallManager = new WallManager
  var wallId = 0L
  var sheetId = 0L

  "wallManager" should {
    implicit val userId: Long = 0L // admin

    "find all" in {
      wallManager.findAll()
    }

    "properly create new wall" in {
      wallId = wallManager.create(Wall(0, 5, 1.0, 2.0, 3.0, "blah")).get
      val wallOpt = wallManager.find(wallId)
      wallOpt.get must be(Wall(wallId, 0, 1.0, 2.0, 3.0, "blah"))
    }

    "properly get sheets" in {
      wallManager.getSheetIds(0)(userId).size must be(0)
      wallManager.getSheets(0)(userId).size must be(0)
      val sheetIds = wallManager.getSheetIds(1)
      sheetIds.size must be(0)
      val sheets = wallManager.getSheets(1)
      sheets.size must be(0)
    }

    "properly create a sheet" in {
      // creating a sheet on non-existent wall fails
      wallManager.createSheet(0, Sheet(0, 5, 1.0, 2.0, 3.0, 4.0, "text")).isDefined must be(false)
      sheetId = wallManager.createSheet(wallId, Sheet(0, 5, 1.0, 2.0, 3.0, 14.0, "text")).get
      sheetId must be(1)
      val sheets = wallManager.getSheets(wallId)
      sheets.size must be(1)
      sheets.head must be(Sheet(sheetId, 0, 1.0, 2.0, 3.0, 14.0, "text"))
    }

    "properly delete a sheet" in {
      wallManager.deleteSheet(0, 1) must be(false)
      wallManager.deleteSheet(wallId, 1) must be(true)
      wallManager.getSheets(wallId).size must be(0)
    }

    "properly alter a wall" in {
      wallManager.setZoom(wallId, 4.0) must be(true)
      wallManager.find(wallId).get must be(Wall(wallId, 0, 1.0, 2.0, 4.0, "blah"))
      wallManager.setTitle(wallId, "blah2") must be(true)
      wallManager.find(wallId).get must be(Wall(wallId, 0, 1.0, 2.0, 4.0, "blah2"))
      wallManager.setPan(wallId, 2.0, 3.0) must be(true)
      wallManager.find(wallId).get must be(Wall(wallId, 0, 2.0, 3.0, 4.0, "blah2"))
      wallManager.setView(wallId, 3.0, 4.0, 5.0) must be(true)
      wallManager.find(wallId).get must be(Wall(wallId, 0, 3.0, 4.0, 5.0, "blah2"))

    }

    "properly delete a wall" in {
      val size = wallManager.findAll().size
      wallManager.find(wallId).isDefined must be(true)
      wallManager.delete(wallId) must be(true)
      wallManager.find(wallId).isDefined must be(false)
      wallManager.findAll().size must be(size - 1)
      wallManager.delete(wallId) must be(false)
    }

  }
}
