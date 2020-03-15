package com.learn.scala.json.conver.autoMap

/**
 * @author ymy  2020/3/15
 *         自动映射Reads
 */
object AutoMapReadsDemo extends App {

  case class Resident(name: String, age: Int, role: Option[String])

  import play.api.libs.json._

  //自动Reads
  implicit val residentReads: Reads[Resident] = Json.reads[Resident]

  //  -------------------Json和类之间的转换
  // In a request, a JsValue is likely to come from `request.body.asJson`
  // or just `request.body` if using the `Action(parse.json)` body parser
  val jsonString: JsValue = Json.parse(
    s"""{
  "name" : "Fiver",
  "age" : 4
}""")

  //转换为类
  val residentFromJson: JsResult[Resident] = Json.fromJson[Resident](jsonString)
  residentFromJson match {
    case JsSuccess(r: Resident, path: JsPath) =>
      println("Name: " + r.name)

    case e@JsError(_) =>
      println("Errors: " + JsError.toJson(e).toString())
  }
}
