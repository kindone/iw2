package shared.test

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.data.action.{Action, ChangeSheetContentAction}
import com.kindone.infinitewall.data.versioncontrol._
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithHistory}
import minitest._

object ChangeTest extends SimpleTestSuite {
  test("single change") {

  }

  test("change stream") {
    val br1 = Branch.create()
    val br2 = Branch.create()

    val base = "Hello world"
    val baseState = Sheet(id=0, 0, 0, 0, 0, 0, "Hello world")
    val changeStream:ChangeStream = new ChangeStream(baseState)

    val ac1 = ChangeSheetContentAction(sheetId=0, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(sheetId=0, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)

    changeStream.append(ch1)
    assertEquals(changeStream.getLatestState.asInstanceOf[SheetWithHistory].text, "Hi! world")
    changeStream.rebase(Seq(ch2))
    println(changeStream.getLatestState.asInstanceOf[SheetWithHistory].text)

    assertEquals(changeStream.getLatestState.asInstanceOf[SheetWithHistory].text, "Hi! land")
  }

  test("change stream - multi-party") {
    val br1 = Branch.create()
    val br2 = Branch.create()
    val br3 = Branch.create()

    val base = "Hello world"
    val ac1 = ChangeSheetContentAction(0, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(0, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)


//    assertEquals(ss.text, "Hi! land")
  }
}
