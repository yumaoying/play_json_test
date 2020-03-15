package com.learn.scala.json.conver.hz

import play.api.libs.json.{Json, Reads, Writes, __}
import play.api.libs.functional.syntax._

case class Customized(acsUrl: Option[String], paReq: Option[String])

object Customized {
  //方式1. 分别实现
  //  implicit val customizedReads: Reads[Customized] = (
  //    (__ \ "acsUrl").readNullable[String] ~
  //      (__ \ "paReq").readNullable[String]
  //    ) (Customized.apply _)
  //
  //  implicit val customizedWrites: Writes[Customized] = (
  //    (__ \ "acsUrl").writeNullable[String] ~
  //      (__ \ "paReq").writeNullable[String]
  //    ) (unlift(Customized.unapply _))

  //方式2. 自动映射Reads和Writes
  //  implicit val customizedReads = Json.reads[Customized]
  //  implicit val customizedWrites = Json.writes[Customized]

  //方式3. 直接使用Format自动转换
  implicit val customizedFormat = Json.format[Customized]
}