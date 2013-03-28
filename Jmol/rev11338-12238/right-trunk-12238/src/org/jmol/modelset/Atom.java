

package org.jmol.modelset;

import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.viewer.Viewer;
import org.jmol.api.SymmetryInterface;
import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.util.Point3fi;
import org.jmol.util.Quaternion;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point3i;

final public class Atom extends Point3fi {

  private final static byte VIBRATION_VECTOR_FLAG = 1;
  private final static byte IS_HETERO_FLAG = 2;
  private final static byte FLAG_MASK = 3;
  
  public static final int RADIUS_MAX = 16;
  

  Group group;
  private BitSet atomSymmetry;
  int atomSite;
  private float userDefinedVanDerWaalRadius;
  
  public int getScreenRadius() {
    return screenDiameter / 2;
  }
  
  private short atomicAndIsotopeNumber;
  private byte formalChargeAndFlags;
  private byte valence;
  char alternateLocationID;
  short madAtom;
  public short getMadAtom() {
    return madAtom;
  }
  
  short colixAtom;
  byte paletteID = JmolConstants.PALETTE_CPK;

  Bond[] bonds;
  int nBondsDisplayed = 0;
  int nBackbonesDisplayed = 0;
  
  public int getNBackbonesDisplayed() {
    return nBackbonesDisplayed;
  }
  
  int clickabilityFlags;
  int shapeVisibilityFlags;
  boolean isSimple = false;
  public boolean isSimple() {
    return isSimple;
  }
  
  public Atom(Point3f pt) {
    
    isSimple = true;
    this.x = pt.x; this.y = pt.y; this.z = pt.z;
    
  }
  
  Atom(int modelIndex, int atomIndex,
        float x, float y, float z, float radius,
        BitSet atomSymmetry, int atomSite,
        short atomicAndIsotopeNumber, int formalCharge, 
        boolean isHetero, char chainID, char alternateLocationID) {
    this.modelIndex = (short)modelIndex;
    this.atomSymmetry = atomSymmetry;
    this.atomSite = atomSite;
    this.index = atomIndex;
    this.atomicAndIsotopeNumber = atomicAndIsotopeNumber;
    if (isHetero)
      formalChargeAndFlags = IS_HETERO_FLAG;
    setFormalCharge(formalCharge);
    this.alternateLocationID = alternateLocationID;
    userDefinedVanDerWaalRadius = radius;
    set(x, y, z);
  }

  public final void setShapeVisibilityFlags(int flag) {
    shapeVisibilityFlags = flag;
  }

  public final void setShapeVisibility(int shapeVisibilityFlag, boolean isVisible) {
    if(isVisible) {
      shapeVisibilityFlags |= shapeVisibilityFlag;        
    } else {
      shapeVisibilityFlags &=~shapeVisibilityFlag;
    }
  }
  
  public boolean isBonded(Atom atomOther) {
    if (bonds != null)
      for (int i = bonds.length; --i >= 0;)
        if (bonds[i].getOtherAtom(this) == atomOther)
          return true;
    return false;
  }

  public Bond getBond(Atom atomOther) {
    if (bonds != null)
      for (int i = bonds.length; --i >= 0;)
        if (bonds[i].getOtherAtom(atomOther) != null)
          return bonds[i];
    return null;
  }

  void addDisplayedBond(int stickVisibilityFlag, boolean isVisible){
    nBondsDisplayed+=(isVisible ? 1 : -1);
    setShapeVisibility(stickVisibilityFlag, isVisible);
  } 
  
  public void addDisplayedBackbone(int backboneVisibilityFlag, boolean isVisible){
    nBackbonesDisplayed+=(isVisible ? 1 : -1);
    setShapeVisibility(backboneVisibilityFlag, isVisible);
  }
  
  void deleteBond(Bond bond) {
    
    for (int i = bonds.length; --i >= 0; )
      if (bonds[i] == bond) {
        deleteBond(i);
        return;
      }
  }

  private void deleteBond(int i) {
    int newLength = bonds.length - 1;
    if (newLength == 0) {
      bonds = null;
      return;
    }
    Bond[] bondsNew = new Bond[newLength];
    int j = 0;
    for ( ; j < i; ++j)
      bondsNew[j] = bonds[j];
    for ( ; j < newLength; ++j)
      bondsNew[j] = bonds[j + 1];
    bonds = bondsNew;
  }

