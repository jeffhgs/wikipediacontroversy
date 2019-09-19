package wikiedits

import java.io.ByteArrayInputStream

import org.scalatest.FunSpec

class TestXml extends FunSpec {
  val xmlInput1 =
    """<mediawiki xmlns="http://www.mediawiki.org/xml/export-0.10/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-
      |instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/expo
      |rt-0.10.xsd" version="0.10" xml:lang="en">
      |  <siteinfo>
      |    <sitename>Wikipedia</sitename>
      |    <dbname>enwiki</dbname>
      |    <base>https://en.wikipedia.org/wiki/Main_Page</base>
      |    <generator>MediaWiki 1.34.0-wmf.15</generator>
      |    <case>first-letter</case>
      |    <namespaces>
      |      <namespace key="-2" case="first-letter">Media</namespace>
      |      <namespace key="-1" case="first-letter">Special</namespace>
      |      <namespace key="2303" case="case-sensitive">Gadget definition talk</namespace>
      |    </namespaces>
      |  </siteinfo>
      |  <page>
      |    <title>Northern cavefish</title>
      |    <ns>0</ns>
      |    <id>1043</id>
      |    <revision>
      |      <id>234542</id>
      |      <parentid>307361776</parentid>
      |      <timestamp>2001-06-27T12:56:51Z</timestamp>
      |      <contributor>
      |        <username>Larry Sanger</username>
      |        <id>216</id>
      |      </contributor>
      |      <comment>*</comment>
      |      <model>wikitext</model>
      |      <format>text/x-wiki</format>
      |      <text xml:space="preserve">Northern Blindfish:  found in caves through Kentucky and southern Indian
      |      </text>
      |      <sha1>3wzg88fiapsv6bo154zh40veeogbjnj</sha1>
      |    </revision>
      |    <revision>
      |      <id>1057448</id>
      |      <parentid>234542</parentid>
      |      <timestamp>2002-02-25T15:51:15Z</timestamp>
      |      <contributor>
      |        <username>Conversion script</username>
      |        <id>1226483</id>
      |      </contributor>
      |      <minor />
      |      <comment>Automated conversion</comment>
      |      <model>wikitext</model>
      |      <format>text/x-wiki</format>
      |      <text xml:space="preserve">Northern Blindfish:  found in caves through Kentucky and southern Indiana.
      |The White River, flowing east to west south of Bedford Indiana, delimits the
      |northern range of ''Amblyopsis spelea''.  These fish are not found in caves
      |north of the White River.  See also [[cave fish]].
      |</text>
      |      <sha1>qdn6psb4rbw59h3yuedhhbw94awzj7v</sha1>
      |    </revision>
      |  </page>
      |  <page>
      |    <title>Southern cavefish</title>
      |  </page>
      |</mediawiki>
    """.stripMargin

  describe("XML") {
    import collection.JavaConverters._
    describe("parsing") {
      it("should have size 0") {
        assert(Set.empty.size == 0)
      }
      it("should parse") {
        val builder = new nu.xom.Builder()
        val source = builder.build(new ByteArrayInputStream(xmlInput1.getBytes))
        for(node <- XpathViaXom.filterXslt(XpathViaXom.ss1, source).iterator().asScala) {
          println(s"node: ${node.getValue}") //
        }
      }
      it("should open 7z") {
        val path = "./enwiki-latest-pages-meta-history1.xml-p1043p2036.7z"
        var c = 0
        for(node <- XpathViaXom.findPageRevisions(path).iterator().asScala) {
          //println(s"node: ${node.getValue}")
          c += 1
        }
        println(s"found in 7z ${c} revisions")
      }
    }
  }
}
