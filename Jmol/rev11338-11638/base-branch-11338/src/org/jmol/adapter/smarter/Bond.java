

package org.jmol.adapter.smarter;
public class Bond {
  public int atomIndex1;
  public int atomIndex2;
  public int order;

  public Bond() {
    atomIndex1 = atomIndex2 = -1;
    order = 1;
  }

  public Bond(int atomIndex1, int atomIndex2, int order) {
    this.atomIndex1 = atomIndex1;
    this.atomIndex2 = atomIndex2;
    this.order = order;
  }
}
