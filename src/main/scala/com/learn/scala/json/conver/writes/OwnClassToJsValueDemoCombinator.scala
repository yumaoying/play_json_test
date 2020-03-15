package com.learn.scala.json.conver.writes

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * @author ymy  2020/3/15
 *         实现Writes------自定义类转换为JsValue(组合器模式)
 *         对于自定义类型要转换成JsValue，必须在一定范围内隐式定义Writes
 *         JsValue转换为基本类型或自定义类的方式
 */
object OwnClassToJsValueDemoCombinator extends App {

  //6.自定义类型转换为JsValue，通过组合器
  case class Location(lat: Double, long: Double)

  //and 在play.api.libs.functional.syntax._ 包下
  implicit val locationWrites: Writes[Location] = (
    (JsPath \ "lat").write[Double] and
      (JsPath \ "long").write[Double]
    ) (unlift(Location.unapply))


  case class Resident(name: String, age: Int, role: Option[String])

  implicit val residentWrites: Writes[Resident] = (
    (__ \ "name").write[String] ~ //and 可用 ~代替
      (__ \ "age").write[Int] ~ // JsPath 可用 __ 双下化线代替
      (__ \ "role").write[Option[String]]
    ) (unlift(Resident.unapply))


  case class Place(name: String, location: Location, residents: Seq[Resident])

  implicit val placeWrites: Writes[Place] = (
    (__ \ "name").write[String] ~
      (__ \ "location").write[Location] ~
      (__ \ "residents").write[Seq[Resident]]) (unlift(Place.unapply))

  val place2: Place = Place("Watership Down2",
    Location(51.235685, -1.309197),
    Seq(Resident("Fiver2", 4, None), Resident("Bigwig2", 6, Some("Owsla"))))


  val json = Json.toJson(place2)
  println("自定义类型转换为JsValue: " + json)
}
