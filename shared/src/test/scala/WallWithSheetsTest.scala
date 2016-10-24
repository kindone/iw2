package shared.test

import com.kindone.infinitewall.data.action.{ChangePanAction, ChangeSheetContentAction}
import com.kindone.infinitewall.data.versioncontrol._
import com.kindone.infinitewall.data.{SheetsInWall, Wall, WallWithSheets, Sheet}
import minitest._
import shared.test.ChangeTest._

/**
 * Created by kindone on 2016. 10. 20..
 */
object WallWithSheetsTest extends SimpleTestSuite {
  test("wall with sheets history") {

    val br1 = Branch.create()
    val br2 = Branch.create()

    val sheet = Sheet(id=0, 0, 0, 0, 0, 0, "Hello world")
    val wall = Wall(0, 0, 0.0, 0.0, 1.0, "untitled wall")
    val sheetsInWall = SheetsInWall(wall.id, Map(sheet.id -> sheet))
    val wallWithSheets = WallWithSheets(wall, sheetsInWall)

    val repository:Repository = new Repository(wallWithSheets)

    val ac1 = ChangeSheetContentAction(sheetId=sheet.id, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(sheetId=sheet.id, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)

    repository.append(ch1)
    val sheet1 = repository.getLatestState.asInstanceOf[WallWithSheetsWithHistory].sheetsInWall.sheets.get(0).get
    assertEquals(sheet1.text, "Hi! world")
    repository.rebase(Seq(ch2))
    val sheet2 = repository.getLatestState.asInstanceOf[WallWithSheetsWithHistory].sheetsInWall.sheets.get(0).get

    assertEquals(sheet2.text, "Hi! land")

    val ac3 = ChangePanAction(wall.id, 100, 100)
    val ch3 = Change(ac3, repository.baseLogId, br1)
    repository.append(ch3)

    assertEquals(repository.getLatestState.asInstanceOf[WallWithSheetsWithHistory].wall.x, 100)
    assertEquals(repository.getLatestState.asInstanceOf[WallWithSheetsWithHistory].wall.y, 100)
  }
}
