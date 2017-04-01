package models

import com.kindone.infinitewall.data.state.{Wall, Sheet}
import play.api.Play.current
import com.kindone.infinitewall.data.Wall
import anorm._
import play.api.db.DB

/**
 * Created by kindone on 2016. 3. 20..
 */
class WallManager {

  val wallParser = RowParser[Wall] {
    case Row(id: Long, stateId: Long, x: Double, y: Double, scale: Double, title: String) => Success(Wall(id, stateId, x, y, scale, title))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  val sheetParser = RowParser[Sheet] {
    case Row(id: Long, stateId: Long, x: Double, y: Double,
      width: Double, height: Double, content: java.sql.Clob) => Success(Sheet(id, stateId, x, y, width, height, content.getSubString(1, content.length.asInstanceOf[Int])))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  val idParser = RowParser[Long] {
    case Row(id: Long) => Success(id)
    case row           => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def findAll()(implicit userId: Long) =
    DB.withConnection { implicit c =>
      SQL"""select walls.id,walls.state_id,walls.x,walls.y,walls.scale,walls.title from walls, walls_of_user
           where walls_of_user.user_id = $userId and walls_of_user.wall_id = walls.id
         """.as(wallParser.*)
    }

  def find(id: Long)(implicit userId: Long): Option[Wall] =
    wallOfUser(id, userId).flatMap { wallId =>
      DB.withConnection { implicit c =>
        SQL"select id,state_id,x,y,scale,title from walls where id = $wallId".as(wallParser.singleOpt)
      }
    }

  def create(wall: Wall)(implicit userId: Long): Option[Long] =
    // returns id
    DB.withTransaction { implicit c =>
      val wallIdOpt: Option[Long] = SQL"insert into walls(state_id, x, y, scale, title) values(0, ${wall.x}, ${wall.y}, ${wall.scale}, ${wall.title})".executeInsert()
      wallIdOpt.foreach { wallId => SQL"insert into walls_of_user(wall_id, user_id) values(${wallId}, $userId)".executeInsert() }
      wallIdOpt
    }

  def delete(id: Long)(implicit userId: Long): Boolean =
    wallOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"delete from walls where id = $id".executeUpdate()
      } == 1
    }.getOrElse(false)

  def setPan(id: Long, x: Double, y: Double)(implicit userId: Long): Boolean =
    wallOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update walls set x = $x, y = $y where id = $id".executeUpdate()
      } == 1
    }.getOrElse(false)

  def setZoom(id: Long, scale: Double)(implicit userId: Long): Boolean =
    wallOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update walls set scale = $scale where id = $id".executeUpdate()
      } == 1
    }.getOrElse(false)

  def setView(id: Long, x: Double, y: Double, scale: Double)(implicit userId: Long): Boolean =
    wallOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update walls set x = $x, y = $y, scale = $scale where id = $id".executeUpdate()
      } == 1
    }.getOrElse(false)

  def setTitle(id: Long, title: String)(implicit userId: Long): Boolean =
    wallOfUser(id, userId).map { _ =>
      DB.withConnection { implicit c =>
        SQL"update walls set title=$title where id = $id".executeUpdate()
      } == 1
    }.getOrElse(false)

  def getSheets(id: Long)(implicit userId: Long): List[Sheet] = DB.withConnection { implicit c =>
    wallOfUser(id, userId).map { wallId =>
      SQL"""select
            sheets.id, sheets.state_id, sheets.x, sheets.y,
            sheets.width, sheets.height, sheets.content
            from sheets INNER JOIN sheets_in_wall
            ON sheets.id = sheets_in_wall.sheet_id
            where sheets_in_wall.wall_id = $id""".as(sheetParser.*)
    }.getOrElse(List())
  }

  def getSheetIds(id: Long)(implicit userId: Long): Set[Long] = DB.withConnection { implicit c =>
    wallOfUser(id, userId).map { wallId =>
      SQL"""select
            sheets.id
            from sheets INNER JOIN sheets_in_wall
            ON sheets.id = sheets_in_wall.sheet_id
            where sheets_in_wall.wall_id = $id""".as(idParser.*).toSet
    }.getOrElse(Set())
  }

  def createSheet(id: Long, sheet: Sheet)(implicit userId: Long): Option[Long] = DB.withTransaction { implicit c =>
    wallOfUser(id, userId).flatMap { wallId =>
      val sheetId: Option[Long] = SQL"""insert into sheets(state_id, x, y, width, height, content)
        values(0, ${sheet.x}, ${sheet.y}, ${sheet.width}, ${sheet.height}, ${sheet.text})""".executeInsert()
      SQL"insert into sheets_in_wall(wall_id, sheet_id) values($id, ${sheetId.get})".executeInsert()
      sheetId
    }
  }

  def deleteSheet(id: Long, sheetId: Long)(implicit userId: Long): Boolean = DB.withTransaction { implicit c =>
    // must be owned by user
    wallOfUser(id, userId).map { wallId =>
      (SQL"delete from sheets where id = $sheetId".executeUpdate() == 1) &&
        (SQL"delete from sheets_in_wall where wall_id = $wallId and sheet_id = $sheetId".executeUpdate() == 1)
    }.getOrElse(false)
  }

  private def wallOfUser(wallId: Long, userId: Long): Option[Long] = DB.withConnection { implicit c =>
    SQL"""select wall_id from walls_of_user
         where user_id = $userId and wall_id = $wallId""".as(idParser.singleOpt)
  }

}