  void clearBonds() {
    bonds = null;
  }

  int getBondedAtomIndex(int bondIndex) {
    return bonds[bondIndex].getOtherAtom(this).index;
  }

  

  public void setMadAtom(Viewer viewer, RadiusData rd) {
    madAtom = calculateMad(viewer, rd);
  }
  
  
  public short calculateMad(Viewer viewer, RadiusData rd) {
    if (rd == null)
      return 0;
    float f = rd.value;
    if (f == 0)
      return 0;
    switch (rd.type) {
    case RadiusData.TYPE_SCREEN:
       return (short) f;
    case RadiusData.TYPE_FACTOR:
    case RadiusData.TYPE_OFFSET:
      float r = 0;
      switch (rd.vdwType) {
      case Token.temperature:
        float tmax = viewer.getBfactor100Hi();
        r = (tmax > 0 ? getBfactor100() / tmax : 0);
        break;
      case Token.ionic:
        r = getBondingRadiusFloat();
        break;
      case Token.adpmin:
      case Token.adpmax:
        r = getADPMinMax(rd.vdwType == Token.adpmax);
        break;
      default:
        r = getVanderwaalsRadiusFloat(viewer, rd.vdwType);
      }
      if (rd.type == RadiusData.TYPE_FACTOR)
        f *= r;
      else
        r += r;
      break;
    default:
    case RadiusData.TYPE_ABSOLUTE:
      break;
    }
    short mad = (short) (f * 2000);
    if (mad < 0)
      mad = 0;
    return mad; 
  }

  public float getADPMinMax(boolean isMax) {
    Object[] ellipsoid = getEllipsoid();
    if (ellipsoid == null)
      return 0;
    return ((float[])ellipsoid[1])[isMax ? 5 : 3];
  }

  public int getRasMolRadius() {
    return Math.abs(madAtom / 8); 
  }

  public int getCovalentBondCount() {
    if (bonds == null)
      return 0;
    int n = 0;
    Bond b;
    for (int i = bonds.length; --i >= 0; )
      if (((b = bonds[i]).order & JmolConstants.BOND_COVALENT_MASK) != 0
          && !b.getOtherAtom(this).isDeleted())
        ++n;
    return n;
  }

  int getCovalentHydrogenCount() {
    if (bonds == null)
      return 0;
    int n = 0;
    for (int i = bonds.length; --i >= 0; ) {
      if ((bonds[i].order & JmolConstants.BOND_COVALENT_MASK) == 0)
        continue;
      Atom a = bonds[i].getOtherAtom(this);
      if (a.valence >= 0 && a.getElementNumber() == 1)
        ++n;
    }
    return n;
  }

  public Bond[] getBonds() {
    return bonds;
  }

  public void setColixAtom(short colixAtom) {
    this.colixAtom = colixAtom;
  }

  public void setPaletteID(byte paletteID) {
    this.paletteID = paletteID;
  }

  public void setTranslucent(boolean isTranslucent, float translucentLevel) {
    colixAtom = Graphics3D.getColixTranslucent(colixAtom, isTranslucent, translucentLevel);    
  }

  public boolean isTranslucent() {
    return Graphics3D.isColixTranslucent(colixAtom);
  }

  public short getElementNumber() {
    return (short) (atomicAndIsotopeNumber % 128);
  }
  
  public short getIsotopeNumber() {
    return (short) (atomicAndIsotopeNumber >> 7);
  }
  
  public short getAtomicAndIsotopeNumber() {
    return atomicAndIsotopeNumber;
  }

  public void setAtomicAndIsotopeNumber(int n) {
    if (n < 0 || (n % 128) >= JmolConstants.elementNumberMax || n > Short.MAX_VALUE)
      n = 0;
    atomicAndIsotopeNumber = (short) n;
  }

  public String getElementSymbol(boolean withIsotope) {
    return JmolConstants.elementSymbolFromNumber(withIsotope ? atomicAndIsotopeNumber : atomicAndIsotopeNumber % 128);    
  }
  
  public String getElementSymbol() {
    return getElementSymbol(true);
  }

  public char getAlternateLocationID() {
    return alternateLocationID;
  }
  
  boolean isAlternateLocationMatch(String strPattern) {
    if (strPattern == null)
      return (alternateLocationID == '\0');
    if (strPattern.length() != 1)
      return false;
    char ch = strPattern.charAt(0);
    return (ch == '*' 
        || ch == '?' && alternateLocationID != '\0' 
        || alternateLocationID == ch);
  }

