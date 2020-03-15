package com.learn.scala.json.conver.JsPath

import play.api.libs.json.{JsLookupResult, JsValue, Json}

/**
 * @author ymy  2020/3/15
 *         jsPath---路径遍历
 */
object JsPathDemo extends {

  //  -----------------------------------------------/ 遍历JsValue
  //对JsValue使用运算符\会返回JsLookupResult类型的数据，它可以是JsDefined，也可以是JsUndefined。
  //可以链接多个运算符，如果找不到任何中间值，则结果将为JsUndefined。
  //在JsLookupResult上调用get将尝试获取定义的值，如果没有，则抛出异常。
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

  //------------------------------------ 简单查找
  val lats: JsLookupResult = (json \ "location" \ "lat")
  println("查找结果:" + lats)
  //returns JsDefined(51.235685)

  //获取元素
  val lat = lats.get
  println("获取Place的属性值lat:" + lat)
  //returns JsNumber(51.235685)

  //获取集合中某个下标元素
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

}
