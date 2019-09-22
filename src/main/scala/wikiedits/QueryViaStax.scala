package wikiedits

import java.io.{ByteArrayInputStream, InputStream}

import javax.xml.XMLConstants
import javax.xml.stream.events.XMLEvent
import nu.xom.Nodes
import wikiedits.XpathViaXom.openLocal7z

import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

object QueryViaStax {
  import ParseViaStax._
  import TraverseViaStax._

  abstract class TransIterator[S,R](sInitial:S) extends Iterator[R] {

    def trans(state:S) : (S,Boolean,Option[R]) = ???

    var _state : S = sInitial
    var _isDone : Boolean = false

    private var _next : Option[R] = None
    override def hasNext: Boolean = {
      while(!_isDone) {
        val (stateNext, isDone, nextNext) = trans(_state)
        _state = stateNext
        _isDone = isDone
        if(nextNext.isDefined) {
          _next = nextNext
          return true
        }
      }
      return false
    }

    override def next(): R = {
      _next.get
    }
  }

  case class AttrPage(titlePage:String, idpage:String)
  case class Contributor(idContrib:String, name:String)
  case class AttrRev(idRev:String, contributor:Contributor, timestamp:String, sha1:String)
  case class PageRev(page:AttrPage, rev:AttrRev)

  trait State
  case class StateInitial() extends State
  case class StatePage(page:AttrPage) extends State

  case class findPageRevisions(it0: BufferedIterator[XMLEvent]) extends TransIterator[State,PageRev](StateInitial()) {
    val it = DepthXmlEventReader(it0)

    override def trans(state:State):(State,Boolean,Option[PageRev]) = {
      if(!it.hasNext)
        return (state,true,None)
      val el = it.next()
      state match {
        case StateInitial() =>
          if (it.depth == 2 && el.isStartElement && el.asStartElement().getName().getLocalPart == "page") {
            val elsPage = childrenUntil(it, HashSet("revision"))
            val page = AttrPage(
              innerText(child("title", elsPage)),
              innerText(child("id", elsPage)))
            return (StatePage(page), false, None)
          } else if(it.depth == 2 && el.isEndElement) {
            return (StateInitial(), false, None)
          } else {
            return (state, false, None)
          }
        case StatePage(page) =>
          if(el.isEndElement) {
            return (StateInitial(), false, None)
          }
          else if (el.isStartElement && el.asStartElement().getName().getLocalPart == "revision") {
            val elsRev = childrenUntil(it, HashSet())
            val elsContrib = child("contributor", elsRev).drop(1)
            val contributor = Contributor(
              innerText(child("username",elsContrib)),
              innerText(child("id",elsContrib)))
            val rev = AttrRev(
              innerText(child("id", elsRev)),
              contributor,
              innerText(child("timestamp", elsRev)),
              innerText(child("sha1", elsRev)))
            exit(it) // revision
            val pagerev = PageRev(page, rev)
            return (state, false, Some(pagerev))
          } else {
            return (state, true, None)
          }
      }
    }
  }
}
