package com.learn.scala.json.conver.format

/**
 * @author ymy  2020/3/15
 *         可以定义Format使用组合器
 *         如果读写是对称的（在实际应用中可能不是这样,对称即要求jsValue中的属性与model的属性名称一一对应），
 *  -----实现format可以实现类和json字符串的相互转换
 */
object FormatDemo2 extends App {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import play.api.libs.json.Reads._

  //定义case class
  case class Location(lat: Double, long: Double)

  implicit val locationFormat: Format[Location] = (
    (JsPath \ "lat").format[Double](min(-90.0) keepAnd max(90.0)) and
      (JsPath \ "long").format[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply, unlift(Location.unapply))


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
