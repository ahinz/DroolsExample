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
    println(s"Message [$messageType]")
    messages = (obj, messageType) :: messages
  }

  override def toString():String = s"Messages($messages)"
}

case class DailyDataPoint(calories: Int, hoursOfData: Double)

case class User(data: Seq[DailyDataPoint], isMale: Boolean, weight: Double, height: Double, age: Double) {

  def today() = data.head

  @BeanProperty
  var hitCalorieGoal: Boolean = false

  @BeanProperty
  var validToday: Boolean = false

  def dataFrom(i: Int) = data.take(i).asJava

}
