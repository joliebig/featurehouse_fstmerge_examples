

package org.jmol.symmetry;

import java.util.Arrays;
import java.util.Hashtable;

import javax.vecmath.Point3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.jmol.api.SymmetryInterface;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;



class SpaceGroup {

  int index;
  String hallSymbol;
  
  String hmSymbol; 
  String hmSymbolFull; 
  
  String hmSymbolExt;
  String hmSymbolAbbr;
  String hmSymbolAlternative;
  String hmSymbolAbbrShort;
  char ambiguityType;
  char uniqueAxis; 
  char axisChoice;
  
  
  String intlTableNumber;
  String intlTableNumberFull;
  String intlTableNumberExt;
  HallInfo hallInfo;
  int latticeParameter;
  char latticeCode;
  SymmetryOperation[] operations;
  int operationCount;
  boolean doNormalize = true;

  SpaceGroup(boolean doNormalize) {
    this.doNormalize = doNormalize;
    addSymmetry("x,y,z");
  }
  
  private SpaceGroup(String cifLine) {
    buildSpaceGroup(cifLine);
  }

  static SpaceGroup createSpaceGroup(int desiredSpaceGroupIndex,
                                                  String name,
                                                  float[] notionalUnitcell, boolean doNormalize) {

    SpaceGroup sg = null;
    if (desiredSpaceGroupIndex >= 0) {
      sg = spaceGroupDefinitions[desiredSpaceGroupIndex];
    } else {
      sg = determineSpaceGroup(name, notionalUnitcell);
      if (sg == null)
        sg = createSpaceGroup(name, doNormalize);
    }
    if (sg != null)
      sg.generateAllOperators(null);
    return sg;
  }

  static int determineSpaceGroupIndex(String name) {
    int i = determineSpaceGroupIndex(name, 0f, 0f, 0f, 0f, 0f, 0f, -1);
    return i;
  }

  static SpaceGroup determineSpaceGroup(String name, float a,
                                                     float b, float c,
                                                     float alpha, float beta,
                                                     float gamma, int lastIndex) {
    
    int i = determineSpaceGroupIndex(name, a, b, c, alpha, beta,  gamma, lastIndex);
    return (i >=0 ? spaceGroupDefinitions[i] : null);
  }

  int addSymmetry(String xyz) {
    xyz = xyz.toLowerCase();
    if (xyz.indexOf("x") < 0 || xyz.indexOf("y") < 0 || xyz.indexOf("z") < 0)
      return -1;
    return addOperation(xyz);
  }
   
  SymmetryOperation[] finalOperations;
  
  void setFinalOperations(Point3f[] atoms, int atomIndex,
                                                int count, boolean doNormalize) {
    
    if (hallInfo == null && latticeParameter != 0) {
      HallInfo h = new HallInfo(Translation
          .getHallLatticeEquivalent(latticeParameter));
      generateAllOperators(h);
      doNormalize = false;
    }

    finalOperations = new SymmetryOperation[operationCount];
    if (doNormalize && count > 0) {
      
      
      
      finalOperations[0] = new SymmetryOperation(operations[0], atoms,
          atomIndex, count, true);
      Point3f atom = atoms[atomIndex];
      Point3f c = new Point3f(atom);
      finalOperations[0].transform(c);
      if (c.distance(atom) > 0.0001) 
        for (int i = 0; i < count; i++) {
          atom = atoms[atomIndex + i];
          c.set(atom);
          finalOperations[0].transform(c);
          atom.set(c);
        }
    }
    for (int i = 0; i < operationCount; i++) {
      finalOperations[i] = new SymmetryOperation(operations[i], atoms,
          atomIndex, count, doNormalize);
    }
  }

  int getOperationCount() {
    return finalOperations.length;
  }

  Matrix4f getOperation(int i) {
    return finalOperations[i];
  }

  String getXyz(int i, boolean doNormalize) {
    return finalOperations[i].getXyz(doNormalize);
  }

  void newPoint(int i, Point3f atom1, Point3f atom2,
                       int transX, int transY, int transZ) {
    finalOperations[i].newPoint(atom1, atom2, transX, transY, transZ);
  }
    
  Object rotateEllipsoid(int i, Point3f ptTemp, Vector3f[] axes,
                                UnitCell unitCell, Point3f ptTemp1,
                                Point3f ptTemp2) {
    return finalOperations[i].rotateEllipsoid(ptTemp, axes, unitCell, ptTemp1,
        ptTemp2);
  }

  static String getInfo(String spaceGroup, SymmetryInterface cellInfo) {
    SpaceGroup sg;
    if (cellInfo != null) {
      if (spaceGroup.indexOf("[") >= 0)
        spaceGroup = spaceGroup.substring(0, spaceGroup.indexOf("[")).trim();
      if (spaceGroup.equals("unspecified *"))
        return "no space group identified in file";
      sg = SpaceGroup.determineSpaceGroup(spaceGroup, cellInfo.getNotionalUnitCell());
    } else if (spaceGroup.equalsIgnoreCase("ALL")) {
      return SpaceGroup.dumpAll();
    } else if (spaceGroup.equalsIgnoreCase("ALLSEITZ")) {
      return SpaceGroup.dumpAllSeitz();
    } else {
      sg = SpaceGroup.determineSpaceGroup(spaceGroup);
      if (sg == null) {
        sg = SpaceGroup.createSpaceGroup(spaceGroup, false);
      } else {
        StringBuffer sb = new StringBuffer();
        while (sg != null) {
          sb.append(sg.dumpInfo(null));
          sg = SpaceGroup.determineSpaceGroup(spaceGroup, sg);
        }
        return sb.toString();
      }
    }
    return sg == null ? "?" : sg.dumpInfo(cellInfo);
  }
  
  String dumpInfo(SymmetryInterface cellInfo) {
    Object info  = dumpCanonicalSeitzList();
    if (info instanceof SpaceGroup)
      return ((SpaceGroup)info).dumpInfo(null);
    StringBuffer sb = new StringBuffer("\nHermann-Mauguin symbol: ");
    sb.append(hmSymbol).append(hmSymbolExt.length() > 0 ? ":" + hmSymbolExt : "")
        .append("\ninternational table number: ").append(intlTableNumber)
        .append(intlTableNumberExt.length() > 0 ? ":" + intlTableNumberExt : "")
        .append("\n\n").append(operationCount).append(" operators")
        .append(!hallInfo.hallSymbol.equals("--") ? " from Hall symbol "  + hallInfo.hallSymbol: "")
        .append(": ");
    for (int i = 0; i < operationCount; i++) {
      sb.append("\n").append(operations[i].xyz);
    }
    sb.append("\n\n").append(hallInfo == null ? "invalid Hall symbol" : hallInfo.dumpInfo());

    sb.append("\n\ncanonical Seitz: ").append((String) info) 
        .append("\n----------------------------------------------------\n");
    return sb.toString();
  }

  String getName() {
    return hallSymbol + " ["+hmSymbolFull+"]";  
  }
 
  String getLatticeDesignation() {    
    return latticeCode + ": " + Translation.getLatticeDesignation(latticeParameter);
  }  
 
  void setLattice(int latticeParameter) {
    
    
    
    
    this.latticeParameter = latticeParameter;
    latticeCode = Translation.getLatticeCode(latticeParameter);
    if (latticeParameter > 10) { 
      this.latticeParameter = -Translation.getLatticeIndex(latticeCode);
    }
  }

  
  
  private void buildSpaceGroup(String cifLine) {
    index = ++sgIndex;
    line = cifLine;
    intlTableNumberFull = term = extractLine(); 
    intlTableNumber = extractTerm(':');
    intlTableNumberExt = term;
    extractLine();
    term = extractLine() + "  "; 
    hmSymbolFull = term = (term.substring(0, 2).toUpperCase() 
        + term.substring(2)).trim();
    hmSymbol = extractTerm(':');
    hmSymbolExt = term.toLowerCase();
    int pt = hmSymbol.indexOf(" -3");
    if (pt >= 1)
      if ("admn".indexOf(hmSymbol.charAt(pt - 1)) >= 0)
        hmSymbolAlternative = hmSymbol.substring(0, pt) + " 3" + hmSymbol.substring(pt+3);
    char c;
    term = "";
    for (int i = 0; i < hmSymbol.length(); i++)
      if ((c = hmSymbol.charAt(i)) != ' ')
        term += c;
    
    term = hmSymbol + " ";
    hmSymbolAbbr = "";
    hmSymbolAbbrShort = "";
    for (int i = 0; i < term.length(); i++)
      if ((c = term.charAt(i)) != ' ')
        hmSymbolAbbr += c;
    for (int i = 0; i < term.length(); i++)
      if ((c = term.charAt(i)) != ' ')
        hmSymbolAbbrShort += c;
      else if (term.indexOf(" 1 ", i) == i)
        i++;

    term = extractLine() + "  "; 
    hallSymbol = (term.substring(0, 2).toUpperCase() 
        + term.substring(2)).trim();
    term = intlTableNumberExt;
    ambiguityType = '\0';
    if (term.length() == 0)
      return;
    if (term.startsWith("-"))
      term = term.substring(1);
    if (term.equals("h") || term.equals("r")) {
      ambiguityType = 't';
      axisChoice = intlTableNumberExt.charAt(0);
    } else if (intlTableNumberExt.startsWith("1")
        || intlTableNumberExt.startsWith("2")) {
      ambiguityType = 'o';
     
    } else if (intlTableNumberExt.length() <= 2) { 
      ambiguityType = 'a';
      uniqueAxis = intlTableNumberExt.charAt(0);
     
       
    }
  }

