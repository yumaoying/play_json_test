# Play-Json 使用说明
## 1、基本介绍
官网地址: https://index.scala-lang.org/playframework/play-json/play-json/2.5.10?target=_2.11
Play JSON是功能强大的Scala JSON库，最初由Play团队开发。它使用Jackson进行Json的解析(Java生态圈中有很多处理JSON和XML格式化的类库,Jackson是其中比较著名的一个)，并且没有Play依赖。

## 2、关于JSON基础知识
现在的web程序通常需要解析和生成JSON(JavaScript Object Notation）格式的数据，Play通过其JSON库支持此功能。

JSON是一种轻量级的数据交换格式，格式如下:

```
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
```



## 3、JSON库

该[`play.api.libs.json`](https://www.playframework.com/documentation/2.8.x/api/scala/play/api/libs/json/index.html)软件包包含用于表示JSON数据的数据结构以及用于在这些数据结构和其他数据表示之间进行转换的实用程序。该软件包的某些功能包括：

- 用最少的范例和case classes之间进行的自动[转换](https://www.playframework.com/documentation/2.8.x/ScalaJsonAutomated)。如果您想以最少的代码快速启动并运行，那么可能是开始的地方。
- 解析时进行[自定义验证](https://www.playframework.com/documentation/2.8.x/ScalaJsonCombinators#Validation-with-Reads)。
- 在请求正文中[自动解析](https://www.playframework.com/documentation/2.8.x/ScalaBodyParsers#The-default-body-parser) JSON，如果内容不可解析或提供了错误的Content-type标头，则会自动生成错误。
- 可以在Play应用程序外部用作独立库。只需添加`libraryDependencies += "com.typesafe.play" %% "play-json" % playVersion`到`build.sbt`文件即可。（在maven项目中可直接导入依赖）
- 高度可定制的。



### JSON类型

这是代表任何JSON值的特征，JSON具有一个case class扩展了JsValue，为表示每种有效的JSON类型。使用JsValue的各种类型，可以构造任何JSON结构的表示形式。

- JsString  

- JsNumber

- JsBoolean

- JsObject

- JsArray

- JsNull

  比如`play.api.libs.json`接口中对各类型定义如下:

  ```scala
  case class JsString(value: String) extends JsValue with Product with Serializable

  case class JsNumber(value: BigDecimal) extends JsValue with Product with Serializable

  sealed abstract class JsBoolean extends JsValue with Product with Serializable

  case class JsObject(underlying: Map[String, JsValue]) extends JsValue with Product with Serializable

  case class JsArray(value: IndexedSeq[JsValue] = Array[JsValue]()) extends JsValue with Product with Serializable

  object JsNull extends JsValue with Product with Serializable
  ```

  ​

### Json

该`Json`对象提供实用程序，主要用于与`JsValue`结构之间的转换。

```scala
object Json extends JsonFacade
```



### JsPath

表示进入`JsValue`结构的路径，类似于XPath for XML。这用于遍历`JsValue`结构和隐式转换器的模式。

```scala
case class JsPath(path: List[PathNode] = List()) extends Product with Serializable
```



## 4、转换为JsValue

### 4.1. 使用Json.parse解析
```scala
import play.api.libs.json._

val json: JsValue = Json.parse("""
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
```



### 4.2 .使用构造方法

```scala
import play.api.libs.json._

val json: JsValue = JsObject(Seq(
  "name" -> JsString("Watership Down"),
  "location" -> JsObject(Seq("lat" -> JsNumber(51.235685), "long" -> JsNumber(-1.309197))),
  "residents" -> JsArray(IndexedSeq(
    JsObject(Seq(
      "name" -> JsString("Fiver"),
      "age" -> JsNumber(4),
      "role" -> JsNull
    )),
    JsObject(Seq(
      "name" -> JsString("Bigwig"),
      "age" -> JsNumber(6),
      "role" -> JsString("Owsla")
    ))
  ))
))
```

### 4.3. `Json.obj`并`Json.arr`可以简化构造。

注意，大多数值不需要由JsValue类显式包装，工厂方法使用隐式转换

````scala
import play.api.libs.json.{ JsNull, Json, JsString, JsValue }

val json: JsValue = Json.obj(
  "name" -> "Watership Down",
  "location" -> Json.obj("lat" -> 51.235685, "long" -> -1.309197),
  "residents" -> Json.arr(
    Json.obj(
      "name" -> "Fiver",
      "age" -> 4,
      "role" -> JsNull
    ),
    Json.obj(
      "name" -> "Bigwig",
      "age" -> 6,
      "role" -> "Owsla"
    )
  )
)
````

### 4.4. 使用Writes转换

Scala到JsValue的转换由函数`Json.toJson[T](T)(implicit writes: Writes[T])`执行。这个功能依赖于Writes[T]类型的转换器，它可以将T转换为JsValue。

#### 4.4.1基本类型及基本类型的集合

Play JSON API为大多数基本类型（如Int、Double、String和Boolean）提供隐式写入。它还支持对存在Writes[T]的任何类型T的集合进行写操作。

```scala
  //基础类型
  val jsonString: JsValue = Json.toJson("Fiver")
  val jsonNumber: JsValue = Json.toJson(4)
  val jsonBoolean: JsValue = Json.toJson(true)

  //基础类型集合转换
  val jsonArrayOfInts: JsValue = Json.toJson(Seq(1, 2, 3, 4))
  val jsonArrayOfStrings: JsValue = Json.toJson(List("Fiver", "Bigwig"))
```

#### 4.4.2 自定义类型

对于自定义类型要转换成JsValue，必须在一定范围内隐式定义`Writes`

```scala
case class Location(lat: Double, long: Double)
case class Resident(name: String, age: Int, role: Option[String])
case class Place(name: String, location: Location, residents: Seq[Resident])
import play.api.libs.json._

//隐式实现Writes
implicit val locationWrites = new Writes[Location] {
  def writes(location: Location) = Json.obj(
    "lat" -> location.lat,
    "long" -> location.long
  )
}

implicit val residentWrites = new Writes[Resident] {
  def writes(resident: Resident) = Json.obj(
    "name" -> resident.name,
    "age" -> resident.age,
    "role" -> resident.role
  )
}

implicit val placeWrites = new Writes[Place] {
  def writes(place: Place) = Json.obj(
    "name" -> place.name,
    "location" -> place.location,
    "residents" -> place.residents
  )
}

//对于实现了Writes的类型可用
val place = Place(
  "Watership Down",
  Location(51.235685, -1.309197),
  Seq(
    Resident("Fiver", 4, None),
    Resident("Bigwig", 6, Some("Owsla"))
  )
)

val json = Json.toJson(place)
```

另外，`Writes`可以使用组合器模式来自定义：

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._

implicit val locationWrites: Writes[Location] = (
  (JsPath \ "lat").write[Double] and
  (JsPath \ "long").write[Double]
)(unlift(Location.unapply))

implicit val residentWrites: Writes[Resident] = (
  (JsPath \ "name").write[String] and
  (JsPath \ "age").write[Int] and
  (JsPath \ "role").writeNullable[String]
)(unlift(Resident.unapply))

implicit val placeWrites: Writes[Place] = (
  (JsPath \ "name").write[String] and
  (JsPath \ "location").write[Location] and
  (JsPath \ "residents").write[Seq[Resident]]
)(unlift(Place.unapply))
```

注意: and 操作符需要导入`play.api.libs.functional.syntax._`包，`and`可用`~`替换



## 5、遍历JsValue结构

可以遍历`JsValue`结构并提取特定值。语法和功能类似于Scala XML处理。

#### 5.1 简单路径 `\`

对JsValue应用`\`运算符将返回与`JsObject`中的field参数或`JsArray`中该索引处的项对应的属性

```scala
  val lats: JsLookupResult = (json \ "location" \ "lat")
  //returns JsDefined(51.235685)
  val lat = lats.get
  // returns JsNumber(51.235685)
  //查找数组中第1个元素的值
  val bigwig = (json \ "residents" \ 1).get
  // returns {"name":"Bigwig","age":6,"role":"Owsla"}
  //查找数组中第0个元素的gender属性值
  val lookResult = (json \ "residents" \ 0 \ "gender")
  // returns JsUndefined('gender' is undefined on object: {"name":"Fiver2","age":4,"role":null})
```

对JsValue使用运算符`\`会返回JsLookupResult类型的数据，它可以是JsDefined，也可以是JsUndefined。可以链接多个运算符，如果找不到任何中间值，则结果将为JsUndefined。在JsLookupResult上调用get将尝试获取定义的值，如果没有，则抛出异常。

#### 5.2 递归路径 `\\`

应用`\\`运算符将对当前对象和所有后代中的字段进行查找。

```scala
val names = json \\ "name"
// returns Seq(JsString("Watership Down"), JsString("Fiver"), JsString("Bigwig"))
```

#### 5.3 直接查找

可以使用.apply运算符在JsArray或JsObject中检索值，该运算符与简单的`\`相同，只是它直接返回值（而不是将其包装在JsLookupResult中），如果找不到索引或键，则引发异常：

```scala
val name = json("name")
// returns JsString("Watership Down")

val bigwig2 = json("residents")(1)
// returns {"name":"Bigwig","age":6,"role":"Owsla"}

// (json("residents")(3)
// throws an IndexOutOfBoundsException

// json("bogus")
// throws a NoSuchElementException
```



## 6、将JsValue中转换为其他类型

### 6.1 使用String utilities

缩小版

```scala
val minifiedString: String = Json.stringify(json)
{"name":"Watership Down","location":{"lat":51.235685,"long":-1.309197},"residents":[{"name":"Fiver","age":4,"role":null},{"name":"Bigwig","age":6,"role":"Owsla"}]}
```

可读版

```scala
val readableString: String = Json.prettyPrint(json)
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
```



### 6.2  使用JsValue.as/asOpt 

将JsValue转换为另一种类型的最简单方法是使用`JsValue.as[T]（implicit fjs:Reads[T]）：T`。这需要使用Reads[T]类型的隐式转换器将JsValue转换为T（Writes[T]）。与写操作一样，JSON API为基本类型提供读操作。

```scala
val name = (json \ "name").as[String]
// "Watership Down"

val names = (json \\ "name").map(_.as[String])
// Seq("Watership Down", "Fiver", "Bigwig")
```

如果找不到路径或无法进行转换，as方法将引发JsResultException。一个更安全的方法是`JsValue.asOpt[T](implicit fjs: Reads[T]): Option[T]`

```scala
val nameOption = (json \ "name").asOpt[String]
// Some("Watership Down")

val bogusOption = (json \ "bogus").asOpt[String]
// None
```

虽然asOpt方法更安全，但是任何错误信息都会丢失。

### 6.3 使用验证

从JsValue转换为另一种类型的首选方法是使用其validate方法（它接受Reads类型的参数）。这将执行验证和转换，返回一种JsResult类型。JsResult由两个类实现：

- `JsSuccess`: 表示验证成功并包装结果
- `JsError`: 表示验证/转换失败，并包含验证错误列表。

```scala
val json = { ... }

val nameResult: JsResult[String] = (json \ "name").validate[String]

// Pattern matching
nameResult match {
  case JsSuccess(name, _) => println(s"Name: $name")
  case e: JsError => println(s"Errors: ${JsError toJson e}")
}

// Fallback value
val nameOrFallback = nameResult.getOrElse("Undefined")

// map
val nameUpperResult: JsResult[String] = nameResult.map(_.toUpperCase)

// fold
val nameOption: Option[String] = nameResult.fold(
  invalid = {
    fieldErrors =>
      fieldErrors.foreach { x =>
        println(s"field: ${x._1}, errors: ${x._2}")
      }
      Option.empty[String]
  },
  valid = Some(_)
)
```

### 6.4 JsValue 转换为 model

要从JsValue转换为模型，必须定义隐式读取，其中T是model的类型。

```scala
case class Location(lat: Double, long: Double)
case class Resident(name: String, age: Int, role: Option[String])
case class Place(name: String, location: Location, residents: Seq[Resident])
import play.api.libs.json._
import play.api.libs.functional.syntax._

implicit val locationReads: Reads[Location] = (
  (JsPath \ "lat").read[Double] and
  (JsPath \ "long").read[Double]
)(Location.apply _)

implicit val residentReads: Reads[Resident] = (
  (JsPath \ "name").read[String] and
  (JsPath \ "age").read[Int] and
  (JsPath \ "role").readNullable[String]
)(Resident.apply _)

implicit val placeReads: Reads[Place] = (
  (JsPath \ "name").read[String] and
  (JsPath \ "location").read[Location] and
  (JsPath \ "residents").read[Seq[Resident]]
)(Place.apply _)

//之前转换的JsValue值
val json = { ... } 

val placeResult: JsResult[Place] = json.validate[Place]
// JsSuccess(Place(...),)

val residentResult: JsResult[Resident] = (json \ "residents")(1).validate[Resident]
// JsSuccess(Resident(Bigwig,6,Some(Owsla)),)
```

