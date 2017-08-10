package org.goingok.httpServer

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s._


/** The main API structure
  *
  * Provides the generic API version and config details.
  * Provides stub routes: ''customRoutes'', and ''adminRoutes''
  * which are replaced by [[org.goingok.httpServer.GoingOkAPI]]
  *
  * @todo - Need to wire in config information for API details.
  *
  */
trait GenericApi extends Json4sSupport {
  val version = "v0"
  val details: String = "no details yet" // ApiInfo(Config.name,Config.description,Config.version,Config.colour)

  implicit val formats: Formats = DefaultFormats
  implicit val jacksonSerialization: Serialization = jackson.Serialization

  lazy val nothingHere = complete(ResponseMessage("There is nothing at this URL"))

  val healthRoute = path("health") { get(complete(ResponseMessage("ok"))) }
  val adminRoutes = path("admin") {  nothingHere }
  val customRoutes = path("custom") { nothingHere }

  val apiDetails = pathEnd(complete(ResponseMessage(details)))

  val root = pathSingleSlash { get(complete(ResponseMessage("The current version of this API can be found at /" + version))) }

  val routes = root ~ pathPrefix("v") {
    healthRoute ~
    adminRoutes ~
    customRoutes ~
    nothingHere
  }

}





