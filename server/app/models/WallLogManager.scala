package models

import play.api.Logger
import play.api.Play.current
import anorm._
import anorm.Row
import anorm.SqlParser.scalar
import play.api.db.DB

/**
 * Created by kindone on 2016. 6. 26..
 */
case class WallLog(wallId: Long, logId: Long, actionType: Int, action: Option[String])

class WallLogManager {

  val parser = RowParser[WallLog] {
    case Row(wallId: Long, logId: Long,
      actionType: Int, action: Option[String]) => Success(WallLog(wallId, logId, actionType, action))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def find(wallId: Long)(implicit userId: Long): List[WallLog] = DB.withConnection { implicit c =>
    SQL"""select log.wall_id,log.log_id, log.action_type, log.action
          from wall_logs as log, walls_of_user
          where walls_of_user.user_id = $userId
          and walls_of_user.wall_id = log.wall_id
          and log.wall_id = $wallId""".as(parser.*)
  }

  def create(wallLog: WallLog)(implicit userId: Long): LogCreationResult = {
    // returns id

    DB.withTransaction { implicit c =>
      val wallId = wallOfUser(wallLog.wallId, userId).get // will throw error if not exists

      val maxId =
        SQL"""select COALESCE(MAX(log_id),0) from wall_logs
             where wall_id = ${wallId}""".map {
          case Row(id: Long) => id
        }.as(scalar[Long].single)

      if (maxId == wallLog.logId) {
        SQL"""insert into wall_logs(wall_id, log_id, action_type, action)
           VALUES(${wallId}, ${maxId}+1, ${wallLog.actionType}, ${wallLog.action})
        """.executeInsert()

        LogCreationResult(maxId + 1, true)
      } else
        LogCreationResult(maxId, false)
    }
  }

  private def wallOfUser(wallId: Long, userId: Long): Option[Long] = DB.withConnection { implicit c =>
    SQL"""select wall_id from walls_of_user
         where user_id = $userId and wall_id = $wallId""".as(scalar[Long].singleOpt)
  }

}
