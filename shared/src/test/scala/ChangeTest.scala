package shared.test

import com.kindone.infinitewall.data.Sheet
import com.kindone.infinitewall.data.action.{Action, ChangeSheetContentAction}
import com.kindone.infinitewall.data.versioncontrol.{SheetWithHistory, Repository, Branch, Change}
import com.kindone.infinitewall.data.versioncontrol.util.{TextOperation, StringWithHistory}
import minitest._

object ChangeTest extends SimpleTestSuite {
  test("single change") {

  }

  test("reposistory") {
    val br1 = Branch.create()
    val br2 = Branch.create()

//    println(br1.hash)
//    println(br2.hash)

    val base = "Hello world"
    val baseState = Sheet(id=0, 0, 0, 0, 0, 0, base)
    val repository:Repository = new Repository(baseState)

    val ac1 = ChangeSheetContentAction(sheetId=0, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(sheetId=0, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)

    repository.append(ch1)
    assertEquals(repository.getLatestState.asInstanceOf[SheetWithHistory].text, "Hi! world")
    repository.rebase(Seq(ch2))
    println(repository.getLatestState.asInstanceOf[SheetWithHistory].text)

    assertEquals(repository.getLatestState.asInstanceOf[SheetWithHistory].text, "Hi! land")
  }

  test("change stream - multi-party") {
    val br1 = Branch.create()
    val br2 = Branch.create()
    val br3 = Branch.create()

//    println(br1.hash)
//    println(br2.hash)
//    println(br3.hash)

    val base = "Hello world"
    val ac1 = ChangeSheetContentAction(0, "Hi!", 0, 5)  // replace Hello
    val ac2 = ChangeSheetContentAction(0, "land", 6, 5) // replace world

    val ch1 = Change(ac1, 0, br1)
    val ch2 = Change(ac2, 0, br2)


//    assertEquals(ss.text, "Hi! land")
  }
}
