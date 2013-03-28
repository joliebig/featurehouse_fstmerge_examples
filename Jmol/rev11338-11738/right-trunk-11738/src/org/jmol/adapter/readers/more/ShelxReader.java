
package org.jmol.adapter.readers.more;

import org.jmol.adapter.smarter.*;


import java.io.BufferedReader;

import org.jmol.util.ArrayUtil;



public class ShelxReader extends AtomSetCollectionReader {

  String[] sfacElementSymbols;
  boolean isCmdf = false;
  boolean iHaveAtomSet = false;

 public void readAtomSetCollection(BufferedReader reader) {
    this.reader = reader;
    atomSetCollection = new AtomSetCollection("shelx", this);
    try {
      setFractionalCoordinates(true);
      int lineLength;
      boolean modelRead = false;
      do {
        if (modelRead && isLastModel(modelNumber))
          break;
        boolean readThisModel = doGetModel(++modelNumber);
        if (readThisModel) {
          modelRead = true;
          sfacElementSymbols = null;
          applySymmetryAndSetTrajectory();
          setFractionalCoordinates(true);
          isCmdf = false;
          iHaveAtomSet = false;
        }
        readLine_loop: while (readLine() != null) {
          lineLength = line.trim().length();
          
          while (lineLength > 0 && line.charAt(lineLength - 1) == '=')
            line = line.substring(0, lineLength - 1) + readLine();
          if (lineLength >= 3) {
            String command = (line + " ").substring(0, 4).toUpperCase().trim();
            if (command.equals("END")) {
              break;
            } else if (line.equals("NOTE")) {
              isCmdf = true;
              atomSetCollection.setFileTypeName("cmdf");
              continue;
            }
            if (readThisModel && isCmdf && line.equals("ATOM")) {
              processCmdfAtoms();
              break;
            }
            for (int i = unsupportedRecordTypes.length; --i >= 0;)
              if (command.equals(unsupportedRecordTypes[i]))
                continue readLine_loop;
            for (int i = supportedRecordTypes.length; --i >= 0;)
              if (command.equals(supportedRecordTypes[i])) {
                if (readThisModel)
                  processSupportedRecord(i);
                continue readLine_loop;
              }
            if (readThisModel && !isCmdf && iHaveAtomSet)
              assumeAtomRecord();
          }
        }
      } while (readLine() != null);
      applySymmetryAndSetTrajectory();
    } catch (Exception e) {
      setError(e);
    }

  }

  final static String[] supportedRecordTypes = { "TITL", "CELL", "SPGR",
      "SFAC", "LATT", "SYMM" };

  void processSupportedRecord(int recordIndex) throws Exception {
    
    if (!iHaveAtomSet)
      atomSetCollection.newAtomSet();
    iHaveAtomSet = true;
    switch (recordIndex) {
    case 0: 
      atomSetCollection.setAtomSetName(parseTrimmed(line, 4));
      break;
    case 1: 
      cell();
      setSymmetryOperator("x,y,z");
      break;
    case 2: 
      setSpaceGroupName(parseTrimmed(line, 4));
      break;
    case 3: 
      parseSfacRecord();
      break;
    case 4: 
      parseLattRecord();
      break;
    case 5: 
      parseSymmRecord();
      break;
    }
  }

  void parseLattRecord() throws Exception {
    parseToken(line);
    int latt = parseInt();
    atomSetCollection.setLatticeParameter(latt);
  }

  void parseSymmRecord() throws Exception {
    setSymmetryOperator(parseTrimmed(line, 4));
  }

  void cell() throws Exception {
    

    String[] tokens = getTokens();
    int ioff = 1;
    if (isCmdf) {
      ioff = 0;
    } else {
      float wavelength = parseFloat(tokens[0]);
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("wavelength",
          new Float(wavelength));
    }
    for (int ipt = 0; ipt < 6; ipt++)
      setUnitCellItem(ipt, parseFloat(tokens[ipt + ioff + 1]));
  }

