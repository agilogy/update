package com.agilogy.play

import com.agilogy.update.{KeepValue, SetValue, Update}
import play.api.data.validation.ValidationError
import play.api.libs.json._

object UpdateFormatHelper {
  module =>

  private val KeepValueJsonObj = Json.obj("$keepValue" -> true)
  private val MissingPathError = List(ValidationError(List("error.path.missing")))

  implicit def ur[T:Reads]: Reads[Update[T]] = new Reads[Update[T]] {
    override def reads(json: JsValue): JsResult[Update[T]] = {
      if(json == KeepValueJsonObj) JsSuccess(KeepValue)
      else implicitly[Reads[T]].reads(json).map(SetValue.apply)
    }
  }

  implicit def uw[T:Writes]: Writes[Update[T]] = new Writes[Update[T]] {
    override def writes(o: Update[T]): JsValue = o match {
      case KeepValue => KeepValueJsonObj
      case SetValue(v) => implicitly[Writes[T]].writes(v)
    }
  }

  def readWithUpdates[T](r:Reads[T]):Reads[T] = new Reads[T] {
    override def reads(json: JsValue): JsResult[T] = {
      r.reads(json) match {
        case JsSuccess(v,path) => JsSuccess(v,path)
        case JsError(errs) =>
          val (objectWithKeepValues,paths) = errs.foldLeft((json.asInstanceOf[JsObject],Seq.empty[JsPath])){
            case ((o,ps),(path@JsPath(List(KeyPathNode(k))),MissingPathError)) =>
              (o + (k -> KeepValueJsonObj),ps :+ path)
            case (acc,err) =>
              acc
          }
          r.reads(objectWithKeepValues) match {
            case JsSuccess(v,path) => JsSuccess(v,path)
            case JsError(errs2) =>
              val newErrors: Seq[(JsPath, Seq[ValidationError])] = errs2.map{
                case (p,s) if paths.contains(p) => (p, MissingPathError)
                case ps => ps
              }
              JsError(newErrors)
          }
      }
    }
  }

  def writeWithUpdates[T](w:Writes[T]):Writes[T] = new Writes[T] {
    override def writes(o: T): JsValue = {
      def removeKeepValues(j:JsValue):JsValue = j match {
        case JsObject(u) =>
          val newValues = u.flatMap{
            case (_,KeepValueJsonObj) => None
            case (k, o:JsObject) => Some(k -> removeKeepValues(o))
            case kv => Some(kv)
          }
          JsObject(newValues)
        case j => j
      }
      removeKeepValues(w.writes(o))
    }
  }


  def withUpdates[T](f:Format[T]):Format[T] = Format(readWithUpdates(f),writeWithUpdates(f))

  implicit class ReadsOps[T](r:Reads[T]){
    def readWithUpdates:Reads[T] = module.readWithUpdates(r)
  }

  implicit class WritesOps[T](w:Writes[T]){
    def writeWithUpdates:Writes[T] = module.writeWithUpdates(w)
  }

  implicit class FormatOps[T](f:Format[T]){
    def withUpdates:Format[T] = module.withUpdates(f)
  }


}
