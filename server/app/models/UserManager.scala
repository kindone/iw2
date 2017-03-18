package models

import play.api.Play.current
import anorm.Row
import play.api.db.DB
import anorm._
import org.mindrot.jbcrypt.BCrypt

/**
 * Created by kindone on 2016. 4. 2..
 */
class UserManager {
  def findAll() =
    DB.withConnection { implicit c =>
      SQL"select id,email,password_hashed from users".map {
        case Row(id: Long, email: String, passwordHashed: String) => User(id, email, passwordHashed)
      }.list()
    }

  def find(id: Long) =
    DB.withConnection { implicit c =>
      SQL"select id,email,password_hashed from users where id=$id".map {
        case Row(id: Long, email: String, passwordHashed: String) => User(id, email, passwordHashed)
      }.singleOpt()
    }

  def hashPassword(passwordRaw: String) = BCrypt.hashpw(passwordRaw, BCrypt.gensalt)

  def find(email: String, passwordRaw: String) =
    DB.withConnection { implicit c =>
      SQL"select id,password_hashed from users where email=$email".map {
        case Row(id: Long, passwordHashed: String) => (id, passwordHashed)
      }.singleOpt.flatMap {
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

  def delete(id: Long) = {
    // admin
    if (id == 0L) {
      false
    } else {
      DB.withConnection { implicit c =>
        SQL"delete from users where id = $id".executeUpdate()
      } == 1
    }
  }

  def changePassword(id: Long, oldPassword: String, newPassword: String) =
    DB.withTransaction { implicit c =>
      val passwordHashed = SQL"select id,password_hashed from users where id=$id".map {
        case Row(id: Long, passwordHashed: String) => passwordHashed
      }.single

      if (BCrypt.checkpw(oldPassword, passwordHashed)) {
        val newPasswordHashed = hashPassword(newPassword)
        SQL"update users set password_hashed = $newPasswordHashed where id = $id".executeUpdate()
        true
      } else false
    }
}
