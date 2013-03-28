

package edu.rice.cs.drjava.model;

import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;


public class BrowserDocumentRegion implements IDocumentRegion, Comparable<BrowserDocumentRegion> {
  private static volatile int _indexCounter = 0;  
  private final int _index;                        
  protected final OpenDefinitionsDocument _doc;    
  protected final File _file;                      
  protected Position _startPosition;               
  protected Position _endPosition;                 
  protected volatile DefaultMutableTreeNode _treeNode;
  
  
  
  public BrowserDocumentRegion(OpenDefinitionsDocument doc, Position sp, Position ep) {
    assert doc != null;
    _index = _indexCounter++;    
    _doc = doc;
    _file = doc.getRawFile();  
    _startPosition = sp;
    _endPosition = ep;
    _treeNode = null;
  }
  
  
  
  
  public int hashCode() { return _index; }
  
  
  public int compareTo(BrowserDocumentRegion r) { return _index - r._index; }
  
  public int getIndex() { return _index; }
  
  
  public OpenDefinitionsDocument getDocument() { return _doc; }

  
  public File getFile() { return _file; }

  
  public int getStartOffset() { return _startPosition.getOffset(); }

  
  public int getEndOffset() { return _endPosition.getOffset(); }
  






  public void update(BrowserDocumentRegion other) {
    if (other.getDocument()!=_doc) throw new IllegalArgumentException("Regions must have the same document.");
    _startPosition = other._startPosition;
    _endPosition = other._endPosition;
  }
  
  public String toString() {
    return _doc.toString() + "[" + getStartOffset() + "]";
  }
}
