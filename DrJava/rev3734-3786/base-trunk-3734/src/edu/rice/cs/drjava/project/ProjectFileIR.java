

package edu.rice.cs.drjava.project;

import java.io.*;
import java.util.List;

import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugWatchData;

public interface ProjectFileIR {
  
  public DocFile[] getSourceFiles();
    
  
  public DocFile[] getAuxiliaryFiles();
    
  
  public File getBuildDirectory();
  
   
  public File getWorkingDirectory();
  
  
  public String[] getCollapsedPaths();
    
  
  public File[] getClassPaths();
  
  
  public File getMainClass();
  
  
  public File getProjectFile();
  
  
  public File getProjectRoot();
  
  
  public File getCreateJarFile();
  
  
  public int getCreateJarFlags();
  
  
  public DebugBreakpointData[] getBreakpoints();
  
  
  public DebugWatchData[] getWatches();
  
  public void setSourceFiles(List<DocFile> sf);
  public void setAuxiliaryFiles(List<DocFile> aux);
  public void setCollapsedPaths(List<String> paths);
  public void setClassPaths(List<File> cp);
  public void setBuildDirectory(File dir);
  public void setWorkingDirectory(File dir);
  public void setMainClass(File main);
  public void setProjectRoot(File root);
  public void setCreateJarFile(File createJarFile);
  public void setCreateJarFlags(int createJarFlags);
  public void setBreakpoints(List<DebugBreakpointData> bps);
  public void setWatches(List<DebugWatchData> ws);
}
