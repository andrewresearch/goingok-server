package org.goingok.httpServer

import java.util.UUID

import com.softwaremill.session.{InMemoryRefreshTokenStorage, SessionConfig, SessionManager, SessionUtil}

/**
  * Created by andrew@andrewresearch.net on 7/8/17.
  */
trait SessionSupport {

import org.goingok.GoingOkContext._
  import com.softwaremill.session.SessionDirectives._
  import com.softwaremill.session.SessionOptions._

  val sessionConfig = SessionConfig.default(SessionUtil.randomServerSecret())
  implicit val sessionManager = new SessionManager[String](sessionConfig)
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[String] {
    def log(msg: String) = System.out.println(msg)
  }

  def sessionSet(session: String) = setSession(oneOff, usingHeaders, session)

  val sessionRequired = requiredSession(oneOff, usingHeaders)
  val sessionInvalidate = invalidateSession(oneOff, usingHeaders)

}
