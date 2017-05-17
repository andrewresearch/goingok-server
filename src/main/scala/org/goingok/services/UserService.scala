package org.goingok.services

import java.util.UUID

import org.goingok.data.models.{User, UserAuth}
import org.goingok.data.persistence.db.DatabaseOps

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by andrew@andrewresearch.net on 12/4/17.
  */
object UserService {

  import org.goingok.GoingOkContext._

  def getOrCreateGoingokId(google_id:String,google_email:String): Future[UUID] = {
    DatabaseOps.selectUserAuthForGoogleId(google_id).map { uao =>
      if(uao.nonEmpty) {
        val gokId = uao.get.goingok_id
        log.info(s"Authorised user with goingok_id: $gokId")
        gokId
      }
      else {
        val newUserAuth = UserAuth(google_id=google_id,google_email=google_email)
        val newUser = User(newUserAuth.goingok_id,"","NON-NON-NON")
        log.info(s"Creating new user with goingok_id: ${newUserAuth.goingok_id}")
        DatabaseOps.insertUserAuth(newUserAuth)
        DatabaseOps.insertUser(newUser)
        newUserAuth.goingok_id
      }
    }
  }
}
