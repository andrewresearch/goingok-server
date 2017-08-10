package org.goingok.httpServer

import akka.http.scaladsl.model.HttpMethod

import scala.collection.immutable.Seq


/**
  * Created by andrew@andrewresearch.net on 8/8/17.
  */
trait CorsSupport {

  import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
  import ch.megard.akka.http.cors.scaladsl.model.HttpHeaderRange
  import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
  import akka.http.scaladsl.model.HttpMethods._
  import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}

  lazy val origins = HttpOriginRange(HttpOrigin("http://localhost:4200"), HttpOrigin("http://goingok.org"))
  val corsDefaults = CorsSettings.defaultSettings

  val basicSettings = corsDefaults.copy(
    allowedOrigins = origins,
    allowedMethods = Seq(GET, OPTIONS)
  )

  val authSettings = corsDefaults.copy(
    allowedOrigins = origins,
    allowedMethods = Seq(GET, POST, HEAD, OPTIONS),
    allowCredentials = true,
    allowedHeaders = HttpHeaderRange("Set-Authorization","Authorization"),
    exposedHeaders = Seq("Set-Authorization","Authorization")
  )

  val profileRead = corsDefaults.copy(
    allowedOrigins = origins,
    allowedMethods = Seq(GET, OPTIONS),
    allowCredentials = true,
    allowedHeaders = HttpHeaderRange("Authorization"),
    exposedHeaders = Seq("Authorization")
  )

  val profileWrite = corsDefaults.copy(
    allowedOrigins = origins,
    allowedMethods = Seq(GET, POST, HEAD, OPTIONS),
    allowCredentials = true,
    allowedHeaders = HttpHeaderRange("Authorization"),
    exposedHeaders = Seq("Authorization")
  )


  val allowBasicCors = cors(basicSettings)
  val allowAuthCors = cors(authSettings)
  val allowProfReadCors = cors(profileRead)
  val allowProfWriteCors = cors(profileWrite)

}
