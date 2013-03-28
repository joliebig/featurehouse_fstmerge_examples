

package org.jmol.shape;

import java.util.BitSet;

import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.viewer.JmolConstants;

import java.util.Hashtable;

public class Balls extends AtomShape {
  
  public void setSize(RadiusData rd, BitSet bsSelected) {
    isActive = true;
    if (bsSizeSet == null)
      bsSizeSet = new BitSet();
    int bsLength = Math.min(atoms.length, bsSelected.length());
    for (int i = bsLength; --i >= 0; ) {
      if (bsSelected.get(i)) {
        Atom atom = atoms[i];
        atom.setMadAtom(viewer, rd);
        bsSizeSet.set(i);
      }
    }
  }

  public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("color" == propertyName) {
      short colix = Graphics3D.getColix(value);
      if (colix == Graphics3D.INHERIT_ALL)
        colix = Graphics3D.USE_PALETTE;
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      byte pid = JmolConstants.pidOf(value);
      for (int i = atomCount; --i >= 0;)
        if (bs.get(i)) {
          Atom atom = atoms[i];
          atom.setColixAtom(setColix(colix, pid, atom));
          bsColixSet.set(i, colix != Graphics3D.USE_PALETTE
              || pid != JmolConstants.PALETTE_NONE);
          atom.setPaletteID(pid);
        }
      return;
    }
    if ("colorValues" == propertyName) {
      int[] values = (int[]) value;
      if (values.length == 0)
        return;
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      int n = 0;
      Integer color = null;
      for (int i = 0; i < atomCount; i++)
        if (bs.get(i)) {
          if (n >= values.length)
            return;
          color = new Integer(values[n++]);
          short colix = Graphics3D.getColix(color);
          if (colix == Graphics3D.INHERIT_ALL)
            colix = Graphics3D.USE_PALETTE;
          byte pid = JmolConstants.pidOf(color);
          Atom atom = atoms[i];
          atom.setColixAtom(setColix(colix, pid, atom));
          bsColixSet.set(i, colix != Graphics3D.USE_PALETTE
              || pid != JmolConstants.PALETTE_NONE);
          atom.setPaletteID(pid);
        }
      return;
    }
    if ("translucency" == propertyName) {
      boolean isTranslucent = (((String)value).equals("translucent"));
      if (bsColixSet == null)
        bsColixSet = new BitSet();
      for (int i = atomCount; --i >= 0;)
        if (bs.get(i)) {
          atoms[i].setTranslucent(isTranslucent, translucentLevel);
          if (isTranslucent)
            bsColixSet.set(i);
        }
      return;
    }
    super.setProperty(propertyName, value, bs);
 }

 public void setModelClickability() {
   BitSet bs = viewer.getDeletedAtoms();
   if (bs == null)
     bs = new BitSet();
   for (int i = atomCount; --i >= 0;) {
     Atom atom = atoms[i];
     atom.setClickable(0);
     if (bs.get(i) || (atom.getShapeVisibilityFlags() & myVisibilityFlag) == 0
         || modelSet.isAtomHidden(i))
       continue;
     atom.setClickable(myVisibilityFlag);
   }
 }
  
 public void setVisibilityFlags(BitSet bs) {
    boolean showHydrogens = viewer.getShowHydrogens();
    BitSet bsDeleted = viewer.getDeletedAtoms();
    if (bsDeleted == null)
      bsDeleted = new BitSet();
    for (int i = atomCount; --i >= 0; ) {
      Atom atom = atoms[i];
      int flag = atom.getShapeVisibilityFlags();
      flag &= (~JmolConstants.ATOM_IN_FRAME & ~myVisibilityFlag);
      atom.setShapeVisibilityFlags(flag);
      if (bsDeleted.get(i) || !showHydrogens && atom.getElementNumber() == 1)
        continue;
      int modelIndex = atom.getModelIndex();
      if (bs.get(modelIndex)) { 
        atom.setShapeVisibility(JmolConstants.ATOM_IN_FRAME, true);
        if (atom.getMadAtom() != 0 &&  !modelSet.isAtomHidden(i))
          atom.setShapeVisibility(myVisibilityFlag, true);
      }
    }
  }

 public String getShapeState() {
    Hashtable temp = new Hashtable();
    float r = 0;
    for (int i = 0; i < atomCount; i++) {
      if (bsSizeSet != null && bsSizeSet.get(i)) {
        if ((r = atoms[i].getMadAtom()) < 0)
          setStateInfo(temp, i, "Spacefill on");
        else
          setStateInfo(temp, i, "Spacefill " + (r / 2000f));
      }
      if (bsColixSet != null && bsColixSet.get(i)) {
        byte pid = atoms[i].getPaletteID();
        if (pid != JmolConstants.PALETTE_CPK || atoms[i].isTranslucent())
          setStateInfo(temp, i, getColorCommand("atoms", pid, atoms[i].getColix()));
      }
    }
    return getShapeCommands(temp, null, atomCount);
  }
  
  

}
