package com.ephox
package argonaut

import Argonaut._

object CodecDemo {
  case class Person(name: String, age: Int, spouse: Option[Person], children: List[Person])

  object Person {
    implicit val DecodePerson: DecodeJson[Person] =
      DecodeJson(j => j.array match {
        case Some(n::a::s::c::Nil) =>
          for {
            nn <- n.decode[String]
            aa <- a.decode[Int]
            ss <- s.decode[Option[Person]]
            cc <- c.decode[List[Person]]
          } yield Person(nn, aa, ss, cc)
        case _ => decodeError(j, "Person")
      })

    implicit val EncodePerson: EncodeJson[Person] =
      EncodeJson({
        case Person(n, a, s, c) => jArray(List(
          n.encode
        , a.encode
        , s.encode
        , c.encode
        ))
      }, "Person")
  }

  def main(args: Array[String]) {
    val children = List(Person("Bob", 10, None, Nil), Person("Jill", 12, None, Nil))
    val fred = Person("Fred", 40, Some(Person("Mary", 41, None, children)), Person("Tom", 15, None, Nil) :: children)
    val enc = fred.encode
    println(enc.spaces2)
    val decode = enc.decode[Person]
    println(decode.run match {
      case Left(e) => e
      case Right(p) => p.toString
    })
  }
}
