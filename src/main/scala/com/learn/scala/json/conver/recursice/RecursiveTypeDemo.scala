package com.learn.scala.json.conver.recursice

import play.api.libs.functional.syntax.unlift
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * @author ymy  2020/3/15
 *         递归类型的转换
 *
 */
object RecursiveTypeDemo extends App {

  //递归类型model
  case class User(name: String, friends: Seq[User])

  //使用lazyRead
  implicit lazy val userReades: Reads[User] = (
    (__ \ "name").read[String] and
      (__ \ "friends").lazyRead(Reads.seq[User](userReades))) (User)

  //lazyWrites，它们接受按名称调用的参数
  implicit lazy val userWrites: Writes[User] = (
    (__ \ "name").write[String] and
      (__ \ "friends").lazyWrite(Writes.seq[User](userWrites))
    ) (unlift(User.unapply))

  val user = User("一", friends = Seq(User("二", Seq(User("三", Seq.empty)))))
  val userJsValue: JsValue = Json.toJson(user)
  println("user jsValue:" + userJsValue)
  println(userJsValue.validate)
  println(userJsValue.as[User])
}
