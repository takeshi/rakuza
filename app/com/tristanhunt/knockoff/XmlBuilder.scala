package com.tristanhunt.knockoff
import scala.collection.mutable.StringBuilder
import scala.xml.XML
import scala.collection.mutable.Map
import java.io.StringReader
import scala.xml.Node
import org.xml.sax.SAXException

class XmlBuilder(tag: String) {

  val b = new StringBuilder

  var nested = false

  b.append("<" + tag + " ")

  def attr(attrs: Map[String, String]) {
    if (attrs != null)
      attrs.foreach { k =>
        attr(k._1, k._2)
      }
  }

  def attr(k: String, v: String) {
    b append k
    b append "="
    b append "\""
    b append v
    b append "\" "
  }

  def nest(s: Seq[Node]) {
    if (s.size > 0) {
      b.append(">")
      nested = true
      s.foreach { seq =>
        b.append(seq)
      }
    }
  }

  def nest(s: String) {
    val v = s.trim()
    if (v != null && !v.isEmpty()) {
      b.append(">")
      nested = true
      b.append(v)
    }
  }

  def toXml() = {
    if (nested)
      b.append("</" + tag + ">")
    else
      b.append(" />")
    try {
      XML.load(new StringReader(b.toString()))
    } catch {
      case e: SAXException => throw new RuntimeException(e.getMessage() + "error xml is " + b.toString(), e)
      case e: RuntimeException => throw e
    }
  }
}