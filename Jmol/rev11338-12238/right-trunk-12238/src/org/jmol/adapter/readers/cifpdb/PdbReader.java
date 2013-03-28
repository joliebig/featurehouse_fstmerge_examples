

package org.jmol.adapter.readers.cifpdb;

import org.jmol.adapter.smarter.*;


import org.jmol.api.JmolAdapter;
import org.jmol.util.Logger;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;



public class PdbReader extends AtomSetCollectionReader {
  private int lineLength;
  
  
  private final Hashtable htFormul = new Hashtable();
  private Hashtable htHetero = null;
  private Hashtable htSites = null;
  protected String fileType = "pdb";  
  private String currentGroup3;
  private String compnd;
  private Hashtable htElementsInCurrentGroup;
  private int maxSerial;
  private int[] chainAtomCounts;
  private int nUNK;
  private int nRes;
  
 final private static String lineOptions = 
   "ATOM    " + 
   "HETATM  " + 
   "MODEL   " + 
   "CONECT  " + 
   "HELIX   " + 
   "SHEET   " +
   "TURN    " +
   "HET     " + 
   "HETNAM  " + 
   "ANISOU  " + 
   "SITE    " + 
   "CRYST1  " + 
   "SCALE1  " + 
   "SCALE2  " +
   "SCALE3  " +
   "EXPDTA  " + 
   "FORMUL  " + 
   "REMARK  " + 
   "HEADER  " + 
   "COMPND  ";  

 public void readAtomSetCollection(BufferedReader reader) {
    
    this.reader = reader;
    atomSetCollection = new AtomSetCollection(fileType, this);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isPDB", Boolean.TRUE);
    setFractionalCoordinates(false);
    htFormul.clear();
    currentGroup3 = null;
    boolean iHaveModel = false;
    boolean iHaveModelStatement = false;
    boolean iHaveLine = false;
    StringBuffer pdbHeader = (getHeader ? new StringBuffer() : null);
    try {
      while (iHaveLine || readLine() != null) {
        iHaveLine = false;
        int ptOption = ((lineLength = line.length()) < 6 ? -1 : lineOptions
            .indexOf(line.substring(0, 6))) >> 3;
        boolean isAtom = (ptOption == 0 || ptOption == 1);
        boolean isModel = (ptOption == 2);
        if (getHeader) {
          if (isAtom || isModel)
            getHeader = false;
          else
            pdbHeader.append(line).append('\n');
        }
        if (isModel) {
          getHeader = false;
          iHaveModelStatement = true;
          
          int modelNo = getModelNumber();
          modelNumber = (bsModels == null ? modelNo : modelNumber + 1);
          if (!doGetModel(modelNumber)) {
            if (isLastModel(modelNumber) && iHaveModel)
              break;
            iHaveModel = false;
            continue;
          }
          iHaveModel = true;
          atomSetCollection.connectAll(maxSerial);
          applySymmetryAndSetTrajectory();
          
          model(modelNo);
          continue;
        }
        
        if (iHaveModelStatement && !iHaveModel)
          continue;
        if (isAtom) {
          getHeader = false;
          atom();
          continue;
        }
        switch (ptOption) {
        case 3:
          
          conect();
          continue;
        case 4:
        case 5:
        case 6:
          
          
          structure();
          continue;
        case 7:
          
          het();
          continue;
        case 8:
          
          hetnam();
          continue;
        case 9:
          
          anisou();
          continue;
        case 10:
          
          site();
          continue;
        case 11:
          
          cryst1();
          continue;
        case 12:
        case 13:
        case 14:
          
          
          
          scale(ptOption - 11);
          continue;
        case 15:
          
          expdta();
          continue;
        case 16:
          
          formul();
          continue;
        case 17:
          
          if (line.startsWith("REMARK 350")) {
            remark350();
            iHaveLine = true;
            continue;
          }
          if (line.startsWith("REMARK 290")) {
            remark290();
            iHaveLine = true;
            continue;
          }
          checkLineForScript();
          continue;
        case 18:
          header();
          continue;
        case 19:
          
          compnd();
          continue;
        }
      }
      checkNotPDB();
      atomSetCollection.connectAll(maxSerial);
      if (biomolecules != null && biomolecules.size() > 0 && atomSetCollection.getAtomCount() > 0) {
        atomSetCollection.setAtomSetAuxiliaryInfo("biomolecules", biomolecules);
        setBiomoleculeAtomCounts();
        if (biomts != null && filter != null
            && filter.toUpperCase().indexOf("NOSYMMETRY") < 0) {
          atomSetCollection.applySymmetry(biomts, applySymmetryToBonds, filter);
        }

      }
      applySymmetryAndSetTrajectory();
      if (htSites != null)
        addSites(htSites);
      if (pdbHeader != null)
        atomSetCollection.setAtomSetCollectionAuxiliaryInfo("fileHeader",
            pdbHeader.toString());
    } catch (Exception e) {
      setError(e);
    }
  }

