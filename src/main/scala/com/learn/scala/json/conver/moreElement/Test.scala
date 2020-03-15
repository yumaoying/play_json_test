package com.learn.scala.json.conver.moreElement

import play.api.libs.json.Json

/**
 * @author ymy  2020/3/15
 *         带有多个元素的case class和Json互转
 *         case class的元素超过22个以后即会编译报错
 */
object Test extends App {

  val someREsponse = SomeResponse(compositeKey = "1",
    id1 = "1",
    id2 = "1",
    firstName = "1",
    lastName = "1",
    email = "1",
    email2 = "1",
    birth = 21,
    gender = "1",
    phone = "1",
    city = "1",
    zip = "1",
    carriage = true,
    carriage2 = true,
    fooLong = 1,
    fooLong2 = 2,
    suspended = true,
    foo = "1",
    address = "1",
    suite = "1",
    state = "1",
    instructions = "1",
    in = "1",
    out = "2",
    other = "3")
  println(Json.toJson(someREsponse).toString())
}