  private static String[] canonicalSeitzList;
  private Object dumpCanonicalSeitzList() {
    if (hallInfo == null)
      hallInfo = new HallInfo(hallSymbol);
    generateAllOperators(null);
    String[] list = new String[operationCount];
    for (int i = 0; i < operationCount; i++)
      list[i] = SymmetryOperation.dumpCanonicalSeitz(operations[i]);
    Arrays.sort(list, 0, operationCount);
    StringBuffer sb = new StringBuffer("\n[");
    for (int i = 0; i < operationCount; i++)
      sb.append(list[i].replace('\t',' ').replace('\n',' ')).append("; ");
    sb.append("]");
    if (index >= spaceGroupDefinitions.length) {
      if (canonicalSeitzList == null) {
      canonicalSeitzList = new String[spaceGroupDefinitions.length];
      for (int i = 0; i < spaceGroupDefinitions.length; i++)
        canonicalSeitzList[i] = (String) spaceGroupDefinitions[i].dumpCanonicalSeitzList();
      }
      String s = sb.toString();
      for (int i = 0; i < spaceGroupDefinitions.length; i++)
        if (canonicalSeitzList[i].indexOf(s) >= 0)
          return spaceGroupDefinitions[i];
    }
    return (index >= 0 && index < spaceGroupDefinitions.length 
        ? hallSymbol + " = " : "") + sb.toString();
  }
  
  private final static String dumpAll() {
   StringBuffer sb = new StringBuffer();
   for (int i = 0; i < spaceGroupDefinitions.length; i++)
     sb.append("\n----------------------\n" + spaceGroupDefinitions[i].dumpInfo(null));
   return sb.toString();
  }
  
