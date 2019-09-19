package wikiedits

/*
class XpathQueryViaNux {
  //import nu.xom.{ Builder, Element, Nodes }
  import nux.xom.xquery.{ StreamingPathFilter, StreamingTransform, XQueryUtil }
  nu.xom.Element

  ///Users/jehenrik/.ivy2/cache/nux/nux/jars/nux-1.6.jar!/nux/xom/xquery/StreamingTransform.class:12
  val processor = new StreamingTransform {
    def transform(record: Element) = {
      val id = XQueryUtil.xquery(record, "IndexCatalogueID").get(0)
      val placeResults = XQueryUtil.xquery(record, "//Place")
      val places = (0 until placeResults.size) map placeResults.get

      println(id.getValue + " " + places.map(_.getValue).mkString(", "))
      new Nodes()
    }
  }
}
*/
