package wikiedits

import java.io.ByteArrayInputStream

import javax.xml.stream.events.XMLEvent
import org.scalatest.FunSpec

import scala.collection.immutable.HashSet

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

  val xmlInput2 =
    """<mediawiki xmlns="http://www.mediawiki.org/xml/export-0.10/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-
      |instance" xsi:schemaLocation="http://www.mediawiki.org/xml/export-0.10/ http://www.mediawiki.org/xml/expo
      |rt-0.10.xsd" version="0.10" xml:lang="en">
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
      |      <text xml:space="preserve"></text>
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
      |      <text xml:space="preserve"></text>
      |      <sha1>qdn6psb4rbw59h3yuedhhbw94awzj7v</sha1>
      |    </revision>
      |  </page>
      |    <page>
      |    <title>Talk:Abandonment (legal)</title>
      |    <ns>1</ns>
      |    <id>1045</id>
      |    <revision>
      |      <id>15899550</id>
      |      <timestamp>2002-02-25T15:43:11Z</timestamp>
      |      <contributor>
      |        <username>Conversion script</username>
      |        <id>1226483</id>
      |      </contributor>
      |      <minor />
      |      <comment>Automated conversion</comment>
      |      <model>wikitext</model>
      |      <format>text/x-wiki</format>
      |      <text xml:space="preserve"></text>
      |      <sha1>6xw3k93fdspbs09nxfoknbylsikxpy7</sha1>
      |    </revision>
      |    <revision>
      |      <id>21616712</id>
      |      <parentid>15899550</parentid>
      |      <timestamp>2005-08-23T02:23:57Z</timestamp>
      |      <contributor>
      |        <username>Ceyockey</username>
      |        <id>150564</id>
      |      </contributor>
      |      <comment>Disambiguation pages</comment>
      |      <model>wikitext</model>
      |      <format>text/x-wiki</format>
      |      <text xml:space="preserve"></text>
      |      <sha1>o0v01pzm8jm93xtgjullneg5v99qzvs</sha1>
      |    </revision>
      |  </page>
      |</mediawiki>
    """.stripMargin

  val pathTestIn1 = "./data/enwiki-latest-pages-meta-history1.xml-p1043p2036-300MB.7z"

  def getXml1Second() = {
    ParseViaStax.loadAndDecompress(pathTestIn1, inTest=true)
  }

  describe("decompression") {
    ignore("should decompress a long stream") {
      val is = Decompress.openLocal7z(java.nio.file.Paths.get(pathTestIn1))
      var c : Long = 0
      var cRead : Long = 0
      val buf :Array[Byte] = Array.fill[Byte](1024*1024)(0)
      while(cRead >= 0) {
        cRead = is.read(buf,0,1024*1024)
        c += cRead
      }
      println(s"read ${c} bytes")
    }
  }
  describe("XML") {
    describe("scalability") {
      it("should find events in a small input") {
        val it = ParseViaStax.parseString(xmlInput1)
        var c = 0
        for(pageRev <- it) {
          c += 1
        }
        println(s"found ${c} events in small input")
        assert(c>0)
      }
      it("should find revisions in a small input") {
        val it = ParseViaStax.parseString(xmlInput1)
        var c = 0
        for(pageRev <- QueryViaStax.findPageRevisions(it)) {
          c += 1
          println(s"found ${pageRev}")
        }
        println(s"found ${c} revisions in small input")
        assert(c>0)
      }
      it("should find revisions in a small input 2") {
        val it = ParseViaStax.parseString(xmlInput2)
        var c = 0
        for(pageRev <- QueryViaStax.findPageRevisions(it)) {
          c += 1
          println(s"found ${pageRev}")
        }
        println(s"found ${c} revisions in small input 2")
        assert(c>=4)
      }
      it("should find events in a large input") {
        var c = 0
        for(el <- getXml1Second()) {
          c += 1
        }
        println(s"found ${c} events in 1s input")
        assert(c>0)
      }
      it("should find revisions in a large input") {
        var c = 0
        for(pageRev <- QueryViaStax.findPageRevisions(getXml1Second())) {
          c += 1
        }
        println(s"found ${c} revisions in 1s input")
        assert(c>0)
      }
      it("countReverts should find reverts in a large input") {
        var numPages = 0
        var numPagesIncomplete = 0
        var numPagesReverted = 0
        var numReverts = 0
        for(page <- QueryViaStax.countReverts(
          for(pagerev <- QueryViaStax.findPageRevisions(getXml1Second());
              if pagerev.page.idpage.length >= 1 && pagerev.rev.sha1.length >= 1
          ) yield pagerev
        )) {
          numPages += 1
          if(page.numReverts > 0)
            numPagesReverted += 1
          numReverts += page.numReverts
        }
        println(s"found ${numPages} pages, ${numPagesReverted} reverted, and ${numReverts} reverts")
      }

    }
    def elsTest(st: String) = {
      ParseViaStax.parse(new ByteArrayInputStream(st.getBytes())).toArray
    }
    def elsTestChildren(st:String, keepEnd:Boolean) = {
      val els = elsTest(st)
        .drop(1) // <?xml ...>
        .drop(1) // <foo> (ElementStart)
      if(keepEnd)
        els
      else
        els.dropRight(2) // discard ElementEnd and DocumentEnd
    }
    def stTargetChild(isCompressed:Boolean) = {
      if(isCompressed)
        "<bar/>"
      else
        "<bar></bar>"
    }
    describe("child") {
      it("should find an only child") {
        Seq(true, false).foreach(isTargetCompressed => {
          val st =
            s"""
               |<foo>
               |${stTargetChild(isTargetCompressed)}
               |</foo>
            """.stripMargin
          Seq(true, false).foreach(keepEnd => {
            val elsIn = elsTestChildren(st, keepEnd)
            val els = TraverseViaStax.child("bar", elsIn)
            assert(els.length > 0)
          })
        })
      }
      it("should find a right child") {
        Seq(true, false).foreach(isTargetCompressed => {
          val st =
            s"""
               |<foo>
               |<baz></baz>
               |${stTargetChild(isTargetCompressed)}
               |</foo>
          """.stripMargin
          Seq(true, false).foreach(keepEnd => {
            val elsIn = elsTestChildren(st, keepEnd)
            val els = TraverseViaStax.child("bar", elsIn)
            assert(els.length > 0)
          })
        })
      }
      it("should find a left child") {
        Seq(true, false).foreach(isTargetCompressed => {
          val st =
            s"""
               |<foo>
               |<baz></baz>
               |${stTargetChild(isTargetCompressed)}
               |</foo>
          """.stripMargin
          Seq(true, false).foreach(keepEnd => {
            val elsIn = elsTestChildren(st, keepEnd)
            val els = TraverseViaStax.child("bar", elsIn)
            assert(els.length > 0)
          })
        })
      }
      it("should not find a missing child") {
        val st =
          s"""
             |<foo>
             |</foo>
          """.stripMargin
        Seq(true, false).foreach(keepEnd => {
          val elsIn = elsTestChildren(st, keepEnd)
          val els = TraverseViaStax.child("bar", elsIn)
          assert(els.length == 0)
        })
      }
    }
    describe("childrenUntil") {
      it("should find uninterrupted children") {
        val hist =
          0.to(2).flatMap(numChildren => {
            val stTarget = 1.to(numChildren).map(_ => "<bar/>\n").mkString("")
            val st =
              s"""
                 |<foo>
                 |${stTarget}
                 |</foo>
            """.stripMargin
            Seq(true, false).flatMap(keepEnd => {
              val elsIn = elsTestChildren(st, keepEnd).iterator.buffered
              val els = TraverseViaStax.childrenUntil(elsIn, HashSet("baz")).filter(!_.isCharacters)
              val ok = (els.length == 2*numChildren) // 2: StartElement and EndElement
              Seq((Map("numChildren"->numChildren, "keepEnd"->keepEnd), ok))
            })
          }).groupBy(_._2)
        assert(hist.get(false).isEmpty)
      }
      it("should find interrupted children") {
        val hist =
          0.to(2).flatMap(numChildren => {
            val stTarget = 1.to(numChildren).map(_ => "<bar/>\n").mkString("")
            val st =
              s"""
                 |<foo>
                 |${stTarget}
                 |<baz/>
                 |<bar/>
                 |</foo>
            """.stripMargin
            Seq(true, false).flatMap(keepEnd => {
              val elsIn = elsTestChildren(st, keepEnd).iterator.buffered
              val els = TraverseViaStax.childrenUntil(elsIn, HashSet("baz")).filter(!_.isCharacters)
              val ok = (els.length == 2*numChildren) // 2: StartElement and EndElement
              Seq((Map("numChildren"->numChildren, "keepEnd"->keepEnd), ok))
            })
          }).groupBy(_._2)
        assert(hist.get(false).isEmpty)
      }
    }
    describe("text extraction") {
      describe("innerText") {
        it("should extract text") {
          Seq("hello"," hello", "hello ","hello\n","\nhello").foreach(st => {
            val stXml =
              """
                |<foo>hello
                |</foo>
              """.stripMargin
            val stExpected = st.trim
            val elsIn = elsTest(stXml)
            val stActual = TraverseViaStax.innerText(elsIn)
            assert(stExpected == stActual.trim)
          })
        }
      }

    }
  }

}
