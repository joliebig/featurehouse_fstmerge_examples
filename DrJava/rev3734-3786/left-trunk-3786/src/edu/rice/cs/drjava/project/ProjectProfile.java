

package edu.rice.cs.drjava.project;

import java.util.ArrayList;
import java.util.List;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.*;

import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.Version;
import edu.rice.cs.util.Pair;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.DebugException;

import static edu.rice.cs.util.StringOps.*;
import static edu.rice.cs.util.FileOps.*;


public class ProjectProfile implements ProjectFileIR {
  
  
  
  private List<DocFile> _sourceFiles = new ArrayList<DocFile>();
  private List<DocFile> _auxFiles = new ArrayList<DocFile>();
  private List<String> _collapsedPaths = new ArrayList<String>();
  
  private File _buildDir = null;
  private File _workDir = null;
  
  private List<File> _classPathFiles = new ArrayList<File>();
  
  private File _mainClass = null;
  
  
  private File _projectRoot;
  
  private File _projectFile;  
  
  private File _createJarFile = null;
  
  private int _createJarFlags = 0;
  
  private List<DebugBreakpointData> _breakpoints = new ArrayList<DebugBreakpointData>();
  private List<DebugWatchData> _watches = new ArrayList<DebugWatchData>();
  
  
  public ProjectProfile(String fileName) throws IOException { this(new File(fileName)); }
  
  
  public ProjectProfile(File f) throws IOException { 
    _projectFile = f; 
    _projectRoot = _projectFile.getParentFile();
    if (! _projectRoot.exists()) throw new IOException("Parent directory of project root " + _projectRoot + 
                                                       " does not exist");
  }
  
  
  
  
  public DocFile[] getSourceFiles() { return _sourceFiles.toArray(new DocFile[_sourceFiles.size()]); }
    
  
  public DocFile[] getAuxiliaryFiles() { return _auxFiles.toArray(new DocFile[_auxFiles.size()]); }
  
  
  public File getProjectFile() { return _projectFile; }
    
  
  public File getBuildDirectory() { return _buildDir; }
  
   
  public File getWorkingDirectory() { return _workDir; }
  
  
  public String[] getCollapsedPaths() { return _collapsedPaths.toArray(new String[_collapsedPaths.size()]); }
    
  
  public File[] getClassPaths() { return _classPathFiles.toArray(new File[_classPathFiles.size()]); }
  
  
  public File getMainClass() { return _mainClass; }
  
  
  public File getProjectRoot() { return _projectRoot; }
  
  
  public File getCreateJarFile() { return _createJarFile; }
  
  
  public int getCreateJarFlags() { return _createJarFlags; }
  
  
  public DebugBreakpointData[] getBreakpoints() { return _breakpoints.toArray(new DebugBreakpointData[_breakpoints.size()]); }
  
  
  public DebugWatchData[] getWatches() { return _watches.toArray(new DebugWatchData[_watches.size()]); }
  
  
  
  public void addSourceFile(DocFile df) { _sourceFiles.add(df); }
  
  public void addSourceFile(DocumentInfoGetter getter) {
    if (!getter.isUntitled()) {
      try { addSourceFile(docFileFromGetter(getter)); }
      catch(IOException e) { throw new UnexpectedException(e); }
    }
  }
  
  public void addAuxiliaryFile(DocFile df) { _auxFiles.add(df); }
    
  public void addAuxiliaryFile(DocumentInfoGetter getter) {
    if (! getter.isUntitled()) {
      try { addAuxiliaryFile(docFileFromGetter(getter)); }
      catch(IOException e) { throw new UnexpectedException(e); }
    }
  }
  
