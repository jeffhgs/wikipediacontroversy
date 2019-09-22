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

  case class PageRev(titlePage:String, idpage:String, idRev:String, sha1:String)

  def findPageRevisions(it0: BufferedIterator[XMLEvent]) = new Iterator[PageRev] {
    val it = DepthXmlEventReader(it0)

    val stateInitial = 0
    val statePage = 4
    val stateRevision = 2
    val stateDone = 3
    var state = 0

    var elsPage = Seq[XMLEvent]()
    var title = ""
    var idPage = ""

    var elsRev = Seq[XMLEvent]()
    var idRev = ""
    var sha1 = ""

    private def nextImpl():(Int,Option[PageRev]) = {
      val el = it.next()
      state match {
        case 0 => // stateInitial
          if (it.depth == 2 && el.isStartElement && el.asStartElement().getName().getLocalPart == "page") {
            elsPage = childrenUntil(it, HashSet("revision"))
            title = innerText(child("title", elsPage))
            idPage = innerText(child("id", elsPage))
            return (statePage, None)
          } else if(it.depth == 2 && el.isEndElement) {
            elsPage = Seq()
            title = ""
            idPage = ""
            return (stateInitial, None)
          } else {
            return (state, None)
          }
        case 4 => // statePage
          if(el.isEndElement) {
            return (stateInitial, None)
          }
          else if (el.isStartElement && el.asStartElement().getName().getLocalPart == "revision") {
            elsRev = childrenUntil(it, HashSet())
            idRev = innerText(child("id", elsRev))
            sha1 = innerText(child("sha1", elsRev))
            exit(it) // revision
            val rev = PageRev(title, idPage, idRev, sha1)
            elsRev = Seq[XMLEvent]()
            idRev = ""
            sha1 = ""
            return (statePage, Some(rev))
          } else {
            return (statePage, None)
          }
        case 2 => // stateRevision
          return(stateRevision, None)
        case 3 => // stateDone
          (stateDone, None)
      }
    }

    private var _next : PageRev = null
    override def hasNext: Boolean = {
      if(state == stateDone)
        return false
      while(it.hasNext && state != stateDone) {
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
