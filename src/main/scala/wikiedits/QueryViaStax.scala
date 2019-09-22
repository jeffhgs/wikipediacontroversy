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

  case class AttrPage(titlePage:String, idpage:String)
  case class AttrRev(idRev:String, sha1:String)
  case class PageRev(page:AttrPage, rev:AttrRev)

  def findPageRevisions(it0: BufferedIterator[XMLEvent]) = new Iterator[PageRev] {
    val it = DepthXmlEventReader(it0)

    trait State
    case class StateInitial() extends State
    case class StatePage(page:AttrPage) extends State
    var _state : State = StateInitial()
    var _isDone : Boolean = false

    private def nextImpl(state:State):(State,Boolean,Option[PageRev]) = {
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
            val rev = AttrRev(
              innerText(child("id", elsRev)),
              innerText(child("sha1", elsRev)))
            exit(it) // revision
            val pagerev = PageRev(page, rev)
            return (state, false, Some(pagerev))
          } else {
            return (state, true, None)
          }
      }
    }

    private var _next : PageRev = null
    override def hasNext: Boolean = {
      if(_isDone)
        return false
      while(it.hasNext && !_isDone) {
        val (stateNext, isDone, nextNext) = nextImpl(_state)
        _state = stateNext
        _isDone = isDone
        if(nextNext.isDefined) {
          _next = nextNext.get
          return true
        }
      }
      return false
    }

    override def next(): PageRev = {
      _next
    }
  }

}
