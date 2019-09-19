package wikiedits

import java.io.{ByteArrayInputStream, InputStream, InputStreamReader}

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream
import nu.xom.{Builder, Document}

object XpathViaXom {
  lazy val ss1 = {
    val builder = new nu.xom.Builder()
    val textSs =
      """
        |<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        |  xmlns:w="http://www.mediawiki.org/xml/export-0.10/"
        |  version="1.0">
        |<xsl:template match="/">
        |  <xsl:for-each select="w:mediawiki">
        |    <xsl:for-each select="w:page">
        |      <xsl:variable name="nameOfPage" select='w:title' />
        |      <xsl:variable name="idOfPage" select='w:id' />
        |      <xsl:for-each select="w:revision">
        |        <xsl:variable name="idOfRevision" select='w:id' />
        |        <xsl:variable name="timestamp" select='w:timestamp' />
        |        <xsl:variable name="sha1" select='w:sha1' />
        |        <rev>
        |        <xsl:value-of select="concat($nameOfPage, ',', $idOfPage, ',', $idOfRevision, ',', $timestamp, ',', $sha1)"/>
        |        </rev>
        |      </xsl:for-each>
        |    </xsl:for-each>
        |  </xsl:for-each>
        |</xsl:template>
        |</xsl:stylesheet>
      """.stripMargin
    builder.build(new ByteArrayInputStream(textSs.getBytes))
  }

  def filterXslt(stylesheet: Document, doc: Document) = {
    import nu.xom.Nodes
    import nu.xom.xslt.XSLTransform

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
      val output: Nodes = filterXslt(ss1, doc)
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
