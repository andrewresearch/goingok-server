package org.goingok.handlers

import java.util.Collections

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier.Builder
import org.goingok.message.{HandlerMessage, JsonMessage}
import com.google.api.client.googleapis.auth.oauth2.{GoogleIdToken, GoogleIdTokenVerifier}
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.goingok.Config
import org.goingok.data.models.{GoogleUser, UserAuth}
import org.goingok.data.persistence.db.DatabaseOps
import org.goingok.services.UserService

import scala.util.Success
//import org.goingok.GokId

import scala.concurrent.Future

/**
  * Created by andrew@andrewresearch.net on 3/3/17.
  */
object AuthorisationHandler {

  import org.goingok.GoingOkContext._

  def process(msg:HandlerMessage.Authorisation):Future[String] = {

    val transport:HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    val jsonFactory = JacksonFactory.getDefaultInstance()

    val verifier = new Builder(transport, jsonFactory)
      .setAudience(Collections.singletonList(Config.string("google.client_id")))
      .build()

    val idToken = verifier.verify(msg.token)

    val payload:Payload = if (idToken != null) idToken.getPayload()
      else {
        log.warning("Invalid ID token.")
        new Payload
      }

    val googleUser = GoogleUser(payload.getSubject,payload.get("given_name").toString,payload.get("family_name").toString,payload .getEmail(),payload.getEmailVerified(),payload.get("locale").toString)



    log.info(s"User authorised: $googleUser")

   UserService.getOrCreateGoingokId(payload.getSubject,payload.getEmail).map(_.toString)
  }


}


//JsonMessage.formatStringResults(s"|$email|$emailVerified|$name|$pictureUrl|$locale|$familyName|$givenName|","The token has been processed")
//JsonMessage.formatResults[Profile](Future(Profile()),s"|$email|$emailVerified|$name|$pictureUrl|$locale|$familyName|$givenName|")


// Print user identifier
//      val userId = payload.getSubject()
//      System.out.println("User ID: " + userId)

// Get profile information from payload
//      val email = payload .getEmail()
//      val emailVerified = payload.getEmailVerified()
//      val name = payload.get("name")
//      val pictureUrl = payload.get("picture")
//      val locale = payload.get("locale")
//      val familyName = payload.get("family_name")
//      val givenName = payload.get("given_name")