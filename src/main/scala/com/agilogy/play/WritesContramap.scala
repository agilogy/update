package com.agilogy.play

import play.api.libs.json.{JsObject, JsValue, OWrites, Writes}

object WritesContramap {

  implicit class WritesOps[A](w:Writes[A]){

    def contramap[B](f: B=>A):Writes[B] = new Writes[B] {
      override def writes(o: B): JsValue = w.writes(f(o))
    }
  }

  implicit class OWritesOps[A](w:OWrites[A]){

    def contramap[B](f: B=>A):OWrites[B] = new OWrites[B] {
      override def writes(o: B): JsObject = w.writes(f(o))
    }
  }

}
