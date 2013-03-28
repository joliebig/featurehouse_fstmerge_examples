

package edu.rice.cs.drjava.model;


public interface OrderedDocumentRegion extends IDocumentRegion, Comparable<OrderedDocumentRegion> {
  public int getLineStartOffset();
  public int getLineEndOffset();
  public void update();
  public String getString();
  public boolean isEmpty();
}

