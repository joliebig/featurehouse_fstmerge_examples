

package edu.rice.cs.drjava.project;

import java.io.File;

import edu.rice.cs.plt.tuple.Pair;


public interface DocumentInfoGetter {
  
  
  public Pair<Integer,Integer> getSelection();
  
  
  public Pair<Integer,Integer> getScroll();
  
  
  public File getFile();
  
  
  public String getPackage();
  
  
  public boolean isActive();
  
  
  public boolean isUntitled();
}