  public void applySymmetryAndSetTrajectory() throws Exception {
    
    
    
    
    atomSetCollection.setCheckSpecial(false);
    super.applySymmetryAndSetTrajectory();
  }

  private void header() {
    if (lineLength < 8)
      return;
    if (lineLength >= 66)
      atomSetCollection.setCollectionName(line.substring(62, 66));
    if (lineLength > 50)
      line = line.substring(0, 50);
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("CLASSIFICATION", line.substring(7).trim());
  }

  private void compnd() {
    if (compnd == null)
      compnd = "";
    else
      compnd += " ";
    if (lineLength > 62)
      line = line.substring(0, 62);
    compnd += line.substring(10).trim();
    atomSetCollection.setAtomSetCollectionAuxiliaryInfo("COMPND", compnd);
  }

  private void setBiomoleculeAtomCounts() {
    for (int i = biomolecules.size(); --i >= 0;) {
      Hashtable biomolecule = (Hashtable) (biomolecules.elementAt(i));
      String chain = (String) biomolecule.get("chains");
      int nTransforms = ((Vector) biomolecule.get("biomts")).size();
      int nAtoms = 0;
      for (int j = chain.length() - 1; --j >= 0;)
        if (chain.charAt(j) == ':')
          nAtoms += chainAtomCounts[chain.charAt(j + 1)];
      biomolecule.put("atomCount", new Integer(nAtoms * nTransforms));
    }
  }


 
  private Vector biomolecules;
  private Vector biomts;
  
