package wikiedits

import java.io.{ByteArrayInputStream, InputStream}

import javax.xml.XMLConstants
import javax.xml.stream.events.XMLEvent
import nu.xom.Nodes
import wikiedits.XpathViaXom.openLocal7z

import scala.collection.immutable.HashSet
import scala.collection.mutable.ArrayBuffer

object QueryViaStax {
  import collection.JavaConverters._
  import util.control.Breaks._

  def childrenUntil(it: BufferedIterator[XMLEvent], tags:Set[String]) = {
    var b = ArrayBuffer[XMLEvent]()
    var depth = 0
    var done = false
    while(!done && it.hasNext) {
      val el = it.head
      if(el == null)
        // end of stream
        done = true
      else if(depth == 0 && el.isEndElement)
        // end of element
        done = true
      else if(depth == 0 && el.isStartElement && tags.contains(el.asStartElement().getName().getLocalPart))
        // hit stopping element
        done = true
      else {
        b += el
        // maintain depth
        if (el.isStartElement)
          depth += 1
        else if (el.isEndElement)
          depth -= 1
        it.next()
      }
    }
    b
  }

  def child(tag:String, els:Seq[XMLEvent]): ArrayBuffer[XMLEvent] = {
    var b = ArrayBuffer[XMLEvent]()
    var depth = 0
    var done = false
    var isChild = false
    for(el <- els) {
      if(!done) {
        if (depth == 0 && el.isEndElement) {
          // end of all children
          done = true
        }
        else if (depth == 0 && el.isStartElement && tag == el.asStartElement().getName().getLocalPart) {
          // found the matching child subtree
          isChild = true
        }
        if (isChild) {
          b += el
          if(depth == 1 && el.isEndElement) {
            isChild = false
          }
        }
        // maintain depth
        if (el.isStartElement)
          depth += 1
        else if (el.isEndElement)
          depth -= 1
      }
    }
    b
  }

  def innerText(els:Seq[XMLEvent]) = {
    els.filter(_.isCharacters).map(_.asCharacters()).mkString("")
  }

  case class DepthXmlEventReader(it: BufferedIterator[XMLEvent]) extends BufferedIterator[XMLEvent] {
    private var _depth = 0
    def depth = {
      _depth
    }
    override def head: XMLEvent = {
      it.head
    }

    override def hasNext: Boolean = {
      it.hasNext
    }

    override def next(): XMLEvent = {
      val el : XMLEvent = it.next()
      if (el.isStartElement) {
        _depth += 1
      }
      else if(el.isEndElement) {
        _depth -= 1
      }
      el
    }
  }

  def loadDecompressAndFindPageRevisions(path: String, ss: String) = {
    val is = openLocal7z(java.nio.file.Paths.get(path))
    System.setProperty("jdk.xml.totalEntitySizeLimit", "2000000000")
    parse(is)
  }

  def parseString(st:String) = {
    parse(new ByteArrayInputStream(st.getBytes()))
  }

  def parse(is: InputStream) = {
    import javax.xml.stream.XMLInputFactory
    val factory = XMLInputFactory.newInstance
    //factory.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING, false)
    //factory.setProperty("jdk.xml.totalEntitySizeLimit", "2000000000")
    val it = factory.createXMLEventReader(is)
    new BufferedIterator[XMLEvent]() {
      override def hasNext: Boolean = {
        it.hasNext
      }

      override def next(): XMLEvent = {
        it.nextEvent()
      }

      override def head: XMLEvent = {
        it.peek()
      }
    }
  }
}
