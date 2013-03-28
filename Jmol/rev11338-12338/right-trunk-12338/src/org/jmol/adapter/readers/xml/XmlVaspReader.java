

package org.jmol.adapter.readers.xml;

import java.io.BufferedReader;
import java.util.HashMap;

import javax.vecmath.Vector3f;

import netscape.javascript.JSObject;

import org.jmol.adapter.smarter.*;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.xml.sax.XMLReader;



public class XmlVaspReader extends XmlReader {
  
  

  String[] vaspImplementedAttributes = { "name" };

  XmlVaspReader() {
  }
  
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new VaspHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    this.modelNumber = parent.modelNumber;
    implementedAttributes = vaspImplementedAttributes;
    
    ((VaspHandler) (new VaspHandler())).walkDOMTree(DOMNode);
  }

  StringBuffer data;
  String name;
  int atomCount;
  int iAtom;
  boolean modelRead = false;
  boolean readThisModel = true;
  
  public void processStartElement(String namespaceURI, String localName,
                                  String qName, HashMap atts) {
    if (Logger.debugging) 
      Logger.debug("xmlvasp: start " + localName);

    if ("structure".equals(localName)) {
      if (modelRead && parent.isLastModel(modelNumber))
        return;
      readThisModel = parent.doGetModel(++modelNumber);
      if (readThisModel) {
        modelRead = true;
        parent.setFractionalCoordinates(true);
        atomSetCollection.setDoFixPeriodic();
        atomSetCollection.newAtomSet();
      }
      return;
    }

    if (!readThisModel)
      return;
    
    if ("v".equals(localName)) {
      keepChars = (data != null);
      return;
    }

    if ("c".equals(localName)) {
      keepChars = (iAtom < atomCount);
      return;
    }

    if ("varray".equals(localName)) {
      name = (String) atts.get("name");
      if (name != null && Parser.isOneOf(name, "basis;positions;forces"))
        data = new StringBuffer();
      return;
    }

    if ("atoms".equals(localName)) {
      keepChars = true;
      return;
    }

  }

  boolean haveUnitCell = false;
  String[] atomNames;
  String[] atomSyms;
  String atomName;
  String atomSym;
  float a;
  float b;
  float c;
  float alpha;
  float beta;
  float gamma;
  
  public void processEndElement(String uri, String localName, String qName) {

    if (Logger.debugging) 
      Logger.debug("xmlvasp: end " + localName + " " + name);

    while (true) {

      if ("v".equals(localName) && data != null) {
        data.append(chars);
        break;
      }

      if ("c".equals(localName)) {
        if (iAtom < atomCount) {
          if (atomName == null) {
            atomName = atomSym = chars.trim();
          } else {
            atomNames[iAtom++] = atomName + chars.trim();
            atomName = null;
          }
        }
        break;
      }

      if ("atoms".equals(localName)) {
        atomCount = parseInt(chars);
        atomNames = new String[atomCount];
        atomSyms = new String[atomCount];
        iAtom = 0;
        break;
      }

      if ("varray".equals(localName) && data != null) {
        if (name == null) {
        } else if ("basis".equals(name) && !haveUnitCell) {
          haveUnitCell = true;
          float[] ijk = new float[9];
          getTokensFloat(data.toString(), ijk, 9);
          Vector3f va = new Vector3f(ijk[0], ijk[1], ijk[2]);
          Vector3f vb = new Vector3f(ijk[3], ijk[4], ijk[5]);
          Vector3f vc = new Vector3f(ijk[6], ijk[7], ijk[8]);
          a = va.length();
          b = vb.length();
          c = vc.length();
          va.normalize();
          vb.normalize();
          vc.normalize();
          alpha = (float) (Math.acos(vb.dot(vc)) * 180 / Math.PI);
          beta = (float) (Math.acos(va.dot(vc)) * 180 / Math.PI);
          gamma = (float) (Math.acos(va.dot(vb)) * 180 / Math.PI);
        } else if ("positions".equals(name)) {
          parent.setUnitCell(a, b, c, alpha, beta, gamma);
          float[] fdata = new float[atomCount * 3];
          getTokensFloat(data.toString(), fdata, atomCount * 3);
          int fpt = 0;
          for (int i = 0; i < atomCount; i++) {
            Atom atom = atomSetCollection.addNewAtom();
            atom.set(fdata[fpt++], fdata[fpt++], fdata[fpt++]);
            atom.elementSymbol = atomSyms[i];
            atom.atomName = atomNames[i];
            parent.setAtomCoord(atom);
          }
        } else if ("forces".equals(name)) {
          float[] fdata = new float[atomCount * 3];
          getTokensFloat(data.toString(), fdata, atomCount * 3);
          int fpt = 0;
          int i0 = atomSetCollection.getLastAtomSetAtomIndex();

          

          for (int i = 0; i < atomCount; i++)
            atomSetCollection.addVibrationVector(i0 + i, fdata[fpt++],
                fdata[fpt++], fdata[fpt++]);
        }
        data = null;
        break;
      }
      if ("structure".equals(localName)) {
        try {
          parent.applySymmetryAndSetTrajectory();
        } catch (Exception e) {
          
        }
        break;
      }
      
      return;
    }
    chars = null;
    keepChars = false;
  }

  class VaspHandler extends JmolXmlHandler {

    VaspHandler() {
    }

    VaspHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }

  

}
