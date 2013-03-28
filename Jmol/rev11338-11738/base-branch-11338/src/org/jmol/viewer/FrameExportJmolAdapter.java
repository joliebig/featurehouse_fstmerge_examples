

package org.jmol.viewer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolFileReaderInterface;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.modelset.ModelSet;


final public class FrameExportJmolAdapter extends JmolAdapter {

  Viewer viewer;
  ModelSet modelSet;

  FrameExportJmolAdapter(Viewer viewer, ModelSet modelSet) {
    super("FrameExportJmolAdapter");
    this.viewer = viewer;
    this.modelSet = modelSet;
  }

  public String getAtomSetCollectionName(Object clientFile) {
    return viewer.getModelSetName();
  }

  public int getEstimatedAtomCount(Object clientFile) {
    return modelSet.getAtomCount();
  }

  public float[] getNotionalUnitcell(Object clientFile) {
    return modelSet.getNotionalUnitcell();
  }

  public JmolAdapter.AtomIterator
    getAtomIterator(Object clientFile) {
    return new AtomIterator();
  }

  public JmolAdapter.BondIterator
    getBondIterator(Object clientFile) {
    return new BondIterator();
  }

  public StructureIterator getStructureIterator(Object atomSetCollection) {
    
    return null;
  }
  class AtomIterator extends JmolAdapter.AtomIterator {
    int iatom;
    Atom atom;

    public boolean hasNext() {
      if (iatom == modelSet.getAtomCount())
        return false;
      atom = modelSet.atoms[iatom++];
      return true;
    }
    public Object getUniqueID() { return new Integer(iatom); }
    public int getElementNumber() { return atom.getElementNumber(); }
    public String getElementSymbol() { return atom.getElementSymbol(); }
    public int getFormalCharge() { return atom.getFormalCharge(); }
    public float getPartialCharge() { return atom.getPartialCharge(); }
    public float getX() { return atom.x; }
    public float getY() { return atom.y; }
    public float getZ() { return atom.z; }
  }

  class BondIterator extends JmolAdapter.BondIterator {
    int ibond;
    Bond bond;

    public boolean hasNext() {
      if (ibond >= modelSet.getBondCount())
        return false;
      bond = modelSet.getBonds()[ibond++];
      return true;
    }
    public Object getAtomUniqueID1(){
      return new Integer(bond.getAtomIndex1());
    }
    public Object getAtomUniqueID2() {
      return new Integer(bond.getAtomIndex2());
    }
    public int getEncodedOrder() { return bond.getOrder(); }
  }

  public Object getAtomSetCollectionFromReader(String name, String type, BufferedReader bufferedReader, Hashtable htParams) {
    
    return null;
  }

  public Object getAtomSetCollectionFromReaders(JmolFileReaderInterface fileReader, String[] names, String[] types, Hashtable[] htParams) {
    
    return null;
  }

  public Object getAtomSetCollectionOrBufferedReaderFromZip(InputStream is, String fileName, String[] zipDirectory, Hashtable htParams, boolean asBufferedReader) {
    
    return null;
  }

  public Object getAtomSetCollectionFromDOM(Object DOMNode) {
    
    return null;
  }

  public String getFileTypeName(Object atomSetCollection) {
    
    return null;
  }

  public Properties getAtomSetCollectionProperties(Object atomSetCollection) {
    
    return null;
  }

  public Hashtable getAtomSetCollectionAuxiliaryInfo(Object atomSetCollection) {
    
    return null;
  }

  public int getAtomSetCount(Object atomSetCollection) {
    
    return 0;
  }

  public int getAtomSetNumber(Object atomSetCollection, int atomSetIndex) {
    
    return 0;
  }

  public String getAtomSetName(Object atomSetCollection, int atomSetIndex) {
    
    return null;
  }

  public Properties getAtomSetProperties(Object atomSetCollection, int atomSetIndex) {
    
    return null;
  }

  public Hashtable getAtomSetAuxiliaryInfo(Object atomSetCollection, int atomSetIndex) {
    
    return null;
  }

  public boolean coordinatesAreFractional(Object atomSetCollection) {
    
    return false;
  }

  public float[] getPdbScaleMatrix(Object atomSetCollection) {
    
    return null;
  }

  public float[] getPdbScaleTranslate(Object atomSetCollection) {
    
    return null;
  }

  public String getClientAtomStringProperty(Object clientAtom, String propertyName) {
    
    return null;
  }

}