  public boolean isHetero() {
    return (formalChargeAndFlags & IS_HETERO_FLAG) != 0;
  }

  void setFormalCharge(int charge) {
    formalChargeAndFlags = (byte)((formalChargeAndFlags & FLAG_MASK) 
        | ((charge == Integer.MIN_VALUE ? 0 : charge > 7 ? 7 : charge < -3 ? -3 : charge) << 2));
  }
  
  void setVibrationVector() {
    formalChargeAndFlags |= VIBRATION_VECTOR_FLAG;
  }
  
  public int getFormalCharge() {
    return formalChargeAndFlags >> 2;
  }

  
  public int getOccupancy100() {
    byte[] occupancies = group.chain.modelSet.occupancies;
    return occupancies == null ? 100 : occupancies[index];
  }

  
  
  public int getBfactor100() {
    short[] bfactor100s = group.chain.modelSet.bfactor100s;
    if (bfactor100s == null)
      return 0;
    return bfactor100s[index];
  }

  public boolean setRadius(float radius) {
    return !Float.isNaN(userDefinedVanDerWaalRadius = (radius > 0 ? radius : Float.NaN));  
  }
  
  public void delete() {
    valence = -1;
    if (bonds != null)
      for (int i = bonds.length; --i >= 0; )
        bonds[i].getOtherAtom(this).deleteBond(bonds[i]);
    bonds = null;
  }

  public boolean isDeleted() {
    return (valence < 0);
  }

  public void setValence(int nBonds) {
    if (isDeleted()) 
      return;
    valence = (byte) (nBonds < 0 ? 0 : nBonds < 0xEF ? nBonds : 0xEF);
  }

  public int getValence() {
    if (isDeleted())
      return -1;
    int n = valence;
    if (n == 0 && bonds != null)
      for (int i = bonds.length; --i >= 0;)
        n += bonds[i].getValence();
    return n;
  }

  public float getDimensionValue(int dimension) {
    return (dimension == 0 ? x : (dimension == 1 ? y : z));
  }

  public float getVanderwaalsRadiusFloat(Viewer viewer, int iType) {
    
    
    
    
    
    return (Float.isNaN(userDefinedVanDerWaalRadius) 
        ? viewer.getVanderwaalsMar(atomicAndIsotopeNumber % 128, getVdwType(iType)) / 1000f
        : userDefinedVanDerWaalRadius);
  }

  
  private int getVdwType(int iType) {
    switch (iType) {
    case JmolConstants.VDW_AUTO:
      iType = group.chain.modelSet.getDefaultVdwType(modelIndex);
      break;
    case JmolConstants.VDW_NOJMOL:
      iType = group.chain.modelSet.getDefaultVdwType(modelIndex);
      if (iType == JmolConstants.VDW_AUTO_JMOL)
        iType = JmolConstants.VDW_AUTO_BABEL;
      break;
    }
    return iType;
  }

  public float getCovalentRadiusFloat() {
    return JmolConstants.getBondingRadiusFloat(atomicAndIsotopeNumber % 128, 0);
  }

  public float getBondingRadiusFloat() {
    return JmolConstants.getBondingRadiusFloat(atomicAndIsotopeNumber % 128,
        getFormalCharge());
  }

  float getVolume(int iType) {
    float r1 = (iType < 0 ? userDefinedVanDerWaalRadius : Float.NaN);
    if (Float.isNaN(r1))
      r1 = group.chain.modelSet.viewer.getVanderwaalsMar(getElementNumber(), getVdwType(iType)) / 1000f;
    double volume = 0;
    for (int j = 0; j < bonds.length; j++) {
      if (!bonds[j].isCovalent())
        continue;
      Atom atom2 = bonds[j].getOtherAtom(this);
      float r2 = (iType < 0 ? atom2.userDefinedVanDerWaalRadius : Float.NaN);
      if (Float.isNaN(r2))
          r2= group.chain.modelSet.viewer.getVanderwaalsMar(atom2.getElementNumber(), atom2.getVdwType(iType)) / 1000f;
      float d = distance(atom2);
      if (d > r1 + r2)
        continue;
      if (d + r1 <= r2)
        return 0;

      
      
      
      double h = r1 - (r1*r1 + d*d - r2*r2) / (2.0 * d);
      volume -= Math.PI / 3 * h * h * (3 * r1 - h);
    }
    return (float) (volume + 4 * Math.PI / 3 * r1 * r1 * r1);
  }

