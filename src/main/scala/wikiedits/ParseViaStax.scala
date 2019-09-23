package wikiedits

import java.io.{ByteArrayInputStream, InputStream}

import javax.xml.stream.XMLEventFactory
import javax.xml.stream.events.XMLEvent

object ParseViaStax {
  def loadAndDecompress(path: String, inTest:Boolean = false) = {
    val is = Decompress.openLocal7z(java.nio.file.Paths.get(path))
    System.setProperty("jdk.xml.totalEntitySizeLimit", "2000000000")
    if(inTest)
      parseInTest(is)
    else
      parse(is)
  }

  def parseString(st:String) = {
    parse(new ByteArrayInputStream(st.getBytes()))
  }

  def parse(is: InputStream) = {
    import javax.xml.stream.XMLInputFactory
    val factory = XMLInputFactory.newInstance
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

  def parseInTest(is: InputStream) = {
    import javax.xml.stream.XMLInputFactory
    val factory = XMLInputFactory.newInstance
    val it = factory.createXMLEventReader(is)
    new BufferedIterator[XMLEvent]() {
      var done = false
      override def hasNext: Boolean = {
        if(done)
          return false
        it.hasNext
      }

      override def next(): XMLEvent = {
        try {
          it.nextEvent()
        } catch {
          case ex : java.lang.NullPointerException =>
            quit()
          case ex : javax.xml.stream.XMLStreamException =>
            quit()
        }
      }

      override def head: XMLEvent = {
        try {
          it.peek()
        } catch {
          case ex : java.lang.NullPointerException =>
            quit()
          case ex : javax.xml.stream.XMLStreamException =>
            quit()
        }
      }

      private def quit() = {
        done = true
        XMLEventFactory.newInstance().createCharacters("")
      }
    }
  }
}
