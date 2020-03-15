package com.learn.scala.json.conver.moreElement

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, Json, OFormat, __}

/**
 * @author ymy  2020/3/15
 *         元素个数很多时--分元组
 *         --- case class的元素超过22个以后即会编译报错
 *         -------使用tupled分组 或者将此类拆分为多个case class
 */
case class SomeResponse(var compositeKey: String,
                        var id1: String,
                        var id2: String,
                        var firstName: String,
                        var lastName: String,
                        var email: String,
                        var email2: String,
                        var birth: Long,
                        var gender: String,
                        var phone: String,
                        var city: String,
                        var zip: String,
                        var carriage: Boolean,
                        var carriage2: Boolean,
                        var fooLong: Long,
                        var fooLong2: Long,
                        var suspended: Boolean,
                        var foo: String,
                        var address: String,
                        var suite: String,
                        var state: String,
                        var instructions: String,
                        var in: String,
                        var out: String,
                        var other: String)

object SomeResponse {
  //  implicit val format = Json.format[SomeResponse]

  val someResponseFirstFormat: OFormat[(String, String, String, String, String, String, String, Long, String, String, String)] =
    ((__ \ "compositeKey").format[String] and
      (__ \ "id1").format[String] and
      (__ \ "id2").format[String] and
      (__ \ "firstName").format[String] and
      (__ \ "lastName").format[String] and
      (__ \ "email").format[String] and
      (__ \ "email2").format[String] and
      (__ \ "birth").format[Long] and
      (__ \ "gender").format[String] and
      (__ \ "phone").format[String] and
      (__ \ "city").format[String]).tupled

  val someResponseSecondFormat: OFormat[(String, Boolean, Boolean, Long, Long, Boolean, String, String, String, String, String)] =
    ((__ \ "zip").format[String] and
      (__ \ "carriage").format[Boolean] and
      (__ \ "carriage2").format[Boolean] and
      (__ \ "fooLong").format[Long] and
      (__ \ "fooLong2").format[Long] and
      (__ \ "suspended").format[Boolean] and
      (__ \ "foo").format[String] and
      (__ \ "address").format[String] and
      (__ \ "suite").format[String] and
      (__ \ "state").format[String] and
      (__ \ "instructions").format[String]).tupled

  val someResponseThreeFormat: OFormat[(String, String, String)] =
    ((__ \ "in").format[String] and
      (__ \ "out").format[String] and
      (__ \ "other").format[String]).tupled

  implicit val formatSome: Format[SomeResponse] = (
    someResponseFirstFormat and someResponseSecondFormat and someResponseThreeFormat
    ).apply({
    case ((compositeKey, id1, id2, firstName, lastName, email, email2, birth, gender, phone, city),
    (zip, carriage, carriage2, fooLong, fooLong2, suspended, foo, address, suite, country, instructions),
    (in, out, other)) =>
      SomeResponse(compositeKey, id1, id2, firstName, lastName, email, email2, birth, gender, phone, city, zip, carriage, carriage2, fooLong, fooLong2, suspended, foo, address, suite, country, instructions, in, out, other)
  }, huge => ((huge.compositeKey, huge.id1, huge.id2, huge.firstName, huge.lastName, huge.email, huge.email2, huge.birth, huge.gender, huge.phone, huge.city),
    (huge.zip, huge.carriage, huge.carriage2, huge.fooLong, huge.fooLong2, huge.suspended, huge.foo, huge.address, huge.suite, huge.state, huge.instructions),
    (huge.in, huge.out, huge.other)))
}
