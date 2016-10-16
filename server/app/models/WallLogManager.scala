package models

import play.api.Logger
import play.api.Play.current
import anorm.Row
import play.api.db.DB
import anorm._

import scala.util.{ Try, Success }

/**
 * Created by kindone on 2016. 6. 26..
 */
case class WallLog(wallId: Long, logId: Long, actionType: Int, action: Option[String])

class WallLogManager {
  def find(wallId: Long)(implicit userId: Long) = DB.withConnection { implicit c =>
    SQL"""select log.wall_id,log.log_id, log.action_type, log.action
          from wall_logs as log, walls_of_user
          where walls_of_user.user_id = $userId
          and walls_of_user.wall_id = log.wall_id
          and log.wall_id = $wallId""".map {
      case Row(wallId: Long, logId: Long,
        actionType: Int, action: Option[String]) => WallLog(wallId, logId, actionType, action)
    }.list
  }

  def create(wallLog: WallLog)(implicit userId: Long): Tuple2[Long, Boolean] = {
    // returns id

    DB.withTransaction { implicit c =>
      val maxId =
        SQL"""select COALESCE(MAX(log_id),0) from wall_logs
             where wall_id = ${wallLog.wallId}""".map {
          case Row(id: Long) => id
        }.single()

      if (maxId == wallLog.logId) {
        SQL"""insert into wall_logs(wall_id, log_id, action_type, action)
           VALUES(${wallLog.wallId}, ${maxId}+1, ${wallLog.actionType}, ${wallLog.action})
        """.executeInsert()

        (maxId + 1, true)
      } else
        (maxId, false)
    }
    //    Logger.info(find(wallLog.wallId).toString)
  }

}
