package models

import play.api.Play.current
import anorm.Row
import play.api.db.DB
import anorm._
import anorm.SqlParser.scalar
import org.mindrot.jbcrypt.BCrypt

/**
 * Created by kindone on 2016. 4. 2..
 */
class UserManager {
  val parser = RowParser[User] {
    case Row(id: Long, email: String, passwordHashed: String) => Success(User(id, email, passwordHashed))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  val idPasswordParser = RowParser[(Long, String)] {
    case Row(id: Long, passwordHashed: String) => Success((id, passwordHashed))
    case row                                   => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def findAll(): List[User] =
    DB.withConnection { implicit c =>
      SQL"select id,email,password_hashed from users".map {
        case Row(id: Long, email: String, passwordHashed: String) => User(id, email, passwordHashed)
      }.as(parser.*)
    }

  def find(id: Long): Option[User] =
    DB.withConnection { implicit c =>
      SQL"select id,email,password_hashed from users where id=$id".map {
        case Row(id: Long, email: String, passwordHashed: String) => User(id, email, passwordHashed)
      }.as(parser.singleOpt)
    }

  def hashPassword(passwordRaw: String) = BCrypt.hashpw(passwordRaw, BCrypt.gensalt)

  def find(email: String, passwordRaw: String): Option[Long] =
    DB.withConnection { implicit c =>
      SQL"select id,password_hashed from users where email=$email".map {
        case Row(id: Long, passwordHashed: String) => (id, passwordHashed)
      }.as(idPasswordParser.singleOpt).flatMap {
        case (id, passwordHashed) =>
          if (BCrypt.checkpw(passwordRaw, passwordHashed))
            Some(id)
          else
            None
        case _ =>
          None
      }
    }

  def create(user: User): Long = {
    // returns id
    val idOpt: Option[Long] = DB.withConnection { implicit c =>
      SQL"insert into users(email, password_hashed) values(${user.email}, ${user.passwordHashed})".executeInsert()
    }
    idOpt.get
  }

  def delete(id: Long): Boolean = {
    // admin
    if (id == 0L) {
      false
    } else {
      DB.withConnection { implicit c =>
        SQL"delete from users where id = $id".executeUpdate()
      } == 1
    }
  }

  def changePassword(id: Long, oldPassword: String, newPassword: String): Boolean =
    DB.withTransaction { implicit c =>
      val passwordHashed = SQL"select password_hashed from users where id=$id".map {
        case Row(passwordHashed: String) => passwordHashed
      }.as(scalar[String].single)

      if (BCrypt.checkpw(oldPassword, passwordHashed)) {
        val newPasswordHashed = hashPassword(newPassword)
        SQL"update users set password_hashed = $newPasswordHashed where id = $id".executeUpdate()
        true
      } else false
    }
}