  private void remark350() throws Exception {
    Vector biomts = null;
    biomolecules = new Vector();
    chainAtomCounts = new int[255];
    String title = "";
    String chainlist = "";
    int iMolecule = 0;
    boolean needLine = true;
    Hashtable info = null;
    int nBiomt = 0;
    while (true) {
      if (needLine)
        readLine();
      else
        needLine = true;
      if (line == null || !line.startsWith("REMARK 350"))
        break;
      try {
        if (line.startsWith("REMARK 350 BIOMOLECULE:")) {
          if (nBiomt > 0)
            Logger.info("biomolecule " + iMolecule + ": number of transforms: "
                + nBiomt);
          info = new Hashtable();
          biomts = new Vector();
          iMolecule = parseInt(line.substring(line.indexOf(":") + 1));
          title = line.trim();
          info.put("molecule", new Integer(iMolecule));
          info.put("title", title);
          info.put("chains", "");
          info.put("biomts", biomts);
          biomolecules.add(info);
          nBiomt = 0;
          
        }
        if (line.indexOf("APPLY THE FOLLOWING TO CHAINS:") >= 0) {
          if (info == null) {
            
            
            needLine = false;
            line = "REMARK 350 BIOMOLECULE: 1  APPLY THE FOLLOWING TO CHAINS:";
            continue;
          }
          chainlist = ":" + line.substring(41).trim().replace(' ', ':');
          needLine = false;
          while (readLine() != null && line.indexOf("BIOMT") < 0)
            chainlist += ":" + line.substring(11).trim().replace(' ', ':');
          if (filter != null
              && filter.toUpperCase().indexOf("BIOMOLECULE " + iMolecule + ";") >= 0) {
            filter += chainlist;
            Logger.info("filter set to \"" + filter + "\"");
            this.biomts = biomts;
          }
          if (info == null)
            return; 
          info.put("chains", chainlist);
          continue;
        }
        
        if (line.startsWith("REMARK 350   BIOMT1 ")) {
          nBiomt++;
          float[] mat = new float[16];
          for (int i = 0; i < 12;) {
            String[] tokens = getTokens();
            mat[i++] = parseFloat(tokens[4]);
            mat[i++] = parseFloat(tokens[5]);
            mat[i++] = parseFloat(tokens[6]);
            mat[i++] = parseFloat(tokens[7]);
            if (i == 4 || i == 8)
              readLine();
          }
          mat[15] = 1;
          biomts.add(mat);
          continue;
        }
      } catch (Exception e) {
        
        this.biomts = null;
        this.biomolecules = null;
        return;
      }
    }
    if (nBiomt > 0)
      Logger.info("biomolecule " + iMolecule + ": number of transforms: "
          + nBiomt);
  }

  
  private void remark290() throws Exception {
    while (readLine() != null && line.startsWith("REMARK 290")) {
      if (line.indexOf("NNNMMM   OPERATOR") >= 0) {
        while (readLine() != null) {
          String[] tokens = getTokens();
          if (tokens.length < 4)
            break;
          setSymmetryOperator(tokens[3]);
        }
      }
    }
  }

  private int atomCount;
  private String lastAtomData;
  private int lastAtomIndex;
  
  private void atom() {
    boolean isHetero = line.startsWith("HETATM");
    char charAlternateLocation = line.charAt(16);

    
    int serial = parseInt(line, 6, 11);
    if (serial > maxSerial)
      maxSerial = serial;
    char chainID = line.charAt(21);
    if (chainAtomCounts != null)
      chainAtomCounts[chainID]++;
    int sequenceNumber = parseInt(line, 22, 26);
    char insertionCode = line.charAt(26);
    String group3 = parseToken(line, 17, 20);
    if (group3 == null) {
      currentGroup3 = null;
      htElementsInCurrentGroup = null;
    } else if (!group3.equals(currentGroup3)) {
      currentGroup3 = group3;
      htElementsInCurrentGroup = (Hashtable) htFormul.get(group3);
      nRes++;
      if (group3.equals("UNK"))
        nUNK++;
    }

    
    
    String elementSymbol = deduceElementSymbol(isHetero);

    
    String rawAtomName = line.substring(12, 16);
    
    
    
    String atomName = rawAtomName.trim();
    
    int charge = 0;
    if (lineLength >= 80) {
      char chMagnitude = line.charAt(78);
      char chSign = line.charAt(79);
      if (chSign >= '0' && chSign <= '7') {
        char chT = chSign;
        chSign = chMagnitude;
        chMagnitude = chT;
      }
      if ((chSign == '+' || chSign == '-' || chSign == ' ')
          && chMagnitude >= '0' && chMagnitude <= '7') {
        charge = chMagnitude - '0';
        if (chSign == '-')
          charge = -charge;
      }
    }

    float bfactor = readBFactor();
    int occupancy = readOccupancy();
    float partialCharge = readPartialCharge();
    float radius = readRadius();
    
    
    float x = parseFloat(line, 30, 38);
    float y = parseFloat(line, 38, 46);
    float z = parseFloat(line, 46, 54);
    
    Atom atom = new Atom();
    atom.atomName = atomName;
    atom.chainID = chainID;
    atom.group3 = currentGroup3;
    if (filter != null)
      if (!filterAtom(atom))
        return;
    atom.elementSymbol = elementSymbol;
    if (charAlternateLocation != ' ')
      atom.alternateLocationID = charAlternateLocation;
    atom.formalCharge = charge;
    if (partialCharge != Float.MAX_VALUE)
      atom.partialCharge = partialCharge;
    atom.occupancy = occupancy;
    atom.bfactor = bfactor;
    setAtomCoord(atom, x, y, z);
    atom.isHetero = isHetero;
    atom.atomSerial = serial;
    atom.sequenceNumber = sequenceNumber;
    atom.insertionCode = JmolAdapter.canonizeInsertionCode(insertionCode);
    atom.radius = radius;
    lastAtomData = line.substring(6, 26);
    lastAtomIndex = atomSetCollection.getAtomCount();
    if (haveMappedSerials)
      atomSetCollection.addAtomWithMappedSerialNumber(atom);
    else
      atomSetCollection.addAtom(atom);
    if (atomCount++ == 0)
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    
    if (isHetero) {
      if (htHetero != null) {
        atomSetCollection.setAtomSetAuxiliaryInfo("hetNames", htHetero);
        htHetero = null;
      }
    }
  }

