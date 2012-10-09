package com.tristanhunt.knockoff
import scala.collection.mutable.HashMap

object LinkCommand {

  def toParamMap(params: String) = {
    val map = new HashMap[String, String]
    val p = params.trim();
    if (!p.trim().isEmpty()) {
      val target = if (p.startsWith(":")) {
        p.substring(1)
      } else {
        p
      }
      target.split("\\s+|,").foreach { v =>
        if (v.contains("=")) {
          val pos = v.indexWhere('=' == _)
          val key = v.substring(0, pos).trim();
          if (!key.isEmpty()) {
            val t = v.substring(pos + 1, v.length).trim().replaceAll("\"","");
            map.put(key, t)
          }
        } else {
          val key = v.trim()
          if (!key.isEmpty())
            map.put(key, "true")
        }
      }
    }
    map
  }

  def isCommand(url: String) = url.startsWith(":")

  def parse(url: String) = {
    val index = url.indexWhere(' ' == _)
    if (index > 0) {
      val name = url.subSequence(1, index)
      val params = url.subSequence(index + 1, url.length())
      (name.toString(), toParamMap(params.toString()))
    } else {
      (url.substring(1), new HashMap[String, String]())
    }
  }
}