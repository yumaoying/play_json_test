package com.learn.scala.json.conver.basic

import play.api.libs.json.{JsError, JsResult, JsSuccess, Json}

/**
 * @author ymy  2020/3/15
 *         JsValue----》转换为基本类型
 * 1.Json.stringify(str)
 * 2.Json.prettyPrint(str)
 * 3.JsLookupResult.as
 * 4.JsLookupResult.asOpt
 * 5.JsLookupResult.validate-------隐式实现Reads
 */
object CoverJsToOtherDemo extends App {
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
}
