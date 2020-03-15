package com.learn.scala.json.conver.autoMap

import AutoMapWritesDemo.Resident

/**
 * @author ymy  2020/3/15
 *         自动映射 Format[T]--可同时完成读，写的转换
 */
object AutoMapFormatDemo extends App {

  case class Resident(name: String, age: Int, role: Option[String])

  import play.api.libs.json._

  //自动转换 Format[T]
  implicit val residentFormat = Json.format[Resident]


  //-----------------json和类之前的转换------------------------
  val resident = Resident(name = "Fiver", age = 4, role = None)

  //将case class 类转换为jsValue
  val residentJson: JsValue = Json.toJson(resident)
  println(residentJson)
  //returns {"name":"Fiver","age":4}

  //json字符串
  val jsonString: JsValue = Json.parse(s"""{ "name" : "Fiver", "age" : 4}""")
  //jsValue转换为case class
  val resident2 = jsonString.as[Resident]
  println(resident2)
  //returns Resident(Fiver,4,None)
}
