package com.agilogy.update

import play.api.libs.json._

object UpdateFormat {

  implicit def nullableUpdateWrites[T](path:JsPath)(implicit writes:Writes[T]):OWrites[Update[Option[T]]] = OWrites[Update[Option[T]]]{
    case SetValue(Some(t)) => JsPath.createObj(path -> writes.writes(t))
    case SetValue(None) => JsPath.createObj(path -> JsNull)
    case KeepValue => Json.obj()
  }

  implicit def nullableUpdateReads[T](path: JsPath)(implicit reads: Reads[T]):Reads[Update[Option[T]]] = Reads[Update[Option[T]]]{
    json =>
      path.applyTillLast(json).fold(
        jserr => jserr,
        jsres => jsres.fold(
          _ => JsSuccess(KeepValue),
          {
            case JsNull => JsSuccess(SetValue(None))
            case js => reads.reads(js).repath(path).map(v => SetValue(Some(v)))
          }
        )
      )
  }

  implicit def nullableUpdateFormat[A](path: JsPath)(implicit f: Format[A]): OFormat[Update[Option[A]]] =
    OFormat(nullableUpdateReads(path)(f), nullableUpdateWrites(path)(f))

  implicit class UpdatePathOps(path: JsPath) {

    def formatNullableUpdate[T](implicit f: Format[T]): OFormat[Update[Option[T]]] = nullableUpdateFormat(path)(f)
    def readNullableUpdate[T](implicit f: Reads[T]): Reads[Update[Option[T]]] = nullableUpdateReads(path)(f)
    def writeNullableUpdate[T](implicit f: OWrites[T]): OWrites[Update[Option[T]]] = nullableUpdateWrites(path)(f)

    def formatUpdate[T](implicit f: Format[T]): OFormat[Update[T]] = OFormat(readUpdate[T],writeUpdate[T])

    import com.agilogy.play.WritesContramap._

    def writeUpdate[T](implicit writes: Writes[T]): OWrites[Update[T]] = Writes.nullable[T](path)(writes).contramap[Update[T]](_.toOption)

    def readUpdate[T](implicit reads: Reads[T]): Reads[Update[T]] = Reads.nullable[T](path)(reads).map(_.map(SetValue.apply).getOrElse(KeepValue))
  }

}