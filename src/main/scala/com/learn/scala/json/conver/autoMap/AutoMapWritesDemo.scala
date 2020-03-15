package com.learn.scala.json.conver.autoMap

/**
 * @author ymy  2020/3/15
 *         自动映射 Writes[T]
 */
object AutoMapWritesDemo extends App {

  case class Resident(name: String, age: Int, role: Option[String])

  import play.api.libs.json._

  //自动映射Writes
  implicit val residentWrites: OWrites[Resident] = Json.writes[Resident]

  val resident = Resident(name = "Fiver", age = 4, role = None)

  //将类转换为jsValue
  val residentJson: JsValue = Json.toJson(resident)
  println(residentJson)
  //{"name":"Fiver","age":4}
}