  protected int readOccupancy() {

    
    int occupancy = 100;
    float floatOccupancy = parseFloat(line, 54, 60);
    if (!Float.isNaN(floatOccupancy))
      occupancy = (int) (floatOccupancy * 100);
    return occupancy;
  }
  
  protected float readBFactor() {
    
    return parseFloat(line, 60, 66);
  }
  
  protected float readPartialCharge() {
    return Float.MAX_VALUE; 
  }
  
  protected float readRadius() {
    return Float.NaN; 
  }
  
  private String deduceElementSymbol(boolean isHetero) {
    if (lineLength >= 78) {
      char ch76 = line.charAt(76);
      char ch77 = line.charAt(77);
      if (ch76 == ' ' && Atom.isValidElementSymbol(ch77))
        return "" + ch77;
      if (Atom.isValidElementSymbolNoCaseSecondChar(ch76, ch77))
        return "" + ch76 + ch77;
    }
    char ch12 = line.charAt(12);
    char ch13 = line.charAt(13);
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get(line.substring(12, 14)) != null) &&
        Atom.isValidElementSymbolNoCaseSecondChar(ch12, ch13))
      return (isHetero || ch12 != 'H' ? "" + ch12 + ch13 : "H");
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get("" + ch13) != null) &&
        Atom.isValidElementSymbol(ch13))
      return "" + ch13;
    if ((htElementsInCurrentGroup == null ||
         htElementsInCurrentGroup.get("" + ch12) != null) &&
        Atom.isValidElementSymbol(ch12))
      return "" + ch12;
    return "Xx";
  }

  private void conect() {
    int sourceSerial = -1;
    sourceSerial = parseInt(line, 6, 11);
    if (sourceSerial < 0)
      return;
    for (int i = 0; i < 9; i += (i == 5 ? 2 : 1)) {
      int offset = i * 5 + 11;
      int offsetEnd = offset + 5;
      int targetSerial = (offsetEnd <= lineLength ? parseInt(line, offset,
          offsetEnd) : -1);
      if (targetSerial < sourceSerial)
        continue;
      atomSetCollection.addConnection(new int[] { sourceSerial, targetSerial,
          i < 4 ? 1 : JmolAdapter.ORDER_HBOND });
    }
  }

  
  private void structure() {
    String structureType = "none";
    int startChainIDIndex;
    int startIndex;
    int endChainIDIndex;
    int endIndex;
    int strandCount = 0;
    if (line.startsWith("HELIX ")) {
      structureType = "helix";
      startChainIDIndex = 19;
      startIndex = 21;
      endChainIDIndex = 31;
      endIndex = 33;
    } else if (line.startsWith("SHEET ")) {
      structureType = "sheet";
      startChainIDIndex = 21;
      startIndex = 22;
      endChainIDIndex = 32;
      endIndex = 33;
      strandCount = parseInt(line.substring(14, 16));
    } else if (line.startsWith("TURN  ")) {
      structureType = "turn";
      startChainIDIndex = 19;
      startIndex = 20;
      endChainIDIndex = 30;
      endIndex = 31;
    } else
      return;

    if (lineLength < endIndex + 4)
      return;

    String structureID = line.substring(11,15).trim();
    int serialID = parseInt(line.substring(7,10));
    char startChainID = line.charAt(startChainIDIndex);
    int startSequenceNumber = parseInt(line, startIndex, startIndex + 4);
    char startInsertionCode = line.charAt(startIndex + 4);
    char endChainID = line.charAt(endChainIDIndex);
    int endSequenceNumber = parseInt(line, endIndex, endIndex + 4);
    
    char endInsertionCode = ' ';
    if (lineLength > endIndex + 4)
      endInsertionCode = line.charAt(endIndex + 4);

    
    
    
    Structure structure = new Structure(-1, structureType, structureID, serialID, 
                                        strandCount, startChainID, startSequenceNumber,
                                        startInsertionCode, endChainID,
                                        endSequenceNumber, endInsertionCode);
    atomSetCollection.addStructure(structure);
  }

  private int getModelNumber() {
    try {
      int startModelColumn = 6; 
      int endModelColumn = 14;
      if (endModelColumn > lineLength)
        endModelColumn = lineLength;
      return parseInt(line, startModelColumn, endModelColumn);
    } catch (NumberFormatException e) {
      return 0;
    }
  }
  
  private void model(int modelNumber) {
    
    checkNotPDB();
    haveMappedSerials = false;
    atomSetCollection.newAtomSet();
    atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.TRUE);
    atomSetCollection.setAtomSetNumber(modelNumber);
  }

  private void checkNotPDB() {
    if (atomSetCollection.getAtomCount() > 0 && nUNK == nRes)
      atomSetCollection.setAtomSetAuxiliaryInfo("isPDB", Boolean.FALSE);
    nUNK = nRes = 0;
  }

  private void cryst1() throws Exception {
    setUnitCell(getFloat(6, 9), getFloat(15, 9), getFloat(24, 9), getFloat(33,
        7), getFloat(40, 7), getFloat(47, 7));
    setSpaceGroupName(parseTrimmed(line, 55, 66));
  }

  private float getFloat(int ich, int cch) throws Exception {
    return parseFloat(line, ich, ich+cch);
  }

  private void scale(int n) throws Exception {
    int pt = n * 4 + 2;
    setUnitCellItem(pt++,getFloat(10, 10));
    setUnitCellItem(pt++,getFloat(20, 10));
    setUnitCellItem(pt++,getFloat(30, 10));
    setUnitCellItem(pt++,getFloat(45, 10));
  }

  private void expdta() {
    if (line.toUpperCase().indexOf("NMR") >= 0)
      atomSetCollection.setAtomSetCollectionAuxiliaryInfo("isNMRdata", "true");
  }

  private void formul() {
    String groupName = parseToken(line, 12, 15);
    String formula = parseTrimmed(line, 19, 70);
    int ichLeftParen = formula.indexOf('(');
    if (ichLeftParen >= 0) {
      int ichRightParen = formula.indexOf(')');
      if (ichRightParen < 0 || ichLeftParen >= ichRightParen ||
          ichLeftParen + 1 == ichRightParen ) 
        return; 
      formula = parseTrimmed(formula, ichLeftParen + 1, ichRightParen);
    }
    Hashtable htElementsInGroup = (Hashtable)htFormul.get(groupName);
    if (htElementsInGroup == null)
      htFormul.put(groupName, htElementsInGroup = new Hashtable());
    
    next[0] = 0;
    String elementWithCount;
    while ((elementWithCount = parseTokenNext(formula)) != null) {
      if (elementWithCount.length() < 2)
        continue;
      char chFirst = elementWithCount.charAt(0);
      char chSecond = elementWithCount.charAt(1);
      if (Atom.isValidElementSymbolNoCaseSecondChar(chFirst, chSecond))
        htElementsInGroup.put("" + chFirst + chSecond, Boolean.TRUE);
      else if (Atom.isValidElementSymbol(chFirst))
        htElementsInGroup.put("" + chFirst, Boolean.TRUE);
    }
  }
  
  private void het() {
    if (line.length() < 30)
      return;
    if (htHetero == null)
      htHetero = new Hashtable();
    String groupName = parseToken(line, 7, 10);
    if (htHetero.contains(groupName))
      return;
    String hetName = parseTrimmed(line, 30, 70);
    htHetero.put(groupName, hetName);
  }
  
  private void hetnam() {
    if (htHetero == null)
      htHetero = new Hashtable();
    String groupName = parseToken(line, 11, 14);
    String hetName = parseTrimmed(line, 15, 70);
    if (groupName == null) {
      System.out.println("ERROR: HETNAM record does not contain a group name: " + line);
      return;
    }
    String htName = (String) htHetero.get(groupName);
    if (htName != null)
      hetName = htName + hetName;
    htHetero.put(groupName, hetName);
    
  }
  
  
  private boolean  haveMappedSerials;
  
  private void anisou() {
    float[] data = new float[8];
    data[6] = 1; 
    int serial = parseInt(line, 6, 11);
    int index;
    if (line.substring(6, 26).equals(lastAtomData)) {
      index = lastAtomIndex;
    } else {
      if (!haveMappedSerials)
        atomSetCollection.createAtomSerialMap();
      index = atomSetCollection.getAtomSerialNumberIndex(serial);
      haveMappedSerials = true;
    }
    if (index < 0) {
      
      
      return;
    }
    Atom atom = atomSetCollection.getAtom(index);
    for (int i = 28, pt = 0; i < 70; i += 7, pt++)
      data[pt] = parseFloat(line, i, i + 7);
    for (int i = 0; i < 6; i++) {
      if (Float.isNaN(data[i])) {
          System.out.println("Bad ANISOU record: " + line);
          return;
      }
      data[i] /= 10000f;
    }
    atom.anisoBorU = data;
    atom.anisoBorU[6] = 8; 
  }
  
  
  private void site() {
    if (htSites == null)
      htSites = new Hashtable();
    int seqNum = parseInt(line, 7, 10);
    int nResidues = parseInt(line, 15, 17);
    String siteID = parseTrimmed(line, 11, 14);
    Hashtable htSite = (Hashtable) htSites.get(siteID);
    if (htSite == null) {
      htSite = new Hashtable();
      htSite.put("seqNum", "site_" + seqNum);
      htSite.put("nResidues", new Integer(nResidues));
      htSite.put("groups", "");
      htSites.put(siteID, htSite);
    }
    String groups = (String)htSite.get("groups");
    for (int i = 0; i < 4; i++) {
      int pt = 18 + i * 11;
      String resName = parseTrimmed(line, pt, pt + 3);
      if (resName.length() == 0)
        break;
      String chainID = parseTrimmed(line, pt + 4, pt + 5);
      String seq = parseTrimmed(line, pt + 5, pt + 9);
      String iCode = parseTrimmed(line, pt + 9, pt + 10);
      groups += (groups.length() == 0 ? "" : ",") + "[" + resName + "]" + seq;
      if (iCode.length() > 0)
        groups += "^" + iCode;
      if (chainID.length() > 0)
        groups += ":" + chainID;
      htSite.put("groups", groups);
    }
  }
}

