package com.quanttus.rules

import scala.collection.JavaConverters._
import scala.beans.BeanProperty

import com.github.nscala_time.time.Imports._

trait Notifier {
  def message(obj: User, messageType: String)
}

class ListNotifier extends Notifier {
  var messages = List[(User,String)]()

  def message(obj: User, messageType: String) {
    messages = (obj, messageType) :: messages
  }

  override def toString():String = s"Messages($messages)"
}

case class User(isMale: Boolean, weight: Double, height: Double, age: Double) {
  @BeanProperty
  var hitCalorieGoal: Boolean = false

  @BeanProperty
  var validToday: Boolean = false

  @BeanProperty
  var validWeek: Boolean = false
}

case class Now(date: Long)

case class DailyDataPoint(user: User, calories: Int, hoursOfData: Double, date: Long)
