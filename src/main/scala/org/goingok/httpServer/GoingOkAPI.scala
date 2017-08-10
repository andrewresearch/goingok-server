package org.goingok.httpServer

import akka.http.scaladsl.model.{HttpHeader, HttpRequest}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.StandardRoute
import org.goingok._
import org.goingok.data.models.{GokId, ReflectionEntry, Research, ServerInfo}
import org.goingok.handlers.{AuthorisationHandler, ProfileHandler}
import org.goingok.message.HandlerMessage
import org.goingok.services.ProfileService

/**
  * Created by andrew@andrewresearch.net on 20/2/17.
  */
trait GoingOkAPI extends GenericApi with CorsSupport {

  import GoingOkContext._

  override val version = "v1"
  override val details: String = "no details yet" // ApiInfo(Config.name,Config.description,Config.version,Config.colour)

  override val routes = root ~pathPrefix(version) {
    healthRoute ~ adminRoutes ~ customRoutes ~ nothingHere
  }

    override val customRoutes = {
      pathPrefix("client") {
        //extractRequest { request =>
          //log.warning(s"Headers: ${request.headers}")
          profileRoutes ~ authRoutes
        //}
      }
    }

  val authRoutes = allowAuthCors {
    pathPrefix("auth") {
      post {
        import akka.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers.stringUnmarshaller
        entity(as[String]) { str =>
          if (str.isEmpty) complete("An  ID Token is required")
          else {
            log.debug(s"ID token:  $str")
            val authMsg = HandlerMessage.Authorisation(str)
            onComplete(AuthorisationHandler.process(authMsg)) { gokId =>
              val id = gokId.get
              log.info(s"Session set to: $id")
              respondWithHeader(RawHeader("Set-Authorization",id)) {
                complete(GokId(id))
              }
            }
          }
        }
      }
    }
  }

  def completeWithSession(request:HttpRequest,handler:String="profile",entry:Option[Any] = None):StandardRoute = {
    //log.warning("HEADERS: "+request.headers.toString)
    val session = request.headers.map(h => (h.name -> h.value.split(" ").last)).toMap.getOrElse("Authorization","no-session")
    //log.warning("Extracted Session: "+session)
    handler match {
      case "profile" => complete(ProfileHandler.process(HandlerMessage.Profile(session)))
      case "reflection" => {
        if (entry.nonEmpty) complete(ProfileHandler.process(HandlerMessage.ReflectionMsg(session, entry.get.asInstanceOf[ReflectionEntry])))
        else complete("A reflection entry is required")
      }
      case "research" => {
        log.info(s"ResearchMsg entry for $session: ${entry.toString}")
        if (entry.nonEmpty) complete(ProfileHandler.process(HandlerMessage.ResearchMsg(session, entry.get.asInstanceOf[Research])))
        else complete("A research entry is required")
      }
    }

  }

  val profileRoutes = pathPrefix("profile") {
    allowProfReadCors(get(extractRequest(completeWithSession(_)))) ~
      pathPrefix("reflection") {
        import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
        cors() {
          post {
            entity(as[Option[ReflectionEntry]]) { ref =>
              log.warning("ReflectionEntry: "+ref)
              extractRequest(completeWithSession(_,"reflection",ref))
            }
          }
        }



      } ~ pathPrefix("research") {
          //allowProfWriteCors(options(complete("pre-flight for research"))) ~
      import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
          cors() {
            post {
              entity(as[Option[Research]]) { res =>
                if (res.isEmpty) complete("A Research  Entry is required")
                else {
                  extractRequest(completeWithSession(_, "research", res))
                  //complete(ProfileHandler.process(HandlerMessage.ResearchMsg(session, entry)))
                }
              }
            }
          }
      }
    }



  lazy val serverInfo = ServerInfo(BuildInfo.name, BuildInfo.version, BuildInfo.builtAtString)

  override val adminRoutes = allowBasicCors {
    //extractRequest { request =>
      //log.warning(s"Headers: ${request.headers}")
      pathPrefix("admin") {
        path("version") {
          get {
            log.info("Version request")
            complete(serverInfo)
          }
        } ~
        get {
          nothingHere
        }
      }
    //}
  }


}

//override val CustomExceptionHandler = ExceptionHandler {
//  case _: UnknownAnalysisType =>
//  extractUri { uri =>
//  log.error(s"Request to $uri did not include a valid analysis type")
//  complete(HttpResponse(StatusCodes.InternalServerError, entity = "The request did not include a valid analysis type"))
//}
//  case e: UnAuthorizedAccess => {
//  log.error(s"Request is not authorized")
//  complete(HttpResponse(StatusCodes.Forbidden, entity = e.message))
//}
//
//}
//    extractUnmatchedPath { param =>
//      if (!param.isEmpty) {
//        post {
//          import akka.http.scaladsl.unmarshalling.PredefinedFromEntityUnmarshallers.byteStringUnmarshaller
//          entity(as[ByteString]) { str =>
//            if (str.isEmpty) complete("Text required for analysis")
//            else {
//              val analysisType = param.dropChars(1).head.toString
//              println("analysisType - {}",analysisType)
//              val analysisMsg = JsonMessage.ByteStringAnalysis(str,analysisType)
//              complete(TextAnalysisHandler.analyse(analysisMsg))
//            }
//          }
//        }
//      }
//      else nothingHere
//    }
