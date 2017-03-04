package thirdparty.macwire

/**
 * Created by kindone on 2017. 2. 12..
 */
class DatabaseAccess()
class SecurityFilter()
class UserFinder(databaseAccess: DatabaseAccess, securityFilter: SecurityFilter)
class UserStatusReader(userFinder: UserFinder)

trait UserModule {
  import com.softwaremill.macwire._

  lazy val theDatabaseAccess = wire[DatabaseAccess]
  lazy val theSecurityFilter = wire[SecurityFilter]
  lazy val theUserFinder = wire[UserFinder]
  lazy val theUserStatusReader = wire[UserStatusReader]
}