  public void addClassPathFile(File cp) { if (cp != null) _classPathFiles.add(cp); }
  public void addCollapsedPath(String cp) { if (cp != null) _collapsedPaths.add(cp); }
  public void setBuildDirectory(File dir) { 

    _buildDir = FileOps.validate(dir); 

  }
  public void setWorkingDirectory(File dir) { _workDir = FileOps.validate(dir); }
  public void setMainClass(File main) { _mainClass = main;  }
  public void setSourceFiles(List<DocFile> sf) { _sourceFiles = new ArrayList<DocFile>(sf); }
  public void setClassPaths(List<? extends File> cpf) { _classPathFiles = new ArrayList<File>(cpf); }
  public void setCollapsedPaths(List<String> cp) { _collapsedPaths = new ArrayList<String>(cp); }
  public void setAuxiliaryFiles(List<DocFile> af) { _auxFiles = new ArrayList<DocFile>(af); }

  
  public void setProjectRoot(File root) { 
    _projectRoot = root; 
    assert root.getParentFile() != null;
  }
  
  public void setCreateJarFile(File createJarFile) { _createJarFile = createJarFile; }
  public void setCreateJarFlags(int createJarFlags) { _createJarFlags = createJarFlags; }
  
  public void setBreakpoints(List<DebugBreakpointData> bps) { _breakpoints = new ArrayList<DebugBreakpointData>(bps); }
  public void setWatches(List<DebugWatchData> ws) { _watches = new ArrayList<DebugWatchData>(ws); }
  
  
  public void write() throws IOException {
    FileWriter fw = new FileWriter(_projectFile);
    
    
    fw.write(";; DrJava project file, written by build " + Version.getBuildTimeString());
    fw.write("\n;; files in the source tree are relative to: " + _projectRoot.getCanonicalPath());
    fw.write("\n;; other files with relative paths are rooted at (the parent of) this project file");
    
    
    
    if (_projectRoot != null) {
      fw.write("\n(proj-root-and-base");

      fw.write("\n" + encodeFileRelative(_projectRoot, "  ", _projectFile));
      fw.write(")");
    }
    else fw.write("\n;; no project root; should never happen");
        
    
    
    if (!_sourceFiles.isEmpty()) {
      fw.write("\n(source-files");
      for(DocFile df: _sourceFiles) { fw.write("\n" + encodeDocFileRelative(df, "  ")); }
      fw.write(")"); 
    }
    else fw.write("\n;; no source files");
    
    
    if (!_auxFiles.isEmpty()) {
      fw.write("\n(auxiliary");
      for(DocFile df: _auxFiles) { fw.write("\n" + encodeDocFileAbsolute(df, "  ")); }
      fw.write(")"); 
    }
    else fw.write("\n;; no aux files");
    
    
    if (!_collapsedPaths.isEmpty()) {
      fw.write("\n(collapsed");
      for(String s: _collapsedPaths) {
        fw.write("\n  (path " + convertToLiteral(s) + ")");
      }
      fw.write(")"); 
    }
    else fw.write("\n;; no collapsed branches");
    
    
    if (!_classPathFiles.isEmpty()) {
      fw.write("\n(classpaths");
      for(File f: _classPathFiles) {
        fw.write("\n" + encodeFileAbsolute(f, "  "));
      }
      fw.write(")"); 
    }
    else fw.write("\n;; no classpaths files");
    
    
    if (_buildDir != null && _buildDir.getPath() != "") {
      fw.write("\n(build-dir");
      fw.write("\n" + encodeFileRelative(_buildDir, "  ", _projectFile));
      fw.write(")");
    }
    else fw.write("\n;; no build directory");
    
     
    if (_workDir != null && _workDir.getPath() != "") {
      fw.write("\n(work-dir");
      fw.write("\n" + encodeFileRelative(_workDir, "  ", _projectFile));
      fw.write(")");
    }
    else fw.write("\n;; no working directory");
    
    
    if (_mainClass != null) {
      fw.write("\n(main-class");
      fw.write("\n" + encodeFileRelative(_mainClass, "  "));
      fw.write(")");
    }
    else fw.write("\n;; no main class");
    














    
    if (!_breakpoints.isEmpty()) {
      fw.write("\n(breakpoints");
      for(DebugBreakpointData bp: _breakpoints) { fw.write("\n" + encodeBreakpointRelative(bp, "  ")); }
      fw.write(")"); 
    }
    else fw.write("\n;; no breakpoints");

    
    if (!_watches.isEmpty()) {
      fw.write("\n(watches");
      for(DebugWatchData w: _watches) { fw.write("\n" + encodeWatch(w, "  ")); }
      fw.write(")"); 
    }
    else fw.write("\n;; no watches");

    fw.close();
  }
  

  
  
  
  private DocFile docFileFromGetter(DocumentInfoGetter g) throws IOException {    
      return new DocFile(g.getFile().getCanonicalPath(), g.getSelection(), g.getScroll(), g.isActive(), g.getPackage());
  }
  
  
  
