


package edu.rice.cs.drjava.model.compiler;

import java.io.IOException;
import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public interface CompilerModel {
  
  
  public static final String[] EXTENSIONS = new String[]{".java", ".dj0", ".dj1", ".dj2"};
  
  
  
  
  public Object getSlaveJVMLock();
  
  
  
  
  public void addListener(CompilerListener listener);

  
  public void removeListener(CompilerListener listener);

  
  public void removeAllListeners();
  
  
  
  
  public void compileAll() throws IOException;
  
  
  public void compile(List<OpenDefinitionsDocument> docs) throws IOException;
  
  
  public void compile(OpenDefinitionsDocument doc) throws IOException;
  
  
  
  
  public CompilerErrorModel getCompilerErrorModel();
  
  
  public int getNumErrors();
  
  
  public void resetCompilerErrors();
  
  

  
  public CompilerInterface[] getAvailableCompilers();

  
  public CompilerInterface getActiveCompiler(); 

  
  public void setActiveCompiler(CompilerInterface compiler);
}