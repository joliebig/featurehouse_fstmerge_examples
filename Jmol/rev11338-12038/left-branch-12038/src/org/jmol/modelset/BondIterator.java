
package org.jmol.modelset;


public interface BondIterator {
  public boolean hasNext();
  public int nextIndex();
  public Bond next();
}
