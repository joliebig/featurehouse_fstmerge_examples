

package edu.rice.cs.drjava.model.repl.newjvm;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.lang.ClassLoader;
import edu.rice.cs.drjava.model.ClassPathEntry;
import edu.rice.cs.drjava.model.DeadClassLoader;
import edu.rice.cs.drjava.model.BrainClassLoader;

import edu.rice.cs.util.ClassPathVector;


public class ClassPathManager{
  
  private LinkedList<ClassPathEntry> projectCP;              
  private LinkedList<ClassPathEntry> buildCP;                
  private LinkedList<ClassPathEntry> projectFilesCP;         
  private LinkedList<ClassPathEntry> externalFilesCP;        
  private LinkedList<ClassPathEntry> extraCP;                 
  


  
  public ClassPathManager() {
    projectCP = new LinkedList<ClassPathEntry>();
    buildCP = new LinkedList<ClassPathEntry>();
    projectFilesCP = new LinkedList<ClassPathEntry>();
    externalFilesCP = new LinkedList<ClassPathEntry>();
    extraCP = new LinkedList<ClassPathEntry>();


  }
  
  
  public synchronized void addProjectCP(URL f) { projectCP.add(0, new ClassPathEntry(f)); }
  
  public synchronized ClassPathEntry[] getProjectCP() { 
    return projectCP.toArray(new ClassPathEntry[projectCP.size()]); 
  }
  
  
  public synchronized void addBuildDirectoryCP(URL f) {
    buildCP.addFirst(new ClassPathEntry(f));
  }

  public synchronized ClassPathEntry[] getBuildDirectoryCP() { 
    return buildCP.toArray(new ClassPathEntry[buildCP.size()]); 
  }
  
  
  public synchronized void addProjectFilesCP(URL f) { projectFilesCP.addFirst(new ClassPathEntry(f)); }
  
  public synchronized ClassPathEntry[] getProjectFilesCP() { 
    return projectFilesCP.toArray(new ClassPathEntry[projectFilesCP.size()]); 
  }
  
  
  public void addExternalFilesCP(URL f) { externalFilesCP.add(0, new ClassPathEntry(f)); }
  
  public ClassPathEntry[] getExternalFilesCP() { 
    return externalFilesCP.toArray(new ClassPathEntry[externalFilesCP.size()]); 
  }
  
  
  public synchronized void addExtraCP(URL f) { extraCP.addFirst(new ClassPathEntry(f)); }
  
  public ClassPathEntry[] getExtraCP() { return extraCP.toArray(new ClassPathEntry[extraCP.size()]); }
  
  
  public synchronized ClassLoader getClassLoader() {
    return new BrainClassLoader(buildClassLoader(projectCP), 
                                buildClassLoader(buildCP), 
                                buildClassLoader(projectFilesCP), 
                                buildClassLoader(externalFilesCP), 
                                buildClassLoader(extraCP));
  }
  
  
  private ClassLoader buildClassLoader(List<ClassPathEntry>locpe) {
    ClassLoader c = new DeadClassLoader();
    for(ClassPathEntry cpe: locpe) { c = cpe.getClassLoader(c); }
    return c;
  }

  
  public synchronized ClassPathVector getAugmentedClassPath() {
    ClassPathVector ret = new ClassPathVector();
  
    for (ClassPathEntry e: getProjectCP()) { ret.add(e.getEntry()); }

    for (ClassPathEntry e: getBuildDirectoryCP()) { ret.add(e.getEntry()); }

    for (ClassPathEntry e: getProjectFilesCP()) { ret.add(e.getEntry()); }

    for (ClassPathEntry e: getExternalFilesCP()) { ret.add(e.getEntry()); }

    for (ClassPathEntry e: getExtraCP()) { ret.add(e.getEntry()); }
    return ret;
  }

}

