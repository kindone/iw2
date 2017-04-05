package shared.test

import com.kindone.infinitewall.data.action.{Action, ChangeSheetContentAction}
import com.kindone.infinitewall.data.state.Sheet
import com.kindone.infinitewall.data.versioncontrol._
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, JournaledString}
import minitest._

object ChangeTest extends SimpleTestSuite {
  test("single change") {

  }

  test("change stream") {
    val br1 = Branch.create()
    val br2 = Branch.create()

    val base = "Hello world"
    val baseState = Sheet(id=0, 0, 0, 0, 0, 0, "Hello world")
    val history:History = new History(baseState)

    val ac1 = ChangeSheetContentAction(sheetId=0, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(sheetId=0, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)

    history.append(ch1)
    assertEquals(history.last.asInstanceOf[JournaledSheet].text, "Hi! world")
    history.rebase(Seq(ch2))
    println(history.last.asInstanceOf[JournaledSheet].text)

    assertEquals(history.last.asInstanceOf[JournaledSheet].text, "Hi! land")
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
