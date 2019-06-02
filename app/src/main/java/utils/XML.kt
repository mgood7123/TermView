package utils

import org.w3c.dom.Document
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import java.io.IOException
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import org.xml.sax.SAXException
import java.io.StringWriter
import javax.xml.transform.OutputKeys

@Throws(Exception::class)
fun prettyPrint(xml: Document) {
    val tf = TransformerFactory.newInstance().newTransformer()
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
    tf.setOutputProperty(OutputKeys.INDENT, "yes")
    val out = StringWriter()
    tf.transform(DOMSource(xml), StreamResult(out))
    System.out.println(out.toString())
}

val NodeList.mutableList: MutableList<Node>
    get() {
        val nodeList = mutableListOf<Node>()
        var index = 0
        while (index < this.length) nodeList.add(this.item(index++))
        return nodeList
    }

val NamedNodeMap.mutableList: MutableList<Node>
    get() {
        val nodeList = mutableListOf<Node>()
        var index = 0
        while (index < this.length) nodeList.add(this.item(index++))
        return nodeList
    }

fun Node.addPermission(permission: String) {
    this.ownerDocument.firstChild.appendChild(
        this.ownerDocument.createElement("uses-permission").also {
            it.setAttribute("android:name", permission)
        }
    )
}

fun ReadAndModifyXMLFile(xmlFilePath: String) = try {

    val documentBuilderFactory = DocumentBuilderFactory.newInstance()

    val documentBuilder = documentBuilderFactory.newDocumentBuilder()

    val document = documentBuilder.parse(File(xmlFilePath))
    if (document.hasChildNodes()) {
        document.childNodes.mutableList.forEach {
            if (it.nodeName == "manifest") {
                if (document.firstChild.hasChildNodes()) {
                    document.firstChild.addPermission("android.permission.CALL_LOG")
                    document.firstChild.childNodes.mutableList.forEach {
                        if (it.nodeType == Node.ELEMENT_NODE &&
                            it.nodeName == "uses-permission" &&
                            it.hasAttributes()
                        ) {
                            it.attributes.mutableList.forEach {
                                println("found permission: " + it.nodeValue)
                            }
                        }
                    }
                } else throw IndexOutOfBoundsException("0")
            }
        }
    } else throw IndexOutOfBoundsException("0")
    prettyPrint(document)

    val transformerFactory = TransformerFactory.newInstance()

    val transformer = transformerFactory.newTransformer()
    val domSource = DOMSource(document)

    val streamResult = StreamResult(File(xmlFilePath))
    transformer.transform(domSource, streamResult)

} catch (pce: ParserConfigurationException) {
    pce.printStackTrace()
} catch (tfe: TransformerException) {
    tfe.printStackTrace()
} catch (ioe: IOException) {
    ioe.printStackTrace()
} catch (sae: SAXException) {
    sae.printStackTrace()
}