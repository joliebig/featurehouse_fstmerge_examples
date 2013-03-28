


package edu.rice.cs.drjava.model.compiler;

import java.io.IOException;
import java.io.File;
import java.util.List;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;


public interface CompilerModel {
  
  
  
  
  public Object getSlaveJVMLock();
  
  
  
  
  public void addListener(CompilerListener listener);

  
  public void removeListener(CompilerListener listener);

  
  public void removeAllListeners();
  
  
  
  
  public void compileAll() throws IOException;
  
  
  public void compileAll(List<File> sourceroots, List<File> files) throws IOException ;
  
  
  public void compile(List<OpenDefinitionsDocument> doc) throws IOException;
  
  
  public void compile(OpenDefinitionsDocument doc) throws IOException;
  
  
  
  
  public CompilerErrorModel getCompilerErrorModel();
  
  public int getNumErrors();
  
  
  public void resetCompilerErrors();
  
  

  
  public CompilerInterface[] getAvailableCompilers();

  
  public CompilerInterface getActiveCompiler(); 

  
  public void setActiveCompiler(CompilerInterface compiler);
}