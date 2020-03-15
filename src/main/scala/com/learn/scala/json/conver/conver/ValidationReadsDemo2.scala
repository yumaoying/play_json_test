package com.learn.scala.json.conver.conver

/**
 * @author ymy  2020/3/15
 *         通过使用复杂读取和自定义验证，读取model
 */
object ValidationReadsDemo2 extends App {

  //自定义类
  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
      (JsPath \ "long").read[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply _)

  implicit val residentReads: Reads[Resident] = (
    (JsPath \ "name").read[String](minLength[String](12)) and
      (JsPath \ "age").read[Int](min(0) keepAnd max(150)) and
      (JsPath \ "role").readNullable[String]
    ) (Resident.apply _)

  implicit val placeReads: Reads[Place] = (
    (JsPath \ "name").read[String](minLength[String](2)) and
      (JsPath \ "location").read[Location] and
      (JsPath \ "residents").read[Seq[Resident]]
    ) (Place.apply _)

  val json: JsValue = Json.parse(
    s"""
  {
    "name" : "Watership Down",
    "location" : {
      "lat" : 51.235685,
      "long" : -1.309197
    },
    "residents" : [ {
      "name" : "Fiver",
      "age" : 4,
      "role" : null
    }, {
      "name" : "Bigwig",
      "age" : 6,
      "role" : "Owsla"
    } ]
  }
  """)

  json.validate[Place] match {
    case s: JsSuccess[Location] => println("解析成功：" + s.get)
    case e: JsError => println(s"Errors: ${JsError.toJson(e)}")
  }


}

