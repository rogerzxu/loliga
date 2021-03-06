package com.rxu.duoesports.service.dao

import anorm.SQL
import com.google.inject.{Inject, Singleton}
import com.google.inject.name.Named
import com.rxu.duoesports.models.VerificationCode
import com.typesafe.scalalogging.LazyLogging
import play.api.db.Database

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class VerificationCodeDao @Inject()(
  db: Database
)(
  @Named("jdbcEC") implicit val ec: ExecutionContext
) extends LazyLogging {

  def upsert(verificationCode: VerificationCode): Future[Unit] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           INSERT INTO VerificationCode (userId, code)
           VALUES ({userId}, {code})
           ON DUPLICATE KEY UPDATE
             userId = {userId},
             code = {code}
         """
      ).on(
        'userId -> verificationCode.userId,
        'code -> verificationCode.code
      ).execute()
    }
  }

  def findByUserId(userId: Long): Future[Option[VerificationCode]] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           SELECT * FROM VerificationCode
           WHERE userId = {userId}
         """
      ).on('userId -> userId).as(VerificationCode.parser.singleOpt)
    }
  }

  def deleteByUser(userId: Long): Future[Boolean] = Future {
    db.withConnection { implicit c =>
      SQL(
        s"""
           DELETE FROM VerificationCode
           WHERE userId = {userId}
         """
      ).on('userId -> userId).execute()
    }
  }

}
