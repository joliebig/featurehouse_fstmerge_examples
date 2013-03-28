

package edu.rice.cs.drjava.model;

import java.io.IOException;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.OperationCanceledException;


public interface ILoadDocuments {
  
  public OpenDefinitionsDocument openFile(FileOpenSelector com) throws IOException, OperationCanceledException, 
    AlreadyOpenException;

  
  public OpenDefinitionsDocument[] openFiles(FileOpenSelector com) throws IOException, OperationCanceledException, 
    AlreadyOpenException;
}