  void parseSfacRecord() {
    
    
    
    String[] sfacTokens = getTokens(line, 4);
    boolean allElementSymbols = true;
    for (int i = sfacTokens.length; allElementSymbols && --i >= 0;) {
      String token = sfacTokens[i];
      allElementSymbols = Atom.isValidElementSymbolNoCaseSecondChar(token);
    }
    if (allElementSymbols)
      parseSfacElementSymbols(sfacTokens);
    else
      parseSfacCoefficients(sfacTokens);
  }

  void parseSfacElementSymbols(String[] sfacTokens) {
    if (sfacElementSymbols == null) {
      sfacElementSymbols = sfacTokens;
    } else {
      int oldCount = sfacElementSymbols.length;
      int tokenCount = sfacTokens.length;
      sfacElementSymbols = ArrayUtil.setLength(sfacElementSymbols, oldCount + tokenCount);
      for (int i = tokenCount; --i >= 0;)
        sfacElementSymbols[oldCount + i] = sfacTokens[i];
    }
  }
  
  void parseSfacCoefficients(String[] sfacTokens) {
    float a1 = parseFloat(sfacTokens[1]);
    float a2 = parseFloat(sfacTokens[3]);
    float a3 = parseFloat(sfacTokens[5]);
    float a4 = parseFloat(sfacTokens[7]);
    float c = parseFloat(sfacTokens[9]);
    
    int z = (int) (a1 + a2 + a3 + a4 + c + 0.5f);
    String elementSymbol = getElementSymbol(z);
    int oldCount = 0;
    if (sfacElementSymbols == null) {
      sfacElementSymbols = new String[1];
    } else {
      oldCount = sfacElementSymbols.length;
      sfacElementSymbols = ArrayUtil.setLength(sfacElementSymbols, oldCount + 1);
      sfacElementSymbols[oldCount] = elementSymbol;
    }
    sfacElementSymbols[oldCount] = elementSymbol;
  }

  void assumeAtomRecord() throws Exception {
    
    
    String atomName = parseToken(line);
    int scatterFactor = parseInt();
    float a = parseFloat();
    float b = parseFloat();
    float c = parseFloat();
    

    Atom atom = atomSetCollection.addNewAtom();
    atom.atomName = atomName;
    if (sfacElementSymbols != null) {
      int elementIndex = scatterFactor - 1;
      if (elementIndex >= 0 && elementIndex < sfacElementSymbols.length)
        atom.elementSymbol = sfacElementSymbols[elementIndex];
    }
    setAtomCoord(atom, a, b, c);
  }

  final static String[] unsupportedRecordTypes = {
  
  "ZERR", "DISP", "UNIT", "LAUE", "REM", "MORE", "TIME",
  
  "HKLF", "OMIT", "SHEL", "BASF", "TWIN", "EXTI", "SWAT", "HOPE", "MERG",
  
  "SPEC", "RESI", "MOVE", "ANIS", "AFIX", "HFIX", "FRAG", "FEND", "EXYZ",
      "EXTI", "EADP", "EQIV",
      
      "CONN", "PART", "BIND", "FREE",
      
      "DFIX", "DANG", "BUMP", "SAME", "SADI", "CHIV", "FLAT", "DELU", "SIMU",
      "DEFS", "ISOR", "NCSY", "SUMP",
      
      "L.S.", "CGLS", "BLOC", "DAMP", "STIR", "WGHT", "FVAR",
      
      "BOND", "CONF", "MPLA", "RTAB", "HTAB", "LIST", "ACTA", "SIZE", "TEMP",
      "WPDB",
      
      "FMAP", "GRID", "PLAN", "MOLE"
      };

  void processCmdfAtoms() throws Exception {
    while (readLine() != null && line.length() > 10) {
      Atom atom = atomSetCollection.addNewAtom();
      String[] tokens = getTokens();
      atom.elementSymbol = getSymbol(tokens[0]);
      setAtomCoord(atom, parseFloat(tokens[2]), parseFloat(tokens[3]),
          parseFloat(tokens[4]));
    }
  }

  String getSymbol(String sym) {
    if (sym == null)
      return "Xx";
    int len = sym.length();
    if (len < 2)
      return sym;
    char ch1 = sym.charAt(1);
    if (ch1 >= 'a' && ch1 <= 'z')
      return sym.substring(0, 2);
    return "" + sym.charAt(0);
  }

}
