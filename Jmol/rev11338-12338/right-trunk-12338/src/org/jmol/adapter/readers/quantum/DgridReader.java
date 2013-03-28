
package org.jmol.adapter.readers.quantum;

import org.jmol.adapter.smarter.*;
import org.jmol.quantum.SlaterData;
import org.jmol.util.Logger;

import java.io.BufferedReader;
import java.util.Hashtable;


public class DgridReader extends SlaterReader {

  private String title;

  
  public void readAtomSetCollection(BufferedReader reader) {
    atomSetCollection = new AtomSetCollection("dgrid", this);
    this.reader = reader;
    modelNumber = 0;
    try {
      while (readLine() != null) {
        if (line.indexOf(":title") == 0) {
          readTitle();
          continue;
        }
        if (line.indexOf("basis:  CARTESIAN  STO") >= 0) {        
          readSlaterBasis(); 
          continue;
        }
        if (line.indexOf(":atom") == 0) {
          readCoordinates();
          continue;
        }
        if (line.indexOf(" MO  DATA ") >= 0) {
          readMolecularOrbitals();
          continue;
        }
      }
    } catch (Exception e) {
      setError(e);
    }

  }

  private void readTitle() throws Exception {
    title = readLine().substring(2);
  }

  
  private void readCoordinates() throws Exception {

    
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetName(title);
    discardLinesUntilContains("----");
    while (readLine() != null && !line.startsWith(":-----")) {
      String[] tokens = getTokens();
      if (tokens.length < 5)
        break;
      String symbol = tokens[0];
      Atom atom = atomSetCollection.addNewAtom();
      atom.elementSymbol = symbol;
      atom.set(parseFloat(tokens[2]), parseFloat(tokens[3]), parseFloat(tokens[4]));
      atom.scale(ANGSTROMS_PER_BOHR);
    }
  }

  Hashtable htExponents = new Hashtable();
  private void readSlaterBasis() throws Exception {
     
    discardLinesUntilContains(":-");
    char ch = 'a';    
    while (readLine() != null && line.indexOf(":-") < 0) {
      String atomSymbol = line.substring(3,6).trim();
      String xyz = line.substring(19, 21);
      String code = atomSymbol + xyz;
      if (htExponents.get(code) == null) {
        ch = 'a';
      } else {
        code += "_" + ch++;
      }
      String exp = line.substring(34);
      htExponents.put(code, new Float(parseFloat(exp)));
    }
  }

  private Hashtable htFuncMap;
  private void readMolecularOrbitals() throws Exception {
    
    htFuncMap = new Hashtable();
    discardLines(3);
    while (line != null && line.indexOf(":") != 0) {
      discardLinesUntilContains("sym: ");
      String symmetry = line.substring(4, 10).trim();
      if (symmetry.indexOf("_FC") >= 0)
        break;
      StringBuffer data = new StringBuffer();
      data.append(line.substring(10));
      while (readLine() != null && line.length() >= 10)
        data.append(line);
      String[] tokens = getTokens(data.toString());
      int nFuncs = tokens.length / 2;
      int[] ptSlater = new int[nFuncs];
      Atom[] atoms = atomSetCollection.getAtoms();
      for (int i = 0, pt = 0; i < tokens.length;) {
        int iAtom = parseInt(tokens[i++]) - 1;
        String code = tokens[i++];
        String key = iAtom + "_" + code;
        if (htFuncMap.containsKey(key)) {
          ptSlater[pt++] = ((Integer) htFuncMap.get(key)).intValue();
        } else {
          int n = slaters.size();
          ptSlater[pt++] = n;
          htFuncMap.put(key, new Integer(n));
          
          addSlater(createSlaterData(iAtom, atoms[iAtom].elementSymbol, code), n);
        }
      }
      discardLinesUntilContains(":-");
      readLine();
      while (line != null && line.length() >= 20) {
        int iOrb = parseInt(line.substring(0, 10));
        float energy = parseFloat(line.substring(10, 20));
        StringBuffer cData = new StringBuffer();
        cData.append(line.substring(20));
        while (readLine() != null && line.length() >= 10) {
          if (line.charAt(3) != ' ')
            break;
          cData.append(line);
        }
        float[] list = new float[slaters.size()];
        tokens = getTokens(cData.toString());
        if (tokens.length != nFuncs)
          Logger
              .error("DgridReader: number of coefficients does not equal number of functions");
        for (int i = 0; i < tokens.length; i++) {
          int pt = ptSlater[i];
          list[pt] = parseFloat(tokens[i]);
        }
        Hashtable mo = new Hashtable();
        orbitals.add(mo);
        mo.put("energy", new Float(energy));
        mo.put("coefficients", list);
        mo.put("symmetry", symmetry + "_" + iOrb);
        
      }
    }

    
    discardLinesUntilContains(":  #  symmetry");
    readLine();
    for (int i = 0; i < orbitals.size(); i++) {
      readLine();
      float occupancy = parseFloat(line.substring(31, 45)) + parseFloat(line.substring(47, 61));
      ((Hashtable) orbitals.get(i)).put("occupancy", new Float(occupancy));
    }
    sortOrbitals();
    
    setSlaters(true, true);
    setMOs("eV");
  }

  private SlaterData createSlaterData(int iAtom, String atomSymbol, String xyz) {
    char ch;
    char abc = ' ';
    char type = ' ';
    int exp = 1;
    int el = 0;
    int x = 0;
    int y = 0;
    int z = 0;
    for (int i = xyz.length(); --i >= 0;) {
      switch (ch = xyz.charAt(i)) {
      case '_':
        type = abc;
        break;
      case '1':
      case '2':
      case '3':
      case '4':
        exp = ch - '0';
        break;
      case 'x':
        x = exp;
        el += exp;
        exp = 1;
        break;
      case 'y':
        y = exp;
        el += exp;
        exp = 1;
        break;
      case 'z':
        z = exp;
        el += exp;
        exp = 1;
        break;
      case 's':
      case 'p':
      case 'd':
      case 'f':
      default:
        abc = ch;
      }
    }
    int r = (exp - el - 1);
    String code = atomSymbol + xyz.substring(0, 2);
    if (type != ' ')
      code += "_" + type;
    Float f = (Float) htExponents.get(code);
    float zeta = 0;
    if (f == null)
      Logger.error("Exponent for " + code + " not found");
    else
      zeta = f.floatValue();
    
        
    return new SlaterData(iAtom, x, y, z, r, zeta, 1);
  }
}
