
package org.jmol.adapter.readers.xml;

import org.jmol.adapter.smarter.*;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Hashtable;

import netscape.javascript.JSObject;

import org.jmol.util.Logger;
import org.xml.sax.*;



public class XmlChem3dReader extends XmlReader {

  

  String[] chem3dImplementedAttributes = { "id", 
      "symbol", "cartCoords", 
      "bondAtom1", "bondAtom2", "bondOrder", 
      "gridDatXDim", "gridDatYDim", "gridDatZDim",    
      "gridDatXSize", "gridDatYSize", "gridDatZSize",    
      "gridDatOrigin", "gridDatDat",   
      "calcPartialCharges", "calcAtoms" 
  };

  
  
  

  XmlChem3dReader() {
  }
  protected void processXml(XmlReader parent,
                           AtomSetCollection atomSetCollection,
                           BufferedReader reader, XMLReader xmlReader) {
    this.parent = parent;
    this.reader = reader;
    this.atomSetCollection = atomSetCollection;
    new Chem3dHandler(xmlReader);
    parseReaderXML(xmlReader);
  }

  protected void processXml(XmlReader parent,
                            AtomSetCollection atomSetCollection,
                            BufferedReader reader, JSObject DOMNode) {
    this.parent = parent;
    this.atomSetCollection = atomSetCollection;
    implementedAttributes = chem3dImplementedAttributes;
    ((Chem3dHandler) (new Chem3dHandler())).walkDOMTree(DOMNode);
  }

  public void processStartElement(String namespaceURI, String localName, String qName,
                           HashMap atts) {
    String[] tokens;
    
    if ("model".equals(localName)) {
      atomSetCollection.newAtomSet();
      return;
    }

    if ("atom".equals(localName)) {
      atom = new Atom();
      atom.atomName = (String) atts.get("id");
      atom.elementSymbol = (String) atts.get("symbol");
      if (atts.containsKey("cartCoords")) {
        String xyz = (String) atts.get("cartCoords");
        tokens = getTokens(xyz);
        atom.set(parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]));
      }
      return;
    }
    if ("bond".equals(localName)) {
      String atom1 = (String) atts.get("bondAtom1");
      String atom2 = (String) atts.get("bondAtom2");
      int order = 1;
      if (atts.containsKey("bondOrder"))
        order = parseInt((String) atts.get("bondOrder"));
      atomSetCollection.addNewBond(atom1, atom2, order);
      return;
    }

    if ("electronicStructureCalculation".equals(localName)) {
      tokens = getTokens((String) atts.get("calcPartialCharges"));
      String[] tokens2 = getTokens((String) atts.get("calcAtoms"));
      for (int i = parseInt(tokens[0]); --i >= 0;)
        atomSetCollection.mapPartialCharge(tokens2[i + 1],
            parseFloat(tokens[i + 1]));
    }

    if ("gridData".equals(localName)) {
      atomSetCollection.newVolumeData();
      int nPointsX = parseInt((String) atts.get("gridDatXDim"));
      int nPointsY = parseInt((String) atts.get("gridDatYDim"));
      int nPointsZ = parseInt((String) atts.get("gridDatZDim"));
      atomSetCollection.setVoxelCounts(nPointsX, nPointsY, nPointsZ);
      float xStep = parseFloat((String) atts.get("gridDatXSize"))
          / (nPointsX);
      float yStep = parseFloat((String) atts.get("gridDatYSize"))
          / (nPointsY);
      float zStep = parseFloat((String) atts.get("gridDatZSize"))
          / (nPointsZ);
      atomSetCollection.setVolumetricVector(0, xStep, 0, 0);
      atomSetCollection.setVolumetricVector(1, 0, yStep, 0);
      atomSetCollection.setVolumetricVector(2, 0, 0, zStep);

      tokens = getTokens((String) atts.get("gridDatOrigin"));
      atomSetCollection.setVolumetricOrigin(parseFloat(tokens[0]), parseFloat(tokens[1]), parseFloat(tokens[2]));
      
      tokens = getTokens((String) atts.get("gridDatData"));
      int nData = parseInt(tokens[0]);
      int pt = 1;
      float[][][] voxelData = new float[nPointsX][nPointsY][nPointsZ];
      
      
      

      
      
      for (int z = 0; z < nPointsZ; z++)
        for (int y = 0; y < nPointsY; y++)
          for (int x = 0; x < nPointsX; x++)
            voxelData[x][y][z] = parseFloat(tokens[pt++]);
      atomSetCollection.setVoxelData(voxelData);
      Hashtable surfaceInfo = new Hashtable();
      surfaceInfo.put("surfaceDataType", "mo");
      surfaceInfo.put("defaultCutoff", new Float(0.01));
      surfaceInfo.put("nCubeData", new Integer(nData));
      surfaceInfo.put("volumeData", atomSetCollection.getVolumeData());
      atomSetCollection.setAtomSetAuxiliaryInfo("jmolSurfaceInfo", surfaceInfo);
      Logger.debug("Chem3D molecular orbital data displayable using:  isosurface sign \"\" ");
      return;
    }
  }

  public void processEndElement(String uri, String localName, String qName) {
    
    if ("atom".equals(localName)) {
      if (atom.elementSymbol != null && !Float.isNaN(atom.z)) {
        atomSetCollection.addAtomWithMappedName(atom);
      }
      atom = null;
      return;
    }
    keepChars = false;
    chars = null;
  }

  class Chem3dHandler extends JmolXmlHandler {

    Chem3dHandler() {
    }

    Chem3dHandler(XMLReader xmlReader) {
      setHandler(xmlReader, this);
    }
  }
}
