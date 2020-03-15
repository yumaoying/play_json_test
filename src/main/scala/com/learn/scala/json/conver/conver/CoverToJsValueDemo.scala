package com.learn.scala.json.conver.conver

import play.api.libs.json._


/**
 * @author ymy 2020/3/15
 *         转换为JsValue
 *
 */
object CoverToJsValueDemo extends App {

  //1.使用字符串转换
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
  println("使用Json.parse 转换字符串为JsValue" + json)

  //2.使用类构造
  val json2 = JsObject(Seq(
    "name" -> JsString("Watership Down"),
    "location" -> JsObject(Seq("lat" -> JsNumber(51.235685), "long" -> JsNumber(-1.309197))),
    "residents" -> JsArray(IndexedSeq(
      JsObject(Seq("name" -> JsString("Fiver"),
        "age" -> JsNumber(4),
        "role" -> JsNull)),
      JsObject(Seq("name" -> JsString("Bigwig"),
        "age" -> JsNumber(6),
        "role" -> JsString("Owsla")))))
  ))


  println("构造jsValue值:" + json2)

  //3.可直接使用Json.obj或Json.arr简化够造，
  //大多数值不需要由JsValue类显式包装，工厂方法使用隐式转换
  val json3 = Json.obj(
    "name" -> "Watership Down",
    "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
    "residents" -> Json.arr(
      Json.obj("name" -> "Fiver",
        "age" -> 4,
        "role" -> JsNull), //注意空值不能直接写null
      Json.obj("name" -> "Bigwig",
        "age" -> 6,
        "role" -> "Owsla")))
  println("使用Json.obj,Json.arr隐式转换为jsValue" + json3)

  //4.使用Json.toJson类型进行转换
  //play json api提供了对基础类型(Int、Double，String,Boolean)的隐士Writes转换,
  //它也支持实现了存在Writes[T]的T类型集合的转换
  //基础类型
  val jsonString: JsValue = Json.toJson("Fiver")
  val jsonNumber: JsValue = Json.toJson(4)
  val jsonBoolean: JsValue = Json.toJson(true)
  //基础类型集合转换
  val jsonArrayOfInts: JsValue = Json.toJson(Seq(1, 2, 3, 4))
  val jsonArrayOfStrings: JsValue = Json.toJson(List("Fiver", "Bigwig"))

}


