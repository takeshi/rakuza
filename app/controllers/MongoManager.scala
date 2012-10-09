package controllers
import com.mongodb.Mongo
import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.casbah.MongoConnection
import java.net.URI
import com.mongodb.MongoURI
import com.mongodb.casbah.MongoDB

object MongoManager {

  lazy val db: MongoDB = {
    val url = java.lang.System.getenv("MONGOHQ_URL");
    if (url != null) {
      var mongoURI = new MongoURI(System.getenv("MONGOHQ_URL"));
      var db = mongoURI.connectDB();
      db.authenticate(mongoURI.getUsername(), mongoURI.getPassword());
      new MongoDB(db)
    } else {
      MongoConnection().getDB("rakuza")
    }
  }

  lazy val article = db("article");

  def article(id: String, data: String) {
    article.remove(MongoDBObject("id" -> id))
    article += (MongoDBObject("id" -> id, "data" -> data))
  }
  def article(id: String): String = {
    val query = MongoDBObject("id" -> id)
    val result = article.findOne(query)

    result.map { data =>
      String.valueOf(data.get("data"))
    }.getOrElse {
      ""
    }

  }
}