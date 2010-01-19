import com.mongodb._
import com.osinka.mongodb._
import com.osinka.mongodb.shape._

import scala.collection.mutable.{Map=>MMap,ArrayBuffer}
import System.err

class Twit extends MongoObject {
  var id: Double = 0.
  var text: String = ""
  var createdAt: String = ""
}

object Twit extends MongoObjectShape[Twit] with FunctionalShape[Twit] {
  object id extends Scalar[Double]("id", _.id) 
  with Updatable[Double] { override def update(x: Twit, id: Double) { x.id = id } }
  object text extends Scalar[String]("text", _.text) 
  with Updatable[String] { override def update(x: Twit, text: String) { x.text = text } }
  // should we convert to-from DateTime on the fly?
  object createdAt extends Scalar[String]("created_at", _.createdAt)
  with Updatable[String] { override def update(x: Twit, createdAt: String) { x.createdAt = createdAt } }
  override lazy val * = id :: text :: createdAt :: super.*
  override def factory(dbo: DBObject) = Some(new Twit)
}

case class ReplyEdge(screenName: String, inReplyToScreenName: String, createdAt: String) extends MongoObject

object ReplyEdge2 extends MongoObjectShape[ReplyEdge] with FunctionalShape[ReplyEdge] {
  object screenName extends Scalar[String]("screen_name", _.screenName) 
    with Functional[String]
  object inReplyToScreenName extends Scalar[String]("in_reply_to_screen_name",_.inReplyToScreenName)
    with Functional[String]
  object createdAt extends Scalar[String]("created_at", _.createdAt)
    with Functional[String]
  override lazy val * = screenName :: inReplyToScreenName :: createdAt :: super.*
  override def factory(dbo: DBObject): Option[ReplyEdge] =
     for { screenName(f) <- Some(dbo)
           inReplyToScreenName(t) <- Some(dbo)
           createdAt(c) <- Some(dbo)} yield new ReplyEdge(f,t,c)
           
  // def unapply = ...
}

import Preamble._ // for implicits later in interactive testing, e.g. dbColl save Map...

val Host = "localhost"
val Port = 27017

val mongo = new Mongo(Host, Port).getDB("twitter")
val dbColl = mongo.getCollection("hose")
// val twits = dbColl.of(Twit)
val replies = dbColl of ReplyEdge2
val r1 = replies.head

type User = String
type Time = String
val reps: MMap[User,MMap[User,ArrayBuffer[Time]]] = MMap.empty

var counter: Long = 0

timed {
  replies.toStream/*.take(1000)*/ foreach { case ReplyEdge(from,to,when) => 
  reps.get(from) match {
    case Some(tos) => tos.get(to) match {
      case Some(a) => a += when
      case _       => reps(from)(to) = ArrayBuffer(when)
    }
    case _ => reps(from) = MMap(to->ArrayBuffer(when))
  }
  counter += 1
  if (counter % 10000 == 0) err.print(".")
  }
}

