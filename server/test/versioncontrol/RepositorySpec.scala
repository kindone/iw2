package versioncontrol

//import com.kindone.infinitewall.data.versioncontrol.{ GroupedSnapshot, SingleSnapshot, Repository }

import com.kindone.infinitewall.data.state.Sheet
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._

@RunWith(classOf[JUnitRunner])
class RepositorySpec extends Specification {
  "The 'Hello world' string" should {
    "contain 11 characters" in {
      "Hello world" must have size (11)
    }
    "start with 'Hello'" in {
      "Hello world" must startWith("Hello")
    }
    "end with 'world'" in {
      "Hello world" must endWith("world")
    }
  }

  //  "The Repository" should {
  //    "" in {
  //      val repository = new Repository
  //      val sheet = Sheet(0, 0, 0, 0, 0, 0, "")
  //      val sheetSnapshot = SingleSnapshot("hash1", sheet)
  //      val initialSnapshot = GroupedSnapshot("hash2", List(sheetSnapshot))
  //      repository.initialize(initialSnapshot)
  //      repository.findChange("hash1")
  //      "Hello world" must have size (11)
  //    }
  //  }
}