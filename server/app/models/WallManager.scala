package models

import play.api.Play.current
import com.kindone.infinitewall.data.{ Sheet, Wall }
import anorm._
import play.api.db.DB

/**
 * Created by kindone on 2016. 3. 20..
 */
class WallManager {

  def findAll() =
    DB.withConnection { implicit c =>
      SQL"select id,x,y,scale,title from walls".map {
        case Row(id: Long, x: Double, y: Double, scale: Double, title: String) => Wall(id, x, y, scale, title)
      }.list()
    }

  def find(id: Long) =
    DB.withConnection { implicit c =>
      SQL"select id,x,y,scale,title from walls where id = $id".map {
        case Row(id: Long, x: Double, y: Double, scale: Double, title: String) => Wall(id, x, y, scale, title)
      }.singleOpt
    }

  def create(wall: Wall): Long = {
    // returns id
    val idOpt: Option[Long] = DB.withConnection { implicit c =>
      SQL"insert into walls(x, y, scale, title) values(${wall.x}, ${wall.y}, ${wall.scale}, ${wall.title})".executeInsert()
    }
    idOpt.get
  }

  def delete(id: Long) =
    DB.withConnection { implicit c =>
      SQL"delete from walls where id = $id".executeUpdate()
    }

  def setPan(id: Long, x: Double, y: Double) =
    DB.withConnection { implicit c =>
      SQL"update walls set x = $x, y = $y where id = $id".executeUpdate()
    }

  def setZoom(id: Long, scale: Double) =
    DB.withConnection { implicit c =>
      SQL"update walls set scale = $scale where id = $id".executeUpdate()
    }

  def setView(id: Long, x: Double, y: Double, scale: Double) =
    DB.withConnection { implicit c =>
      SQL"update walls set x = $x, y = $y, scale = $scale where id = $id".executeUpdate()
    }

  def getSheets(id: Long) = DB.withConnection { implicit c =>
    SQL"""select
          sheets.id, sheets.x, sheets.y,
          sheets.width, sheets.height, sheets.content
          from sheets,sheets_in_wall
          where sheets_in_wall.wall_id = $id
          and sheets.id = sheets_in_wall.sheet_id""".map {
      case Row(id: Long, x: Double, y: Double,
        width: Double, height: Double, content: String) => Sheet(id, x, y, width, height, content)
    }.list()
  }

  def createSheet(id: Long, sheet: Sheet) = DB.withTransaction { implicit c =>
    val sheetId: Option[Long] = SQL"""insert into sheets(x, y, width, height, content)
      values(${sheet.x}, ${sheet.y}, ${sheet.width}, ${sheet.height}, ${sheet.text})""".executeInsert()
    SQL"insert into sheets_in_wall(wall_id, sheet_id) values($id, ${sheetId.get})".executeInsert()
    sheetId.get
  }

  def deleteSheet(id: Long, sheetId: Long) = DB.withTransaction { implicit c =>
    SQL"delete from sheets where id = $sheetId".executeUpdate()
    SQL"delete from sheets_in_wall where wall_id = $id and sheet_id = $sheetId".executeUpdate()
  }

}
