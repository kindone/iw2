package thirdparty.macwire

import utest._
import utest.framework.{ Test, Tree }

trait TestUserModule {
  import com.softwaremill.macwire._

  class TestDatabaseAccess extends DatabaseAccess {
  }
  class TestSecurityFilter extends SecurityFilter {
    println("TestSecurityFilter")

    val someVal = 5
  }

  lazy val theDatabaseAccess = wire[TestDatabaseAccess]
  lazy val theSecurityFilter = wire[TestSecurityFilter]
  lazy val theUserFinder = wire[UserFinder]
  lazy val theUserStatusReader = wire[UserStatusReader]
}

/**
 * Created by kindone on 2017. 2. 12..
 */
object UserModuleTest extends TestSuite {
  override def tests = this{
    'testUserModule{
      val module = new UserModule {}
    }
    'testAnotherUserModule{
      val module = new TestUserModule {}
      module.theSecurityFilter.someVal
    }

  }
}
