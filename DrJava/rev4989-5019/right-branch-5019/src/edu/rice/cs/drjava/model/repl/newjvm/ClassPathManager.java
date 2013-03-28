

package edu.rice.cs.drjava.model.repl.newjvm;

import java.io.File;
import java.util.LinkedList;
import java.lang.ClassLoader;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.plt.reflect.PathClassLoader;

import static edu.rice.cs.plt.debug.DebugUtil.error;
import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class ClassPathManager implements Lambda<ClassLoader, ClassLoader> {
  
  
  
  
  private final LinkedList<File> _projectCP;       
  private final LinkedList<File> _buildCP;         
  private final LinkedList<File> _projectFilesCP;  
  private final LinkedList<File> _externalFilesCP; 
  private final LinkedList<File> _extraCP;         
  
  
  private final Iterable<File> _fullPath;
  
  public ClassPathManager(Iterable<File> builtInCP) {
    _projectCP = new LinkedList<File>();
    _buildCP = new LinkedList<File>();
    _projectFilesCP = new LinkedList<File>();
    _externalFilesCP = new LinkedList<File>();
    _extraCP = new LinkedList<File>();
    
    Iterable<Iterable<File>> allPaths =
      IterUtil.<Iterable<File>>make(IterUtil.asSizedIterable(_projectCP),
                                    IterUtil.asSizedIterable(_buildCP),
                                    IterUtil.asSizedIterable(_projectFilesCP),
                                    IterUtil.asSizedIterable(_externalFilesCP),
                                    IterUtil.asSizedIterable(_extraCP),
                                    IterUtil.snapshot(builtInCP));
    
    
    _fullPath = IterUtil.collapse(IterUtil.map(allPaths, _makeSafeSnapshot));
  }
  
  private final Lambda<Iterable<File>, Iterable<File>> _makeSafeSnapshot =
    new Lambda<Iterable<File>, Iterable<File>>() {
    public Iterable<File> value(Iterable<File> arg) {
      synchronized(ClassPathManager.this) { return IterUtil.snapshot(arg); }
    }
  };
  
  
  public synchronized void addProjectCP(File f) { _projectCP.addFirst(f); }
  
  public synchronized Iterable<File> getProjectCP() { return IterUtil.snapshot(_projectCP); }
  
  
  public synchronized void addBuildDirectoryCP(File f) {
    _buildCP.remove(f); 
    _buildCP.addFirst(f);
  }
  
  public synchronized Iterable<File> getBuildDirectoryCP() { return IterUtil.snapshot(_buildCP); }
  
  
  public synchronized void addProjectFilesCP(File f) {
    _projectFilesCP.remove(f); 
    _projectFilesCP.addFirst(f);
  }
  
  public synchronized Iterable<File> getProjectFilesCP() { return IterUtil.snapshot(_projectFilesCP); }
  
  
  public synchronized void addExternalFilesCP(File f) {
    _externalFilesCP.remove(f); 
    _externalFilesCP.addFirst(f);
  }
  
  public synchronized Iterable<File> getExternalFilesCP() { return IterUtil.snapshot(_externalFilesCP); }
  
  
  public synchronized void addExtraCP(File f) {
    _extraCP.remove(f); 
    _extraCP.addFirst(f);
  }
  
  public Iterable<File> getExtraCP() { return IterUtil.snapshot(_extraCP); }
  
  
  public synchronized ClassLoader makeClassLoader(ClassLoader parent) {
    return new PathClassLoader(parent, _fullPath);
  }
  
  
  public ClassLoader value(ClassLoader parent) { return makeClassLoader(parent); }
  
  
  public synchronized Iterable<File> getClassPath() { return _fullPath; }
}
