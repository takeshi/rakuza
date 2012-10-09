package com.tristanhunt.knockoff

import scala.collection.mutable.Map
import java.util.LinkedList
import scala.xml._

object CommandRegistory {
  def default = Map(
    "defalut" -> "def")

  val registry = Map[String, (Map[String, String], Seq[Node]) => Elem]()

  def appendCommand(name: String)(method: (Map[String, String], Seq[Node]) => Elem) {
    registry.put(name, method)
  }

  def defaultCommand(name: String): (Map[String, String], Seq[Node]) => Elem = {
    (params: Map[String, String], nested: Seq[Node]) =>
      {
        val builder = new XmlBuilder("CommandNotFound")
        params.put("command-name", name)
        builder.attr(params)
        if (nested != null) {
          builder.nest(nested)
        }
        builder.toXml()
      }
  }

  def command(name: String): (Map[String, String], Seq[Node]) => Elem = {
    registry.get(name) match {
      case None => defaultCommand(name)
      case Some(func) => func
    }
  }

  appendCommand("test") { (map, nested) =>
    val p = Map("test" -> "sample") ++: map
    <samples test={ p("test") }>
      { nested }
    </samples>
  }

  def main(args: Array[String]) {
    def xml = <samples/>
    println(command("test")(Map("test" -> "sample2"), xml))
  }
}