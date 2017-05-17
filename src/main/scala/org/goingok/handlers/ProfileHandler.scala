package org.goingok.handlers

import java.util.{Collections, UUID}

import akka.http.scaladsl.model.DateTime
import org.goingok._
import org.goingok.data.models._
import org.goingok.message.{HandlerMessage, JsonMessage}
import org.goingok.services.ProfileService
import org.json4s.JsonAST.JString

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
  * Created by andrew@andrewresearch.net on 30/3/17.
  */
object ProfileHandler {

  import org.goingok.GoingOkContext.log

  def process(msg: HandlerMessage.Profile): Future[Profile] = {
    log.info("Handling profile for: " + msg.id)
    //ProfileService.dummyProfile()
    ProfileService.getFullProfileForGoingokId(msg.id)
  }

  def process(msg: HandlerMessage.ReflectionMsg): Future[String] = {
    ProfileService.saveReflection(msg.id,msg.reflection)
  }

  def process(msg: HandlerMessage.ResearchMsg): Future[String] = {
    ProfileService.saveResearch(msg.id,msg.research)
  }


}
