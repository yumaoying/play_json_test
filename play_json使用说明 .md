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

  Maven的pom配置

  ```scala
        <dependency>
              <groupId>com.typesafe.play</groupId>
              <artifactId>play-json_2.11</artifactId>
              <version>2.7.4</version>
          </dependency>
  ```

  ​

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
//
{"name":"Watership Down","location":{"lat":51.235685,"long":-1.309197},"residents":[{"name":"Fiver","age":4,"role":null},{"name":"Bigwig","age":6,"role":"Owsla"}]}
```

可读版

```scala
val readableString: String = Json.prettyPrint(json)
//
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

### 6.3 使用validate

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

要从JsValue转换为模型，必须定义隐式Reads，其中T是model的类型。

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



## 7、Json 结合Http

Play通过结合使用HTTP API和JSON库，支持内容类型为JSON的HTTP请求和响应。

下面将通过设计一个简单的RESTful web服务来演示必要的概念，以获取实体列表并接受创建新实体的例子。该服务将对所有数据使用JSON内容类型。





## 8、Json的Reads/Writes/Format组合器

JSON基础知识引入了`Reads`和`Writes` 转换器，用于在JsValue结构和其他数据类型之间进行转换。本页将更详细地介绍如何构建这些转换器以及如何在转换期间使用验证。

### 8.1 JsPath

JsPath是创建`Reads`/`Writes`的核心构建块。JsPath表示JsValue结构中数据的位置。通过使用类似于遍历JsValue的语法，可以使用JsPath对象（根路径）定义JsPath子实例：

```scala
import play.api.libs.json._

val json = { ... }
// 简单路径
val latPath = JsPath \ "location" \ "lat"
// 递归路径
val namesPath = JsPath \\ "name"
// 索引路径
val firstResidentPath = (JsPath \ "residents")(0)
```

`play.api.libs.json`包定义了`JsPath`: `__` （双下划线）的别名。如果您愿意，可以使用这个：

```scala
val longPath = __ \ "location" \ "long"
```



### 8.2 Reads

`Reads`转换器用于将JsValue转换为另一种类型。您可以组合和嵌套`Reads`以创建更复杂的`Reads`。

需要导入下列包来创建`Reads`

```scala
import play.api.libs.json._ // JSON library
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax
```

#### 8.2.1 Path Reads(路径读取)

`JsPath`具有创建特殊`Reads`的方法，这些方法将另一个`Reads`应用于指定路径处的`JsValue`：

- `JsPath.read[T](implicit r: Reads[T]): Reads[T]` ： 创建将隐式参数`r`应用于此路径处`JsValue`的`Reads[T]`
- `JsPath.readNullable[T](implicit r: Reads[T]): Reads[Option[T]]`:用于可能丢失或包含空值的路径。

注意：JSON库为基本类型（如String、Int、Double等）提供隐式读取。

定义单个路径读取如下：

```scala
val nameReads: Reads[String] = (JsPath \ "name").read[String]
//或者
val nameReads: Reads[String] = (__ \ "name").read[String]
```



#### 8.2.2 复杂读取

可以组合单个路径读取以形成更复杂的读取，这些读取可用于转换为复杂model。

为了更容易理解，我们将把组合功能分解成两个语句。combine使用和combinator读取对象：

```scala
val locationReadsBuilder =
  (JsPath \ "lat").read[Double] and
    (JsPath \ "long").read[Double]
```

这将产生一种FunctionalBuilder[Reads]#CanBuild2[Double，Double]。这是一个中介对象，您不必太担心它，只需知道它用于创建复杂的读取。

第二次调用CanBuildX的apply方法，使用一个函数将单个值转换为model，这将返回复杂的读取结果。如果case类具有匹配的构造函数，则可以使用其apply方法：

```scala
implicit val locationReads = locationReadsBuilder.apply(Location.apply _)
```

完整一条语句完成上述功能

```scala
implicit val locationReads: Reads[Location] = (
  (JsPath \ "lat").read[Double] and
  (JsPath \ "long").read[Double]
)(Location.apply _)
```



#### 8.2.3 读取验证

JSON basics中引入了`JsValue.validate`方法，作为执行从JsValue到另一类型的验证和转换的首选方法。基本模式如下：

```scala
val json = { ... }

val nameReads: Reads[String] = (JsPath \ "name").read[String]

val nameResult: JsResult[String] = json.validate[String](nameReads)

nameResult match {
  case s: JsSuccess[String] => println("Name: " + s.get)
  case e: JsError => println("Errors: " + JsError.toJson(e).toString())
}
```

读取的默认验证是最小的，例如检查类型转换错误。可以使用读取验证帮助程序定义自定义验证规则。以下是一些常用的(在包`play.api.libs.json.Reads._`中)：

- `Reads.email` - 验证字符串是否式邮箱格式
- `Reads.minLength(nb)` - 验证集合或字符串的最小长度
- `Reads.min` - 验证最小值
- `Reads.max` - 验证最大值
- `Reads[A] keepAnd Reads[B] => Reads[A]` -尝试读取[A]和[B]但只保留读取结果[A]的运算符（对于那些知道Scala解析器组合器的人来说，keepAnd==<~）
- `Reads[A] andKeep Reads[B] => Reads[B]` - 尝试读取[A]和[B]但只保留读取结果[B]的运算符（对于那些知道Scala解析器组合器并keep=~>的人）。
- `Reads[A] or Reads[B] => Reads` - 执行逻辑或并保持上次读取检查结果的运算符

