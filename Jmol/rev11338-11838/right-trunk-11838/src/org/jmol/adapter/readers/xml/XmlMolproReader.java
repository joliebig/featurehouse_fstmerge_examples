
package org.jmol.adapter.readers.xml;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.HashMap;
import netscape.javascript.JSObject;
import org.xml.sax.*;



public class XmlMolproReader extends XmlCmlReader {

  

  static String[] molProImplementedAttributes = { "id", "length", "type", 
      "x3", "y3", "z3", "elementType", 
      "name", 
      "groups", "cartesianLength", "primitives", 
      "minL", "maxL", "angular", "contractions", 
      "occupation", "energy", "symmetryID", 
      "wavenumber", "units", 
  };

  XmlMolproReader() {  
  }
  
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new MolproHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    implementedAttributes = molProImplementedAttributes;
    ((MolproHandler) (new MolproHandler())).walkDOMTree(DOMNode);
  }

  public void processStartElement2(String namespaceURI, String localName,
                                   String qName, HashMap atts) {
    if (localName.equals("normalCoordinate")) {
      keepChars = false;
      if (!parent.doGetVibration(++vibrationNumber))
        return;
      try {
        atomSetCollection.cloneLastAtomSet();
      } catch (Exception e) {
        e.printStackTrace();
        atomSetCollection.errorMessage = "Error processing normalCoordinate: " + e.getMessage();
        vibrationNumber = 0;
        return;
      }
      if (atts.containsKey("wavenumber")) {
        String wavenumber = (String) atts.get("wavenumber");
        String units = "cm^-1";
        if (atts.containsKey("units")) {
          units = (String) atts.get("units");
          if (units.startsWith("inverseCent"))
            units = "cm^-1";
        }
        atomSetCollection.setAtomSetName(wavenumber + " " + units);
        atomSetCollection.setAtomSetProperty("Frequency", wavenumber + " " + units);
        atomSetCollection.setAtomSetProperty(SmarterJmolAdapter.PATH_KEY, "Frequencies");
        keepChars = true;
      }
      return;
    }

    if (localName.equals("vibrations")) {
      vibrationNumber = 0;
      return;
    }
  }

  public void processEndElement2(String uri, String localName, String qName) {
    if (localName.equals("normalCoordinate")) {
      if (!keepChars)
        return;
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      int baseAtomIndex = atomSetCollection.getLastAtomSetAtomIndex();
      tokens = getTokens(chars);
      for (int offset = tokens.length - atomCount * 3, i = 0; i < atomCount; i++) {
        atomSetCollection.addVibrationVector(i + baseAtomIndex,
            parseFloat(tokens[offset++]),
            parseFloat(tokens[offset++]),
            parseFloat(tokens[offset++])
        );
      }
    }
  }

  class MolproHandler extends CmlHandler {

    MolproHandler() {
    }

    MolproHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }

    public void startElement(String namespaceURI, String localName,
                             String qName, Attributes attributes) {
      super.startElement(namespaceURI, localName, qName, attributes);
      processStartElement2(namespaceURI, localName, qName, atts);
    }

    public void endElement(String uri, String localName, String qName) {
      processEndElement2(uri, localName, qName);
      super.endElement(uri, localName, qName);
    }
  }
}
