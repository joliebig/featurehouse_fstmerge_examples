

package edu.rice.cs.drjava.model;

import java.util.List;
import java.io.File;
import java.io.IOException;
import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.util.ClassPathVector;


public interface FileGroupingState {

  
  public boolean isProjectActive();
  
  
  public boolean inProjectPath(OpenDefinitionsDocument doc);
  
  
  public boolean inProjectPath(File f);
  


  
  
  public File getProjectFile();
  
  
  public File getProjectRoot();
  
  
  public void setProjectFile(File f);
  
  
  public void setProjectRoot(File f);
  
  
  public File getBuildDirectory();
  
  
  public void setBuildDirectory(File f);
  
  
  public File getWorkingDirectory();
  
   
  public void setWorkingDirectory(File f);
  
  
  public File getMainClass();
  
  
  public void setMainClass(File f);
  
  
  public void setCreateJarFile(File f);
  
  
  public File getCreateJarFile();
  
  
  public void setCreateJarFlags(int f);
  
  
  public int getCreateJarFlags();
  
  
  public File[] getProjectFiles();
  
  
  public boolean inProject(File f);
  
  
  public boolean isAuxiliaryFile(File f);
  
  
  public boolean isProjectChanged();
  
  
  public void setProjectChanged(boolean changed); 

  
  public void cleanBuildDirectory() throws FileMovedException, IOException;
  
  
  public List<File> getClassFiles();
  
  
  public void jarAll();
  
  
  public ClassPathVector getExtraClassPath();
  
  
  public void setExtraClassPath(ClassPathVector cp);
  
}