要添加验证

```scala
val improvedNameReads =
  (JsPath \ "name").read[String](minLength[String](2))
```



#### 8.2.4 所有结合使用

通过使用复杂读取和自定义验证，我们可以为示例model定义一组有效读取并应用它们：

```scala
//自定义类
  case class Location(lat: Double, long: Double)

  case class Resident(name: String, age: Int, role: Option[String])

  case class Place(name: String, location: Location, residents: Seq[Resident])

  import play.api.libs.json._
  import play.api.libs.json.Reads._
  import play.api.libs.functional.syntax._

  implicit val locationReads: Reads[Location] = (
    (JsPath \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
      (JsPath \ "long").read[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply _)

  implicit val residentReads: Reads[Resident] = (
    (JsPath \ "name").read[String](minLength[String](2)) and
      (JsPath \ "age").read[Int](min(0) keepAnd max(150)) and
      (JsPath \ "role").readNullable[String]
    ) (Resident.apply _)

  implicit val placeReads: Reads[Place] = (
    (JsPath \ "name").read[String](minLength[String](2)) and
      (JsPath \ "location").read[Location] and
      (JsPath \ "residents").read[Seq[Resident]]
    ) (Place.apply _)

  val json: JsValue = Json.parse(
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

  json.validate[Place] match {
    case s: JsSuccess[Location] => println("解析成功：" + s.get)
    case e: JsError => println(s"Errors: ${JsError.toJson(e)}")
  }
//解析成功：Place(Watership Down,Location(51.235685,-1.309197),Vector(Resident(Fiver,4,None), Resident(Bigwig,6,Some(Owsla))))
```

请注意，复杂的读取可以嵌套。如上述例子中，`placeReads`在结构中的特定路径使用先前定义的隐式`locationReads`和`residentrads`。



### 8.3 Writes

`Writes`转换器用于从某种类型转换为`JsValue`。

可以使用JsPath和组合器构建复杂的`Writes`，这与`Reads`非常类似。

下面是我们示例model的编写：

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

`Writes`和`Reads`有一些区别

* 单独的路径`Writes`是使用`JsPath.write`方法创建的
* 转换为`JsValue`时没有验证，这使得结构更简单，您不需要任何验证帮助程序。
* 中间函数`FunctionalBuilder#CanBuildX`（由`and`组合器创建）接受一个函数，该函数将复杂类型T转换为与单个路径写入匹配的元组。尽管这与Reads case对称，case类的unapply方法返回属性元组的选项，并且必须与unlift一起使用才能提取元组。

### 8.4 递归类型

我们的示例模型没有演示的一个特殊情况是如何处理递归类型的`Reads`和`Writes`操作。JsPath提供了`lazyRead`和`lazyWrite`方法，它们接受按名称调用的参数来处理此问题：

```scala
case class User(name: String, friends: Seq[User])

import play.api.libs.json._
import play.api.libs.functional.syntax._

//lazyRead
implicit lazy val userReads: Reads[User] = (
  (__ \ "name").read[String] and
  (__ \ "friends").lazyRead(Reads.seq[User](userReads))
)(User)

//lazyWrite
implicit lazy val userWrites: Writes[User] = (
  (__ \ "name").write[String] and
  (__ \ "friends").lazyWrite(Writes.seq[User](userWrites))
)(unlift(User.unapply))

  val user = User("一", friends = Seq(User("二", Seq(User("三", Seq.empty)))))
  val userJsValue: JsValue = Json.toJson(user)
//{"name":"一","friends":[{"name":"二","friends":[{"name":"三","friends":[]}]}]}
```



### 8.5 Format

Format[T]只是读写特性的混合，可以用来代替其组件进行隐式转换。

#### 8.5.1 从Read和Writes创建Format

可以定义`Format`使用同一类型的`Reads`和`Writes`

```scala
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

  //定义case class
  case class Location(lat: Double, long: Double)

  val locationReads: Reads[Location] = (
    (__ \ "lat").read[Double](min(-90.0) keepAnd max(90.0)) and
      (__ \ "long").read[Double](min(-180.0) keepAnd max(180.0))
    ) (Location.apply _)

  val locationWrites: Writes[Location] = (
    (__ \ "lat").write[Double] and
      (__ \ "long").write[Double]
    ) (unlift(Location.unapply))

  //使用Writes和Reads实现隐式的format
  implicit val locationFormat: Format[Location] = Format(locationReads, locationWrites)

  //类转换为JsValue
  val location = Location(30, 20)
  val jsValue = Json.toJson(location)
  println(s"类转换为jsValue:  $jsValue")
  //类转换为jsValue:  {"lat":30,"long":20}

  //jsValue转换为类
  val location2 = jsValue.as[Location]
  println("jsValue转换为实体类:" + location2)
  //jsValue转换为实体类:Location(30.0,20.0)
```



#### 8.5.2 使用组合创建Format

如果读写是对称的（在实际应用中可能不是这样,对称即要求jsValue中的属性与model的属性名称一一对应），可以直接从组合器定义格式：

```scala
implicit val locationFormat: Format[Location] = (
  (JsPath \ "lat").format[Double](min(-90.0) keepAnd max(90.0)) and
  (JsPath \ "long").format[Double](min(-180.0) keepAnd max(180.0))
)(Location.apply, unlift(Location.unapply))
```



## 9、Json自动映射

如果JSON直接映射到类，可以直接只用这样您就不必手动编写Reads[t]、Writes[t]或Format[t]

```

```

