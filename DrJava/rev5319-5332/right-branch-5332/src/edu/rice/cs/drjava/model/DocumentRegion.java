

package edu.rice.cs.drjava.model;


public class DocumentRegion implements OrderedDocumentRegion {
  protected final OpenDefinitionsDocument _doc;
  
  protected volatile int _start; 
  protected volatile int _end;  
  
  
 
  
  public DocumentRegion(OpenDefinitionsDocument doc, int start, int end) {
    assert doc != null && end >= start;
    _doc = doc;
    _start = start;
    _end = end;
  }
  
  
  public final boolean equals(Object o) {
    if (o == null || ! (o instanceof IDocumentRegion)) return false;
    IDocumentRegion r = (IDocumentRegion) o;
    return getDocument() == r.getDocument() && getStartOffset() == r.getStartOffset() && getEndOffset() == r.getEndOffset();
  }
  
  
  public int compareTo(OrderedDocumentRegion r) {
    int docRel = getDocument().compareTo(r.getDocument());
    if (docRel != 0) return docRel;
    
    
    assert getDocument() == r.getDocument();  
    int end1 = getEndOffset();
    int end2 = r.getEndOffset();
    int endDiff = end1 - end2;
    if (endDiff != 0) return endDiff;
    
    int start1 = getStartOffset();
    int start2 = r.getStartOffset();
    return start1 - start2;
  }
  
  
  
  
  public OpenDefinitionsDocument getDocument() { return _doc; }

  
  public int getStartOffset() { return _start; }

  
  public int getEndOffset() { return _end; }
  
  
  public int getLineStartOffset() { 
    throw new UnsupportedOperationException("DocumentRegion does not suppport getLineStart()"); 
  }
  
  
  public int getLineEndOffset() { 
    throw new UnsupportedOperationException("DocumentRegion does not suppport getLineEnd()"); 
  }
  
  
  public String getString() { 
    throw new UnsupportedOperationException("DocumentRegion does not suppport getString()"); 
  }
  
  
  public void update() { 
    throw new UnsupportedOperationException("DocumentRegion does not suppport updateLines()"); 
  }
  
  public boolean isEmpty() { return getStartOffset() == getEndOffset(); }
  
  public String toString() {
    return ( _doc.toString() ) + "[" + getStartOffset() + " .. " + getEndOffset() + "]";
  }
}
