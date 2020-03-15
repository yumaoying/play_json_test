package com.learn.scala.json.conver.format

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

/**
 * @author ymy  2020/3/15
 *         可以定义Format使用同一类型的Reads和Writes组合
 *         -----实现format可以实现类和json字符串的相互转换
 */
object FormatDemo extends App {

  //定义case class
  case class Location(lat: Double, long: Double)

  val locationReads: Reads[Location] = (
    (__ \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
      (__ \ "long").read[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply _)

  val locationWrites: Writes[Location] = (
    (__ \ "lat").write[Double] and
      (__ \ "long").write[Double]
    ) (unlift(Location.unapply))

  //使用Writes和Reads实现隐式的format
  implicit val locationFormat: Format[Location] = Format(locationReads, locationWrites)

  //类转换为JsValue
  val location = Location(30, 20)
  val jsValue = Json.toJson(location)
  println(s"类转换为jsValue:  $jsValue")
  //类转换为jsValue:  {"lat":30,"long":20}

  //jsValue转换为类
  val location2 = jsValue.as[Location]
  println("jsValue转换为实体类:" + location2)
  //jsValue转换为实体类:Location(30.0,20.0)
}
