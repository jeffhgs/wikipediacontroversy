package wikiedits

import java.io.IOException
import java.util
import java.util.{ArrayList, Arrays, List}

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathExpressionException
import javax.xml.xpath.XPathFactory
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.xml.sax.SAXException


object XPathQueryExample {
  def main(args: Array[String]): Unit = {
    val factory = DocumentBuilderFactory.newInstance
    factory.setNamespaceAware(true)
    var builder : DocumentBuilder = null
    var doc : Document = null
    try {
      builder = factory.newDocumentBuilder
      doc = builder.parse("/Users/pankaj/employees.xml")
      // Create XPathFactory object
      val xpathFactory = XPathFactory.newInstance
      // Create XPath object
      val xpath = xpathFactory.newXPath
      val name = getEmployeeNameById(doc, xpath, 4)
      System.out.println("Employee Name with ID 4: " + name)
      val names = getEmployeeNameWithAge(doc, xpath, 30)
      System.out.println("Employees with 'age>30' are:" + util.Arrays.toString(names.toArray))
      val femaleEmps = getFemaleEmployeesName(doc, xpath)
      System.out.println("Female Employees names are:" + util.Arrays.toString(femaleEmps.toArray))
    } catch {
      case e@(_: ParserConfigurationException | _: SAXException | _: IOException) =>
        e.printStackTrace()
    }
  }

  private def getFemaleEmployeesName(doc: Document, xpath: XPath) = {
    val list = new util.ArrayList[String]
    try { //create XPathExpression object
      val expr = xpath.compile("/Employees/Employee[gender='Female']/name/text()")
      //evaluate expression result on XML document
      val nodes = expr.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList]
      var i = 0
      while ( {
        i < nodes.getLength
      }) {
        list.add(nodes.item(i).getNodeValue)
        i += 1; i - 1
      }
    } catch {
      case e: XPathExpressionException =>
        e.printStackTrace()
    }
    list
  }

  private def getEmployeeNameWithAge(doc: Document, xpath: XPath, age: Int) = {
    val list = new util.ArrayList[String]
    try {
      val expr = xpath.compile("/Employees/Employee[age>" + age + "]/name/text()")
      val nodes = expr.evaluate(doc, XPathConstants.NODESET).asInstanceOf[NodeList]
      var i = 0
      while ( {
        i < nodes.getLength
      }) {
        list.add(nodes.item(i).getNodeValue)
        i += 1; i - 1
      }
    } catch {
      case e: XPathExpressionException =>
        e.printStackTrace()
    }
    list
  }

  private def getEmployeeNameById(doc: Document, xpath: XPath, id: Int) = {
    var name : String = null
    try {
      val expr = xpath.compile("/Employees/Employee[@id='" + id + "']/name/text()")
      name = expr.evaluate(doc, XPathConstants.STRING).asInstanceOf[String]
    } catch {
      case e: XPathExpressionException =>
        e.printStackTrace()
    }
    name
  }
}

