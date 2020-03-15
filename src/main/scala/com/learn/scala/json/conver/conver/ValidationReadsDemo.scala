import play.api.libs.json._
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * @author ymy  2020/3/15
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


//分解Reads方法
//1.combine Reads (使用别名__双下划线替换JsPath)
//val locationReadsBuilder: FunctionalBuilder[Reads]#CanBuild2[Double, Double] =
//(__ \ "lat").read[Double] and
//  (__ \ "long").read[Double]
////2.combinator读取对象
////第二次调用CanBuildX的apply方法，使用一个函数将单个值转换为model
//implicit val locationReads2 = locationReadsBuilder.apply(Location.apply _)
////3.完整写法
//implicit val locationRead3: Reads[Location] = (
//  (JsPath \ "lat").read[Double] and
//    (JsPath \ "long").read[Double]
//  ) (Location.apply _)
