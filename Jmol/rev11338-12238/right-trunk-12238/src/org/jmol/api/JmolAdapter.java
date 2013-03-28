

package org.jmol.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Properties;
import java.util.Hashtable;
import java.util.BitSet;

import org.jmol.modelset.Group;
import org.jmol.viewer.JmolConstants;


public abstract class JmolAdapter {
  
  public final static short ORDER_COVALENT_SINGLE = JmolConstants.BOND_COVALENT_SINGLE;
  public final static short ORDER_COVALENT_DOUBLE = JmolConstants.BOND_COVALENT_DOUBLE;
  public final static short ORDER_COVALENT_TRIPLE = JmolConstants.BOND_COVALENT_TRIPLE;
  public final static short ORDER_AROMATIC        = JmolConstants.BOND_AROMATIC;
  public final static short ORDER_AROMATIC_SINGLE = JmolConstants.BOND_AROMATIC_SINGLE;
  public final static short ORDER_AROMATIC_DOUBLE = JmolConstants.BOND_AROMATIC_DOUBLE;
  public final static short ORDER_HBOND           = JmolConstants.BOND_H_REGULAR;
  public final static short ORDER_STEREO_NEAR     = JmolConstants.BOND_COVALENT_SINGLE; 
  public final static short ORDER_STEREO_FAR      = JmolConstants.BOND_COVALENT_SINGLE; 
  public final static short ORDER_PARTIAL01       = JmolConstants.BOND_PARTIAL01;
  public final static short ORDER_PARTIAL12       = JmolConstants.BOND_PARTIAL12;
  public final static short ORDER_PARTIAL23       = JmolConstants.BOND_PARTIAL23;
  public final static short ORDER_PARTIAL32       = JmolConstants.BOND_PARTIAL32;
  public final static short ORDER_UNSPECIFIED     = JmolConstants.BOND_ORDER_UNSPECIFIED;
  
  public final static int        SHELL_S           = JmolConstants.SHELL_S;
  public final static int        SHELL_P           = JmolConstants.SHELL_P;
  public final static int        SHELL_SP          = JmolConstants.SHELL_SP;
  public final static int        SHELL_L           = JmolConstants.SHELL_L;
  public final static int        SHELL_D_CARTESIAN = JmolConstants.SHELL_D_CARTESIAN;
  public final static int        SHELL_D_SPHERICAL = JmolConstants.SHELL_D_SPHERICAL;
  public final static int        SHELL_F_CARTESIAN = JmolConstants.SHELL_F_CARTESIAN;
  public final static int        SHELL_F_SPHERICAL = JmolConstants.SHELL_F_SPHERICAL;
  
  public static String getElementSymbol(int elementNumber) {
    return JmolConstants.elementSymbolFromNumber(elementNumber);
  }
  
  public static int getElementNumber(String elementSymbol) {
    return JmolConstants.elementNumberFromSymbol(elementSymbol);
  }
  
  public static boolean isHetero(String group3) {
    return JmolConstants.isHetero(group3);
  }
  
  public static int getQuantumShellTagID(String tag) {
    return JmolConstants.getQuantumShellTagID(tag);
  }
                                           
  public static int getQuantumShellTagIDSpherical(String tag) {
    return JmolConstants.getQuantumShellTagIDSpherical(tag);
  }
  
  final public static int getQuantumSubshellTagID(int shell, String tag) {
    return JmolConstants.getQuantumSubshellTagID(shell, tag);
  }
                                           
  final public static String getQuantumSubshellTag(int shell, int subshell) {
    return JmolConstants.getQuantumSubshellTag(shell, subshell);
  }
  
  final public static String canonicalizeQuantumSubshellTag(String tag) {
    return JmolConstants.canonicalizeQuantumSubshellTag(tag);
  }

  final public static short lookupGroupID(String group3) {
    return Group.lookupGroupID(group3);
  }


  
  
  


  String adapterName;

  public JmolAdapter(String adapterName) {
    this.adapterName = adapterName;
  }

  public String getAdapterName() {
    return adapterName;
  }
  

abstract public Object getAtomSetCollectionFromReader(String name, String type,
                                 BufferedReader bufferedReader, Hashtable htParams);


  
  abstract public Object getAtomSetCollectionFromReaders(JmolFileReaderInterface fileReader, String[] names, String[] types,
                                    Hashtable[] htParams);

  abstract public Object getAtomSetCollectionOrBufferedReaderFromZip(InputStream is, String fileName, String[] zipDirectory,
                             Hashtable htParams, boolean asBufferedReader);
  
 

  public Object openBufferedReader(String name, BufferedReader bufferedReader) {
    return getAtomSetCollectionFromReader(name, null, bufferedReader, null);
  }

  public Object openBufferedReader(String name, BufferedReader bufferedReader,
                                   Hashtable htParams) {
    return getAtomSetCollectionFromReader(name, null, bufferedReader, htParams);
  }

