package com.rxu.duoesports.service.dao

import anorm.SQL
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.rxu.duoesports.models.Region.Region
import com.rxu.duoesports.models.UserAlt
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAltDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def findByUserId(userId: Long): Future[Seq[UserAlt]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
          SELECT * FROM UserAlt
          WHERE userId = {userId}
        """
      ).on('userId -> userId).as(UserAlt.parser.*)
    }
  }

  def insert(userAlt: UserAlt): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO UserAlt (userId, summonerName, summonerId, region)
           VALUES ({userId}, {summonerName}, {summonerId}, {region})
         """
      ).on(
        'userId -> userAlt.userId,
        'summonerName -> userAlt.summonerName,
        'summonerId -> userAlt.summonerId,
        'region -> userAlt.region.toString
      ).executeInsert()
    }
  }

  def delete(summonerName: String, region: Region): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           DELETE FROM UserAlt
           WHERE summonerName = {summonerName}
           AND region = {region}
         """
      ).on(
        'summonerName -> summonerName,
        'region -> region.toString
      ).execute()
    }
  }

}
