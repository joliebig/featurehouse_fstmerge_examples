

package org.jmol.minimize;

import java.util.Vector;

import org.jmol.modelset.Atom;

public class MinAtom {

  int index;
  public Atom atom;
  public double[] coord = new double[3];
  public double[] force = new double[3];
  public Vector bonds = new Vector();
  public int nBonds;
  
  public String type;
  int[] bondedAtoms;
  
  MinAtom(int index, Atom atom, double[] coord, String type) {
    this.index = index;
    this.atom = atom;
    this.coord = coord;
    this.type = type;
  }

  void set() {
    coord[0] = atom.x;
    coord[1] = atom.y;
    coord[2] = atom.z;
  }

  public MinBond getBondTo(int iAtom) {
    getBondedAtomIndexes();
    for (int i = 0; i < nBonds; i++)
      if (bondedAtoms[i] == iAtom)
        return (MinBond) bonds.elementAt(i);
    return null;
  }
  
  public int[] getBondedAtomIndexes() {
    if (bondedAtoms == null) {
      bondedAtoms = new int[nBonds];
      for (int i = nBonds; --i >= 0;)
        bondedAtoms[i] = ((MinBond) bonds.elementAt(i)).getOtherAtom(index);
    }
    return bondedAtoms;
  }

  public String getIdentity() {
    return atom.getInfo();
  }

}
