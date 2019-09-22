package wikiedits

import java.io.{ByteArrayInputStream, InputStream}

import javax.xml.stream.events.XMLEvent

object ParseViaStax {
  def loadDecompressAndFindPageRevisions(path: String, ss: String) = {
    val is = Decompress.openLocal7z(java.nio.file.Paths.get(path))
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
