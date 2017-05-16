package org.goingok

import com.typesafe.config.ConfigFactory
import org.goingok.httpServer.Server

/** The Application entry point starts the akka-http server
  *
  * @todo - Need to wire in config
  *
  */
object Application extends App {
  Server.startServer(Config.string("server.host"),Config.int("server.port"))
}

object Config {
  private val config = ConfigFactory.load()
  def string(path:String):String = config.getString(path)
  def int(path:String):Int = config.getInt(path)
}