  int getCurrentBondCount() {
    return bonds == null ? 0 : bonds.length;
  }

  public short getColix() {
    return colixAtom;
  }

  public byte getPaletteID() {
    return paletteID;
  }

  public float getRadius() {
    return Math.abs(madAtom / (1000f * 2));
  }

  public int getIndex() {
    return index;
  }

  public int getAtomSite() {
    return atomSite;
  }

  public BitSet getAtomSymmetry() {
    return atomSymmetry;
  }

   void setGroup(Group group) {
     this.group = group;
   }

   public Group getGroup() {
     return group;
   }
   
   public void transform(Viewer viewer) {
     Point3i screen;
     Vector3f[] vibrationVectors;
     if ((formalChargeAndFlags & VIBRATION_VECTOR_FLAG) == 0 ||
         (vibrationVectors = group.chain.modelSet.vibrationVectors) == null)
       screen = viewer.transformPoint(this);
     else 
       screen = viewer.transformPoint(this, vibrationVectors[index]);
     screenX = screen.x;
     screenY = screen.y;
     screenZ = screen.z;
     screenDiameter = viewer.scaleToScreen(screenZ, Math.abs(madAtom));
   }

   
   
   
   public String getAtomName() {
     return group.chain.modelSet.atomNames[index];
   }
   
   public String getAtomType() {
    String[] atomTypes = group.chain.modelSet.atomTypes;
    String type = (atomTypes == null ? null : atomTypes[index]);
    return (type == null ? group.chain.modelSet.atomNames[index] : type);
  }
   
   public int getAtomNumber() {
     int[] atomSerials = group.chain.modelSet.atomSerials;
     return (atomSerials != null ? atomSerials[index] : index);

   }

   public boolean isInFrame() {
     return ((shapeVisibilityFlags & JmolConstants.ATOM_IN_FRAME) != 0);
   }

   public int getShapeVisibilityFlags() {
     return shapeVisibilityFlags;
   }
   
   public boolean isShapeVisible(int shapeVisibilityFlag) {
     return ((shapeVisibilityFlags & shapeVisibilityFlag) != 0);
   }

   public float getPartialCharge() {
     float[] partialCharges = group.chain.modelSet.partialCharges;
     return partialCharges == null ? 0 : partialCharges[index];
   }

   public float getStraightness() {
     return group.getStraightness();
   }

   public Object[] getEllipsoid() {
     return group.chain.modelSet.getEllipsoid(index);
   }

   
   public int getSymmetryTranslation(int symop, int[] cellRange, int nOps) {
     int pt = symop;
     for (int i = 0; i < cellRange.length; i++)
       if (atomSymmetry.get(pt += nOps))
         return cellRange[i];
     return 0;
   }
   
   
   public int getCellTranslation(int cellNNN, int[] cellRange, int nOps) {
     int pt = nOps;
     for (int i = 0; i < cellRange.length; i++)
       for (int j = 0; j < nOps;j++, pt++)
       if (atomSymmetry.get(pt) && cellRange[i] == cellNNN)
         return cellRange[i];
     return 0;
   }
   
   String getSymmetryOperatorList() {
    String str = "";
    ModelSet f = group.chain.modelSet;
    int nOps = f.getModelSymmetryCount(modelIndex);
    if (nOps == 0 || atomSymmetry == null)
      return "";
    int[] cellRange = f.getModelCellRange(modelIndex);
    int pt = nOps;
    int n = (cellRange == null ? 1 : cellRange.length);
    for (int i = 0; i < n; i++)
      for (int j = 0; j < nOps; j++)
        if (atomSymmetry.get(pt++))
          str += "," + (j + 1) + "" + cellRange[i];
    return str.substring(1);
  }
   
   public int getModelIndex() {
     return modelIndex;
   }
   
   public int getMoleculeNumber() {
     return (group.chain.modelSet.getMoleculeIndex(index) + 1);
   }
   
   String getClientAtomStringProperty(String propertyName) {
     Object[] clientAtomReferences = group.chain.modelSet.clientAtomReferences;
     return
       ((clientAtomReferences==null || clientAtomReferences.length<=index)
        ? null : (group.chain.modelSet.viewer.
           getClientAtomStringProperty(clientAtomReferences[index],
                                       propertyName)));
   }