  private String encodeFileRelative(File f, String prefix, File base) throws IOException {
    String path = FileOps.makeRelativeTo(f, base).getPath();
    path = replace(path, File.separator, "/");
    return prefix + "(file (name " + convertToLiteral(path) + "))";
  }

  
  private String encodeFileRelative(File f, String prefix) throws IOException { 
    return encodeFileRelative(f, prefix, _projectRoot); 
  }
    
  
  private String encodeFileAbsolute(File f, String prefix) throws IOException {
    String path = f.getCanonicalPath();
    path = replace(path,File.separator, "/");
    return prefix + "(file (name " + convertToLiteral(path) + "))";
  }
  
  
  private String encodeDocFile(DocFile df, String prefix, boolean relative) throws IOException {
    String ret = "";
    String path;
    if (relative) path = makeRelativeTo(df, _projectRoot).getPath();
    else path = FileOps.getCanonicalPath(df);

    path = replace(path, File.separator, "/");
    ret += prefix + "(file (name " + convertToLiteral(path) + ")";
    
    Pair<Integer,Integer> p1 = df.getSelection();
    Pair<Integer,Integer> p2 = df.getScroll();
    boolean active = df.isActive();
    long modDate = df.lastModified();
    
    if (p1 != null || p2 != null || active)  ret += "\n" + prefix + "      ";

    
    if (p1 != null) ret += "(select " + p1.getFirst() + " " + p1.getSecond() + ")";

    if (p2 != null) ret += "(scroll " + p2.getFirst() + " " + p2.getSecond() + ")";

    if (modDate > 0) {
      String s = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date(modDate));
      ret += "(mod-date " + convertToLiteral(s) + ")";
    }
    
    if (active) ret += "(active)";
    
    
    String pack = df.getPackage();
    if (pack != null) {
      ret += "\n" + prefix + "      "; 
      ret += "(package " + convertToLiteral(pack) + ")";
    }
    
    ret += ")"; 
    
    return ret;
  }
  
  private String encodeDocFileRelative(DocFile df, String prefix) throws IOException {
    return encodeDocFile(df, prefix, true);
  }
  private String encodeDocFileAbsolute(DocFile df, String prefix) throws IOException {
    return encodeDocFile(df, prefix, false);
  }
  
  
  private String encodeBreakpointRelative(DebugBreakpointData bp, String prefix) throws IOException {
    String ret = "";
    String path = makeRelativeTo(bp.getFile(), _projectRoot).getPath();
    
    path = replace(path,File.separator,"/");
    ret += prefix + "(breakpoint (name " + convertToLiteral(path) + ")";
    
    int offset = bp.getOffset();
    int lineNumber = bp.getLineNumber();
    ret += "\n" + prefix + "      ";
    ret += "(offset " + offset + ")";
    ret += "(line " + lineNumber + ")";
    if (bp.isEnabled()) ret += "(enabled)";
    ret += ")"; 
    
    return ret;
  }
 
  
  private String encodeWatch(DebugWatchData w, String prefix) throws IOException {
    String ret = "";

    ret += prefix + "(watch " + convertToLiteral(w.getName()) + ")";
    
    return ret;
  }
}