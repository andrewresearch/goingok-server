package org.goingok.data.persistence.db.postgresql

import java.time.OffsetDateTime
import java.util.UUID

import org.json4s.JsonAST.JString
import org.json4s.{CustomSerializer, _}

/**
  * Created by andrew@andrewresearch.net on 20/10/16.
  */
object UuidSerializer extends CustomSerializer[UUID](format =>
  (
    {
      case JString(s) => UUID.fromString(s)
      case JNull => null
    },
    {
      case x: UUID => JString(x.toString)
    }
  )
)

object OffsetDateTimeSerializer extends CustomSerializer[OffsetDateTime](format => (
  {
    case JString(s) => OffsetDateTime.parse(s)
    case JNull => null
  },
  {
    case o:OffsetDateTime => JString(o.toString)
  }
)
)