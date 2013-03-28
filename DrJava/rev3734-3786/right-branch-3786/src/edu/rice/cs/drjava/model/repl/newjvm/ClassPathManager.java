

package edu.rice.cs.drjava.model.repl.newjvm;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.lang.ClassLoader;
import edu.rice.cs.drjava.model.ClassPathEntry;
import edu.rice.cs.drjava.model.DeadClassLoader;
import edu.rice.cs.drjava.model.BrainClassLoader;

import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.swing.ScrollableDialog;


public class ClassPathManager{

  
  List<ClassPathEntry> projectCP;
  
  List<ClassPathEntry> buildCP;
  
  List<ClassPathEntry> projectFilesCP;
  
  List<ClassPathEntry> externalFilesCP;
  
  List<ClassPathEntry> extraCP;
  
  List<ClassPathEntry> systemCP;




  public ClassPathManager() {
    projectCP = new LinkedList<ClassPathEntry>();
    buildCP = new LinkedList<ClassPathEntry>();
    projectFilesCP = new LinkedList<ClassPathEntry>();
    externalFilesCP = new LinkedList<ClassPathEntry>();
    extraCP = new LinkedList<ClassPathEntry>();
    systemCP = new LinkedList<ClassPathEntry>();

  }

  
  public void addProjectCP(URL f) {
    
    projectCP.add(0, new ClassPathEntry(f));
  }

  public List<ClassPathEntry> getProjectCP() { return projectCP; }

  
  public void addBuildDirectoryCP(URL f) {
    
    buildCP.add(0, new ClassPathEntry(f));
  }

  public List<ClassPathEntry> getBuildDirectoryCP() { return buildCP; }

  
  public void addProjectFilesCP(URL f) {
    
    projectFilesCP.add(0, new ClassPathEntry(f));
  }

  public List<ClassPathEntry> getProjectFilesCP() { return projectFilesCP; }

  
  public void addExternalFilesCP(URL f) { externalFilesCP.add(0, new ClassPathEntry(f)); }

  public List<ClassPathEntry> getExternalFilesCP() { return externalFilesCP; }

  
  public void addExtraCP(URL f) {
    
    extraCP.add(0, new ClassPathEntry(f));
  }

  public List<ClassPathEntry> getExtraCP() { return extraCP; }

  public List<ClassPathEntry> getSystemCP() { return systemCP; }

  
  public ClassLoader getClassLoader() {
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

  
  public ClassPathVector getAugmentedClassPath() {
    ClassPathVector ret = new ClassPathVector();
    List<ClassPathEntry> locpe = getProjectCP();
    for (ClassPathEntry e: locpe) { ret.add(e.getEntry()); }

    locpe = getBuildDirectoryCP();
    for (ClassPathEntry e: locpe) { ret.add(e.getEntry()); }

    locpe = getProjectFilesCP();
    for (ClassPathEntry e: locpe) { ret.add(e.getEntry()); }

    locpe = getExternalFilesCP();
    for (ClassPathEntry e: locpe) { ret.add(e.getEntry()); }

    locpe = getExtraCP();
    for (ClassPathEntry e: locpe) { ret.add(e.getEntry()); }
    return ret;
  }

}

