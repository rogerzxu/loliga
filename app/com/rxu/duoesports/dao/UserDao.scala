package com.rxu.duoesports.dao

import com.google.inject.Singleton
import com.mohiva.play.silhouette.api.LoginInfo
import com.rxu.duoesports.models.User

import java.util.UUID
import scala.collection.mutable
import scala.concurrent.Future

@Singleton
class UserDao {
  val users: mutable.HashMap[UUID, User] = mutable.HashMap()

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo) = Future.successful(
    users.find { case (_, user) => user.loginInfo == loginInfo }.map(_._2)
  )

  /**
    * Finds a user by its user ID.
    *
    * @param userID The ID of the user to find.
    * @return The found user or None if no user for the given ID could be found.
    */
  def find(userID: UUID) = Future.successful(users.get(userID))

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: User) = {
    users += (user.userId -> user)
    Future.successful(user)
  }
}