package org.goingok.data.persistence.db.postgresql

/**
  * Created by andrew@andrewresearch.net on 2/09/2016.
  */
import java.sql.JDBCType
import java.util.UUID

import com.github.tminglei.slickpg._
import org.json4s._
import slick.driver.JdbcProfile
import slick.jdbc.{PositionedParameters, PositionedResult, SetParameter}
import slick.profile.Capability

trait GoingOkPostgresDriver extends ExPostgresDriver
  with PgArraySupport
  with PgDate2Support
  with PgRangeSupport
  with PgHStoreSupport
  with PgJson4sSupport
  with PgSearchSupport
  //with PgPostGISSupport
  with PgNetSupport
  with PgLTreeSupport
  with array.PgArrayJdbcTypes {

  def pgjson = "jsonb"
  type DOCType = JValue
  override val jsonMethods = org.json4s.jackson.JsonMethods

  // Add back `capabilities.insertOrUpdate` to enable native `upsert` support; for postgres 9.5+
  override protected def computeCapabilities: Set[Capability] =
  super.computeCapabilities + JdbcProfile.capabilities.insertOrUpdate

  override val api = MyAPI

  object MyAPI extends API
    with ArrayImplicits
    with DateTimeImplicits
    //with Date2DateTimePlainImplicits
    with Json4sJsonImplicits
    with NetImplicits
    with LTreeImplicits
    with RangeImplicits
    with HStoreImplicits
    with SearchImplicits
  with UUIDPlainImplicits
    with SearchAssistants {
    implicit val strListTypeMapper = new SimpleArrayJdbcType[String]("text").to(_.toList)
    implicit val advancedStringListTypeMapper = new AdvancedArrayJdbcType[String]("text", utils.SimpleArrayUtils.fromString(identity)(_).orNull, utils.SimpleArrayUtils.mkString(identity))
    implicit val json4sJsonArrayTypeMapper =
      new AdvancedArrayJdbcType[JValue](pgjson,
        (s) => utils.SimpleArrayUtils.fromString[JValue](jsonMethods.parse(_))(s).orNull,
        (v) => utils.SimpleArrayUtils.mkString[JValue](j=>jsonMethods.compact(jsonMethods.render(j)))(v)
      ).to(_.toList)
  }
}

object GoingOkPostgresDriver extends GoingOkPostgresDriver {
}

trait UUIDPlainImplicits {

  implicit class PgPositionedResult(val r: PositionedResult) {
    def nextUUID: UUID = UUID.fromString(r.nextString)

    def nextUUIDOption: Option[UUID] = r.nextStringOption().map(UUID.fromString)
  }

  implicit object SetUUID extends SetParameter[UUID] {
    def apply(v: UUID, pp: PositionedParameters) {
      pp.setObject(v, JDBCType.BINARY.getVendorTypeNumber)
    }
  }

}