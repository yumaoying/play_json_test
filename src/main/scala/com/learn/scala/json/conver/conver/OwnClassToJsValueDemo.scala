package com.learn.scala.json.conver.conver

import play.api.libs.json._

/**
 * @author ymy  2020/3/15
 *         自定义类转换为JsValue
 *         对于自定义类型要转换成JsValue，必须在一定范围内隐式定义Writes
 */
object OwnClassToJsValueDemo extends App {

  //5.自定义类型转换为JsValue，需要实现Writes方法
  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])

  private implicit val locationWrites = new Writes[Location] {
    override def writes(location: Location): JsValue = Json.obj(
      "lat" -> location.lat,
      "long" -> location.long)
  }

  private implicit val residentWrites = new Writes[Resident] {
    override def writes(resident: Resident): JsValue = Json.obj(
      "name" -> resident.name,
      "age" -> resident.age,
      "role" -> resident.role
    )
  }

  private implicit val placeWrites = new Writes[Place] {
    override def writes(place: Place): JsValue = Json.obj(
      "name" -> place.name,
      "location" -> place.location,
      "residents" -> place.residents
    )
  }

  val place: Place = Place(
    "Watership Down",
    Location(51.235685, -1.309197),
    Seq(Resident("Fiver", 4, None), Resident("Bigwig", 6, Some("Owsla"))))


  val json = Json.toJson(place)
  println("自定义类型转换为JsValue" + json)
}
