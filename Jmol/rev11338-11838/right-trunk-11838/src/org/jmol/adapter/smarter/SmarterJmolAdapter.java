

package org.jmol.adapter.smarter;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolFileReaderInterface;
import org.jmol.util.CompoundDocument;
import org.jmol.util.TextFormat;
import org.jmol.util.ZipUtil;
import org.jmol.util.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SmarterJmolAdapter extends JmolAdapter {

  public SmarterJmolAdapter() {
    super("SmarterJmolAdapter");
  }

  

  public final static String PATH_KEY = ".PATH";
  public final static String PATH_SEPARATOR =
    System.getProperty("path.separator");
  
  public void finish(Object atomSetCollection) {
    ((AtomSetCollection)atomSetCollection).finish();
  }

  public String[] specialLoad(String name, String type) {
    return Resolver.specialLoad(name, type);  
  }
  

  public Object getAtomSetCollectionFromReader(String name, String type,
                                   BufferedReader bufferedReader, Hashtable htParams) {
    return staticGetAtomSetCollectionFromReader(name, type, bufferedReader, htParams);
  }

  private static Object staticGetAtomSetCollectionFromReader(String name, String type,
                                                 BufferedReader bufferedReader,
                                                 Hashtable htParams) {
    
    try {
      Object atomSetCollectionOrErrorMessage = Resolver.getAtomCollectionAndCloseReader(name, type,
          bufferedReader, htParams, -1);
      if (atomSetCollectionOrErrorMessage instanceof String)
        return atomSetCollectionOrErrorMessage;
      AtomSetCollection atomSetCollection = (AtomSetCollection) atomSetCollectionOrErrorMessage;
      if (atomSetCollection.errorMessage != null)
        return atomSetCollection.errorMessage;
      return atomSetCollection;
    } catch (Throwable e) {
      try {
        bufferedReader.close();
      } catch (Exception ex) {
        
      }
      bufferedReader = null;
      Logger.error(null, e);
      return "" + e;
    }
  }

  public Object getAtomSetCollectionFromReaders(JmolFileReaderInterface fileReader, String[] names, String[] types,
                                    Hashtable[] htparamsSet) {
    return staticGetAtomSetCollectionFromReaders(fileReader, names, types, htparamsSet);
  }

  private static Object staticGetAtomSetCollectionFromReaders(
                                                  JmolFileReaderInterface fileReader,
                                                  String[] names,
                                                  String[] types,
                                                  Hashtable[] htparamsSet) {
    
    int size = names.length;
    AtomSetCollection[] atomSetCollections = new AtomSetCollection[size];
    for (int i = 0; i < size; i++) {
      try {
        Object reader = fileReader.getBufferedReader(i);
        if (!(reader instanceof BufferedReader))
          return reader;
        Object atomSetCollectionOrErrorMessage = Resolver.getAtomCollectionAndCloseReader(names[i],
            (types == null ? null : types[i]), (BufferedReader) reader, (htparamsSet == null ? null
                : htparamsSet[i]), i);
        if (atomSetCollectionOrErrorMessage instanceof String)
          return atomSetCollectionOrErrorMessage;
        if (atomSetCollectionOrErrorMessage instanceof AtomSetCollection) {
          atomSetCollections[i] = (AtomSetCollection) atomSetCollectionOrErrorMessage;
          if (atomSetCollections[i].errorMessage != null)
            return atomSetCollections[i].errorMessage;
        } else {
          return "unknown reader error";
        }
      } catch (Exception e) {
        Logger.error(null, e);
        return "" + e;
      } catch (Error er) {
        Logger.error(null, er);
        return "" + er;
      }
    }
    if (htparamsSet != null && htparamsSet[0].containsKey("trajectorySteps")) {
      
      
      
      atomSetCollections[0].finalizeTrajectory((Vector) htparamsSet[0]
          .get("trajectorySteps"));
      return atomSetCollections[0];
    }
    AtomSetCollection result = new AtomSetCollection(atomSetCollections);
    if (result.errorMessage != null)
      return result.errorMessage;
    return result;
  }

  public Object getAtomSetCollectionOrBufferedReaderFromZip(InputStream is, String fileName, String[] zipDirectory,
                             Hashtable htParams, boolean asBufferedReader) {
    return staticGetAtomSetCollectionOrBufferedReaderFromZip(is, fileName, zipDirectory, htParams, 1, asBufferedReader);
  }

  private static Object staticGetAtomSetCollectionOrBufferedReaderFromZip(
                                                                          InputStream is,
                                                                          String fileName,
                                                                          String[] zipDirectory,
                                                                          Hashtable htParams,
                                                                          int subFilePtr,
                                                                          boolean asBufferedReader) {

    
    
    
    boolean doCombine = (subFilePtr == 1);
    int selectedFile = 0;
    if (htParams != null && htParams.containsKey("modelNumber")) {
      selectedFile = ((Integer)htParams.get("modelNumber")).intValue();
      if(selectedFile > 0 && doCombine)
        htParams.remove("modelNumber");
    }
    String[] subFileList = (htParams == null ? null : (String[]) htParams
        .get("subFileList"));
    if (subFileList == null)
      subFileList = Resolver.checkSpecialInZip(zipDirectory);
    
    String subFileName = (subFileList == null
        || subFilePtr >= subFileList.length ? null : subFileList[subFilePtr]);
    if (subFileName != null
        && (subFileName.startsWith("/") || subFileName.startsWith("\\")))
      subFileName = subFileName.substring(1);

    
    String manifest = (htParams == null ? null : (String) htParams
        .get("manifest"));
    if (manifest == null)
      manifest = (zipDirectory.length > 0 ? zipDirectory[0] : "");
    boolean haveManifest = (manifest.length() > 0);
    if (haveManifest) {
      if (Logger.debugging)
        Logger.info("manifest for  " + fileName + ":\n" + manifest);
      manifest = '|' + manifest.replace('\r', '|').replace('\n', '|') + '|';
    }
    boolean ignoreErrors = (manifest.indexOf("IGNORE_ERRORS") >= 0);
    boolean selectAll = (manifest.indexOf("IGNORE_MANIFEST") >= 0);
    boolean exceptFiles = (manifest.indexOf("EXCEPT_FILES") >= 0);
    if (selectAll || subFileName != null)
      haveManifest = false;
    Vector vCollections = new Vector();
    Hashtable htCollections = (haveManifest ? new Hashtable() : null);
    int nFiles = 0;
    

    
    
    
    

    Object ret = Resolver.checkSpecialData(is, zipDirectory);
    if (ret instanceof String)
      return (String) ret;
    StringBuffer data = (StringBuffer) ret;
    try {
      if (data != null) {
        BufferedReader reader = new BufferedReader(new StringReader(data
            .toString()));
        if (asBufferedReader) {
          return reader;
        }
        Object atomSetCollectionOrError = Resolver
            .getAtomCollectionAndCloseReader(fileName, null, reader, htParams, -1);
        if (atomSetCollectionOrError instanceof String)
          return atomSetCollectionOrError;
        if (atomSetCollectionOrError instanceof AtomSetCollection) {
          AtomSetCollection atomSetCollection = (AtomSetCollection) atomSetCollectionOrError;
          if (atomSetCollection.errorMessage != null) {
            if (ignoreErrors)
              return null;
            return atomSetCollection.errorMessage;
          }
          return atomSetCollection;
        }
        if (ignoreErrors)
          return null;
        return "unknown reader error";
      }
      ZipInputStream zis = ZipUtil.getStream(is);
      ZipEntry ze;
      while ((ze = zis.getNextEntry()) != null
          && (selectedFile <= 0 || vCollections.size() < selectedFile)) {
        if (ze.isDirectory())
          continue;
        byte[] bytes = ZipUtil.getZipEntryAsBytes(zis);
        String thisEntry = ze.getName();
        if (subFileName != null && !thisEntry.equals(subFileName))
          continue;
        if (ZipUtil.isJmolManifest(thisEntry) || haveManifest 
            && exceptFiles == manifest.indexOf("|" + thisEntry + "|") >= 0)
          continue;
        if (ZipUtil.isZipFile(bytes)) {
          BufferedInputStream bis = new BufferedInputStream(
              new ByteArrayInputStream(bytes));
          String[] zipDir2 = ZipUtil.getZipDirectoryAndClose(bis, true);
          bis = new BufferedInputStream(new ByteArrayInputStream(bytes));
          Object atomSetCollections = staticGetAtomSetCollectionOrBufferedReaderFromZip(
              bis, fileName + "|" + thisEntry, zipDir2, htParams, ++subFilePtr,
              asBufferedReader);
          if (atomSetCollections instanceof String) {
            if (ignoreErrors)
              continue;
            return atomSetCollections;
          } else if (atomSetCollections instanceof AtomSetCollection
              || atomSetCollections instanceof Vector) {
            if (haveManifest && !exceptFiles)
              htCollections.put(thisEntry, atomSetCollections);
            else
              vCollections.addElement(atomSetCollections);
          } else if (atomSetCollections instanceof BufferedReader) {
            if (doCombine)
              zis.close();
            return atomSetCollections; 
            
          } else {
            if (ignoreErrors)
              continue;
            zis.close();
            return "unknown zip reader error";
          }
        } else {
          String sData = (CompoundDocument.isCompoundDocument(bytes) 
              ? (new CompoundDocument(new BufferedInputStream(new ByteArrayInputStream(bytes))))
                 .getAllData("Molecule").toString() 
              : ZipUtil.isGzip(bytes) ? ZipUtil.getGzippedBytesAsString(bytes)
              : new String(bytes));
          BufferedReader reader = new BufferedReader(new StringReader(sData));
          if (asBufferedReader) {
            if (doCombine)
              zis.close();
            return reader;
          }
          Object atomSetCollection = Resolver.getAtomCollectionAndCloseReader(
              fileName + "|" + ze.getName(), null, reader, htParams, -1);
          if (atomSetCollection instanceof AtomSetCollection) {
            if (haveManifest && !exceptFiles)
              htCollections.put(thisEntry, atomSetCollection);
            else
              vCollections.addElement(atomSetCollection);
            AtomSetCollection a = (AtomSetCollection) atomSetCollection;
            if (a.errorMessage != null) {
              if (ignoreErrors)
                continue;
              zis.close();
              return a.errorMessage;
            }
          } else {
            if (ignoreErrors)
              continue;
            zis.close();
            return "" + atomSetCollection;
          }
        }
      }
      if (doCombine)
        zis.close();

      

      if (haveManifest && !exceptFiles) {
        String[] list = TextFormat.split(manifest, '|');
        for (int i = 0; i < list.length; i++) {
          String file = list[i];
          if (file.length() == 0 || file.indexOf("#") == 0)
            continue;
          if (htCollections.containsKey(file))
            vCollections.add(htCollections.get(file));
          else if (Logger.debugging)
            Logger.info("manifested file " + file + " was not found in "
                + fileName);
        }
      }
      if (!doCombine)
        return vCollections;
      AtomSetCollection result = new AtomSetCollection(vCollections);
      if (result.errorMessage != null) {
        if (ignoreErrors)
          return null;
        return result.errorMessage;
      }
      if (nFiles == 1)
        selectedFile = 1;
      if (selectedFile > 0 && selectedFile <= vCollections.size())
        return vCollections.elementAt(selectedFile - 1);
      return result;

    } catch (Exception e) {
      if (ignoreErrors)
        return null;
      Logger.error(null, e);
      return "" + e;
    } catch (Error er) {
      Logger.error(null, er);
      return "" + er;
    }
  }

  public Object getAtomSetCollectionFromDOM(Object DOMNode, Hashtable htParams) {
    try {
      Object atomSetCollectionOrErrorMessage = 
        Resolver.DOMResolve(DOMNode, htParams);
      if (atomSetCollectionOrErrorMessage instanceof String)
        return atomSetCollectionOrErrorMessage;
      if (atomSetCollectionOrErrorMessage instanceof AtomSetCollection) {
        AtomSetCollection atomSetCollection =
          (AtomSetCollection)atomSetCollectionOrErrorMessage;
        if (atomSetCollection.errorMessage != null)
          return atomSetCollection.errorMessage;
        return atomSetCollection;
      }
      return "unknown DOM reader error";
    } catch (Exception e) {
      Logger.error(null, e);
      return "" + e;
    } catch (Error er) {
      Logger.error(null, er);
      return "" + er;
    }
  }

  public String getFileTypeName(Object atomSetCollectionOrReader) {
    return staticGetFileTypeName(atomSetCollectionOrReader);
  }

  private static String staticGetFileTypeName(Object atomSetCollectionOrReader) {
    if (atomSetCollectionOrReader == null)
      return null;
    if (atomSetCollectionOrReader instanceof BufferedReader)
      return Resolver.getFileType((BufferedReader)atomSetCollectionOrReader);
    if (atomSetCollectionOrReader instanceof AtomSetCollection)
      return ((AtomSetCollection)atomSetCollectionOrReader).getFileTypeName();
    return null;
  }

  public String getAtomSetCollectionName(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getCollectionName();
  }
  
  public Properties getAtomSetCollectionProperties(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetCollectionProperties();
  }

  public Hashtable getAtomSetCollectionAuxiliaryInfo(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetCollectionAuxiliaryInfo();
  }

  public int getAtomSetCount(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetCount();
  }

  public int getAtomSetNumber(Object atomSetCollection, int atomSetIndex) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetNumber(atomSetIndex);
  }

  public String getAtomSetName(Object atomSetCollection, int atomSetIndex) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetName(atomSetIndex);
  }
  
  public Properties getAtomSetProperties(Object atomSetCollection, int atomSetIndex) {
    return ((AtomSetCollection)atomSetCollection).getAtomSetProperties(atomSetIndex);
  }
  
  public Hashtable getAtomSetAuxiliaryInfo(Object atomSetCollection, int atomSetIndex) {
    return ((AtomSetCollection) atomSetCollection)
        .getAtomSetAuxiliaryInfo(atomSetIndex);
  }

  

  public int getEstimatedAtomCount(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getAtomCount();
  }

  public boolean coordinatesAreFractional(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).coordinatesAreFractional;
  }

  public float[] getNotionalUnitcell(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).notionalUnitCell;
  }

  public float[] getPdbScaleMatrix(Object atomSetCollection) {
    float[] a = ((AtomSetCollection)atomSetCollection).notionalUnitCell;
    if (a.length < 22)
      return null;
    float[] b = new float[16];
    for (int i = 0; i < 16; i++)
      b[i] = a[6 + i];
    return b;
  }

  public float[] getPdbScaleTranslate(Object atomSetCollection) {
    float[] a = ((AtomSetCollection)atomSetCollection).notionalUnitCell;
    if (a.length < 22)
      return null;
    float[] b = new float[3];
    b[0] = a[6 + 4*0 + 3];
    b[1] = a[6 + 4*1 + 3];
    b[2] = a[6 + 4*2 + 3];
    return b;
  }
  
  public String getClientAtomStringProperty(Object clientAtom, String propertyName) {
    return null;
  }

  

  public JmolAdapter.AtomIterator
    getAtomIterator(Object atomSetCollection) {
    return new AtomIterator((AtomSetCollection)atomSetCollection);
  }

  public JmolAdapter.BondIterator
    getBondIterator(Object atomSetCollection) {
    return new BondIterator((AtomSetCollection)atomSetCollection);
  }

  public JmolAdapter.StructureIterator
    getStructureIterator(Object atomSetCollection) {
    return ((AtomSetCollection)atomSetCollection).getStructureCount() == 0 ? 
        null : new StructureIterator((AtomSetCollection)atomSetCollection);
  }

  
  class AtomIterator extends JmolAdapter.AtomIterator {
    private int iatom;
    private Atom atom;
    private int atomCount;
    private Atom[] atoms;

    AtomIterator(AtomSetCollection atomSetCollection) {
      atomCount = atomSetCollection.getAtomCount();
      atoms = atomSetCollection.getAtoms();
      iatom = 0;
    }
    public boolean hasNext() {
      if (iatom == atomCount)
        return false;
      atom = atoms[iatom++];
      return true;
    }
    public int getAtomSetIndex() { return atom.atomSetIndex; }
    public BitSet getAtomSymmetry() { return atom.bsSymmetry; }
    public int getAtomSite() { return atom.atomSite + 1; }
    public Object getUniqueID() { return new Integer(atom.atomIndex); }
    public String getElementSymbol() {
      if (atom.elementSymbol != null)
        return atom.elementSymbol;
      return atom.getElementSymbol();
    }
    public int getElementNumber() { return atom.elementNumber; }
    public String getAtomName() { return atom.atomName; }
    public int getFormalCharge() { return atom.formalCharge; }
    public float getPartialCharge() { return atom.partialCharge; }
    public Object[] getEllipsoid() { return atom.ellipsoid; }
    public float getRadius() { return atom.radius; }
    public float getX() { return atom.x; }
    public float getY() { return atom.y; }
    public float getZ() { return atom.z; }
    public float getVectorX() { return atom.vectorX; }
    public float getVectorY() { return atom.vectorY; }
    public float getVectorZ() { return atom.vectorZ; }
    public float getBfactor() { return Float.isNaN(atom.bfactor) && atom.anisoBorU != null ?
        atom.anisoBorU[7] * 100f : atom.bfactor; }
    public int getOccupancy() { return atom.occupancy; }
    public boolean getIsHetero() { return atom.isHetero; }
    public int getAtomSerial() { return atom.atomSerial; }
    public char getChainID() { return canonizeChainID(atom.chainID); }
    public char getAlternateLocationID()
    { return canonizeAlternateLocationID(atom.alternateLocationID); }
    public String getGroup3() { return atom.group3; }
    public int getSequenceNumber() { return atom.sequenceNumber; }
    public char getInsertionCode()
    { return canonizeInsertionCode(atom.insertionCode); }
    
  }

  class BondIterator extends JmolAdapter.BondIterator {
    private Bond[] bonds;
    private int ibond;
    private Bond bond;
    private int bondCount;
    
    BondIterator(AtomSetCollection atomSetCollection) {
      bonds = atomSetCollection.getBonds();
      bondCount = atomSetCollection.getBondCount();      
      ibond = 0;
    }
    public boolean hasNext() {
      if (ibond == bondCount)
        return false;
      bond = bonds[ibond++];
      return true;
    }
    public Object getAtomUniqueID1() {
      return new Integer(bond.atomIndex1);
    }
    public Object getAtomUniqueID2() {
      return new Integer(bond.atomIndex2);
    }
    public int getEncodedOrder() {
      return bond.order;
    }
  }

  public class StructureIterator extends JmolAdapter.StructureIterator {
    private int structureCount;
    private Structure[] structures;
    private Structure structure;
    private int istructure;
    
    StructureIterator(AtomSetCollection atomSetCollection) {
      structureCount = atomSetCollection.getStructureCount();
      structures = atomSetCollection.getStructures();
      istructure = 0;
    }

    public boolean hasNext() {
      if (istructure == structureCount)
        return false;
      structure = structures[istructure++];
      return true;
    }

    public int getModelIndex() {
      return structure.modelIndex;
    }

    public String getStructureType() {
      return structure.structureType;
    }

    public String getStructureID() {
      return structure.structureID;
    }

    public int getSerialID() {
      return structure.serialID;
    }

    public char getStartChainID() {
      return canonizeChainID(structure.startChainID);
    }
    
    public int getStartSequenceNumber() {
      return structure.startSequenceNumber;
    }
    
    public char getStartInsertionCode() {
      return canonizeInsertionCode(structure.startInsertionCode);
    }
    
    public char getEndChainID() {
      return canonizeChainID(structure.endChainID);
    }
    
    public int getEndSequenceNumber() {
      return structure.endSequenceNumber;
    }
      
    public char getEndInsertionCode() {
      return structure.endInsertionCode;
    }

    public int getStrandCount() {
      return structure.strandCount;
    }
  }
}
