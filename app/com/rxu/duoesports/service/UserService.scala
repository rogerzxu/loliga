package com.rxu.duoesports.service

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.rxu.duoesports.dto.{UpdateAccountInfo, UpdatePlayerInfo}
import com.rxu.duoesports.service.dao.UserDao
import com.rxu.duoesports.models.User
import com.rxu.duoesports.util.{ActivateUserException, CreateUserException, GetUserException, UpdateUserException}
import com.typesafe.scalalogging.LazyLogging
import play.api.cache.AsyncCacheApi

import scala.concurrent.{ExecutionContext, Future}

class UserService @Inject()(
  cache: AsyncCacheApi,
  userDao: UserDao
)(
  implicit ec: ExecutionContext
) extends IdentityService[User]
  with LazyLogging {

  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    logger.trace(s"Retrieving user: $loginInfo")
    val cacheKey = s"user-${loginInfo.providerKey}"
    cache.get[Option[User]](cacheKey) flatMap {
      case Some(cachedUser) =>
        logger.trace(s"Found entry in cache for ${cacheKey}")
        Future.successful(cachedUser)
      case None => userDao.findByEmail(email = loginInfo.providerKey) map { user =>
        logger.trace(s"Storing entry in cache for ${cacheKey}")
        cache.set(cacheKey, user)
        user
      }
    }
  }

  def create(user: User): Future[Long] = {
    logger.info(s"Creating user: $user")
    for {
      maybeUserId <- userDao.create(user)
      userId <- maybeUserId match {
        case Some(userId) => Future.successful(userId)
        case None => Future.failed(CreateUserException(s"Failed to create user $user"))
      }
      _ <- cache.remove(user.getCacheKey)
    } yield {
      logger.debug(s"Invalidating ${user.getCacheKey} from cache")
      userId
    }
  }

  def getById(id: Long): Future[User] = {
    findById(id) flatMap {
      case Some(user) => Future.successful(user)
      case None =>
        logger.error(s"Failed to get user $id")
        Future.failed(GetUserException(s"Could not find user $id"))
    }
  }

  def getByEmail(email: String): Future[User] = {
    findByEmail(email) flatMap {
      case Some(user) => Future.successful(user)
      case None =>
        logger.error(s"Failed to get user $email")
        Future.failed(GetUserException(s"Could not find user $email"))
    }
  }

  private def findById(id: Long): Future[Option[User]] = {
    logger.debug(s"Finding user by id $id")
    userDao.findById(id)
  }

  private def findByEmail(email: String): Future[Option[User]] = {
    logger.debug(s"Finding user by email $email")
    userDao.findByEmail(email)
  }

  def activate(id: Long): Future[Unit] = {
    logger.info(s"Activating user by id $id")
    for {
      result <- userDao.activate(id)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to activate user $id")
          Future.failed(ActivateUserException(s"MariaDB failed to activate user $id"))
        case _ => Future.successful(())
      }
      user <- getById(id)
      _ <- cache.remove(user.getCacheKey)
    } yield logger.debug(s"Invalidating ${user.getCacheKey} from cache")
  }

  def update(user: User, updateAccountInfo: UpdateAccountInfo): Future[Unit] = {
    logger.info(s"Updating account info for ${user.id}: $updateAccountInfo")
    for {
      result <- userDao.update(user.id, updateAccountInfo)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update Account Info for ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update Account Info for ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- cache.remove(user.getCacheKey)
    } yield logger.debug(s"Invalidating ${user.getCacheKey} from cache")
  }

  def update(user: User, updatePlayerInfo: UpdatePlayerInfo): Future[Unit] = {
    logger.info(s"Updating player info for ${user.id}: $updatePlayerInfo")
    for {
      result <- userDao.update(user.id, updatePlayerInfo)
      _ <- result match {
        case 0 => logger.error(s"MariaDB failed to update Player Info for ${user.id}")
          Future.failed(UpdateUserException(s"MariaDB failed to update Player Info for ${user.id}"))
        case _ => Future.successful(())
      }
      _ <- cache.remove(user.getCacheKey)
    } yield logger.debug(s"Invalidating ${user.getCacheKey} from cache")
  }

}
