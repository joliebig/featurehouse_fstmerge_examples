

package edu.rice.cs.util;

import java.io.File;
import java.io.Serializable;


public interface FileOpenSelector extends Serializable {
  public File[] getFiles() throws OperationCanceledException;
}
