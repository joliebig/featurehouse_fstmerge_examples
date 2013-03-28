

package edu.rice.cs.drjava.config;

import edu.rice.cs.plt.lambda.Thunk;
import java.io.*;


public class MutableFileProperty extends FileProperty {
  protected File _fileValue = null;
  
  public MutableFileProperty(String name, File initialFile, String help) {
    super(name,new Thunk<File>() { public File value() { return null; } }, help);
    
    _getFile = new Thunk<File>() { public File value() { return _fileValue; } };
    _fileValue = initialFile;
    _value = "";
    resetAttributes();
  }
  
  
  public void setFile(File f) { _fileValue = f; }
} 
