package org.goingok.data.models

import org.json4s.JsonAST.JValue

/**
  * Created by andrew@andrewresearch.net on 3/3/17.
  */

case class GokId(id:String)

case class Profile(id:String = "", messages:List[Message]=List(), reflectionEntries:List[ReflectionEntry]=List(), research:Research = Research())



case class Research(project:ResearchEntry = ResearchEntry(), organisation:ResearchEntry = ResearchEntry(), cohort:ResearchEntry = ResearchEntry(), consent:Boolean = false)

case class ResearchEntry(name:String = "None Selected", code:String = "NON")

case class ReflectionEntry(timestamp:String,reflection:ReflectionData)

case class ReflectionData(point:Double,text:String)

