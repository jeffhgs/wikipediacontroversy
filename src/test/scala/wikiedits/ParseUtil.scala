package wikiedits

import javax.xml.stream.events.XMLEvent

object ParseUtil {
  case class Shorten(it:BufferedIterator[XMLEvent], tag:String, numEltsMax:Int) extends BufferedIterator[XMLEvent] {
    var numElts = 0

    override def head: XMLEvent = {
      it.head
    }

    override def hasNext: Boolean = {
      if(numElts < numEltsMax)
        it.hasNext
      else {
        false
      }
    }

    override def next(): XMLEvent = {
      val el = it.next()
      if(el.isEndElement) {
        if (el.asEndElement().getName.getLocalPart == tag) {
          numElts += 1
        }
      }
      el
    }
  }
}
