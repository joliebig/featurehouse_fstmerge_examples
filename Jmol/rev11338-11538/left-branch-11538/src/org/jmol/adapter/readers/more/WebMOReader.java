

package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;


public class WebMOReader extends MopacDataReader {

 public void readAtomSetCollection(BufferedReader reader)  {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("webmo");
    modelNumber = 0;
    try {
      readLine();
      while (line != null) {
        if (line.equals("[HEADER]")) {
          readHeader();
          continue;
        } else if (line.equals("[ATOMS]")) {
          readAtoms();
          
          continue;
        } else if (line.equals("[BONDS]")) {
          readBonds();
          continue;
        } else if (line.equals("[AO_ORDER]")) {
          readAtomicOrbitalOrder();
          continue;
        } else if (line.equals("[GTO]")) {
          readGaussianBasis();
          continue;
        } else if (line.equals("[STO]")) {
          readSlaterBasis();
          continue;
        } else if (line.indexOf("[MO") == 0) {
          if (doGetModel(++modelNumber)) {
            readMolecularOrbital();
            if (isLastModel(modelNumber))
              break;
          }
          continue;
        }
        readLine();
      }
      if (Logger.debugging)
        Logger.debug(orbitals.size() + " molecular orbitals read");
    } catch (Exception e) {
      setError(e);
    }
  }

  void readHeader() throws Exception {
    while (readLine() != null && line.length() > 0) {
      moData.put("calculationType", "?");
      String[] tokens = getTokens();
      tokens[0] = tokens[0].substring(0, 1).toLowerCase()
          + tokens[0].substring(1, tokens[0].length());
      String str = "";
      for (int i = 1; i < tokens.length; i++)
        str += (i == 1 ? "" : " ") + tokens[i].toLowerCase();
      moData.put(tokens[0], str);
    }
  }

  void readAtoms() throws Exception {
    

    readLine();
    boolean isAtomicNumber = (parseInt(line) != Integer.MIN_VALUE);
    while (line != null && (line.length() == 0 || line.charAt(0) != '[')) {
      if (line.length() != 0) {
        Atom atom = atomSetCollection.addNewAtom();
        String[] tokens = getTokens();
        if (isAtomicNumber) {
          atom.elementSymbol = getElementSymbol(parseInt(tokens[0]));
        } else {
          atom.elementSymbol = tokens[0];
        }
        atom.set(parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
        atom.scale(ANGSTROMS_PER_BOHR);
      }
      readLine();
    }
  }

  void readBonds() throws Exception {
    

    while (readLine() != null
        && (line.length() == 0 || line.charAt(0) != '[')) {
      if (line.length() == 0)
        continue;
      String[] tokens = getTokens();
      int atomIndex1 = parseInt(tokens[0]);
      int atomIndex2 = parseInt(tokens[1]);
      int order = parseInt(tokens[2]);
      atomSetCollection
          .addBond(new Bond(atomIndex1 - 1, atomIndex2 - 1, order));
    }
  }

  void readAtomicOrbitalOrder() throws Exception {
    
    Hashtable info = new Hashtable();
    while (readLine() != null
        && (line.length() == 0 || line.charAt(0) != '[')) {
      if (line.length() == 0)
        continue;
      String[] tokens = getTokens();
      info.put(tokens[0].substring(0, 1), tokens);
    }
    moData.put("atomicOrbitalOrder", info);
  }

  void readGaussianBasis() throws Exception {
    

    Vector sdata = new Vector();
    Vector gdata = new Vector();
    int atomIndex = 0;
    int gaussianPtr = 0;

    while (readLine() != null
        && (line.length() == 0 || line.charAt(0) != '[')) {
      String[] tokens = getTokens();
      if (tokens.length == 0)
        continue;
      if (tokens.length != 1) 
        throw new Exception("Error reading GTOs: missing atom index");
      int[] slater = new int[4];
      atomIndex = parseInt(tokens[0]) - 1;
      tokens = getTokens(readLine());
      int nGaussians = parseInt(tokens[1]);
      slater[0] = atomIndex;
      slater[1] = JmolAdapter.getQuantumShellTagID(tokens[0]);
      slater[2] = gaussianPtr;
      slater[3] = nGaussians;
      for (int i = 0; i < nGaussians; i++) {
        String[] strData = getTokens(readLine());
        int nData = strData.length;
        float[] data = new float[nData];
        for (int d = 0; d < nData; d++)
          data[d] = parseFloat(strData[d]);
        gdata.addElement(data);
        gaussianPtr++;
      }
      sdata.addElement(slater);
    }
    float[][] garray = new float[gaussianPtr][];
    for (int i = 0; i < gaussianPtr; i++)
      garray[i]=(float[])gdata.get(i);
    moData.put("shells", sdata);
    moData.put("gaussians", garray);
    if (Logger.debugging) {
      Logger.debug(sdata.size() + " slater shells read");
      Logger.debug(garray.length + " gaussian primitives read");
    }
    atomSetCollection.setAtomSetAuxiliaryInfo("moData", moData);
  }

  void readSlaterBasis() throws Exception {
    
    while (readLine() != null && (line.length() == 0 || line.charAt(0) != '[')) {
      String[] tokens = getTokens();
      if (tokens.length >= 7)
        addSlater(parseInt(tokens[0]) - 1, parseInt(tokens[1]),
            parseInt(tokens[2]), parseInt(tokens[3]), parseInt(tokens[4]),
            parseFloat(tokens[5]), parseFloat(tokens[6]));
    }
    setSlaters();
  }

  void readMolecularOrbital() throws Exception {
    
    Hashtable mo = new Hashtable();
    Vector data = new Vector();
    float energy = parseFloat(readLine());
    float occupancy = parseFloat(readLine());
    while (readLine() != null
        && (line.length() == 0 || line.charAt(0) != '[')) {
      if (line.length() == 0)
        continue;
      String[] tokens = getTokens();
      data.addElement(tokens[1]);
    }
    float[] coefs = new float[data.size()];
    for (int i = data.size(); --i >= 0;)
      coefs[i] = parseFloat((String) data.get(i));
    mo.put("energy", new Float(energy));
    mo.put("occupancy", new Float(occupancy));
    mo.put("coefficients", coefs);
    orbitals.addElement(mo);
    setMOs("eV");
  }
}
