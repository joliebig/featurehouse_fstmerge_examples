

package edu.rice.cs.drjava.model;

import java.io.File;


public interface FileRegion extends Region {
  
  
  public int getStartOffset();

  
  public int getEndOffset();
  
  
  public File getFile();
}
  
