

package edu.rice.cs.drjava.project;

import java.io.*;
import java.util.List;

import edu.rice.cs.drjava.model.FileRegion;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.util.AbsRelFile;

public interface ProjectFileIR {
  
  public DocFile[] getSourceFiles();
    
  
  public DocFile[] getAuxiliaryFiles();
  
  
  public DocFile[] getExcludedFiles();
    
  
  public File getBuildDirectory();
  
   
  public File getWorkingDirectory();
  
  
  public String[] getCollapsedPaths();
    
  
  public Iterable<AbsRelFile> getClassPaths();
  
  
  public String getMainClass();
  
  
  public File getMainClassContainingFile();
  
  
  public File getProjectFile();
  
  
  public File getProjectRoot();
  
  
  public File getCreateJarFile();
  
  
  public int getCreateJarFlags();
  
  
  public FileRegion[] getBookmarks();
  
  
  public DebugBreakpointData[] getBreakpoints();
  
  
  public DebugWatchData[] getWatches();
  
  public boolean getAutoRefreshStatus();
  
  public void setSourceFiles(List<DocFile> sf);
  public void setAuxiliaryFiles(List<DocFile> aux);
  public void setExcludedFiles(List<DocFile> ef);
  public void setCollapsedPaths(List<String> paths);
  public void setClassPaths(Iterable<? extends AbsRelFile> cp);
  public void setBuildDirectory(File dir);
  public void setWorkingDirectory(File dir);
  public void setMainClass(String main);
  public void setProjectRoot(File root);
  public void setCreateJarFile(File createJarFile);
  public void setCreateJarFlags(int createJarFlags);
  public void setBookmarks(List<? extends FileRegion> bms);
  public void setBreakpoints(List<? extends DebugBreakpointData> bps);
  public void setWatches(List<? extends DebugWatchData> ws);
  public void setAutoRefreshStatus(boolean b);
  
  
  public String getDrJavaVersion();
  
  
  public void setDrJavaVersion(String version);
  
  
  public String getCustomManifest();
  
  
  public void setCustomManifest(String manifest);
}
