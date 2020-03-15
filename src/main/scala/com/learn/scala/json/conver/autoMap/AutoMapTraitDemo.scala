package com.learn.scala.json.conver.autoMap

import play.api.libs.json._

/**
 * @author ymy  2020/3/15
 *         接口自动转换
 *         一个特征也可以被支持，当且仅当它是密封的并且子类型符合前面的要求时：
 * - 它必须有一个有apply和unapply方法的伴生对象。
 * - unapply的返回类型必须匹配apply方法的参数类型。
 * - apply方法的参数名必须与JSON中所需的属性名相同
 *
 *         使用场景待补充?
 **/
object AutoMapTraitDemo extends App {

  //密封的类(即实现在同一个类中完成，外部无法再用此类扩展)
  sealed trait Role

  case object Admin extends Role

  //类
  class Contributor(val organization: String) extends Role {

    override def equals(obj: Any): Boolean = obj match {
      case other: Contributor if obj != null => this.organization == other.organization
      case _ => false
    }

    override def toString = s"Contributor($organization)"
  }

  //1.必须有一个有apply和unapply方法的伴生对象。
  object Contributor {
    //3.apply方法的参数名必须与JSON中所需的属性名相同
    def apply(organization: String): Contributor = new Contributor(organization)

    //2. unapply的返回类型必须匹配apply方法的参数类型。
    def unapply(contributor: Contributor): Option[(String)] = Some(contributor.organization)
  }


  //  然后宏是能够产生Reads[T]，OWrites[T]或OFormat[T]
  //   首先为每个子类型Admin和Contributor提供实例：
  implicit val adminFormat: OFormat[Admin.type] = OFormat[Admin.type](
    Reads[Admin.type] {
      case JsObject(_) => JsSuccess(Admin)
      case _ => JsError("Empty object expected")
    },
    OWrites[Admin.type] { _ => Json.obj() })

  implicit val contributorFormat: OFormat[Contributor] = Json.format[Contributor]

  //最终能够为密封的Role生成Format
  implicit val roleFormat: OFormat[Role] = Json.format[Role]

  //密封族实例的JSON表示形式包括一个discriminator字段，该字段指定有效的子类型（文本字段，默认名称为_type）。
  //每个JSON对象都用 _type 标记，,
  //值要用子类型的完全限定名表示
  val adminJson = Json.parse(
    """{ "_type": "com.learn.scala.json.conver.autoMap.AutoMapTraitDemo.Admin" }""")

  val contributorJson = Json.parse(
    """{
       "_type":"com.learn.scala.json.conver.autoMap.AutoMapTraitDemo.Contributor",
    "organization":"Foo"
  }""")


  println(contributorJson)
  //将jsvalue转换为对象
  val admin = adminJson.as[Admin.type]
  println(admin);
  //returns Admin


  //将jsValue转换为类
  val contributor = contributorJson.as[Contributor]
  println(contributor)
  //returns Contributor(Foo)

  //将类转换为JsValue
  val contributorJsonValue = Json.toJson(contributorJson)
  println(contributorJsonValue)
  //{"_type":"com.learn.scala.json.conver.autoMap.AutoMapTraitDemo.Contributor","organization":"Foo"}

}
