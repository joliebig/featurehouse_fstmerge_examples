

package edu.rice.cs.drjava.model;

import java.io.File;
import java.util.List;
import edu.rice.cs.util.AbsRelFile;


public interface FileGroupingState {
  
  
  public boolean isProjectActive();
  
  
  public boolean inProjectPath(OpenDefinitionsDocument doc);
  
  
  public boolean inProjectPath(File f);
  


  
  
  public File getProjectFile();
  
  
  public File getProjectRoot();
  
  
  public void setProjectFile(File f);
  
  
  public void setProjectRoot(File f);
  
  
  public void addAuxFile(File f);
  
  
  public void remAuxFile(File f);
  
  
  public File getBuildDirectory();
  
  
  public void setBuildDirectory(File f);
  
  
  public File getWorkingDirectory();
  
  
  public void setWorkingDirectory(File f);
  
  
  public String getMainClass();
  
  
  public void setMainClass(String f);
  
  
  public void setCreateJarFile(File f);
  
  
  public File getCreateJarFile();
  
  
  public void setCreateJarFlags(int f);
  
  
  public int getCreateJarFlags();
  
  
  public File[] getProjectFiles();
  
  
  public boolean inProject(File f);
  
  
  public boolean isAuxiliaryFile(File f);
  
  
  public boolean isProjectChanged();
  
  
  public void setProjectChanged(boolean changed); 
  
  
  public void cleanBuildDirectory();
  
  
  public List<File> getClassFiles();
  
  
  public Iterable<AbsRelFile> getExtraClassPath();
  
  
  public void setExtraClassPath(Iterable<AbsRelFile> cp);
  
  
  public void addExcludedFile(File f);
  
  
  public boolean isExcludedFile(File f);
  
  
  public File[] getExclFiles();
  
  
  public void removeExcludedFile(File f);
  
  
  public void setExcludedFiles(File[] fs);

  public boolean getAutoRefreshStatus();
  
  public void setAutoRefreshStatus(boolean b);
  
  
  public void setCustomManifest(String manifest);
  
  
  public String getCustomManifest();
}
