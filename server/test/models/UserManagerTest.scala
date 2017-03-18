package models

import org.scalatest.MustMatchers
import org.scalatestplus.play.{ OneAppPerSuite, PlaySpec }

/**
 * Created by kindone on 2017. 3. 18..
 */
class UserManagerTest extends PlaySpec with MustMatchers with OneAppPerSuite {

  lazy val userManager = new UserManager

  "userManager" should {
    val username = "someusername"
    val rawPassword = "somepassword"
    val newRawPassword = "newpassword"
    val garbageString = "blahblah"
    val hashedPassword = userManager.hashPassword(rawPassword)

    "properly find admin user" in {
      userManager.find(0).isDefined must be(true)
    }

    "properly create a user" in {
      val id = userManager.create(User(0, username, hashedPassword))
      id must be(1L)
    }

    "prevent a user with non-email address" in {
      //TODO
    }

    "properly find the created user" in {

      userManager.find(1).isDefined must be(true)
      userManager.find(username, rawPassword).isDefined must be(true)
      val users = userManager.findAll()
      users.size must be(2)
      users.foreach { user =>
        if (user.id != 0) {
          user.email must be(username)
          user.passwordHashed must be(hashedPassword)
          user.id must be(1)
        }
      }
    }

    "fail to find a user with unknown arguments" in {
      userManager.find(2).isEmpty must be(true)
      userManager.find(garbageString, rawPassword).isEmpty must be(true)
      userManager.find(username, garbageString).isEmpty must be(true)
    }

    "properly change user's password" in {
      userManager.changePassword(1L, rawPassword, newRawPassword) must be(true)
      userManager.find(username, newRawPassword).isDefined must be(true)
      userManager.find(username, rawPassword).isDefined must be(false)
    }

    "properly delete a user" in {
      userManager.delete(1L) must be(true)
      userManager.find(1L).isDefined must be(false)
      userManager.delete(1L) must be(false)
      userManager.delete(2L) must be(false) // deleting a non-existing user
      userManager.delete(0L) must be(false) // deleting admin must be prevented
      userManager.find(0L).isDefined must be(true)
    }

  }

}
