package com.learn.scala.json.conver.conver

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * @author ymy  2020/3/15
 * 1.自定义类转换为JsValue-组合器模式
 *         对于自定义类型要转换成JsValue，必须在一定范围内隐式定义Writes
 * 2.遍历JsValue结构
 *
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
    (JsPath \ "name").write[String] and
      (JsPath \ "age").write[Int] and
      (JsPath \ "role").write[Option[String]]
    ) (unlift(Resident.unapply))


  // and 可用 ~代替
  case class Place(name: String, location: Location, residents: Seq[Resident])

  implicit val placeWrites: Writes[Place] = (
    (JsPath \ "name").write[String] ~
      (JsPath \ "location").write[Location] ~
      (JsPath \ "residents").write[Seq[Resident]]) (unlift(Place.unapply))

  val place2: Place = Place("Watership Down2",
    Location(51.235685, -1.309197),
    Seq(Resident("Fiver2", 4, None), Resident("Bigwig2", 6, Some("Owsla"))))


  val json = Json.toJson(place2)
  println("自定义类型转换为JsValue: " + json)

  //  -----------------------------------------------/ 遍历JsValue
  //对JsValue使用运算符\会返回JsLookupResult类型的数据，它可以是JsDefined，也可以是JsUndefined。
  //可以链接多个运算符，如果找不到任何中间值，则结果将为JsUndefined。
  //在JsLookupResult上调用get将尝试获取定义的值，如果没有，则抛出异常。
  val lats: JsLookupResult = (json \ "location" \ "lat")
  println("查找结果:" + lats)
  //returns JsDefined(51.235685)
  val lat = lats.get
  println("获取Place的属性值lat:" + lat)
  //returns JsNumber(51.235685)
  //获取数组元素
  val bigwig = (json \ "residents" \ 1).get
  println("获取Place的属性值residents" + bigwig)
  // returns {"name":"Bigwig","age":6,"role":"Owsla"}
  val lookResult = (json \ "residents" \ 0 \ "gender")
  println("返回查找结果：" + lookResult)
  // returns JsUndefined('gender' is undefined on object: {"name":"Fiver2","age":4,"role":null})
  //get未获取到元素会抛出异常
  //println(lookResult.get)

  //------------------------------------ // 递归遍历
  //查找当前对象及后代中所有包含某个属性的
  val names: Seq[JsValue] = json \\ "name"
  println("递归查找，查找所有有name属性的:" + names)
  //returns List("Watership Down2", "Fiver2", "Bigwig2")

  //------------------------------------ // 直接查找
  val name2: JsValue = json("name")
  println("直接查找:" + name2)
  val bigwig2 = json("residents")(1)
  println("直接查找数组:" + bigwig2)


  //------------------------------------将JsValue转换为其他类型
  val str = Json.stringify(json)
  println("将JsValue转换为字符串: " + str)
  //  {"name":"Watership Down2","location":{"lat":51.235685,"long":-1.309197},"residents":[{"name":"Fiver2","age":4,"role":null},{"name":"Bigwig2","age":6,"role":"Owsla"}]}
  val strFormat = Json.prettyPrint(json)
  println("将JsValue转换为字符串(可读性): " + strFormat)

  //-----------------------使用as
  val getName = (json \ "name").as[String]
  println("从JsValue转换为字符串" + getName)
  //Watership Down2
  val getNames = (json \\ "name").map(_.as[String])
  println("从JsValue转换为数组" + getNames)
  // List(Watership Down2, Fiver2, Bigwig2)

  //-----------------------使用asOpt
  val nameOption = (json \ "name").asOpt[String]
  println("从JsValue转换" + nameOption)
  // Some(Watership Down2)

  val bogusOption = (json \ "bogus").asOpt[String]
  println("从JsValue转换" + bogusOption)
  // None

  //-----------------------使用validate
  //从JsValue转换为另一种类型的首选方法是使用其validate方法（它接受Reads类型的参数）。
  // 这将执行验证和转换，返回一种JsResult类型。
  //能匹配的
  val nameResult: JsResult[String] = (json \ "name").validate[String]
  nameResult match {
    case JsSuccess(name, _) => println(s"Name: $name")
    case e: JsError => println(s"Errors: ${JsError toJson e}")
  }
  //Name: Watership Down2

  // Fallback value
  val nameOrFallback = nameResult.getOrElse("Undefined")
  println(s"nameOrFallback:$nameOrFallback")


  // map
  val nameUpperResult: JsResult[String] = nameResult.map(_.toUpperCase)
  println(s"nameUpperResult:$nameUpperResult")
  //nameOrFallback:Watership Down2

  // fold
  val nameOptions: Option[String] = nameResult.fold(
    invalid = {
      fieldErrors =>
        fieldErrors.foreach { x =>
          println(s"field: ${x._1}, errors: ${x._2}")
        }
        Option.empty[String]
    },
    valid = Some(_)
  )


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

  val placeResult: JsResult[Place] = json.validate[Place]
  println("validate转换为Place" + placeResult)

  val residentResult: JsResult[Resident] = (json \ "residents")(1).validate[Resident]
  println("validate转换为residentResult" +residentResult)
  // JsSuccess(Resident(Bigwig,6,Some(Owsla)),)
}
