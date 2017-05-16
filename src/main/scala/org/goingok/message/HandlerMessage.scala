package org.goingok.message

import org.goingok.data.models.{ReflectionEntry,Research}

/**
  * Created by andrew@andrewresearch.net on 3/3/17.
  */
object HandlerMessage {
  case class Authorisation(token:String)
  case class Profile(id:String)
  case class ReflectionMsg(id:String, reflection:ReflectionEntry)
  case class ResearchMsg(id:String,research:Research)
}