  public Object openBufferedReader(String name, String type,
                                   BufferedReader bufferedReader) {
    return getAtomSetCollectionFromReader(name, type, bufferedReader, null);
  }

  abstract public Object getAtomSetCollectionFromDOM(Object DOMNode, Hashtable htParams);

  public void finish(Object atomSetCollection) {}

  
  abstract public String getFileTypeName(Object atomSetCollection);

  
  abstract public String getAtomSetCollectionName(Object atomSetCollection);

  
  abstract public Properties getAtomSetCollectionProperties(Object atomSetCollection);

  
  abstract public Hashtable getAtomSetCollectionAuxiliaryInfo(Object atomSetCollection);
  
  
  abstract public int getAtomSetCount(Object atomSetCollection);

  
  abstract public int getAtomSetNumber(Object atomSetCollection, int atomSetIndex);
 
  
  abstract public String getAtomSetName(Object atomSetCollection, int atomSetIndex);

  
  abstract public Properties getAtomSetProperties(Object atomSetCollection, int atomSetIndex);
  
  
  abstract public Hashtable getAtomSetAuxiliaryInfo(Object atomSetCollection, int atomSetIndex);

  
  abstract public int getEstimatedAtomCount(Object atomSetCollection);

  
  
  abstract public boolean coordinatesAreFractional(Object atomSetCollection);

  
  abstract public float[] getNotionalUnitcell(Object atomSetCollection);
  
  
  abstract public float[] getPdbScaleMatrix(Object atomSetCollection);
  
  
  abstract public float[] getPdbScaleTranslate(Object atomSetCollection);

  
  abstract public String getClientAtomStringProperty(Object clientAtom,
                                            String propertyName);
  
  
  abstract public AtomIterator getAtomIterator(Object atomSetCollection);
  
  abstract public BondIterator getBondIterator(Object atomSetCollection);

  

  abstract public StructureIterator getStructureIterator(Object atomSetCollection);

  
  public abstract class AtomIterator {
    public abstract boolean hasNext();
    public int getAtomSetIndex() { return 0; }
    public BitSet getAtomSymmetry() { return null; }
    public int getAtomSite() { return Integer.MIN_VALUE; }
    abstract public Object getUniqueID();
    public int getElementNumber() { return -1; } 
    public String getElementSymbol() { return null; }
    public String getAtomName() { return null; }
    public int getFormalCharge() { return 0; }
    public float getPartialCharge() { return Float.NaN; }
    public Object[] getEllipsoid() { return null; }
    public float getRadius() { return Float.NaN; }
    abstract public float getX();
    abstract public float getY();
    abstract public float getZ();
    public float getVectorX() { return Float.NaN; }
    public float getVectorY() { return Float.NaN; }
    public float getVectorZ() { return Float.NaN; }
    public float getBfactor() { return Float.NaN; }
    public int getOccupancy() { return 100; }
    public boolean getIsHetero() { return false; }
    public int getAtomSerial() { return Integer.MIN_VALUE; }
    public char getChainID() { return (char)0; }
    public char getAlternateLocationID() { return (char)0; }
    public String getGroup3() { return null; }
    public int getSequenceNumber() { return Integer.MIN_VALUE; }
    public char getInsertionCode() { return (char)0; }
    public Object getClientAtomReference() { return null; }
  }

  

  public abstract class BondIterator {
    public abstract boolean hasNext();
    public abstract Object getAtomUniqueID1();
    public abstract Object getAtomUniqueID2();
    public abstract int getEncodedOrder();
  }

  

  public abstract class StructureIterator {
    public abstract boolean hasNext();
    public abstract int getModelIndex();
    public abstract String getStructureType();
    public abstract String getStructureID();
    public abstract int getSerialID();
    public abstract int getStrandCount();
    public abstract char getStartChainID();
    public abstract int getStartSequenceNumber();
    public abstract char getStartInsertionCode();
    public abstract char getEndChainID();
    public abstract int getEndSequenceNumber();
    public abstract char getEndInsertionCode();
  }
  
  
  
  

  public final static char canonizeAlphaDigit(char ch) {
    if ((ch >= 'A' && ch <= 'Z') ||
        (ch >= 'a' && ch <= 'z') ||
        (ch >= '0' && ch <= '9'))
      return ch;
    return '\0';
  }

  public final static char canonizeChainID(char chainID) {
    return canonizeAlphaDigit(chainID);
  }

  public final static char canonizeInsertionCode(char insertionCode) {
    return canonizeAlphaDigit(insertionCode);
  }

  public final static char canonizeAlternateLocationID(char altLoc) {
    
    return canonizeAlphaDigit(altLoc);
  }

  public String[] specialLoad(String name, String type) {
    return null;
  }

}
