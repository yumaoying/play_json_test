package com.learn.scala.json.conver.hz


import play.api.libs.json._

case class AuthenticationRedirect(customized: Option[Customized])

/** *
 * 当case class只有1个元素时的实现
 */
object AuthenticationRedirect extends App {

  //1.直接格式化
  //  implicit val authenticationRedirectFormat: OFormat[AuthenticationRedirect] = Json.format[AuthenticationRedirect]

  //2.分别自动转化Reads和Writes
  implicit val authenticationRedirectReads = Json.reads[AuthenticationRedirect]
  implicit val authenticationRedirectWrites = Json.writes[AuthenticationRedirect]

  //  implicit val authenticationRedirectWrites: Writes[AuthenticationRedirect] = new Writes[AuthenticationRedirect] {
  //    override def writes(authenticationRedirect: AuthenticationRedirect): JsValue = Json.obj("customized" -> authenticationRedirect.customized)
  //  }


  val customized = Customized(Some("1"), Some("2"))
  val authenticationRedirect = AuthenticationRedirect(Some(customized))
  println(Json.toJson(authenticationRedirect).toString())
}
