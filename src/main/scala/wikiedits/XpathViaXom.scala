package wikiedits

import java.io.{ByteArrayInputStream, InputStream, InputStreamReader}

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import nu.xom.{Builder, Document}

object XpathViaXom {
  def filterXslt(builder: Builder, doc: Document) = {
    import nu.xom.Nodes
    import nu.xom.xslt.XSLTransform
    val ss = """<hello></hello>"""
    val stylesheet = builder.build(new ByteArrayInputStream(ss.getBytes))
    val transform = new XSLTransform(stylesheet)
    val output = transform.transform(doc)
    output
  }

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: java nu.xom.samples.Transformer document stylesheet")
      return
    }
    System.setProperty("javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl")
    /* System.setProperty(
            "javax.xml.transform.TransformerFactory",
            "com.icl.saxon.TransformerFactoryImpl"); */
    import nu.xom.Nodes
    import nu.xom.xslt.XSLTransform
    val builder = new nu.xom.Builder()
    try {
      val doc = builder.build(args(0))
      val output: Nodes = filterXslt(builder, doc)
      import scala.collection.JavaConversions._
      for (node <- output) {
        System.out.print(node.toXML)
      }
      System.out.println()
    } catch {
      case ex: Exception =>
        System.err.println(ex)
        ex.printStackTrace()
    }  }

}
