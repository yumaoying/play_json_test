package com.learn.scala.json.conver.autoMap

/**
 * @author ymy  2020/3/15
 *还支持value classes的自动转换，以下给定基于String类型的value
 *
 * https://docs.scala-lang.org/overviews/core/value-classes.html
 * https://www.jianshu.com/p/ed262893e249
 *
 *  待补充，不太理解这个在什么场景下会使用以及如何使用?
 */
object AutoMapValueClassDemo extends App {

  //Value Classes 主要用于扩展已有类的方法和创建类型安全的数据类型。
  final class IdText(val value: String) extends AnyVal

  import play.api.libs.json._

  implicit val idTextReads = Json.valueReads[IdText]
  implicit val idTextWrites = Json.valueWrites[IdText]
  // implicit val idTextFormat = Json.valueFormat[IdText]

  val idText = new IdText("232423")
  val jdTextJson = Json.toJson(idText)
  println(jdTextJson)
}
