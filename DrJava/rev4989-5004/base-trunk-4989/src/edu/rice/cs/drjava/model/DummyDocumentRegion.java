

package edu.rice.cs.drjava.model;

import java.io.File;


public class DummyDocumentRegion implements FileRegion {

  protected final File _file;
  protected volatile int _startOffset;
  protected volatile int _endOffset;
  
  
  public DummyDocumentRegion(File file, int so, int eo) {
    _file = file;
    _startOffset = so;
    _endOffset = eo;
  }

  
  public File getFile() { return _file; }

  
  public int getStartOffset() { return _startOffset; }

  
  public int getEndOffset() { return _endOffset; }
}
