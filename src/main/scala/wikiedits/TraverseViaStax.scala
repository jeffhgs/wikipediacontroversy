package wikiedits

import javax.xml.stream.events.XMLEvent

import scala.collection.mutable.ArrayBuffer

object TraverseViaStax {
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

  def exit(it:BufferedIterator[XMLEvent]): Unit = {
    while(it.hasNext && !it.head.isEndElement) {
      it.next()
    }
    it.next()
  }
}
