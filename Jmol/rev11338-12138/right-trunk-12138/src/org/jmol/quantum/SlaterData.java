

package org.jmol.quantum;

public class SlaterData {

  public boolean isCore;
  public int iAtom;
  public int x;
  public int y;
  public int z;
  public int r;
  public double zeta;
  public double coef;
  public int index;
      
  public SlaterData(int iAtom, int x, int y, int z, int r, double zeta, double coef) {
    this.iAtom = iAtom;
    this.x = x;
    this.y = y;
    this.z = z;
    this.r = r;
    this.zeta = zeta;
    this.coef = coef;
  }
}