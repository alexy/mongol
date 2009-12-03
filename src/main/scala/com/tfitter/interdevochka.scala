import com.mongodb._
import com.osinka.mongodb._
import com.osinka.mongodb.shape._

class Twit extends MongoObject {
  var id: Double = 0.
  var text: String = ""
  var createdAt: String = ""
}

object Twit extends MongoObjectShape[Twit] with FunctionalShape[Twit] {
  object id extends Scalar[Double]("id", _.id) with Updatable[Double] {
    override def update(x: Twit, id: Double) { x.id = id }
  }
  object text extends Scalar[String]("text", _.text) with Updatable[String] {
    override def update(x: Twit, text: String) { x.text = text }
  }
  // should we convert to-from DateTime on the fly?
  object createdAt extends Scalar[String]("created_at", _.createdAt) with Updatable[String] {
    override def update(x: Twit, createdAt: String) { x.createdAt = createdAt }
  }
  override lazy val * = id :: text :: createdAt :: super.*
  override def factory(dbo: DBObject) = Some(new Twit)
}

import Preamble._ // for implicits later in interactive testing, e.g. dbColl save Map...

val Host = "localhost"
val Port = 27017

val mongo = new Mongo(Host, Port).getDB("twitter")
val dbColl = mongo.getCollection("hose")
val coll = dbColl.of(Twit)
