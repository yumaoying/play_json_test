package com.learn.scala.json.conver.readers

import play.api.libs.json.Reads.{max, min}
import play.api.libs.json._
import play.api.libs.functional.syntax._
/**
 * @author ymy  2020/3/15
 *         ---单个类实现Reads，能将json字符串转换类
 *         通过使用复杂读取和自定义验证，读取model
 */
object ValidationReadsDemo extends App {

  //定义case class
  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])

  //读取验证
  implicit val locationReads: Reads[Location] = (
    (__ \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) ~
      (__ \ "long").read[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply _)

  //转换为jsValue
  val json = Json.obj("lat" -> -90, "long" -> -1.309197)

  //验证自定义类型-成功
  json.validate[Location] match {
    case s: JsSuccess[Location] => println("解析成功：" + s.get)
    case e: JsError => println(s"Errors: ${JsError.toJson(e)}")
  }
  //解析成功:Location(51.235685,-1.309197)

  //验证自定义类型-失败
  val json2 = Json.obj("lat" -> 51.235685, "long" -> 190)
  println(json2.validate[Location])
  //JsError(List((/long,List(JsonValidationError(List(error.max),WrappedArray(180.0))))))
}
