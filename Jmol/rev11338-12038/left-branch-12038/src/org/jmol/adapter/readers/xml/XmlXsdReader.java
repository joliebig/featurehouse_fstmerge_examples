
package org.jmol.adapter.readers.xml;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;
import java.util.BitSet;
import java.util.HashMap;

import netscape.javascript.JSObject;

import org.jmol.util.TextFormat;
import org.xml.sax.*;



public class XmlXsdReader extends XmlReader {

  

  String[] xsdImplementedAttributes = { "ID", 
      "XYZ", "Connections", "Components", "IsBackboneAtom",
      "Connects", "Type", 
      "Name", 
  };

  private BitSet bsBackbone = new BitSet();
  
  public XmlXsdReader() {
  }

  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    init(parent, atomSetCollection);
    this.reader = reader;
    new XsdHandler(xmlReader);
    parseReaderXML(xmlReader);
    fin();
  }

  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    init(parent, atomSetCollection);
    implementedAttributes = xsdImplementedAttributes;
    ((XsdHandler) (new XsdHandler())).walkDOMTree(DOMNode);
    fin();
  }

  private void init(XmlReader parent, AtomSetCollection atomSetCollection) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    parent.htParams.put("backboneAtoms", bsBackbone);
  }

  private void fin() {
    atomSetCollection.clearSymbolicMap(); 
  }

  private int iChain = -1;
  private int iGroup = 0;
  private int iAtom = 0;
  
  public void processStartElement(String namespaceURI, String localName, String qName,
                           HashMap atts) {
    String[] tokens;
    System.out.println(namespaceURI + " " + localName + " " + atts);
    
    if ("Molecule".equals(localName)) {
      atomSetCollection.newAtomSet();
      atomSetCollection.setAtomSetName((String) atts.get("Name"));      
      return;
    }
    
    if ("LinearChain".equals(localName)) {
      iGroup = 0;
      iChain++;
    }

    if ("RepeatUnit".equals(localName)) {
      iGroup++;
    }

    if ("Atom3d".equals(localName)) {
      atom = new Atom();
      atom.elementSymbol = (String) atts.get("Components");
      atom.atomName = (String) atts.get("ID");
      atom.atomSerial = ++iAtom;
      if (iChain >= 0)
        atom.chainID = (char) ((iChain - 1)%26 + 'A');
      atom.group3 = "UNK";
      if (iGroup == 0)
        iGroup = 1;
      atom.sequenceNumber = iGroup;
      String xyz = (String) atts.get("XYZ");
      if (xyz != null) {
        tokens = getTokens(xyz.replace(',',' '));
        atom.set(parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]));
      }
      boolean isBackbone = "1".equals((String) atts.get("IsBackboneAtom"));
      if (isBackbone)
        bsBackbone.set(iAtom);
      return;
    }
    if ("Bond".equals(localName)) {
      String[] atoms = TextFormat.split((String) atts.get("Connects"), ',');
      int order = 1;
      if (atts.containsKey("Type")) {
        String type = (String) atts.get("Type");
        if (type.equals("Double"))
          order = 2;
        else if (type.equals("Triple"))
          order = 3;
      }
      atomSetCollection.addNewBond(atoms[0], atoms[1], order);
      return;
    }
  }

  public void processEndElement(String uri, String localName, String qName) {
    if ("Atom3d".equals(localName)) {
      if (atom.elementSymbol != null && !Float.isNaN(atom.z)) {
        atomSetCollection.addAtomWithMappedName(atom);
      }
      atom = null;
      return;
    }
    keepChars = false;
    chars = null;
  }

  class XsdHandler extends JmolXmlHandler {

    XsdHandler() {
    }

    XsdHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }

}
