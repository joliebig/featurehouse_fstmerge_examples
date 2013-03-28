
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

  int frequencyCount;

  public void processStartElement2(String namespaceURI, String localName,
                                   String qName, HashMap atts) {
    if (localName.equals("normalCoordinate")) {
      
      String wavenumber = "";
      String units = "";
      try {
        atomSetCollection.cloneLastAtomSet();
      } catch (Exception e) {
        e.printStackTrace();
        atomSetCollection.errorMessage = "Error processing normalCoordinate: " + e.getMessage();
        frequencyCount = 0;
        return;
      }
      frequencyCount++;
      if (atts.containsKey("wavenumber")) {
        wavenumber = (String) atts.get("wavenumber");
        if (atts.containsKey("units"))
          units = (String) atts.get("units");

        

        atomSetCollection.setAtomSetProperty("Frequency", wavenumber + " "
            + units);
        keepChars = true;
      }
      return;
    }

    if (localName.equals("vibrations")) {
      frequencyCount = 0;
      return;
    }
  }

  public void processEndElement2(String uri, String localName, String qName) {
    if (localName.equals("normalCoordinate")) {
      int atomCount = atomSetCollection.getLastAtomSetAtomCount();
      tokens = getTokens(chars);
      Atom[] atoms = atomSetCollection.getAtoms();
      int baseAtomIndex = atomSetCollection.getCurrentAtomSetIndex() * atomCount;
      for (int offset = tokens.length - atomCount * 3, i = 0; i < atomCount; i++) {
        Atom atom = atoms[i + baseAtomIndex];
        atom.vectorX = parseFloat(tokens[offset++]);
        atom.vectorY = parseFloat(tokens[offset++]);
        atom.vectorZ = parseFloat(tokens[offset++]);
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
