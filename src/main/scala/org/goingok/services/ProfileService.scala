package org.goingok.services

import java.time.OffsetDateTime
import java.util.UUID

import akka.http.scaladsl.model.DateTime
import org.goingok.data.models._
import org.goingok.data.persistence.db.DatabaseOps
import org.json4s.JsonAST.JString

import scala.concurrent.Future

/**
  * Created by andrew@andrewresearch.net on 12/4/17.
  */
object ProfileService {

  import org.goingok.GoingOkContext._

  def getFullProfileForGoingokId(goingok_id:String):Future[Profile] = {

    val id = UUID.fromString(goingok_id)

    for {
      ms <- getMessagesForGoingokId(id)
      rs <- getReflectionsForGoingokId(id)
      r <- getResearchForGoingokId(id)
    } yield Profile(goingok_id,ms,rs,r)

  }

  def getMessagesForGoingokId(goingok_id:UUID):Future[List[Message]] = {
    DatabaseOps.selectMessagesForGoingokId(goingok_id).map { messages =>
      if(messages.nonEmpty) {
        messages.toList
      } else {
        log.info(s"New user profile - creating welcome message for $goingok_id")
        val newMessage = Message(DateTime.now.toIsoDateTimeString(),WelcomeMsg.title,WelcomeMsg.text,WelcomeMsg.value,goingok_id)
        DatabaseOps.insertMessage(newMessage)
        val msgs = Seq()
        (msgs :+ newMessage).toList
      }
    }
  }

  def getReflectionsForGoingokId(goingok_id:UUID):Future[List[ReflectionEntry]] = {
    DatabaseOps.selectReflectionsForGoingokId(goingok_id).map { refs =>
      refs.map( r => ReflectionEntry(r.timestamp,ReflectionData(r.point,r.text))).toList
    }
  }


  def getResearchForGoingokId(goingok_id:UUID):Future[Research] = {
    DatabaseOps.selectUserForGoingokId(goingok_id).map{ user =>
      if(user.nonEmpty) buildResearchFromCode(user.get.research_code,user.get.research_consent)
      else Research()
    }
  }

  private def buildResearchFromCode(code:String,consent:Boolean):Research = {
    val segs = code.split('-')
    Research(ResearchEntry("",segs.apply(0)),ResearchEntry("",segs.apply(1)),ResearchEntry("",segs.apply(2)),consent)
  }

  def saveReflection(goingok_id:String,reflection:ReflectionEntry): Future[String] = {
    log.info(s"Saving new reflection for $goingok_id")
    DatabaseOps.insertReflection(Reflection(reflection.timestamp,reflection.reflection.point,reflection.reflection.text,UUID.fromString(goingok_id)))
    Future("Your reflection has been saved")
  }

  def saveResearch(goingok_id:String,research:Research): Future[String] = {
    log.info(s"Updating research for $goingok_id")
    val code = research.project.code +"-"+ research.organisation.code +"-"+ research.cohort.code
      DatabaseOps.updateUserResearch(UUID.fromString(goingok_id),code,research.consent)
    Future("Your research choice has been saved")
  }


//  def dummyProfile():Profile = {
//
//    import org.goingok.data.models.Profile
//
//    import scala.concurrent.Future
//
//    val messages = ListBuffer[Message]()
//    messages +=  Message(DateTime.now.toIsoDateTimeString(),this.welcomeTitle,this.welcomeMessage,JString("..AG.."),UUID.randomUUID())
//    val entries = ListBuffer[ReflectionEntry]()
//    entries += ReflectionEntry(DateTime.now.-(60000).toIsoDateTimeString(),ReflectionData(30.0,"This should be the most recent entry"))
//    entries += ReflectionEntry(DateTime.now.-(120000).toIsoDateTimeString(),ReflectionData(70.0,"The oldest entry"))
//    val research =  Research(ResearchEntry("a name","T2T"),ResearchEntry(),ResearchEntry())
//    Profile("ThisIsMyDummyId",messages.toList,entries.toList,research)
//  }



}

object WelcomeMsg {
  val title = "Welcome to GoingOK"
  val text = "<p>We hope that you find it helpful to reflect on a regular basis.</p><p>Remember that it is better reflect regularly. It doesn't have to be long - just a couple of sentences on how your day or week has gone. Of course if you want to unload, there is certainly no reason why you can't write a long reflection. To get the most benefit, reflecting regularly is the key.</p><p>If you are using GoingOK as part of a research project, please ensure that you enter your details in the 'Research' box below. Your entry of details is essential for participation. If you are uncertain about the details of the research project, talk to the person responsible for your cohort in your organisation.</p><p>Please let us know if anything on this page can be improved in any way: <a href=\"mailto:support@goingok.com\">support@goingok.com</a></p>"
  val value = JString("GoingOK Team")
}
