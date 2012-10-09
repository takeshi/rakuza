package controllers

import play.api._
import play.api.mvc._
import play.api.libs.iteratee._
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.ConcurrentHashMap
import java.util.List
import java.util.ArrayList
import scala.collection.JavaConversions._
import com.tristanhunt.knockoff.RakuzaDiscounter
import com.tristanhunt.knockoff.RakuzaXHTMLWriter
import com.tristanhunt.knockoff.CommandList
import play.api.libs.iteratee.Iteratee._
import play.api.libs.iteratee.Enumerator._

object Application extends Controller {

  CommandList.init()

  val channelMap = new ConcurrentHashMap[String, List[Pushee[String]]]

  val discounter = new RakuzaDiscounter

  val xmlWriter = new RakuzaXHTMLWriter

  def log(message: String) {

  }

  def info(message: String) {
    println(message)
  }

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def view(id: String) = Action {
    val data = MongoManager.article(id)
    val markdown = discounter.knockoff(data)
    val xhtml = xmlWriter.toXHTML(markdown)
    Ok(views.html.view(id, xhtml.toString()))
  }

  def update(id: String) = Action { request =>
    request.body.asText map { text =>
      onMessage(id, text)
    }
    Ok("ok")
  }

  def edit(id: String) = Action {
    val data = MongoManager.article(id)
    Ok(views.html.edit(id, data))
  }

  def read(id: String) = Action {
    val data = MongoManager.article(id)
    val markdown = discounter.knockoff(data)
    val xhtml = xmlWriter.toXHTML(markdown)
    Ok(views.html.read(id, xhtml.toString()))
  }

  def onMessage(id: String, message: String) {
    MongoManager.article(id, message)

    val markdown = discounter.knockoff(message.trim())
    var html = xmlWriter.toXHTML(markdown)
    info("onMessage " + id)
    var channels = list(id)
    log("write channels id:" + id + " " + channels.size())

    channels.foreach { channel =>
      channel.push(html.toString())
    }
  }

  def list(id: String): List[Pushee[String]] = {
    var list = channelMap.get(id)
    if (list == null) {
      list = new ArrayList[Pushee[String]]
      channelMap.put(id, list);
    }
    list
  }

  def websocket(id: String) = WebSocket.using[String] { request =>
    val channelRef = new AtomicReference[Pushee[String]]
    val in = Iteratee.foreach[String](onMessage(id, _)).mapDone { _ =>
      log("Disconnected " + id)
    }

    val onStart: Pushee[String] => Unit = { channel =>
      channelRef.set(channel)
      list(id).add(channel)
      val data = MongoManager.article(id)
      val markdown = discounter.knockoff(data.trim())
      var html = xmlWriter.toXHTML(markdown)
      channel.push(html.toString())
    }

    val onError: (String, Input[String]) => Unit = { (message, input) =>
      println("onError " + message + " " + input)
    }

    val onComplete: () => Unit = { () =>
      println("onComplete" + id);
      list(id).remove(channelRef.get())
    }

    // Send a single 'Hello!' message
    val out = Enumerator.pushee(onStart, onComplete, onError)

    (in, out)
  };

}