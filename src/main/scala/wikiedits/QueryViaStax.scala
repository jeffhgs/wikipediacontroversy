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
    case class StateDone() extends State
    var state : State = StateInitial()

    private def nextImpl():(State,Option[PageRev]) = {
      val el = it.next()
      state match {
        case StateInitial() =>
          if (it.depth == 2 && el.isStartElement && el.asStartElement().getName().getLocalPart == "page") {
            val elsPage = childrenUntil(it, HashSet("revision"))
            val page = AttrPage(
              innerText(child("title", elsPage)),
              innerText(child("id", elsPage)))
            return (StatePage(page), None)
          } else if(it.depth == 2 && el.isEndElement) {
            return (StateInitial(), None)
          } else {
            return (state, None)
          }
        case StatePage(page) =>
          if(el.isEndElement) {
            return (StateInitial(), None)
          }
          else if (el.isStartElement && el.asStartElement().getName().getLocalPart == "revision") {
            val elsRev = childrenUntil(it, HashSet())
            val rev = AttrRev(
              innerText(child("id", elsRev)),
              innerText(child("sha1", elsRev)))
            exit(it) // revision
            val pagerev = PageRev(page, rev)
            return (state, Some(pagerev))
          } else {
            return (state, None)
          }
        case StateDone() =>
          (state, None)
      }
    }

    private var _next : PageRev = null
    override def hasNext: Boolean = {
      if(state.isInstanceOf[StateDone])
        return false
      while(it.hasNext && !state.isInstanceOf[StateDone]) {
        val (stateNext, nextNext) = nextImpl()
        state = stateNext
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
