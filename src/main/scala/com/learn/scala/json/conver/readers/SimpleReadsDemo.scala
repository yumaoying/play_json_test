package com.learn.scala.json.conver.readers

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * @author ymy  2020/3/15
 *         简单Reads---实现Reads，能将jsValue字符串转换为自定义类
 */
object SimpleReadsDemo extends App {

  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])


  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double] and
      (JsPath \ "long").read[Double]
    ) (Location.apply _)

  implicit val residentReads: Reads[Resident] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "age").read[Int] and
      (JsPath \ "role").readNullable[String]
    ) (Resident.apply _)

  implicit val placeReads: Reads[Place] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "location").read[Location] and
      (JsPath \ "residents").read[Seq[Resident]]
    ) (Place.apply _)

  val json = Json.parse(
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

  val placeResult: JsResult[Place] = json.validate[Place]
  println("validate转换为Place:" + placeResult)
  //JsSuccess(Place(Watership Down,Location(51.235685,-1.309197),Vector(Resident(Fiver,4,None), Resident(Bigwig,6,Some(Owsla)))),)

  //validate 进行转换
  val residentResult: JsResult[Resident] = (json \ "residents") (1).validate[Resident]
  println("validate转换为residentResult：" + residentResult)
  // JsSuccess(Resident(Bigwig,6,Some(Owsla)),)


}
