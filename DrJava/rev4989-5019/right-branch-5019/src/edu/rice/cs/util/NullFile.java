

package edu.rice.cs.util;
import java.io.File;
import java.io.Serializable;


public class NullFile extends File implements Serializable {
  
  private static volatile int ct = 0;

  public NullFile() { 
    this("*NullFile<" + ct + ">"); 
    ct++;
  }
  
  private NullFile(String lexiName) {
    super(lexiName);
  }
  
  
  public String toString() { return "(Untitled)"; }
  public String getName() { return "(Untitled)"; }
  
  public String getLexiName() { return ""; }
  
  
  public boolean equals(Object o) { return o == this; } 
}
