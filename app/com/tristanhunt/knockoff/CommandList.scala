package com.tristanhunt.knockoff

import scala.collection.mutable.Map
import java.util.LinkedList
import scala.xml._
import com.tristanhunt.knockoff.CommandRegistory._
import java.io.StringReader
import scala.collection.mutable.HashMap

object CommandList {

  val jqueryAttrs = List("role", "inline",
    "icon", "iconpos", "mini",
    "type", "theme", "content-theme",
    "expanded-icon", "inset", "collapsed",
    "collapsed-icon", "expanded-icon")

  val positions = List("right", "left", "bottom", "top")

  def toXml(name: String, map: Map[String, String]) = {
    val builder = new XmlBuilder(name)
    builder.attr(map)
    builder.toXml()
  }

  def toXml(name: String, map: Map[String, String], nest: Seq[Node]) = {
    val builder = new XmlBuilder(name)
    builder.attr(map)
    if (nest != null)
      builder.nest(nest)
    builder.toXml()
  }

  def toXml(name: String, map: Map[String, String], nest: String) = {
    val builder = new XmlBuilder(name)
    builder.attr(map)
    if (nest != null)
      builder.nest(nest)
    builder.toXml()
  }
  def setDefault(map: Map[String, String])(key: String, value: String) {
    if (!map.contains(key)) {
      map.put(key, value)
    }
  }

  def renameKey(map: Map[String, String])(from: String, to: String) {
    map.remove(from) map { v =>
      map.put(to, v)
    } getOrElse {
    }
  }

  def replaceDataAttr(map: Map[String, String]) {
    jqueryAttrs.foreach { attr =>
      renameKey(map)(attr, "data-" + attr)
    }
  }

  /**
   * jQuery Mobileの画面構成要素
   */
  appendCommand("page") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "page")
    setDefault(map)("id", "main_page")

    toXml("div", map, nested)
  }

  appendCommand("header") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "header")

    toXml("div", map, nested)
  }

  appendCommand("footer") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "footer")

    toXml("div", map, nested)
  }

  appendCommand("content") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "content")

    toXml("div", map, nested)
  }

  appendCommand("collapsible") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "collapsible")

    toXml("div", map, nested)
  }

  positions.foreach { pos =>
    appendCommand(pos + "-collapsible") { (map, nested) =>
      replaceDataAttr(map)
      setDefault(map)("data-role", "collapsible")
      setDefault(map)("data-iconpos", pos)

      toXml("div", map, nested)
    }

  }

  appendCommand("collapsible-set") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "collapsible-set")

    toXml("div", map, nested)
  }

  appendCommand("fieldcontain") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "fieldcontain")

    toXml("div", map, nested)
  }

  appendCommand("controlgroup") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "controlgroup")
    //    setDefault(map)("data-mini", "true")
    toXml("fieldset", map, nested)
  }

  appendCommand("listview") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "listview")
    toXml("ul", map, nested)
  }
  appendCommand("li") { (map, nested) =>
    replaceDataAttr(map)
    toXml("li", map, nested)
  }
  appendCommand("ul") { (map, nested) =>
    replaceDataAttr(map)
    toXml("ul", map, nested)
  }
  appendCommand("ol") { (map, nested) =>
    replaceDataAttr(map)
    toXml("ol", map, nested)
  }

  appendCommand("list-item") { (map, nested) =>
    val contens = if (map.contains("title")) {
      <div>{ map("title") }</div><p>{ nested.text }</p>
    } else {
      nested
    }

    if (map.contains("count")) {
      val n = contens ++: <div class="ui-li-count">{ map("count") }</div>
      map.remove("count")
      toXml("li", map, n)
    } else {
      toXml("li", map, contens)
    }
  }

  appendCommand("list-link") { (map, nested) =>
    val contens = if (map.contains("title")) {
      <h3>{ map("title") }</h3><p>{ nested.text }</p>
    } else {
      nested
    }
    if (map.contains("count")) {
      val n = contens ++: <div class="ui-li-count">{ map("count") }</div>
      map.remove("count")
      toXml("li", null, command("link")(map, n))
    } else {
      toXml("li", null, command("link")(map, contens))
    }
  }

  appendCommand("count") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("class", "ui-li-count")
    toXml("p", map, nested)
  }

  /**
   * コントロール系
   */
  appendCommand("title") { (map, nested) =>
    //    setDefault(map)("data-mini", "true")
    val size = map.get("size").getOrElse {
      "5"
    }
    toXml("h" + size, map, nested)
  }

  appendCommand("text") { (map, nested) =>
    //    setDefault(map)("data-mini", "true")
    setDefault(map)("type", "text")
    map.put("value", nested.text)
    toXml("input", map)
  }

  appendCommand("slider") { (map, nested) =>
    replaceDataAttr(map)

    setDefault(map)("type", "range")
    setDefault(map)("min", "0")
    setDefault(map)("max", "100")
    map.put("value", nested.text)
    toXml("input", map)
  }

  appendCommand("flip-slider") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "slider")

    toXml("select", map, nested)
  }

  appendCommand("button") { (map, nested) =>
    replaceDataAttr(map)
    setDefault(map)("data-role", "button")

    renameKey(map)("to", "href")

    toXml("a", map, nested)
  }

  positions.foreach { pos =>
    appendCommand(pos + "-button") { (map, nested) =>
      replaceDataAttr(map)
      setDefault(map)("data-role", "button")
      setDefault(map)("data-iconpos", pos)

      renameKey(map)("to", "href")
      renameKey(map)("linkTo", "href")

      toXml("a", map, nested)
    }
  }

  appendCommand("inline-button") { (map, nested) =>
    replaceDataAttr(map)

    setDefault(map)("data-role", "button")
    setDefault(map)("data-inline", "true")
    renameKey(map)("to", "href")

    toXml("a", map, nested)
  }

  appendCommand("option") { (map, nested) =>
    toXml("option", map, nested)
  }

  appendCommand("textarea") { (map, nested) =>
    replaceDataAttr(map)
    if (nested != null) {
      toXml("textarea", map, nested.text)
    } else {
      toXml("textarea", map)
    }
  }

  appendCommand("legend") { (map, nested) =>
    replaceDataAttr(map)

    toXml("legend", map, nested)
  }
  appendCommand("label") { (map, nested) =>
    replaceDataAttr(map)

    toXml("label", map, nested)
  }

  appendCommand("img") { (map, nested) =>
    replaceDataAttr(map)

    toXml("img", map, nested)
  }

  appendCommand("icon") { (map, nested) =>
    replaceDataAttr(map)
    renameKey(map)("to", "href")
    setDefault(map)("data-iconpos", "notext")
    toXml("a", map, nested)
  }

  appendCommand("link") { (map, nested) =>
    replaceDataAttr(map)
    renameKey(map)("to", "href")

    toXml("a", map, nested)
  }

  ('a' to 'z').foreach { c =>
    appendCommand("block-" + c) { (map, nested) =>
      replaceDataAttr(map)
      setDefault(map)("class", "ui-block-" + c)
      toXml("div", map, nested)
    }
    appendCommand("grid-" + c) { (map, nested) =>
      replaceDataAttr(map)
      setDefault(map)("class", "ui-grid-" + c)
      toXml("div", map, nested)
    }
  }

  /**
   * 画面制御系
   */
  appendCommand("changePage") { (map, nested) =>
    replaceDataAttr(map)
    renameKey(map)("to", "href")

    toXml("a", map, nested)
  }

  def init() {}

}