  private final static String dumpAllSeitz() {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < spaceGroupDefinitions.length; i++)
      sb.append("\n").append(spaceGroupDefinitions[i].dumpCanonicalSeitzList());
    return sb.toString();
  }
   
  private void setLattice(char latticeCode, boolean isCentrosymmetric) {
    this.latticeCode = latticeCode;
    latticeParameter = Translation.getLatticeIndex(latticeCode);
    if (!isCentrosymmetric)
      latticeParameter = -latticeParameter;
  }
  
  private final static SpaceGroup createSpaceGroup(String name, boolean doNormalize) {
    SpaceGroup sg = determineSpaceGroup(name);
    HallInfo hallInfo;
    if (sg == null) {
      
      hallInfo = new HallInfo(name);
      if (hallInfo.nRotations > 0) {
        String str = name;
        for (int i = 0; i < str.length(); i++) 
          if (str.charAt(i) == ' ')
            str = str.substring(0, i) + "_" + str.substring(i + 1);
        sg = new SpaceGroup("0 -- -- " + str);
        sg.hallInfo = hallInfo;
      } else if (name.indexOf(",") >= 0) {
        sg = new SpaceGroup("0 -- -- --");
        sg.doNormalize = false;
        sg.generateOperatorsFromXyzInfo(name);
      } else {
        sg = null;
      }
    }
    if (sg != null)
      sg.generateAllOperators(null);
    return sg;
  }
  
  private final static SpaceGroup determineSpaceGroup(String name) {
    return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, -1);
  }

  private final static SpaceGroup determineSpaceGroup(String name, SpaceGroup sg) {
    return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, sg.index);
  }

  private final static SpaceGroup determineSpaceGroup(String name,
                                                     float[] notionalUnitcell) {
    if (notionalUnitcell == null)
      return determineSpaceGroup(name, 0f, 0f, 0f, 0f, 0f, 0f, -1);
    return determineSpaceGroup(name, notionalUnitcell[0], notionalUnitcell[1],
        notionalUnitcell[2], notionalUnitcell[3], notionalUnitcell[4],
        notionalUnitcell[5], -1);
  }

  private final static int determineSpaceGroupIndex(String name, float a,
                                                   float b, float c,
                                                   float alpha, float beta,
                                                   float gamma, int lastIndex) {

    name = name.trim();
    String nameExt = name;
    int i;
    
    
    if (name.indexOf("_") >= 0)
      for (i = name.length(); --i >= 0;)
        if (name.charAt(i) == '_')
          name = name.substring(0, i) + " " + name.substring(i + 1);

    
    if (name.length() >= 2) {
      i = (name.indexOf("-") == 0 ? 2 : 1);
      if (i < name.length() && name.charAt(i) != ' ')
        name = name.substring(0, i) + " " + name.substring(i);
      name = name.substring(0, 2).toUpperCase() + name.substring(2);
    }
    
    
    String ext = "";
    if ((i = name.indexOf(":")) > 0) {
      ext = name.substring(i + 1).toLowerCase();
      name = name.substring(0, i).trim();
    }
    
    
    String abbr = "";
    for (i = 0; i < name.length(); i++) {
      if (" ()".indexOf(name.charAt(i)) < 0)
        abbr += name.charAt(i);
    }
    SpaceGroup s;

    

    

    if (lastIndex < 0)
      lastIndex = spaceGroupDefinitions.length;
    if (ext.length() == 0) 
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hallSymbol.equals(name))
          return i;
      }

    

    for (i = lastIndex; --i >= 0;) {
      s = spaceGroupDefinitions[i];
      if (s.intlTableNumberFull.equals(nameExt))
        return i;
    }

    

    
    

    for (i = lastIndex; --i >= 0;) {
      s = spaceGroupDefinitions[i];
      if (s.hmSymbolFull.equals(nameExt))
        return i;
    }

    
    for (i = lastIndex; --i >= 0;) {
      s = spaceGroupDefinitions[i];
      if (s.hmSymbolAlternative != null
          && s.hmSymbolAlternative.equals(nameExt))
        return i;
    }

    

    if (ext.length() > 0) 
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hmSymbolAbbr.equals(abbr) && s.intlTableNumberExt.equals(ext))
          return i;
      }

    
    if (ext.length() > 0) 
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hmSymbolAbbrShort.equals(abbr)
            && s.intlTableNumberExt.equals(ext))
          return i;
      }

    

    char uniqueAxis = determineUniqueAxis(a, b, c, alpha, beta, gamma);

    if (ext.length() == 0 || ext.charAt(0) == '?')
      
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.hmSymbolAbbr.equals(abbr) || s.hmSymbolAbbrShort.equals(abbr)) {
          switch (s.ambiguityType) {
          case '\0':
            return i;
          case 'a':
            if (s.uniqueAxis == uniqueAxis || uniqueAxis == '\0')
              return i;
            break;
          case 'o':
            if (ext.length() == 0) {
              if (s.hmSymbolExt.equals("2"))
                return i; 
            } else if (s.hmSymbolExt.equals(ext))
              return i;
            break;
          case 't':
            if (ext.length() == 0) {
              if (s.axisChoice == 'h')
                return i; 
            } else if ((s.axisChoice + "").equals(ext))
              return i;
            break;
          }
        }
      }

    

    if (ext.length() == 0)
      for (i = lastIndex; --i >= 0;) {
        s = spaceGroupDefinitions[i];
        if (s.intlTableNumber.equals(nameExt))
          return i;
      }
    return -1;
  }
  
  private final static char determineUniqueAxis(float a, float b, float c, float alpha, float beta, float gamma) {
    if (a == b)
      return (b == c ? '\0' : 'c');
    if (b == c)
      return 'a';
    if (c == a)
      return 'b';
    if (alpha == beta)
      return (beta == gamma ? '\0' : 'c');
    if (beta == gamma)
      return 'a';
    if (gamma == alpha)
      return 'b';
    return '\0';
  }

  Hashtable xyzList = new Hashtable();
  private int addOperation(String xyz0) {
    if (xyz0 == null) {
      xyzList = new Hashtable();
      return -1;
    }
    boolean isSpecial = (xyz0.charAt(0) == '!');
    if (isSpecial) xyz0 = xyz0.substring(1);
    if (xyzList.containsKey(xyz0))
      return ((Integer)xyzList.get(xyz0)).intValue();

    SymmetryOperation symmetryOperation = new SymmetryOperation(doNormalize);
    if (!symmetryOperation.setMatrixFromXYZ(xyz0)) {
      Logger.error("couldn't interpret symmetry operation: " + xyz0);      
      return -1;
    }
    String xyz = symmetryOperation.xyz;
    if (!isSpecial) {
      
      if (xyzList.containsKey(xyz))
        return ((Integer)xyzList.get(xyz)).intValue();
      xyzList.put(xyz, new Integer(operationCount));
    }
    if (!xyz.equals(xyz0))
      xyzList.put(xyz0, new Integer(operationCount));
    if (operations == null) {
      operations = new SymmetryOperation[4];
      operationCount = 0;
    }
    if (operationCount == operations.length)
      operations = (SymmetryOperation[]) ArrayUtil.setLength(operations,
          operationCount * 2);
    operations[operationCount++] = symmetryOperation;
    if (Logger.debugging)
        Logger.debug("\naddOperation " + operationCount
        + symmetryOperation.dumpInfo());
    return operationCount - 1;
  }

  private void generateOperatorsFromXyzInfo(String xyzInfo) {
    addOperation(null);
    addSymmetry("x,y,z");
    term = xyzInfo.toLowerCase();
    while (term.length() > 0)
      addSymmetry(extractTerm(';'));
  }
  
  

  private void generateAllOperators(HallInfo h) {
    if (h == null) {
      h = hallInfo;
      if (operationCount > 0)
        return;
      operations = new SymmetryOperation[4];
      operationCount = 0;
      if (hallInfo == null || hallInfo.nRotations == 0)
        h = hallInfo = new HallInfo(hallSymbol);
      setLattice(hallInfo.latticeCode, hallInfo.isCentrosymmetric);
      addOperation(null);
      addSymmetry("x,y,z");
    }
    Matrix4f mat1 = new Matrix4f();
    Matrix4f operation = new Matrix4f();
    Matrix4f[] newOps = new Matrix4f[7];
    for (int i = 0; i < 7; i++)
      newOps[i] = new Matrix4f();
    
    
    
    for (int i = 0; i < h.nRotations; i++) {
      mat1.set(h.rotationTerms[i].seitzMatrix12ths);
      int nRot = h.rotationTerms[i].order;
      
      newOps[0].setIdentity();
      int nOps = operationCount;
      for (int j = 1; j <= nRot; j++) {
        newOps[j].mul(mat1, newOps[0]);
        newOps[0].set(newOps[j]);
        for (int k = 0; k < nOps; k++) {
          operation.mul(newOps[j], operations[k]);
          addSymmetry(SymmetryOperation.getXYZFromMatrix(operation, true),
              operation);
        }
      }
    }
  }

  private void addSymmetry(String xyz, Matrix4f operation) {
    int iop = addOperation(xyz);
    if (iop < 0)
      return;
    SymmetryOperation symmetryOperation = operations[iop];
    symmetryOperation.set(operation);
  }

  
  
  String line;
  private String extractLine() {
    String info;
    line = line.trim();
    int i = line.indexOf(" ");
    if (i < 0)
      i = line.length();
    info = line.substring(0, i);
    line = line.substring(i);
    if (info.indexOf("_") >= 0)
      for (i = info.length(); --i >= 0;)
        if (info.charAt(i) == '_')
          info = info.substring(0, i) + " " + info.substring(i + 1);
    return info;
  }

  String term;
  private String extractTerm(char sep) {
    int i = term.indexOf(sep);
    String str;
    if (i < 0) {
      str = term;
      term = "";
    } else {
      str = term.substring(0, i);
      term = term.substring(i + 1);
    }
    return str;
  }

  

  private static int sgIndex = -1;
  
  private final static SpaceGroup[] spaceGroupDefinitions = {
      new SpaceGroup("1 c1^1 p_1 p_1")
    , new SpaceGroup("2 ci^1 p_-1 -p_1")
    , new SpaceGroup("3:b c2^1 p_1_2_1 p_2y") 
    , new SpaceGroup("3:b c2^1 p_2 p_2y")
    , new SpaceGroup("3:c c2^1 p_1_1_2 p_2")
    , new SpaceGroup("3:a c2^1 p_2_1_1 p_2x")
    , new SpaceGroup("4:b c2^2 p_1_21_1 p_2yb") 
    , new SpaceGroup("4:b c2^2 p_21 p_2yb")
    , new SpaceGroup("4:b* c2^2 p_1_21_1* p_2y1") 
    , new SpaceGroup("4:c c2^2 p_1_1_21 p_2c")
    , new SpaceGroup("4:c* c2^2 p_1_1_21* p_21") 
    , new SpaceGroup("4:a c2^2 p_21_1_1 p_2xa")
    , new SpaceGroup("4:a* c2^2 p_21_1_1* p_2x1") 
    , new SpaceGroup("5:b1 c2^3 c_1_2_1 c_2y") 
    , new SpaceGroup("5:b1 c2^3 c_2 c_2y")
    , new SpaceGroup("5:b2 c2^3 a_1_2_1 a_2y")
    , new SpaceGroup("5:b3 c2^3 i_1_2_1 i_2y")
    , new SpaceGroup("5:c1 c2^3 a_1_1_2 a_2")
    , new SpaceGroup("5:c2 c2^3 b_1_1_2 b_2")
    , new SpaceGroup("5:c3 c2^3 i_1_1_2 i_2")
    , new SpaceGroup("5:a1 c2^3 b_2_1_1 b_2x")
    , new SpaceGroup("5:a2 c2^3 c_2_1_1 c_2x")
    , new SpaceGroup("5:a3 c2^3 i_2_1_1 i_2x")
    , new SpaceGroup("6:b cs^1 p_1_m_1 p_-2y") 
    , new SpaceGroup("6:b cs^1 p_m p_-2y")
    , new SpaceGroup("6:c cs^1 p_1_1_m p_-2")
    , new SpaceGroup("6:a cs^1 p_m_1_1 p_-2x")
    , new SpaceGroup("7:b1 cs^2 p_1_c_1 p_-2yc") 
    , new SpaceGroup("7:b1 cs^2 p_c p_-2yc")
    , new SpaceGroup("7:b2 cs^2 p_1_n_1 p_-2yac") 
    , new SpaceGroup("7:b2 cs^2 p_n p_-2yac")
    , new SpaceGroup("7:b3 cs^2 p_1_a_1 p_-2ya") 
    , new SpaceGroup("7:b3 cs^2 p_a p_-2ya")
    , new SpaceGroup("7:c1 cs^2 p_1_1_a p_-2a")
    , new SpaceGroup("7:c2 cs^2 p_1_1_n p_-2ab")
    , new SpaceGroup("7:c3 cs^2 p_1_1_b p_-2b")
    , new SpaceGroup("7:a1 cs^2 p_b_1_1 p_-2xb")
    , new SpaceGroup("7:a2 cs^2 p_n_1_1 p_-2xbc")
    , new SpaceGroup("7:a3 cs^2 p_c_1_1 p_-2xc")
    , new SpaceGroup("8:b1 cs^3 c_1_m_1 c_-2y") 
    , new SpaceGroup("8:b1 cs^3 c_m c_-2y")
    , new SpaceGroup("8:b2 cs^3 a_1_m_1 a_-2y")
    , new SpaceGroup("8:b3 cs^3 i_1_m_1 i_-2y") 
    , new SpaceGroup("8:b3 cs^3 i_m i_-2y")
    , new SpaceGroup("8:c1 cs^3 a_1_1_m a_-2")
    , new SpaceGroup("8:c2 cs^3 b_1_1_m b_-2")
    , new SpaceGroup("8:c3 cs^3 i_1_1_m i_-2")
    , new SpaceGroup("8:a1 cs^3 b_m_1_1 b_-2x")
    , new SpaceGroup("8:a2 cs^3 c_m_1_1 c_-2x")
    , new SpaceGroup("8:a3 cs^3 i_m_1_1 i_-2x")
    , new SpaceGroup("9:b1 cs^4 c_1_c_1 c_-2yc") 
    , new SpaceGroup("9:b1 cs^4 c_c c_-2yc")
    , new SpaceGroup("9:b2 cs^4 a_1_n_1 a_-2yab")
    , new SpaceGroup("9:b3 cs^4 i_1_a_1 i_-2ya")
    , new SpaceGroup("9:-b1 cs^4 a_1_a_1 a_-2ya")
    , new SpaceGroup("9:-b2 cs^4 c_1_n_1 c_-2yac")
    , new SpaceGroup("9:-b3 cs^4 i_1_c_1 i_-2yc")
    , new SpaceGroup("9:c1 cs^4 a_1_1_a a_-2a")
    , new SpaceGroup("9:c2 cs^4 b_1_1_n b_-2ab")
    , new SpaceGroup("9:c3 cs^4 i_1_1_b i_-2b")
    , new SpaceGroup("9:-c1 cs^4 b_1_1_b b_-2b")
    , new SpaceGroup("9:-c2 cs^4 a_1_1_n a_-2ab")
    , new SpaceGroup("9:-c3 cs^4 i_1_1_a i_-2a")
    , new SpaceGroup("9:a1 cs^4 b_b_1_1 b_-2xb")
    , new SpaceGroup("9:a2 cs^4 c_n_1_1 c_-2xac")
    , new SpaceGroup("9:a3 cs^4 i_c_1_1 i_-2xc")
    , new SpaceGroup("9:-a1 cs^4 c_c_1_1 c_-2xc")
    , new SpaceGroup("9:-a2 cs^4 b_n_1_1 b_-2xab")
    , new SpaceGroup("9:-a3 cs^4 i_b_1_1 i_-2xb")
    , new SpaceGroup("10:b c2h^1 p_1_2/m_1 -p_2y") 
    , new SpaceGroup("10:b c2h^1 p_2/m -p_2y")
    , new SpaceGroup("10:c c2h^1 p_1_1_2/m -p_2")
    , new SpaceGroup("10:a c2h^1 p_2/m_1_1 -p_2x")
    , new SpaceGroup("11:b c2h^2 p_1_21/m_1 -p_2yb") 
    , new SpaceGroup("11:b c2h^2 p_21/m -p_2yb")
    , new SpaceGroup("11:b* c2h^2 p_1_21/m_1* -p_2y1") 
    , new SpaceGroup("11:c c2h^2 p_1_1_21/m -p_2c")
    , new SpaceGroup("11:c* c2h^2 p_1_1_21/m* -p_21") 
    , new SpaceGroup("11:a c2h^2 p_21/m_1_1 -p_2xa")
    , new SpaceGroup("11:a* c2h^2 p_21/m_1_1* -p_2x1") 
    , new SpaceGroup("12:b1 c2h^3 c_1_2/m_1 -c_2y") 
    , new SpaceGroup("12:b1 c2h^3 c_2/m -c_2y")
    , new SpaceGroup("12:b2 c2h^3 a_1_2/m_1 -a_2y")
    , new SpaceGroup("12:b3 c2h^3 i_1_2/m_1 -i_2y") 
    , new SpaceGroup("12:b3 c2h^3 i_2/m -i_2y")
    , new SpaceGroup("12:c1 c2h^3 a_1_1_2/m -a_2")
    , new SpaceGroup("12:c2 c2h^3 b_1_1_2/m -b_2")
    , new SpaceGroup("12:c3 c2h^3 i_1_1_2/m -i_2")
    , new SpaceGroup("12:a1 c2h^3 b_2/m_1_1 -b_2x")
    , new SpaceGroup("12:a2 c2h^3 c_2/m_1_1 -c_2x")
    , new SpaceGroup("12:a3 c2h^3 i_2/m_1_1 -i_2x")
    , new SpaceGroup("13:b1 c2h^4 p_1_2/c_1 -p_2yc") 
    , new SpaceGroup("13:b1 c2h^4 p_2/c -p_2yc")
    , new SpaceGroup("13:b2 c2h^4 p_1_2/n_1 -p_2yac") 
    , new SpaceGroup("13:b2 c2h^4 p_2/n -p_2yac")
    , new SpaceGroup("13:b3 c2h^4 p_1_2/a_1 -p_2ya") 
    , new SpaceGroup("13:b3 c2h^4 p_2/a -p_2ya")
    , new SpaceGroup("13:c1 c2h^4 p_1_1_2/a -p_2a")
    , new SpaceGroup("13:c2 c2h^4 p_1_1_2/n -p_2ab")
    , new SpaceGroup("13:c3 c2h^4 p_1_1_2/b -p_2b")
    , new SpaceGroup("13:a1 c2h^4 p_2/b_1_1 -p_2xb")
    , new SpaceGroup("13:a2 c2h^4 p_2/n_1_1 -p_2xbc")
    , new SpaceGroup("13:a3 c2h^4 p_2/c_1_1 -p_2xc")
    , new SpaceGroup("14:b1 c2h^5 p_1_21/c_1 -p_2ybc") 
    , new SpaceGroup("14:b1 c2h^5 p_21/c -p_2ybc")
    , new SpaceGroup("14:b2 c2h^5 p_1_21/n_1 -p_2yn") 
    , new SpaceGroup("14:b2 c2h^5 p_21/n -p_2yn")
    , new SpaceGroup("14:b3 c2h^5 p_1_21/a_1 -p_2yab") 
    , new SpaceGroup("14:b3 c2h^5 p_21/a -p_2yab")
    , new SpaceGroup("14:c1 c2h^5 p_1_1_21/a -p_2ac")
    , new SpaceGroup("14:c2 c2h^5 p_1_1_21/n -p_2n")
    , new SpaceGroup("14:c3 c2h^5 p_1_1_21/b -p_2bc")
    , new SpaceGroup("14:a1 c2h^5 p_21/b_1_1 -p_2xab")
    , new SpaceGroup("14:a2 c2h^5 p_21/n_1_1 -p_2xn")
    , new SpaceGroup("14:a3 c2h^5 p_21/c_1_1 -p_2xac")
    , new SpaceGroup("15:b1 c2h^6 c_1_2/c_1 -c_2yc") 
    , new SpaceGroup("15:b1 c2h^6 c_2/c -c_2yc")
    , new SpaceGroup("15:b2 c2h^6 a_1_2/n_1 -a_2yab")
    , new SpaceGroup("15:b3 c2h^6 i_1_2/a_1 -i_2ya") 
    , new SpaceGroup("15:b3 c2h^6 i_2/a -i_2ya")
    , new SpaceGroup("15:-b1 c2h^6 a_1_2/a_1 -a_2ya")
    , new SpaceGroup("15:-b2 c2h^6 c_1_2/n_1 -c_2yac") 
    , new SpaceGroup("15:-b2 c2h^6 c_2/n -c_2yac")
    , new SpaceGroup("15:-b3 c2h^6 i_1_2/c_1 -i_2yc") 
    , new SpaceGroup("15:-b3 c2h^6 i_2/c -i_2yc")
    , new SpaceGroup("15:c1 c2h^6 a_1_1_2/a -a_2a")
    , new SpaceGroup("15:c2 c2h^6 b_1_1_2/n -b_2ab")
    , new SpaceGroup("15:c3 c2h^6 i_1_1_2/b -i_2b")
    , new SpaceGroup("15:-c1 c2h^6 b_1_1_2/b -b_2b")
    , new SpaceGroup("15:-c2 c2h^6 a_1_1_2/n -a_2ab")
    , new SpaceGroup("15:-c3 c2h^6 i_1_1_2/a -i_2a")
    , new SpaceGroup("15:a1 c2h^6 b_2/b_1_1 -b_2xb")
    , new SpaceGroup("15:a2 c2h^6 c_2/n_1_1 -c_2xac")
    , new SpaceGroup("15:a3 c2h^6 i_2/c_1_1 -i_2xc")
    , new SpaceGroup("15:-a1 c2h^6 c_2/c_1_1 -c_2xc")
    , new SpaceGroup("15:-a2 c2h^6 b_2/n_1_1 -b_2xab")
    , new SpaceGroup("15:-a3 c2h^6 i_2/b_1_1 -i_2xb")
    , new SpaceGroup("16 d2^1 p_2_2_2 p_2_2")
    , new SpaceGroup("17 d2^2 p_2_2_21 p_2c_2")
    , new SpaceGroup("17* d2^2 p_2_2_21* p_21_2") 
    , new SpaceGroup("17:cab d2^2 p_21_2_2 p_2a_2a")
    , new SpaceGroup("17:bca d2^2 p_2_21_2 p_2_2b")
    , new SpaceGroup("18 d2^3 p_21_21_2 p_2_2ab")
    , new SpaceGroup("18:cab d2^3 p_2_21_21 p_2bc_2")
    , new SpaceGroup("18:bca d2^3 p_21_2_21 p_2ac_2ac")
    , new SpaceGroup("19 d2^4 p_21_21_21 p_2ac_2ab")
    , new SpaceGroup("20 d2^5 c_2_2_21 c_2c_2")
    , new SpaceGroup("20* d2^5 c_2_2_21* c_21_2") 
    , new SpaceGroup("20:cab d2^5 a_21_2_2 a_2a_2a")
    , new SpaceGroup("20:cab* d2^5 a_21_2_2* a_2a_21") 
    , new SpaceGroup("20:bca d2^5 b_2_21_2 b_2_2b")
    , new SpaceGroup("21 d2^6 c_2_2_2 c_2_2")
    , new SpaceGroup("21:cab d2^6 a_2_2_2 a_2_2")
    , new SpaceGroup("21:bca d2^6 b_2_2_2 b_2_2")
    , new SpaceGroup("22 d2^7 f_2_2_2 f_2_2")
    , new SpaceGroup("23 d2^8 i_2_2_2 i_2_2")
    , new SpaceGroup("24 d2^9 i_21_21_21 i_2b_2c")
    , new SpaceGroup("25 c2v^1 p_m_m_2 p_2_-2")
    , new SpaceGroup("25:cab c2v^1 p_2_m_m p_-2_2")
    , new SpaceGroup("25:bca c2v^1 p_m_2_m p_-2_-2")
    , new SpaceGroup("26 c2v^2 p_m_c_21 p_2c_-2")
    , new SpaceGroup("26* c2v^2 p_m_c_21* p_21_-2") 
    , new SpaceGroup("26:ba-c c2v^2 p_c_m_21 p_2c_-2c")
    , new SpaceGroup("26:ba-c* c2v^2 p_c_m_21* p_21_-2c") 
    , new SpaceGroup("26:cab c2v^2 p_21_m_a p_-2a_2a")
    , new SpaceGroup("26:-cba c2v^2 p_21_a_m p_-2_2a")
    , new SpaceGroup("26:bca c2v^2 p_b_21_m p_-2_-2b")
    , new SpaceGroup("26:a-cb c2v^2 p_m_21_b p_-2b_-2")
    , new SpaceGroup("27 c2v^3 p_c_c_2 p_2_-2c")
    , new SpaceGroup("27:cab c2v^3 p_2_a_a p_-2a_2")
    , new SpaceGroup("27:bca c2v^3 p_b_2_b p_-2b_-2b")
    , new SpaceGroup("28 c2v^4 p_m_a_2 p_2_-2a")
    , new SpaceGroup("28* c2v^4 p_m_a_2* p_2_-21") 
    , new SpaceGroup("28:ba-c c2v^4 p_b_m_2 p_2_-2b")
    , new SpaceGroup("28:cab c2v^4 p_2_m_b p_-2b_2")
    , new SpaceGroup("28:-cba c2v^4 p_2_c_m p_-2c_2")
    , new SpaceGroup("28:-cba* c2v^4 p_2_c_m* p_-21_2") 
    , new SpaceGroup("28:bca c2v^4 p_c_2_m p_-2c_-2c")
    , new SpaceGroup("28:a-cb c2v^4 p_m_2_a p_-2a_-2a")
    , new SpaceGroup("29 c2v^5 p_c_a_21 p_2c_-2ac")
    , new SpaceGroup("29:ba-c c2v^5 p_b_c_21 p_2c_-2b")
    , new SpaceGroup("29:cab c2v^5 p_21_a_b p_-2b_2a")
    , new SpaceGroup("29:-cba c2v^5 p_21_c_a p_-2ac_2a")
    , new SpaceGroup("29:bca c2v^5 p_c_21_b p_-2bc_-2c")
    , new SpaceGroup("29:a-cb c2v^5 p_b_21_a p_-2a_-2ab")
    , new SpaceGroup("30 c2v^6 p_n_c_2 p_2_-2bc")
    , new SpaceGroup("30:ba-c c2v^6 p_c_n_2 p_2_-2ac")
    , new SpaceGroup("30:cab c2v^6 p_2_n_a p_-2ac_2")
    , new SpaceGroup("30:-cba c2v^6 p_2_a_n p_-2ab_2")
    , new SpaceGroup("30:bca c2v^6 p_b_2_n p_-2ab_-2ab")
    , new SpaceGroup("30:a-cb c2v^6 p_n_2_b p_-2bc_-2bc")
    , new SpaceGroup("31 c2v^7 p_m_n_21 p_2ac_-2")
    , new SpaceGroup("31:ba-c c2v^7 p_n_m_21 p_2bc_-2bc")
    , new SpaceGroup("31:cab c2v^7 p_21_m_n p_-2ab_2ab")
    , new SpaceGroup("31:-cba c2v^7 p_21_n_m p_-2_2ac")
    , new SpaceGroup("31:bca c2v^7 p_n_21_m p_-2_-2bc")
    , new SpaceGroup("31:a-cb c2v^7 p_m_21_n p_-2ab_-2")
    , new SpaceGroup("32 c2v^8 p_b_a_2 p_2_-2ab")
    , new SpaceGroup("32:cab c2v^8 p_2_c_b p_-2bc_2")
    , new SpaceGroup("32:bca c2v^8 p_c_2_a p_-2ac_-2ac")
    , new SpaceGroup("33 c2v^9 p_n_a_21 p_2c_-2n")
    , new SpaceGroup("33* c2v^9 p_n_a_21* p_21_-2n") 
    , new SpaceGroup("33:ba-c c2v^9 p_b_n_21 p_2c_-2ab")
    , new SpaceGroup("33:ba-c* c2v^9 p_b_n_21* p_21_-2ab") 
    , new SpaceGroup("33:cab c2v^9 p_21_n_b p_-2bc_2a")
    , new SpaceGroup("33:cab* c2v^9 p_21_n_b* p_-2bc_21") 
    , new SpaceGroup("33:-cba c2v^9 p_21_c_n p_-2n_2a")
    , new SpaceGroup("33:-cba* c2v^9 p_21_c_n* p_-2n_21") 
    , new SpaceGroup("33:bca c2v^9 p_c_21_n p_-2n_-2ac")
    , new SpaceGroup("33:a-cb c2v^9 p_n_21_a p_-2ac_-2n")
    , new SpaceGroup("34 c2v^10 p_n_n_2 p_2_-2n")
    , new SpaceGroup("34:cab c2v^10 p_2_n_n p_-2n_2")
    , new SpaceGroup("34:bca c2v^10 p_n_2_n p_-2n_-2n")
    , new SpaceGroup("35 c2v^11 c_m_m_2 c_2_-2")
    , new SpaceGroup("35:cab c2v^11 a_2_m_m a_-2_2")
    , new SpaceGroup("35:bca c2v^11 b_m_2_m b_-2_-2")
    , new SpaceGroup("36 c2v^12 c_m_c_21 c_2c_-2")
    , new SpaceGroup("36* c2v^12 c_m_c_21* c_21_-2") 
    , new SpaceGroup("36:ba-c c2v^12 c_c_m_21 c_2c_-2c")
    , new SpaceGroup("36:ba-c* c2v^12 c_c_m_21* c_21_-2c") 
    , new SpaceGroup("36:cab c2v^12 a_21_m_a a_-2a_2a")
    , new SpaceGroup("36:cab* c2v^12 a_21_m_a* a_-2a_21") 
    , new SpaceGroup("36:-cba c2v^12 a_21_a_m a_-2_2a")
    , new SpaceGroup("36:-cba* c2v^12 a_21_a_m* a_-2_21") 
    , new SpaceGroup("36:bca c2v^12 b_b_21_m b_-2_-2b")
    , new SpaceGroup("36:a-cb c2v^12 b_m_21_b b_-2b_-2")
    , new SpaceGroup("37 c2v^13 c_c_c_2 c_2_-2c")
    , new SpaceGroup("37:cab c2v^13 a_2_a_a a_-2a_2")
    , new SpaceGroup("37:bca c2v^13 b_b_2_b b_-2b_-2b")
    , new SpaceGroup("38 c2v^14 a_m_m_2 a_2_-2")
    , new SpaceGroup("38:ba-c c2v^14 b_m_m_2 b_2_-2")
    , new SpaceGroup("38:cab c2v^14 b_2_m_m b_-2_2")
    , new SpaceGroup("38:-cba c2v^14 c_2_m_m c_-2_2")
    , new SpaceGroup("38:bca c2v^14 c_m_2_m c_-2_-2")
    , new SpaceGroup("38:a-cb c2v^14 a_m_2_m a_-2_-2")
    , new SpaceGroup("39 c2v^15 a_b_m_2 a_2_-2b")
    , new SpaceGroup("39:ba-c c2v^15 b_m_a_2 b_2_-2a")
    , new SpaceGroup("39:cab c2v^15 b_2_c_m b_-2a_2")
    , new SpaceGroup("39:-cba c2v^15 c_2_m_b c_-2a_2")
    , new SpaceGroup("39:bca c2v^15 c_m_2_a c_-2a_-2a")
    , new SpaceGroup("39:a-cb c2v^15 a_c_2_m a_-2b_-2b")
    , new SpaceGroup("40 c2v^16 a_m_a_2 a_2_-2a")
    , new SpaceGroup("40:ba-c c2v^16 b_b_m_2 b_2_-2b")
    , new SpaceGroup("40:cab c2v^16 b_2_m_b b_-2b_2")
    , new SpaceGroup("40:-cba c2v^16 c_2_c_m c_-2c_2")
    , new SpaceGroup("40:bca c2v^16 c_c_2_m c_-2c_-2c")
    , new SpaceGroup("40:a-cb c2v^16 a_m_2_a a_-2a_-2a")
    , new SpaceGroup("41 c2v^17 a_b_a_2 a_2_-2ab")
    , new SpaceGroup("41:ba-c c2v^17 b_b_a_2 b_2_-2ab")
    , new SpaceGroup("41:cab c2v^17 b_2_c_b b_-2ab_2")
    , new SpaceGroup("41:-cba c2v^17 c_2_c_b c_-2ac_2")
    , new SpaceGroup("41:bca c2v^17 c_c_2_a c_-2ac_-2ac")
    , new SpaceGroup("41:a-cb c2v^17 a_c_2_a a_-2ab_-2ab")
    , new SpaceGroup("42 c2v^18 f_m_m_2 f_2_-2")
    , new SpaceGroup("42:cab c2v^18 f_2_m_m f_-2_2")
    , new SpaceGroup("42:bca c2v^18 f_m_2_m f_-2_-2")
    , new SpaceGroup("43 c2v^19 f_d_d_2 f_2_-2d")
    , new SpaceGroup("43:cab c2v^19 f_2_d_d f_-2d_2")
    , new SpaceGroup("43:bca c2v^19 f_d_2_d f_-2d_-2d")
    , new SpaceGroup("44 c2v^20 i_m_m_2 i_2_-2")
    , new SpaceGroup("44:cab c2v^20 i_2_m_m i_-2_2")
    , new SpaceGroup("44:bca c2v^20 i_m_2_m i_-2_-2")
    , new SpaceGroup("45 c2v^21 i_b_a_2 i_2_-2c")
    , new SpaceGroup("45:cab c2v^21 i_2_c_b i_-2a_2")
    , new SpaceGroup("45:bca c2v^21 i_c_2_a i_-2b_-2b")
    , new SpaceGroup("46 c2v^22 i_m_a_2 i_2_-2a")
    , new SpaceGroup("46:ba-c c2v^22 i_b_m_2 i_2_-2b")
    , new SpaceGroup("46:cab c2v^22 i_2_m_b i_-2b_2")
    , new SpaceGroup("46:-cba c2v^22 i_2_c_m i_-2c_2")
    , new SpaceGroup("46:bca c2v^22 i_c_2_m i_-2c_-2c")
    , new SpaceGroup("46:a-cb c2v^22 i_m_2_a i_-2a_-2a")
    , new SpaceGroup("47 d2h^1 p_m_m_m -p_2_2")
    , new SpaceGroup("48:1 d2h^2 p_n_n_n:1 p_2_2_-1n")
    , new SpaceGroup("48:2 d2h^2 p_n_n_n:2 -p_2ab_2bc")
    , new SpaceGroup("49 d2h^3 p_c_c_m -p_2_2c")
    , new SpaceGroup("49:cab d2h^3 p_m_a_a -p_2a_2")
    , new SpaceGroup("49:bca d2h^3 p_b_m_b -p_2b_2b")
    , new SpaceGroup("50:1 d2h^4 p_b_a_n:1 p_2_2_-1ab")
    , new SpaceGroup("50:2 d2h^4 p_b_a_n:2 -p_2ab_2b")
    , new SpaceGroup("50:1cab d2h^4 p_n_c_b:1 p_2_2_-1bc")
    , new SpaceGroup("50:2cab d2h^4 p_n_c_b:2 -p_2b_2bc")
    , new SpaceGroup("50:1bca d2h^4 p_c_n_a:1 p_2_2_-1ac")
    , new SpaceGroup("50:2bca d2h^4 p_c_n_a:2 -p_2a_2c")
    , new SpaceGroup("51 d2h^5 p_m_m_a -p_2a_2a")
    , new SpaceGroup("51:ba-c d2h^5 p_m_m_b -p_2b_2")
    , new SpaceGroup("51:cab d2h^5 p_b_m_m -p_2_2b")
    , new SpaceGroup("51:-cba d2h^5 p_c_m_m -p_2c_2c")
    , new SpaceGroup("51:bca d2h^5 p_m_c_m -p_2c_2")
    , new SpaceGroup("51:a-cb d2h^5 p_m_a_m -p_2_2a")
    , new SpaceGroup("52 d2h^6 p_n_n_a -p_2a_2bc")
    , new SpaceGroup("52:ba-c d2h^6 p_n_n_b -p_2b_2n")
    , new SpaceGroup("52:cab d2h^6 p_b_n_n -p_2n_2b")
    , new SpaceGroup("52:-cba d2h^6 p_c_n_n -p_2ab_2c")
    , new SpaceGroup("52:bca d2h^6 p_n_c_n -p_2ab_2n")
    , new SpaceGroup("52:a-cb d2h^6 p_n_a_n -p_2n_2bc")
    , new SpaceGroup("53 d2h^7 p_m_n_a -p_2ac_2")
    , new SpaceGroup("53:ba-c d2h^7 p_n_m_b -p_2bc_2bc")
    , new SpaceGroup("53:cab d2h^7 p_b_m_n -p_2ab_2ab")
    , new SpaceGroup("53:-cba d2h^7 p_c_n_m -p_2_2ac")
    , new SpaceGroup("53:bca d2h^7 p_n_c_m -p_2_2bc")
    , new SpaceGroup("53:a-cb d2h^7 p_m_a_n -p_2ab_2")
    , new SpaceGroup("54 d2h^8 p_c_c_a -p_2a_2ac")
    , new SpaceGroup("54:ba-c d2h^8 p_c_c_b -p_2b_2c")
    , new SpaceGroup("54:cab d2h^8 p_b_a_a -p_2a_2b")
    , new SpaceGroup("54:-cba d2h^8 p_c_a_a -p_2ac_2c")
    , new SpaceGroup("54:bca d2h^8 p_b_c_b -p_2bc_2b")
    , new SpaceGroup("54:a-cb d2h^8 p_b_a_b -p_2b_2ab")
    , new SpaceGroup("55 d2h^9 p_b_a_m -p_2_2ab")
    , new SpaceGroup("55:cab d2h^9 p_m_c_b -p_2bc_2")
    , new SpaceGroup("55:bca d2h^9 p_c_m_a -p_2ac_2ac")
    , new SpaceGroup("56 d2h^10 p_c_c_n -p_2ab_2ac")
    , new SpaceGroup("56:cab d2h^10 p_n_a_a -p_2ac_2bc")
    , new SpaceGroup("56:bca d2h^10 p_b_n_b -p_2bc_2ab")
    , new SpaceGroup("57 d2h^11 p_b_c_m -p_2c_2b")
    , new SpaceGroup("57:ba-c d2h^11 p_c_a_m -p_2c_2ac")
    , new SpaceGroup("57:cab d2h^11 p_m_c_a -p_2ac_2a")
    , new SpaceGroup("57:-cba d2h^11 p_m_a_b -p_2b_2a")
    , new SpaceGroup("57:bca d2h^11 p_b_m_a -p_2a_2ab")
    , new SpaceGroup("57:a-cb d2h^11 p_c_m_b -p_2bc_2c")
    , new SpaceGroup("58 d2h^12 p_n_n_m -p_2_2n")
    , new SpaceGroup("58:cab d2h^12 p_m_n_n -p_2n_2")
    , new SpaceGroup("58:bca d2h^12 p_n_m_n -p_2n_2n")
    , new SpaceGroup("59:1 d2h^13 p_m_m_n:1 p_2_2ab_-1ab")
    , new SpaceGroup("59:2 d2h^13 p_m_m_n:2 -p_2ab_2a")
    , new SpaceGroup("59:1cab d2h^13 p_n_m_m:1 p_2bc_2_-1bc")
    , new SpaceGroup("59:2cab d2h^13 p_n_m_m:2 -p_2c_2bc")
    , new SpaceGroup("59:1bca d2h^13 p_m_n_m:1 p_2ac_2ac_-1ac")
    , new SpaceGroup("59:2bca d2h^13 p_m_n_m:2 -p_2c_2a")
    , new SpaceGroup("60 d2h^14 p_b_c_n -p_2n_2ab")
    , new SpaceGroup("60:ba-c d2h^14 p_c_a_n -p_2n_2c")
    , new SpaceGroup("60:cab d2h^14 p_n_c_a -p_2a_2n")
    , new SpaceGroup("60:-cba d2h^14 p_n_a_b -p_2bc_2n")
    , new SpaceGroup("60:bca d2h^14 p_b_n_a -p_2ac_2b")
    , new SpaceGroup("60:a-cb d2h^14 p_c_n_b -p_2b_2ac")
    , new SpaceGroup("61 d2h^15 p_b_c_a -p_2ac_2ab")
    , new SpaceGroup("61:ba-c d2h^15 p_c_a_b -p_2bc_2ac")
    , new SpaceGroup("62 d2h^16 p_n_m_a -p_2ac_2n")
    , new SpaceGroup("62:ba-c d2h^16 p_m_n_b -p_2bc_2a")
    , new SpaceGroup("62:cab d2h^16 p_b_n_m -p_2c_2ab")
    , new SpaceGroup("62:-cba d2h^16 p_c_m_n -p_2n_2ac")
    , new SpaceGroup("62:bca d2h^16 p_m_c_n -p_2n_2a")
    , new SpaceGroup("62:a-cb d2h^16 p_n_a_m -p_2c_2n")
    , new SpaceGroup("63 d2h^17 c_m_c_m -c_2c_2")
    , new SpaceGroup("63:ba-c d2h^17 c_c_m_m -c_2c_2c")
    , new SpaceGroup("63:cab d2h^17 a_m_m_a -a_2a_2a")
    , new SpaceGroup("63:-cba d2h^17 a_m_a_m -a_2_2a")
    , new SpaceGroup("63:bca d2h^17 b_b_m_m -b_2_2b")
    , new SpaceGroup("63:a-cb d2h^17 b_m_m_b -b_2b_2")
    , new SpaceGroup("64 d2h^18 c_m_c_a -c_2ac_2")
    , new SpaceGroup("64:ba-c d2h^18 c_c_m_b -c_2ac_2ac")
    , new SpaceGroup("64:cab d2h^18 a_b_m_a -a_2ab_2ab")
    , new SpaceGroup("64:-cba d2h^18 a_c_a_m -a_2_2ab")
    , new SpaceGroup("64:bca d2h^18 b_b_c_m -b_2_2ab")
    , new SpaceGroup("64:a-cb d2h^18 b_m_a_b -b_2ab_2")
    , new SpaceGroup("65 d2h^19 c_m_m_m -c_2_2")
    , new SpaceGroup("65:cab d2h^19 a_m_m_m -a_2_2")
    , new SpaceGroup("65:bca d2h^19 b_m_m_m -b_2_2")
    , new SpaceGroup("66 d2h^20 c_c_c_m -c_2_2c")
    , new SpaceGroup("66:cab d2h^20 a_m_a_a -a_2a_2")
    , new SpaceGroup("66:bca d2h^20 b_b_m_b -b_2b_2b")
    , new SpaceGroup("67 d2h^21 c_m_m_a -c_2a_2")
    , new SpaceGroup("67:ba-c d2h^21 c_m_m_b -c_2a_2a")
    , new SpaceGroup("67:cab d2h^21 a_b_m_m -a_2b_2b")
    , new SpaceGroup("67:-cba d2h^21 a_c_m_m -a_2_2b")
    , new SpaceGroup("67:bca d2h^21 b_m_c_m -b_2_2a")
    , new SpaceGroup("67:a-cb d2h^21 b_m_a_m -b_2a_2")
    , new SpaceGroup("68:1 d2h^22 c_c_c_a:1 c_2_2_-1ac")
    , new SpaceGroup("68:2 d2h^22 c_c_c_a:2 -c_2a_2ac")
    , new SpaceGroup("68:1ba-c d2h^22 c_c_c_b:1 c_2_2_-1ac")
    , new SpaceGroup("68:2ba-c d2h^22 c_c_c_b:2 -c_2a_2c")
    , new SpaceGroup("68:1cab d2h^22 a_b_a_a:1 a_2_2_-1ab")
    , new SpaceGroup("68:2cab d2h^22 a_b_a_a:2 -a_2a_2b")
    , new SpaceGroup("68:1-cba d2h^22 a_c_a_a:1 a_2_2_-1ab")
    , new SpaceGroup("68:2-cba d2h^22 a_c_a_a:2 -a_2ab_2b")
    , new SpaceGroup("68:1bca d2h^22 b_b_c_b:1 b_2_2_-1ab")
    , new SpaceGroup("68:2bca d2h^22 b_b_c_b:2 -b_2ab_2b")
    , new SpaceGroup("68:1a-cb d2h^22 b_b_a_b:1 b_2_2_-1ab")
    , new SpaceGroup("68:2a-cb d2h^22 b_b_a_b:2 -b_2b_2ab")
    , new SpaceGroup("69 d2h^23 f_m_m_m -f_2_2")
    , new SpaceGroup("70:1 d2h^24 f_d_d_d:1 f_2_2_-1d")
    , new SpaceGroup("70:2 d2h^24 f_d_d_d:2 -f_2uv_2vw")
    , new SpaceGroup("71 d2h^25 i_m_m_m -i_2_2")
    , new SpaceGroup("72 d2h^26 i_b_a_m -i_2_2c")
    , new SpaceGroup("72:cab d2h^26 i_m_c_b -i_2a_2")
    , new SpaceGroup("72:bca d2h^26 i_c_m_a -i_2b_2b")
    , new SpaceGroup("73 d2h^27 i_b_c_a -i_2b_2c")
    , new SpaceGroup("73:ba-c d2h^27 i_c_a_b -i_2a_2b")
    , new SpaceGroup("74 d2h^28 i_m_m_a -i_2b_2")
    , new SpaceGroup("74:ba-c d2h^28 i_m_m_b -i_2a_2a")
    , new SpaceGroup("74:cab d2h^28 i_b_m_m -i_2c_2c")
    , new SpaceGroup("74:-cba d2h^28 i_c_m_m -i_2_2b")
    , new SpaceGroup("74:bca d2h^28 i_m_c_m -i_2_2a")
    , new SpaceGroup("74:a-cb d2h^28 i_m_a_m -i_2c_2")
    , new SpaceGroup("75 c4^1 p_4 p_4")
    , new SpaceGroup("76 c4^2 p_41 p_4w")
    , new SpaceGroup("76* c4^2 p_41* p_41") 
    , new SpaceGroup("77 c4^3 p_42 p_4c")
    , new SpaceGroup("77* c4^3 p_42* p_42") 
    , new SpaceGroup("78 c4^4 p_43 p_4cw")
    , new SpaceGroup("78* c4^4 p_43* p_43") 
    , new SpaceGroup("79 c4^5 i_4 i_4")
    , new SpaceGroup("80 c4^6 i_41 i_4bw")
    , new SpaceGroup("81 s4^1 p_-4 p_-4")
    , new SpaceGroup("82 s4^2 i_-4 i_-4")
    , new SpaceGroup("83 c4h^1 p_4/m -p_4")
    , new SpaceGroup("84 c4h^2 p_42/m -p_4c")
    , new SpaceGroup("84* c4h^2 p_42/m* -p_42") 
    , new SpaceGroup("85:1 c4h^3 p_4/n:1 p_4ab_-1ab")
    , new SpaceGroup("85:2 c4h^3 p_4/n:2 -p_4a")
    , new SpaceGroup("86:1 c4h^4 p_42/n:1 p_4n_-1n")
    , new SpaceGroup("86:2 c4h^4 p_42/n:2 -p_4bc")
    , new SpaceGroup("87 c4h^5 i_4/m -i_4")
    , new SpaceGroup("88:1 c4h^6 i_41/a:1 i_4bw_-1bw")
    , new SpaceGroup("88:2 c4h^6 i_41/a:2 -i_4ad")
    , new SpaceGroup("89 d4^1 p_4_2_2 p_4_2")
    , new SpaceGroup("90 d4^2 p_4_21_2 p_4ab_2ab")
    , new SpaceGroup("91 d4^3 p_41_2_2 p_4w_2c")
    , new SpaceGroup("91* d4^3 p_41_2_2* p_41_2c") 
    , new SpaceGroup("92 d4^4 p_41_21_2 p_4abw_2nw")
    , new SpaceGroup("93 d4^5 p_42_2_2 p_4c_2")
    , new SpaceGroup("93* d4^5 p_42_2_2* p_42_2") 
    , new SpaceGroup("94 d4^6 p_42_21_2 p_4n_2n")
    , new SpaceGroup("95 d4^7 p_43_2_2 p_4cw_2c")
    , new SpaceGroup("95* d4^7 p_43_2_2* p_43_2c") 
    , new SpaceGroup("96 d4^8 p_43_21_2 p_4nw_2abw")
    , new SpaceGroup("97 d4^9 i_4_2_2 i_4_2")
    , new SpaceGroup("98 d4^10 i_41_2_2 i_4bw_2bw")
    , new SpaceGroup("99 c4v^1 p_4_m_m p_4_-2")
    , new SpaceGroup("100 c4v^2 p_4_b_m p_4_-2ab")
    , new SpaceGroup("101 c4v^3 p_42_c_m p_4c_-2c")
    , new SpaceGroup("101* c4v^3 p_42_c_m* p_42_-2c") 
    , new SpaceGroup("102 c4v^4 p_42_n_m p_4n_-2n")
    , new SpaceGroup("103 c4v^5 p_4_c_c p_4_-2c")
    , new SpaceGroup("104 c4v^6 p_4_n_c p_4_-2n")
    , new SpaceGroup("105 c4v^7 p_42_m_c p_4c_-2")
    , new SpaceGroup("105* c4v^7 p_42_m_c* p_42_-2") 
    , new SpaceGroup("106 c4v^8 p_42_b_c p_4c_-2ab")
    , new SpaceGroup("106* c4v^8 p_42_b_c* p_42_-2ab") 
    , new SpaceGroup("107 c4v^9 i_4_m_m i_4_-2")
    , new SpaceGroup("108 c4v^10 i_4_c_m i_4_-2c")
    , new SpaceGroup("109 c4v^11 i_41_m_d i_4bw_-2")
    , new SpaceGroup("110 c4v^12 i_41_c_d i_4bw_-2c")
    , new SpaceGroup("111 d2d^1 p_-4_2_m p_-4_2")
    , new SpaceGroup("112 d2d^2 p_-4_2_c p_-4_2c")
    , new SpaceGroup("113 d2d^3 p_-4_21_m p_-4_2ab")
    , new SpaceGroup("114 d2d^4 p_-4_21_c p_-4_2n")
    , new SpaceGroup("115 d2d^5 p_-4_m_2 p_-4_-2")
    , new SpaceGroup("116 d2d^6 p_-4_c_2 p_-4_-2c")
    , new SpaceGroup("117 d2d^7 p_-4_b_2 p_-4_-2ab")
    , new SpaceGroup("118 d2d^8 p_-4_n_2 p_-4_-2n")
    , new SpaceGroup("119 d2d^9 i_-4_m_2 i_-4_-2")
    , new SpaceGroup("120 d2d^10 i_-4_c_2 i_-4_-2c")
    , new SpaceGroup("121 d2d^11 i_-4_2_m i_-4_2")
    , new SpaceGroup("122 d2d^12 i_-4_2_d i_-4_2bw")
    , new SpaceGroup("123 d4h^1 p_4/m_m_m -p_4_2")
    , new SpaceGroup("124 d4h^2 p_4/m_c_c -p_4_2c")
    , new SpaceGroup("125:1 d4h^3 p_4/n_b_m:1 p_4_2_-1ab")
    , new SpaceGroup("125:2 d4h^3 p_4/n_b_m:2 -p_4a_2b")
    , new SpaceGroup("126:1 d4h^4 p_4/n_n_c:1 p_4_2_-1n")
    , new SpaceGroup("126:2 d4h^4 p_4/n_n_c:2 -p_4a_2bc")
    , new SpaceGroup("127 d4h^5 p_4/m_b_m -p_4_2ab")
    , new SpaceGroup("128 d4h^6 p_4/m_n_c -p_4_2n")
    , new SpaceGroup("129:1 d4h^7 p_4/n_m_m:1 p_4ab_2ab_-1ab")
    , new SpaceGroup("129:2 d4h^7 p_4/n_m_m:2 -p_4a_2a")
    , new SpaceGroup("130:1 d4h^8 p_4/n_c_c:1 p_4ab_2n_-1ab")
    , new SpaceGroup("130:2 d4h^8 p_4/n_c_c:2 -p_4a_2ac")
    , new SpaceGroup("131 d4h^9 p_42/m_m_c -p_4c_2")
    , new SpaceGroup("132 d4h^10 p_42/m_c_m -p_4c_2c")
    , new SpaceGroup("133:1 d4h^11 p_42/n_b_c:1 p_4n_2c_-1n")
    , new SpaceGroup("133:2 d4h^11 p_42/n_b_c:2 -p_4ac_2b")
    , new SpaceGroup("134:1 d4h^12 p_42/n_n_m:1 p_4n_2_-1n")
    , new SpaceGroup("134:2 d4h^12 p_42/n_n_m:2 -p_4ac_2bc")
    , new SpaceGroup("135 d4h^13 p_42/m_b_c -p_4c_2ab")
    , new SpaceGroup("135* d4h^13 p_42/m_b_c* -p_42_2ab") 
    , new SpaceGroup("136 d4h^14 p_42/m_n_m -p_4n_2n")
    , new SpaceGroup("137:1 d4h^15 p_42/n_m_c:1 p_4n_2n_-1n")
    , new SpaceGroup("137:2 d4h^15 p_42/n_m_c:2 -p_4ac_2a")
    , new SpaceGroup("138:1 d4h^16 p_42/n_c_m:1 p_4n_2ab_-1n")
    , new SpaceGroup("138:2 d4h^16 p_42/n_c_m:2 -p_4ac_2ac")
    , new SpaceGroup("139 d4h^17 i_4/m_m_m -i_4_2")
    , new SpaceGroup("140 d4h^18 i_4/m_c_m -i_4_2c")
    , new SpaceGroup("141:1 d4h^19 i_41/a_m_d:1 i_4bw_2bw_-1bw")
    , new SpaceGroup("141:2 d4h^19 i_41/a_m_d:2 -i_4bd_2")
    , new SpaceGroup("142:1 d4h^20 i_41/a_c_d:1 i_4bw_2aw_-1bw")
    , new SpaceGroup("142:2 d4h^20 i_41/a_c_d:2 -i_4bd_2c")
    , new SpaceGroup("143 c3^1 p_3 p_3")
    , new SpaceGroup("144 c3^2 p_31 p_31")
    , new SpaceGroup("145 c3^3 p_32 p_32")
    , new SpaceGroup("146:h c3^4 r_3:h r_3")
    , new SpaceGroup("146:r c3^4 r_3:r p_3*")
    , new SpaceGroup("147 c3i^1 p_-3 -p_3")
    , new SpaceGroup("148:h c3i^2 r_-3:h -r_3")
    , new SpaceGroup("148:r c3i^2 r_-3:r -p_3*")
    , new SpaceGroup("149 d3^1 p_3_1_2 p_3_2")
    , new SpaceGroup("150 d3^2 p_3_2_1 p_3_2\"")
    , new SpaceGroup("151 d3^3 p_31_1_2 p_31_2_(0_0_4)")
    , new SpaceGroup("152 d3^4 p_31_2_1 p_31_2\"")
    , new SpaceGroup("153 d3^5 p_32_1_2 p_32_2_(0_0_2)")
    , new SpaceGroup("154 d3^6 p_32_2_1 p_32_2\"")
    , new SpaceGroup("155:h d3^7 r_3_2:h r_3_2\"")
    , new SpaceGroup("155:r d3^7 r_3_2:r p_3*_2")
    , new SpaceGroup("156 c3v^1 p_3_m_1 p_3_-2\"")
    , new SpaceGroup("157 c3v^2 p_3_1_m p_3_-2")
    , new SpaceGroup("158 c3v^3 p_3_c_1 p_3_-2\"c")
    , new SpaceGroup("159 c3v^4 p_3_1_c p_3_-2c")
    , new SpaceGroup("160:h c3v^5 r_3_m:h r_3_-2\"")
    , new SpaceGroup("160:r c3v^5 r_3_m:r p_3*_-2")
    , new SpaceGroup("161:h c3v^6 r_3_c:h r_3_-2\"c")
    , new SpaceGroup("161:r c3v^6 r_3_c:r p_3*_-2n")
    , new SpaceGroup("162 d3d^1 p_-3_1_m -p_3_2")
    , new SpaceGroup("163 d3d^2 p_-3_1_c -p_3_2c")
    , new SpaceGroup("164 d3d^3 p_-3_m_1 -p_3_2\"")
    , new SpaceGroup("165 d3d^4 p_-3_c_1 -p_3_2\"c")
    , new SpaceGroup("166:h d3d^5 r_-3_m:h -r_3_2\"")
    , new SpaceGroup("166:r d3d^5 r_-3_m:r -p_3*_2")
    , new SpaceGroup("167:h d3d^6 r_-3_c:h -r_3_2\"c")
    , new SpaceGroup("167:r d3d^6 r_-3_c:r -p_3*_2n")
    , new SpaceGroup("168 c6^1 p_6 p_6")
    , new SpaceGroup("169 c6^2 p_61 p_61")
    , new SpaceGroup("170 c6^3 p_65 p_65")
    , new SpaceGroup("171 c6^4 p_62 p_62")
    , new SpaceGroup("172 c6^5 p_64 p_64")
    , new SpaceGroup("173 c6^6 p_63 p_6c")
    , new SpaceGroup("173* c6^6 p_63* p_63") 
    , new SpaceGroup("174 c3h^1 p_-6 p_-6")
    , new SpaceGroup("175 c6h^1 p_6/m -p_6")
    , new SpaceGroup("176 c6h^2 p_63/m -p_6c")
    , new SpaceGroup("176* c6h^2 p_63/m* -p_63") 
    , new SpaceGroup("177 d6^1 p_6_2_2 p_6_2")
    , new SpaceGroup("178 d6^2 p_61_2_2 p_61_2_(0_0_5)")
    , new SpaceGroup("179 d6^3 p_65_2_2 p_65_2_(0_0_1)")
    , new SpaceGroup("180 d6^4 p_62_2_2 p_62_2_(0_0_4)")
    , new SpaceGroup("181 d6^5 p_64_2_2 p_64_2_(0_0_2)")
    , new SpaceGroup("182 d6^6 p_63_2_2 p_6c_2c")
    , new SpaceGroup("182* d6^6 p_63_2_2* p_63_2c") 
    , new SpaceGroup("183 c6v^1 p_6_m_m p_6_-2")
    , new SpaceGroup("184 c6v^2 p_6_c_c p_6_-2c")
    , new SpaceGroup("185 c6v^3 p_63_c_m p_6c_-2")
    , new SpaceGroup("185* c6v^3 p_63_c_m* p_63_-2") 
    , new SpaceGroup("186 c6v^4 p_63_m_c p_6c_-2c")
    , new SpaceGroup("186* c6v^4 p_63_m_c* p_63_-2c") 
    , new SpaceGroup("187 d3h^1 p_-6_m_2 p_-6_2")
    , new SpaceGroup("188 d3h^2 p_-6_c_2 p_-6c_2")
    , new SpaceGroup("189 d3h^3 p_-6_2_m p_-6_-2")
    , new SpaceGroup("190 d3h^4 p_-6_2_c p_-6c_-2c")
    , new SpaceGroup("191 d6h^1 p_6/m_m_m -p_6_2")
    , new SpaceGroup("192 d6h^2 p_6/m_c_c -p_6_2c")
    , new SpaceGroup("193 d6h^3 p_63/m_c_m -p_6c_2")
    , new SpaceGroup("193* d6h^3 p_63/m_c_m* -p_63_2") 
    , new SpaceGroup("194 d6h^4 p_63/m_m_c -p_6c_2c")
    , new SpaceGroup("194* d6h^4 p_63/m_m_c* -p_63_2c") 
    , new SpaceGroup("195 t^1 p_2_3 p_2_2_3")
    , new SpaceGroup("196 t^2 f_2_3 f_2_2_3")
    , new SpaceGroup("197 t^3 i_2_3 i_2_2_3")
    , new SpaceGroup("198 t^4 p_21_3 p_2ac_2ab_3")
    , new SpaceGroup("199 t^5 i_21_3 i_2b_2c_3")
    , new SpaceGroup("200 th^1 p_m_-3 -p_2_2_3")
    , new SpaceGroup("201:1 th^2 p_n_-3:1 p_2_2_3_-1n")
    , new SpaceGroup("201:2 th^2 p_n_-3:2 -p_2ab_2bc_3")
    , new SpaceGroup("202 th^3 f_m_-3 -f_2_2_3")
    , new SpaceGroup("203:1 th^4 f_d_-3:1 f_2_2_3_-1d")
    , new SpaceGroup("203:2 th^4 f_d_-3:2 -f_2uv_2vw_3")
    , new SpaceGroup("204 th^5 i_m_-3 -i_2_2_3")
    , new SpaceGroup("205 th^6 p_a_-3 -p_2ac_2ab_3")
    , new SpaceGroup("206 th^7 i_a_-3 -i_2b_2c_3")
    , new SpaceGroup("207 o^1 p_4_3_2 p_4_2_3")
    , new SpaceGroup("208 o^2 p_42_3_2 p_4n_2_3")
    , new SpaceGroup("209 o^3 f_4_3_2 f_4_2_3")
    , new SpaceGroup("210 o^4 f_41_3_2 f_4d_2_3")
    , new SpaceGroup("211 o^5 i_4_3_2 i_4_2_3")
    , new SpaceGroup("212 o^6 p_43_3_2 p_4acd_2ab_3")
    , new SpaceGroup("213 o^7 p_41_3_2 p_4bd_2ab_3")
    , new SpaceGroup("214 o^8 i_41_3_2 i_4bd_2c_3")
    , new SpaceGroup("215 td^1 p_-4_3_m p_-4_2_3")
    , new SpaceGroup("216 td^2 f_-4_3_m f_-4_2_3")
    , new SpaceGroup("217 td^3 i_-4_3_m i_-4_2_3")
    , new SpaceGroup("218 td^4 p_-4_3_n p_-4n_2_3")
    , new SpaceGroup("219 td^5 f_-4_3_c f_-4a_2_3")
    , new SpaceGroup("220 td^6 i_-4_3_d i_-4bd_2c_3")
    , new SpaceGroup("221 oh^1 p_m_-3_m -p_4_2_3")
    , new SpaceGroup("222:1 oh^2 p_n_-3_n:1 p_4_2_3_-1n")
    , new SpaceGroup("222:2 oh^2 p_n_-3_n:2 -p_4a_2bc_3")
    , new SpaceGroup("223 oh^3 p_m_-3_n -p_4n_2_3")
    , new SpaceGroup("224:1 oh^4 p_n_-3_m:1 p_4n_2_3_-1n")
    , new SpaceGroup("224:2 oh^4 p_n_-3_m:2 -p_4bc_2bc_3")
    , new SpaceGroup("225 oh^5 f_m_-3_m -f_4_2_3")
    , new SpaceGroup("226 oh^6 f_m_-3_c -f_4a_2_3")
    , new SpaceGroup("227:1 oh^7 f_d_-3_m:1 f_4d_2_3_-1d")
    , new SpaceGroup("227:2 oh^7 f_d_-3_m:2 -f_4vw_2vw_3")
    , new SpaceGroup("228:1 oh^8 f_d_-3_c:1 f_4d_2_3_-1ad")
    , new SpaceGroup("228:2 oh^8 f_d_-3_c:2 -f_4ud_2vw_3")
    , new SpaceGroup("229 oh^9 i_m_-3_m -i_4_2_3")
    , new SpaceGroup("230 oh^10 i_a_-3_d -i_4bd_2c_3")
  };
}
