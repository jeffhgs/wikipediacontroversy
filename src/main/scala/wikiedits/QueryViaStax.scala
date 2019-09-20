package wikiedits

import javax.xml.XMLConstants
import javax.xml.stream.events.XMLEvent
import nu.xom.Nodes
import wikiedits.XpathViaXom.openLocal7z

object QueryViaStax {
  import collection.JavaConverters._

  def loadDecompressAndFindPageRevisions(path: String, ss: String) = {
    val is = openLocal7z(java.nio.file.Paths.get(path))
    System.setProperty("jdk.xml.totalEntitySizeLimit", "2000000000")
    parse(is)
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


  def main(args : Array[String]) = {
  }
}
