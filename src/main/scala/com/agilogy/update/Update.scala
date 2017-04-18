package com.agilogy.update

trait MayHaveChanges {
  def hasChanges:Boolean
}

sealed trait Update[+T] extends MayHaveChanges{
  def hasChanges:Boolean = this match {
    case SetValue(_) => true
    case KeepValue => false
  }

  def getResult[T2 >: T](currentValue:T2):T2 = this match{
    case SetValue(v) => v
    case KeepValue => currentValue
  }

  def map[T2](f: T => T2):Update[T2] = this match {
    case SetValue(v) => SetValue(f(v))
    case KeepValue => KeepValue
  }

  def foreach(f: T => Unit):Unit = this match {
    case SetValue(v) => f(v)
    case KeepValue => ()
  }

  def flatMap[T2](f: T => Update[T2]):Update[T2] = this match {
    case SetValue(v) => f(v)
    case KeepValue => KeepValue
  }

  def actualUpdate[T2 >: T](currentValue:T2): Update[T2] = this match {
    case SetValue(v) if v == currentValue => KeepValue
    case _ => this
  }

  def toOption:Option[T] = this match {
    case SetValue(v) => Some(v)
    case KeepValue => None
  }
}
case class SetValue[T](v:T) extends Update[T]
case object KeepValue extends Update[Nothing]

object Update{
  def apply[T](o:Option[T]):Update[T] = o.fold[Update[T]](KeepValue)(SetValue.apply)
}
