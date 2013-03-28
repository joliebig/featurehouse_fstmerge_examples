
package org.jmol.adapter.readers.xml;

import org.jmol.adapter.smarter.*;


import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import java.io.*;
import java.util.HashMap;

import netscape.javascript.JSObject;
import org.jmol.util.Logger;



public class XmlReader extends AtomSetCollectionReader {

  
  protected XmlReader parent;    

  protected Atom atom;

  String[] implementedAttributes = { "id" };

  

 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    XMLReader xmlReader = getXMLReader();
    if (xmlReader == null) {
      atomSetCollection = new AtomSetCollection("xml");
      atomSetCollection.errorMessage = "No XML reader found";
      return;
    }
    try {
      processXml(xmlReader);
    } catch (Exception e) {
      e.printStackTrace();
      atomSetCollection.errorMessage = "XML reader error: " + e.getMessage();
    }
  }

  private XMLReader getXMLReader() {
    XMLReader xmlr = null;
    
    if (xmlr == null
        && System.getProperty("java.version").compareTo("1.4") >= 0)
      xmlr = allocateXmlReader14();
    
    if (xmlr == null)
      xmlr = allocateXmlReaderAelfred2();
    return xmlr;
  }

  private XMLReader allocateXmlReader14() {
    XMLReader xmlr = null;
    try {
      javax.xml.parsers.SAXParserFactory spf = javax.xml.parsers.SAXParserFactory
          .newInstance();
      spf.setNamespaceAware(true);
      javax.xml.parsers.SAXParser saxParser = spf.newSAXParser();
      xmlr = saxParser.getXMLReader();
      Logger.debug("Using JAXP/SAX XML parser.");
    } catch (Exception e) {
      Logger.debug("Could not instantiate JAXP/SAX XML reader: "
          + e.getMessage());
    }
    return xmlr;
  }

  private XMLReader allocateXmlReaderAelfred2() {
    XMLReader xmlr = null;
    try {
      xmlr = (XMLReader) this.getClass().getClassLoader().loadClass(
          "gnu.xml.aelfred2.XmlReader").newInstance();
      Logger.debug("Using Aelfred2 XML parser.");
    } catch (Exception e) {
      Logger.debug("Could not instantiate Aelfred2 XML reader!");
    }
    return xmlr;
  }

  private Object processXml(XMLReader xmlReader) throws Exception {
    atomSetCollection = new AtomSetCollection(readerName);
    Object res = getXmlReader();
    if (res instanceof String)
      return res;
    XmlReader thisReader = (XmlReader)res;
    thisReader.processXml(this, atomSetCollection, reader, xmlReader);
    return thisReader;
  }

  private Object getXmlReader() {    
    String className = null;
    Class atomSetCollectionReaderClass;
    String err = null;
    XmlReader thisReader = null;
    try {
      int pt = readerName.indexOf("(");
      String name = (pt < 0 ? readerName : readerName.substring(0, pt));
      className = Resolver.getReaderClassBase(name);
      atomSetCollectionReaderClass = Class.forName(className);
      thisReader = (XmlReader) atomSetCollectionReaderClass
          .newInstance();
    } catch (Exception e) {
      err = "File reader was not found:" + className;
      Logger.error(err);
      return err;
    }
    return thisReader;
  }
  
  protected void processXml(XmlReader parent,
                         AtomSetCollection atomSetCollection,
                         BufferedReader reader, XMLReader xmlReader) {
  }

  protected void parseReaderXML(XMLReader xmlReader) {
    xmlReader.setEntityResolver(new DummyResolver());
    InputSource is = new InputSource(reader);
    is.setSystemId("foo");
    try {
      xmlReader.parse(is);
    } catch (Exception e) {
      e.printStackTrace();
      atomSetCollection.errorMessage = "XML parsing error: " + e.getMessage();
    }
  }

  

 public void readAtomSetCollectionFromDOM(Object Node) {
    processXml((JSObject) Node);
  }

  private Object processXml(JSObject DOMNode) {
    atomSetCollection = new AtomSetCollection(readerName);
    String className = null;
    Class atomSetCollectionReaderClass;
    String err = null;
    XmlReader thisReader = null;
    String name = readerName.substring(0, readerName.indexOf("("));
    try {
      className = Resolver.getReaderClassBase(name);
      atomSetCollectionReaderClass = Class.forName(className);
      thisReader = (XmlReader) atomSetCollectionReaderClass
          .newInstance();
    } catch (Exception e) {
      err = "File reader was not found:" + className;
      Logger.error(err);
      return err;
    }
    thisReader.processXml(this, atomSetCollection, reader, DOMNode);
    return thisReader;
  }

  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
  }

  protected void processStartElement(String namespaceURI, String localName, String qName,
                           HashMap atts) {
    
  }

  

  protected boolean keepChars;
  protected String chars;
  protected void setKeepChars(boolean TF) {
    keepChars = TF;
    chars = null;
  }

  protected void processEndElement(String uri, String localName, String qName) {
    
  }

  public static class DummyResolver implements EntityResolver {
    public InputSource resolveEntity(String publicID, String systemID)
        throws SAXException {
      if (Logger.debugging) {
        Logger.debug(
            "Jmol SAX EntityResolver not resolving:" +
            "\n  publicID: " + publicID +
            "\n  systemID: " + systemID
          );
      }
      return new InputSource(new BufferedReader(new StringReader("")));
    }
  }
  
  public class JmolXmlHandler extends DefaultHandler implements ErrorHandler {

    public JmolXmlHandler() {
    }

    public JmolXmlHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }

    public void setHandler(XMLReader xmlReader, JmolXmlHandler handler) {
      try {
        xmlReader.setFeature("http://xml.org/sax/features/validation", false);
        xmlReader.setFeature("http://xml.org/sax/features/namespaces", true);
        xmlReader.setEntityResolver(handler);
        xmlReader.setContentHandler(handler);
        xmlReader.setErrorHandler(handler);
      } catch (Exception e) {
        Logger.error("ERROR IN XmlReader.JmolXmlHandler.setHandler", e);
      }

    }

    public void startDocument() {
    }

    public void endDocument() {
    }

    

    public HashMap atts;

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attributes) {
      getAttributes(attributes);
      if (Logger.debugging) {
        Logger.debug("start " + localName);
      }
      startElement(namespaceURI, localName, qName);
    }

    private void startElement(String namespaceURI, String localName, String qName) {
      processStartElement(namespaceURI, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName) {
      if (Logger.debugging) {
        Logger.debug("end " + localName);
      }
      processEndElement(uri, localName, qName);
    }

    

    public void characters(char[] ch, int start, int length) {
      if (keepChars) {
        if (chars == null) {
          chars = new String(ch, start, length);
        } else {
          chars += new String(ch, start, length);
        }
      }
    }

    

    public InputSource resolveEntity(String name, String publicId,
                                     String baseURI, String systemId) {
      if (Logger.debugging) {
        Logger.debug(
            "Not resolving this:" +
            "\n      name: " + name +
            "\n  systemID: " + systemId +
            "\n  publicID: " + publicId +
            "\n   baseURI: " + baseURI
          );
      }
      return null;
    }

    public InputSource resolveEntity(String publicId, String systemId) {
      if (Logger.debugging) {
        Logger.debug(
            "Not resolving this:" +
            "\n  publicID: " + publicId +
            "\n  systemID: " + systemId
          );
      }
      return null;
    }

    public void error(SAXParseException exception) {
      Logger.error("SAX ERROR:" + exception.getMessage());
    }

    public void fatalError(SAXParseException exception) {
      Logger.error("SAX FATAL:" + exception.getMessage());
    }

    public void warning(SAXParseException exception) {
      Logger.warn("SAX WARNING:" + exception.getMessage());
    }

    

    
    
    

    protected void walkDOMTree(JSObject DOMNode) {
      String namespaceURI = (String) DOMNode.getMember("namespaceURI");
      String localName = (String) DOMNode.getMember("localName");
      String qName = (String) DOMNode.getMember("nodeName");
      JSObject attributes = (JSObject) DOMNode.getMember("attributes");
      getAttributes(attributes);
      startElement(namespaceURI, localName, qName);
      if (((Boolean) DOMNode.call("hasChildNodes", (Object[]) null))
          .booleanValue()) {
        for (JSObject nextNode = (JSObject) DOMNode.getMember("firstChild"); nextNode != (JSObject) null; nextNode = (JSObject) nextNode
            .getMember("nextSibling"))
          walkDOMTree(nextNode);
      }
      endElement(namespaceURI, localName, qName);
    }

    

    private void getAttributes(Attributes attributes) {
      int nAtts = attributes.getLength();
      atts = new HashMap(nAtts);
      for (int i = nAtts; --i >= 0;)
        atts.put(attributes.getLocalName(i), attributes.getValue(i));
    }

    private void getAttributes(JSObject attributes) {
      if (attributes == null) {
        atts = new HashMap(0);
        return;
      }

      

      int nAtts = ((Number) attributes.getMember("length")).intValue();
      atts = new HashMap(nAtts);
      for (int i = implementedAttributes.length; --i >= 0;) {
        Object[] attArgs = { implementedAttributes[i] };
        JSObject attNode = (JSObject) attributes.call("getNamedItem", attArgs);
        if (attNode != null) {
          String attLocalName = (String) attNode.getMember("name");
          String attValue = (String) attNode.getMember("value");
          atts.put(attLocalName, attValue);
        }
      }
    }
  }
}