   public byte getSpecialAtomID() {
     byte[] specialAtomIDs = group.chain.modelSet.specialAtomIDs;
     return specialAtomIDs == null ? 0 : specialAtomIDs[index];
   }
   
  public float getFractionalCoord(char ch) {
    Point3f pt = getFractionalCoord();
    return (ch == 'X' ? pt.x : ch == 'Y' ? pt.y : pt.z);
  }
    
  public float getFractionalUnitCoord(char ch) {
    Point3f pt = getFractionalUnitCoord(false);
    return (ch == 'X' ? pt.x : ch == 'Y' ? pt.y : pt.z);
  }

  public Point3f getFractionalCoord() {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    Point3f pt = new Point3f(this);
    if (c != null)
      c[modelIndex].toFractional(pt);
    return pt;
  }
  
  public Point3f getFractionalUnitCoord(boolean asCartesian) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    Point3f pt = new Point3f(this);
    if (c != null) {
      c[modelIndex].toUnitCell(pt, null);
      if (!asCartesian)
        c[modelIndex].toFractional(pt);
    }
    return pt;
  }
  
  public float getFractionalUnitDistance(Point3f pt, Point3f ptTemp1, Point3f ptTemp2) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c == null) 
      return distance(pt);
    ptTemp1.set(this);
    c[modelIndex].toUnitCell(ptTemp1, null);
    ptTemp2.set(pt);
    c[modelIndex].toUnitCell(ptTemp2, null);
    return ptTemp1.distance(ptTemp2);
  }
  
  void setFractionalCoord(int tok, float fValue) {
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c != null)
      c[modelIndex].toFractional(this);
    switch (tok) {
    case Token.fracx:
      x = fValue;
      break;
    case Token.fracy:
      y = fValue;
      break;
    case Token.fracz:
      z = fValue;
      break;
    }
    if (c != null)
      c[modelIndex].toCartesian(this);
  }
  
  void setFractionalCoord(Point3f ptNew) {
    set(ptNew);
    SymmetryInterface[] c = group.chain.modelSet.unitCells;
    if (c != null)
      c[modelIndex].toCartesian(this);
  }
  
  boolean isCursorOnTopOf(int xCursor, int yCursor,
                        int minRadius, Atom competitor) {
    int r = screenDiameter / 2;
    if (r < minRadius)
      r = minRadius;
    int r2 = r * r;
    int dx = screenX - xCursor;
    int dx2 = dx * dx;
    if (dx2 > r2)
      return false;
    int dy = screenY - yCursor;
    int dy2 = dy * dy;
    int dz2 = r2 - (dx2 + dy2);
    if (dz2 < 0)
      return false;
    if (competitor == null)
      return true;
    int z = screenZ;
    int zCompetitor = competitor.screenZ;
    int rCompetitor = competitor.screenDiameter / 2;
    if (z < zCompetitor - rCompetitor)
      return true;
    int dxCompetitor = competitor.screenX - xCursor;
    int dx2Competitor = dxCompetitor * dxCompetitor;
    int dyCompetitor = competitor.screenY - yCursor;
    int dy2Competitor = dyCompetitor * dyCompetitor;
    int r2Competitor = rCompetitor * rCompetitor;
    int dz2Competitor = r2Competitor - (dx2Competitor + dy2Competitor);
    return (z - Math.sqrt(dz2) < zCompetitor - Math.sqrt(dz2Competitor));
  }

  
   
  public String getInfo() {
    return getIdentity(true);
  } 

  String getInfoXYZ(boolean useChimeFormat) {
    if (useChimeFormat) {
      String group3 = getGroup3(true);
      char chainID = getChainID();
      Point3f pt = (group.chain.modelSet.unitCells == null ? null : getFractionalCoord());
      return "Atom: " + (group3 == null ? getElementSymbol() : getAtomName()) + " " + getAtomNumber() 
          + (group3 != null && group3.length() > 0 ? 
              (isHetero() ? " Hetero: " : " Group: ") + group3 + " " + getResno() 
              + (chainID != 0 && chainID != ' ' ? " Chain: " + chainID : "")              
              : "")
          + " Model: " + getModelNumber()
          + " Coordinates: " + x + " " + y + " " + z
          + (pt == null ? "" : " Fractional: "  + pt.x + " " + pt.y + " " + pt.z); 
    }
    return getIdentity(true) + " " + x + " " + y + " " + z;
  }

  String getIdentityXYZ() {
    return getIdentity(false) + " " + x + " " + y + " " + z;
  }
  
  String getIdentity(boolean allInfo) {
    StringBuffer info = new StringBuffer();
    String group3 = getGroup3(true);
    String seqcodeString = getSeqcodeString();
    char chainID = getChainID();
    if (group3 != null && group3.length() > 0) {
      info.append("[");
      info.append(group3);
      info.append("]");
    }
    if (seqcodeString != null)
      info.append(seqcodeString);
    if (chainID != 0 && chainID != ' ') {
      info.append(":");
      info.append(chainID);
    }
    if (!allInfo)
      return info.toString();
    if (info.length() > 0)
      info.append(".");
    info.append(getAtomName());
    if (info.length() == 0) {
      
      info.append(getElementSymbol(false));
      info.append(" ");
      info.append(getAtomNumber());
    }
    if (alternateLocationID != 0) {
      info.append("%");
      info.append(alternateLocationID);
    }
    if (group.chain.modelSet.getModelCount() > 1) {
      info.append("/");
      info.append(getModelNumberForLabel());
    }
    info.append(" #");
    info.append(getAtomNumber());
    return info.toString();
  }

  public int getGroupIndex() {
    return group.getGroupIndex();
  }
  
  public String getGroup3(boolean allowNull) {
    String group3 = group.getGroup3();
    return (allowNull || group3 != null || group3.length() > 0 ? group3 : "UNK");
  }

  public String getGroup1(char c0) {
    char c = group.getGroup1();
    return (c != '\0' ? "" + c : c0 != '\0' ? "" + c0 : "");
  }

  boolean isGroup3(String group3) {
    return group.isGroup3(group3);
  }

  boolean isProtein() {
    return group.isProtein();
  }

  boolean isCarbohydrate() {
    return group.isCarbohydrate();
  }

  boolean isNucleic() {
    return group.isNucleic();
  }

  boolean isDna() {
    return group.isDna();
  }
  
  boolean isRna() {
    return group.isRna();
  }

  boolean isPurine() {
    return group.isPurine();
  }

  boolean isPyrimidine() {
    return group.isPyrimidine();
  }

  int getSeqcode() {
    return group.getSeqcode();
  }

  public int getResno() {
    return group.getResno();   
  }

  public boolean isClickable() {
    
    if (!isVisible(0))
      return false;
    int flags = shapeVisibilityFlags | group.shapeVisibilityFlags;
    return ((flags & clickabilityFlags) != 0);
  }

  public int getClickabilityFlags() {
    return clickabilityFlags;
  }
  
  public void setClickable(int flag) {
    if (flag == 0)
      clickabilityFlags = 0;
    else
      clickabilityFlags |= flag;
  }
  
  
  public boolean isVisible(int flags) {
    
    if (!isInFrame() || group.chain.modelSet.isAtomHidden(index))
      return false;
    
    if (flags != 0)
      return (isShapeVisible(flags));  
    flags = shapeVisibilityFlags;
    
    
    
    
    
    flags |= group.shapeVisibilityFlags;
    if (getSpecialAtomID() != JmolConstants.ATOMID_ALPHA_CARBON)
      flags &= ~JmolConstants.BACKBONE_VISIBILITY_FLAG;

    
    
    
    return ((flags & ~JmolConstants.ATOM_IN_FRAME) != 0);
  }

  public float getGroupPhi() {
    return group.phi;
  }

  public float getGroupPsi() {
    return group.psi;
  }

  public char getChainID() {
    return group.chain.chainID;
  }

  public int getSurfaceDistance100() {
    return group.chain.modelSet.getSurfaceDistance100(index);
  }

  public Vector3f getVibrationVector() {
    return group.chain.modelSet.getVibrationVector(index, false);
  }

  public float getVibrationCoord(char ch) {
    return group.chain.modelSet.getVibrationCoord(index, ch);
  }


  public int getPolymerLength() {
    return group.getBioPolymerLength();
  }

  public Quaternion getQuaternion(char qtype) {
    return group.getQuaternion(qtype);
  }
  
  public int getPolymerIndexInModel() {
    return group.getBioPolymerIndexInModel();
  }

  public int getMonomerIndex() {
    return group.getMonomerIndex();
  }
  
  public int getSelectedGroupCountWithinChain() {
    return group.chain.getSelectedGroupCount();
  }

  public int getSelectedGroupIndexWithinChain() {
    return group.getSelectedGroupIndex();
  }

  public int getSelectedMonomerCountWithinPolymer() {
    return group.getSelectedMonomerCount();
  }

  public int getSelectedMonomerIndexWithinPolymer() {
    return group.getSelectedMonomerIndex();
  }

  Chain getChain() {
    return group.chain;
  }

  String getModelNumberForLabel() {
    return group.chain.modelSet.getModelNumberForAtomLabel(modelIndex);
  }
  
  public int getModelNumber() {
    return group.chain.modelSet.getModelNumber(modelIndex) % 1000000;
  }
  
  public int getModelFileIndex() {
    return group.chain.model.fileIndex;
  }
  
  public int getModelFileNumber() {
    return group.chain.modelSet.getModelFileNumber(modelIndex);
  }
  
  public byte getProteinStructureType() {
    return group.getProteinStructureType();
  }
  
  public int getStrucNo() {
    return group.getStrucNo();
  }

  public String getStructureId() {
    return group.getStructureId();
  }

  public String getProteinStructureTag() {
    return group.getProteinStructureTag();
  }

  public short getGroupID() {
    return group.groupID;
  }

  String getSeqcodeString() {
    return group.getSeqcodeString();
  }

  int getSeqNumber() {
    return group.getSeqNumber();
  }

  public char getInsertionCode() {
    return group.getInsertionCode();
  }
  
  public boolean equals(Object obj) {
    return (this == obj);
  }

  public int hashCode() {
    
    
    
    return index;
  }
  
  public Atom findAromaticNeighbor(BitSet notAtoms) {
    for (int i = bonds.length; --i >= 0; ) {
      Bond bondT = bonds[i];
      Atom a = bondT.getOtherAtom(this);
      if (bondT.isAromatic() && (notAtoms == null || !notAtoms.get(a.index)))
        return a;
    }
    return null;
  }

  public Atom findAromaticNeighbor(int notAtomIndex) {
    for (int i = bonds.length; --i >= 0; ) {
      Bond bondT = bonds[i];
      Atom a = bondT.getOtherAtom(this);
      if (bondT.isAromatic() && a.index != notAtomIndex)
        return a;
    }
    return null;
  }

  
  public static int atomPropertyInt(Atom atom, int tokWhat) {
    switch (tokWhat) {
    case Token.atomno:
      return atom.getAtomNumber();
    case Token.atomid:
      return atom.getSpecialAtomID();
    case Token.atomindex:
      return atom.getIndex();
    case Token.bondcount:
      return atom.getCovalentBondCount();
    case Token.color:
      return atom.group.chain.modelSet.viewer.getColorArgbOrGray(atom.getColix());
    case Token.element:
    case Token.elemno:
      return atom.getElementNumber();
    case Token.file:
      return atom.getModelFileIndex() + 1;
    case Token.formalcharge:
      return atom.getFormalCharge();
    case Token.groupid:
      return atom.getGroupID(); 
    case Token.groupindex:
      return atom.getGroupIndex(); 
    case Token.model:
      
      
      return atom.getModelNumber();
    case -Token.model:
      
      return atom.getModelFileNumber();
    case Token.modelindex:
      return atom.modelIndex;
    case Token.molecule:
      return atom.getMoleculeNumber();
    case Token.occupancy:
      return atom.getOccupancy100();
    case Token.polymerlength:
      return atom.getPolymerLength();
    case Token.radius:
      
      return atom.getRasMolRadius();        
    case Token.resno:
      return atom.getResno();
    case Token.site:
      return atom.getAtomSite();
    case Token.structure:
      return atom.getProteinStructureType();
    case Token.strucno:
      return atom.getStrucNo();
    case Token.valence:
      return atom.getValence();
    }
    return 0;      
  }

    
  public static float atomPropertyFloat(Viewer viewer, Atom atom, int tokWhat) {

    switch (tokWhat) {
    case Token.radius:
      return atom.getRadius();
    case Token.volume:
      return atom.getVolume(JmolConstants.VDW_AUTO);
    case Token.surfacedistance:
      atom.group.chain.modelSet.getSurfaceDistanceMax();
      return atom.getSurfaceDistance100() / 100f;
    case Token.temperature: 
      return atom.getBfactor100() / 100f;

    
    
      
    case Token.adpmax:
      return atom.getADPMinMax(true);
    case Token.adpmin:
      return atom.getADPMinMax(false);
    case Token.atomx:
      return atom.x;
    case Token.atomy:
      return atom.y;
    case Token.atomz:
      return atom.z;
    case Token.covalent:
      return atom.getCovalentRadiusFloat();
    case Token.fracx:
      return atom.getFractionalCoord('X');
    case Token.fracy:
      return atom.getFractionalCoord('Y');
    case Token.fracz:
      return atom.getFractionalCoord('Z');
    case Token.ionic:
      return atom.getBondingRadiusFloat();
    case Token.occupancy:
      return atom.getOccupancy100() / 100f;
    case Token.partialcharge:
      return atom.getPartialCharge();
    case Token.phi:
      return atom.getGroupPhi();
    case Token.psi:
      return atom.getGroupPsi();
    case Token.spacefill:
      return atom.getRadius();
    case Token.backbone:
    case Token.cartoon:
    case Token.dots:
    case Token.ellipsoid:
    case Token.geosurface:
    case Token.halo:
    case Token.meshRibbon:
    case Token.ribbon:
    case Token.rocket:
    case Token.star:
    case Token.strands:
    case Token.trace:
      return atom.group.chain.modelSet.getAtomShapeValue(atom.index, tokWhat);
    case Token.straightness:
      return atom.getStraightness();
    case Token.unitx:
      return atom.getFractionalUnitCoord('X');
    case Token.unity:
      return atom.getFractionalUnitCoord('Y');
    case Token.unitz:
      return atom.getFractionalUnitCoord('Z');
    case Token.vanderwaals:
      return atom.getVanderwaalsRadiusFloat(viewer, JmolConstants.VDW_AUTO);
    case Token.vibx:
      return atom.getVibrationCoord('X');
    case Token.viby:
      return atom.getVibrationCoord('Y');
    case Token.vibz:
      return atom.getVibrationCoord('Z');
    }
    return atomPropertyInt(atom, tokWhat);
  }

  public static String atomPropertyString(Atom atom, int tokWhat) {
    char ch;
    switch (tokWhat) {
    case Token.altloc:
      ch = atom.getAlternateLocationID();
      return (ch == '\0' ? "" : "" + ch);
    case Token.atomname:
      return atom.getAtomName();
    case Token.atomtype:
      return atom.getAtomType();
    case Token.chain:
      ch = atom.getChainID();
      return (ch == '\0' ? "" : "" + ch);
    case Token.sequence:
      return atom.getGroup1('?');
    case Token.group1:
      return atom.getGroup1('\0');
    case Token.group:
      return atom.getGroup3(false);
    case Token.element:
      return atom.getElementSymbol(true);
    case Token.identify:
      return atom.getIdentity(true);
    case Token.insertion:
      ch = atom.getInsertionCode();
      return (ch == '\0' ? "" : "" + ch);
    case Token.label:
    case Token.format:
      String s = atom.group.chain.modelSet.getAtomLabel(atom.getIndex());
      if (s == null)
        s = "";
      return s;
    case Token.structure:
      return JmolConstants.getProteinStructureName(atom.getProteinStructureType());
    case Token.strucid:
      return atom.getStructureId();
    case Token.symbol:
      return atom.getElementSymbol(false);
    case Token.symmetry:
      return atom.getSymmetryOperatorList();
    }
    return ""; 
  }

  public static Tuple3f atomPropertyTuple(Atom atom, int tok) {
    switch (tok) {
    case Token.fracxyz:
      return atom.getFractionalCoord();
    case Token.unitxyz:
      return atom.getFractionalUnitCoord(false);
    case Token.vibxyz:
      Vector3f v = atom.getVibrationVector();
      if (v == null)
        v = new Vector3f();
      return v;
    case Token.xyz:
      return atom;
    case Token.color:
      return Graphics3D.colorPointFromInt2(
          atom.group.chain.modelSet.viewer.getColorArgbOrGray(atom.getColix())
          );
    }
    return null;
  }

  boolean isWithinStructure(byte type) {
    return group.isWithinStructure(type);
  }


  
}
