

package edu.rice.cs.drjava.project;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.io.*;
import org.w3c.dom.Node;
import edu.rice.cs.plt.tuple.Pair;

import edu.rice.cs.util.AbsRelFile;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.drjava.Version;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.FileRegion;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.util.XMLConfig;

import edu.rice.cs.plt.text.TextUtil;

import edu.rice.cs.util.Log;

import static edu.rice.cs.util.StringOps.*;


public class ProjectProfile implements ProjectFileIR {
  static final String MOD_DATE_FORMAT_STRING = "dd-MMM-yyyy HH:mm:ss";
  static final DateFormat MOD_DATE_FORMAT =
    new SimpleDateFormat(MOD_DATE_FORMAT_STRING, Locale.US);
  
  
  
  private List<DocFile> _sourceFiles = new LinkedList<DocFile>();
  private List<DocFile> _auxiliaryFiles = new LinkedList<DocFile>();
  private List<DocFile> _excludedFiles = new ArrayList<DocFile>();
  private List<String> _collapsedPaths = new ArrayList<String>();
  
  private File _buildDir = FileOps.NULL_FILE;
  private File _workDir = FileOps.NULL_FILE;
  
  private List<AbsRelFile> _classPathFiles = new ArrayList<AbsRelFile>();
  
  private String _mainClass = null;
  
  
  private File _projectRoot; 
  
  private File _projectFile;  
  
  private File _createJarFile = FileOps.NULL_FILE;
  
  private int _createJarFlags = 0;
  
  private boolean _autoRefreshStatus = false;
  
  private List<FileRegion> _bookmarks = new ArrayList<FileRegion>();
  private List<DebugBreakpointData> _breakpoints = new ArrayList<DebugBreakpointData>();
  private List<DebugWatchData> _watches = new ArrayList<DebugWatchData>();
  
  private String _version = "unknown";
  
  private String _manifest = null;
  
  private static Log LOG = new Log("ProjectProfile.txt", false);
  
  
  public ProjectProfile(String fileName) throws IOException { this(new File(fileName)); }
  
  
  public ProjectProfile(File f) throws IOException { 
    _projectFile = f; 
    _projectRoot = _projectFile.getParentFile();
    if (! _projectRoot.exists()) throw new IOException("Parent directory of project root " + _projectRoot + 
                                                       " does not exist");
  }
  
  
  
  
  public DocFile[] getSourceFiles() { return _sourceFiles.toArray(new DocFile[_sourceFiles.size()]); }
    
  
  public DocFile[] getAuxiliaryFiles() { return _auxiliaryFiles.toArray(new DocFile[_auxiliaryFiles.size()]); }
  
  
  public DocFile[] getExcludedFiles() { return _excludedFiles.toArray(new DocFile[_excludedFiles.size()]); }
  
  
  public File getProjectFile() { return _projectFile; }
    
  
  public File getBuildDirectory() { return _buildDir; }
  
   
  public File getWorkingDirectory() { return _workDir; }
  
  
  public String[] getCollapsedPaths() { return _collapsedPaths.toArray(new String[_collapsedPaths.size()]); }
    
  
  public Iterable<AbsRelFile> getClassPaths() { return _classPathFiles; }
  
  
  public String getMainClass() { return _mainClass; }
  
  
  public File getMainClassContainingFile(){
    DocFile[] possibleContainers = getSourceFiles();
    
    String main = getMainClass();
    if(main.toLowerCase().endsWith(".java")){
      main = main.substring(0, main.length()-5);
      main = main.replace(File.separatorChar,'.');
    }
    
    for(int i = 0; i < possibleContainers.length; i++){
      String toMatch = possibleContainers[i].getAbsolutePath();
      toMatch = toMatch.substring(0, toMatch.lastIndexOf(".java"));
      toMatch = toMatch.replace(File.separatorChar,'.');
      
      if(toMatch.endsWith(main))
        return possibleContainers[i];
    }
    
    
    File toRet = new File(main.replace('.',File.separatorChar)+".java");
    
    return toRet;
  }
  
  
  public File getProjectRoot() { return _projectRoot; }
  
  
  public File getCreateJarFile() { return _createJarFile; }
  
  
  public int getCreateJarFlags() { return _createJarFlags; }
  
  
  public FileRegion[] getBookmarks() { return _bookmarks.toArray(new FileRegion[_bookmarks.size()]); }
  
  
  public DebugBreakpointData[] getBreakpoints() { return _breakpoints.toArray(new DebugBreakpointData[_breakpoints.size()]); }
  
  
  public DebugWatchData[] getWatches() { return _watches.toArray(new DebugWatchData[_watches.size()]); }
  
  public boolean getAutoRefreshStatus() { return _autoRefreshStatus; }
  
  
  
  public void addSourceFile(DocFile df) { _sourceFiles.add(df); }
  
  public void addSourceFile(DocumentInfoGetter getter) {
    if (!getter.isUntitled()) {
      try { addSourceFile(docFileFromGetter(getter)); }
      catch(IOException e) { throw new UnexpectedException(e); }
    }
  }
  
  public void addAuxiliaryFile(DocFile df) { _auxiliaryFiles.add(df); }
    
  public void addAuxiliaryFile(DocumentInfoGetter getter) {
    if (! getter.isUntitled()) {
      try { addAuxiliaryFile(docFileFromGetter(getter)); }
      catch(IOException e) { throw new UnexpectedException(e); }
    }
  }
  
  public void addExcludedFile(DocFile df) { _excludedFiles.add(df); }
  public void addExcludedFile(File f) { _excludedFiles.add(new DocFile(f)); }
    
  public void addExcludedFile(DocumentInfoGetter getter) {
    if (! getter.isUntitled()) {
      try { addExcludedFile(docFileFromGetter(getter)); }
      catch(IOException e) { throw new UnexpectedException(e); }
    }
  }
  
  public void addClassPathFile(AbsRelFile cp) {
    if (cp != null) _classPathFiles.add(cp);
  }
  public void addCollapsedPath(String cp) { if (cp != null) _collapsedPaths.add(cp); }
  public void setBuildDirectory(File dir) { 



    _buildDir = dir; 

  }
  public void setWorkingDirectory(File dir) { _workDir = FileOps.validate(dir); }
  public void setMainClass(String main) { _mainClass = main;  }
  public void setSourceFiles(List<DocFile> sf) { _sourceFiles = new LinkedList<DocFile>(sf); }
  public void setClassPaths(Iterable<? extends AbsRelFile> cpf) {
    _classPathFiles = new ArrayList<AbsRelFile>();
    for (AbsRelFile f : cpf) { _classPathFiles.add(f); }
  }
  public void setCollapsedPaths(List<String> cp) { _collapsedPaths = new ArrayList<String>(cp); }
  public void setAuxiliaryFiles(List<DocFile> af) { _auxiliaryFiles = new LinkedList<DocFile>(af); }
  public void setExcludedFiles(List<DocFile> ef) { _excludedFiles = new ArrayList<DocFile>(ef); }
  
  
  public void setProjectRoot(File root) { 
    _projectRoot = root; 
    assert root.getParentFile() != null;
  }
  
  public void setCreateJarFile(File createJarFile) { _createJarFile = createJarFile; }
  public void setCreateJarFlags(int createJarFlags) { _createJarFlags = createJarFlags; }
  
  public void setBookmarks(List<? extends FileRegion> bms) { _bookmarks = new ArrayList<FileRegion>(bms); }
  public void setBreakpoints(List<? extends DebugBreakpointData> bps) { _breakpoints = new ArrayList<DebugBreakpointData>(bps); }
  public void setWatches(List<? extends DebugWatchData> ws) { _watches = new ArrayList<DebugWatchData>(ws); }
  
  public void setAutoRefreshStatus(boolean status) { _autoRefreshStatus = status;}
  
  
  public void write() throws IOException {
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(_projectFile);
      write(fos);
    }
    finally { if (fos != null) fos.close(); }
  }
  
  public void write(OutputStream os) throws IOException {    
    XMLConfig xc = new XMLConfig();
    xc.set("drjava.version", edu.rice.cs.drjava.Version.getVersionString());
    String path = FileOps.stringMakeRelativeTo(_projectRoot, _projectFile);
    path = replace(path, File.separator, "/");
    xc.set("drjava/project.root", path);
    path = FileOps.stringMakeRelativeTo(_workDir, _projectFile);
    path = replace(path, File.separator, "/");
    xc.set("drjava/project.work", path);
    
    if(_manifest != null) {
      String cleanManifest = TextUtil.xmlEscape(_manifest);
      xc.set("drjava/project.manifest", cleanManifest);
      
      LOG.log("dirty manifest: "+_manifest);
      LOG.log("clean manifest: "+cleanManifest);
    }
    
    if (_buildDir != null && _buildDir.getPath() != "") {
      path = FileOps.stringMakeRelativeTo(_buildDir, _projectFile);
      path = replace(path, File.separator, "/");
      xc.set("drjava/project.build", path);
    }
    if (_mainClass != null && _mainClass != "") {
      
      xc.set("drjava/project.main", _mainClass);      
    }
    xc.set("drjava/project.autorefresh", String.valueOf(_autoRefreshStatus));
    
    if (_createJarFile != null) {
      path = FileOps.stringMakeRelativeTo(_createJarFile, _createJarFile);
      path = replace(path, File.separator, "/");
      xc.set("drjava/project/createjar.file", path);
    }
    if (_createJarFlags != 0) {
      xc.set("drjava/project/createjar.flags", String.valueOf(_createJarFlags));
    }
    
    xc.createNode("drjava/project/source");
    DocFile active = null;
    if (!_sourceFiles.isEmpty()) {
      for(DocFile df: _sourceFiles) {
        if(df.isActive()) {
          active = df;
          break; 
        }
      }
      
      if (active != null) { _sourceFiles.remove(active); _sourceFiles.add(0,active); }
      for(DocFile df: _sourceFiles) {
        path = FileOps.stringMakeRelativeTo(df, _projectRoot);
        path = replace(path, File.separator, "/");
        Pair<Integer,Integer> pSel = df.getSelection();
        Pair<Integer,Integer> pScr = df.getScroll();
        String s = MOD_DATE_FORMAT.format(new Date(df.lastModified()));

        Node f = xc.createNode("drjava/project/source/file", null, false);      
        xc.set(".name", path, f, true);
        xc.set(".timestamp", s, f, true);
        String pkg = df.getPackage();
        xc.set(".package", (pkg != null)?pkg:"", f, true);
        xc.set("select.from",   String.valueOf((pSel != null)?pSel.first():0),  f, true);
        xc.set("select.to",     String.valueOf((pSel != null)?pSel.second():0), f, true);
        xc.set("scroll.column", String.valueOf((pScr != null)?pScr.first():0),  f, true);
        xc.set("scroll.row",    String.valueOf((pScr != null)?pScr.second():0), f, true);
        if (df==active) xc.set(".active", "true", f, true);
      }
    }
    xc.createNode("drjava/project/included");
    if (!_auxiliaryFiles.isEmpty()) {
      if (active == null) {
        for(DocFile df: _auxiliaryFiles) {
          if(df.isActive()) {
            active = df;
            break; 
          }
        }
        
        if (active != null) { _auxiliaryFiles.remove(active); _auxiliaryFiles.add(0,active); }
      }
      for(DocFile df: _auxiliaryFiles) {
        path = df.getAbsolutePath();
        path = replace(path, File.separator, "/");
        Pair<Integer,Integer> pSel = df.getSelection();
        Pair<Integer,Integer> pScr = df.getScroll();
        String s = MOD_DATE_FORMAT.format(new Date(df.lastModified()));

        Node f = xc.createNode("drjava/project/included/file", null, false);      
        xc.set(".name", path, f, true);
        xc.set(".timestamp", s, f, true);
        String pkg = df.getPackage();
        xc.set(".package", (pkg != null)?pkg:"", f, true);
        xc.set("select.from",   String.valueOf((pSel != null)?pSel.first():0),  f, true);
        xc.set("select.to",     String.valueOf((pSel != null)?pSel.second():0), f, true);
        xc.set("scroll.column", String.valueOf((pScr != null)?pScr.first():0),  f, true);
        xc.set("scroll.row",    String.valueOf((pScr != null)?pScr.second():0), f, true);
        if (df==active) { xc.set(".active", "true", f, true);
        }
      }
    }
    
    xc.createNode("drjava/project/excluded");
    if (!_excludedFiles.isEmpty()) {
      if (active == null) {
        for(DocFile df: _excludedFiles) {
          if(df.isActive()) {
            active = df;
            break; 
          }
        }
        
        if (active != null) { _excludedFiles.remove(active); _excludedFiles.add(0,active); }      
      }
      for(DocFile df: _excludedFiles) {
        path = df.getAbsolutePath();
        path = replace(path, File.separator, "/");
        Pair<Integer,Integer> pSel = df.getSelection();
        Pair<Integer,Integer> pScr = df.getScroll();
        String s = MOD_DATE_FORMAT.format(new Date(df.lastModified()));

        Node f = xc.createNode("drjava/project/excluded/file", null, false);      
        xc.set(".name", path, f, true);
        xc.set(".timestamp", s, f, true);
        String pkg = df.getPackage();
        xc.set(".package", (pkg != null)?pkg:"", f, true);
        xc.set("select.from",   String.valueOf((pSel != null)?pSel.first():0),  f, true);
        xc.set("select.to",     String.valueOf((pSel != null)?pSel.second():0), f, true);
        xc.set("scroll.column", String.valueOf((pScr != null)?pScr.first():0),  f, true);
        xc.set("scroll.row",    String.valueOf((pScr != null)?pScr.second():0), f, true);
        if (df==active) { xc.set(".active", "true", f, true);
        }
      }
    }
    
    xc.createNode("drjava/project/collapsed");
    if (!_collapsedPaths.isEmpty()) {
      for(String s: _collapsedPaths) {
        Node f = xc.createNode("drjava/project/collapsed/path", null, false);
        xc.set(".name", s, f, true);
      }
    }
    xc.createNode("drjava/project/classpath");
    if (!_classPathFiles.isEmpty()) {
      for(AbsRelFile cp: _classPathFiles) {
        path = cp.keepAbsolute()?cp.getAbsolutePath():FileOps.stringMakeRelativeTo(cp, _projectRoot);
        path = replace(path, File.separator, "/");
        Node f = xc.createNode("drjava/project/classpath/file", null, false);
        xc.set(".name", path, f, true);
        xc.set(".absolute", String.valueOf(cp.keepAbsolute()), f, true);
      }
    }
    xc.createNode("drjava/project/breakpoints");
    if (!_breakpoints.isEmpty()) {
      for(DebugBreakpointData bp: _breakpoints) {
        Node f = xc.createNode("drjava/project/breakpoints/breakpoint", null, false);
        path = FileOps.stringMakeRelativeTo(bp.getFile(), _projectRoot);
        path = replace(path, File.separator, "/");
        xc.set(".file", path, f, true);
        xc.set(".line", String.valueOf(bp.getLineNumber()), f, true);
        xc.set(".enabled", String.valueOf(bp.isEnabled()), f, true);
      }
    }
    xc.createNode("drjava/project/watches");
    if (!_watches.isEmpty()) {
      for(DebugWatchData w: _watches) {
        Node f = xc.createNode("drjava/project/watches/watch", null, false);
        xc.set(".name", w.getName(), f, true);
      }
    }
    xc.createNode("drjava/project/bookmarks");
    if (!_bookmarks.isEmpty()) {
      for (FileRegion bm: _bookmarks) {
        Node n = xc.createNode("drjava/project/bookmarks/bookmark", null, false);
        File file = bm.getFile();
        path = FileOps.stringMakeRelativeTo(file, _projectRoot);
        path = replace(path, File.separator, "/");
        xc.set(".file", path, n, true);
        xc.set(".from", String.valueOf(bm.getStartOffset()), n, true);
        xc.set(".to", String.valueOf(bm.getEndOffset()), n, true);
      }
    }
    xc.save(os);
  }
  
  
  public void writeOld() throws IOException {
    FileWriter fw = null;
    try {
      fw = new FileWriter(_projectFile);
      writeOld(fw);
    }
    finally { if (fw != null) fw.close(); }
  }
  
  public String toString() {
    try {
      StringWriter w = new StringWriter();
      writeOld(w);
      return w.toString();
    }
    catch(IOException e) { return e.toString(); }
  }
  
  public void writeOld(Writer fw) throws IOException { 
    assert (_projectRoot != null);
    
    fw.write(";; DrJava project file, written by build " + Version.getVersionString());
    fw.write("\n;; files in the source tree are relative to: " + _projectRoot.getCanonicalPath());
    fw.write("\n;; other files with relative paths are rooted at (the parent of) this project file");
    
    
    

    fw.write("\n(proj-root-and-base");

    fw.write("\n" + encodeFileRelative(_projectRoot, "  ", _projectFile));
    fw.write(")");

    
    if(_manifest != null){
      fw.write("\n(proj-manifest");
      fw.write("\n"+_manifest);
      fw.write(")");
    }
    
    
    
    if (!_sourceFiles.isEmpty()) {
      fw.write("\n(source-files");
      DocFile active = null;
      for(DocFile df: _sourceFiles) {
        if(df.isActive()) {
          active = df;
          fw.write("\n" + encodeDocFileRelative(df, "  "));
          break; 
        }
      }
      for(DocFile df: _sourceFiles) { 
        if(df != active)
          fw.write("\n" + encodeDocFileRelative(df, "  "));
      }
      fw.write(")"); 
    }
    else fw.write("\n;; no source files");
    
    
    if (!_auxiliaryFiles.isEmpty()) {
      fw.write("\n(auxiliary");
      for(DocFile df: _auxiliaryFiles) { fw.write("\n" + encodeDocFileAbsolute(df, "  ")); }
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
      for (AbsRelFile f: _classPathFiles) {
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
    
     
    if (_workDir.getPath() != "") {
      fw.write("\n(work-dir");
      fw.write("\n" + encodeFileRelative(_workDir, "  ", _projectFile));
      fw.write(")");
    }
    else fw.write("\n;; no working directory");
    
    
    if (_mainClass != null) {
      fw.write("\n;; rooted at the (parent of the) project file");
      fw.write("\n(main-class");
      fw.write("\n" + " "+ getMainClass() );
      fw.write(")");
    }
    else fw.write("\n;; no main class");
    
    
    if (_createJarFile != null) {
      fw.write("\n(create-jar-file");
      fw.write("\n" + encodeFileRelative(_createJarFile, "  ", _projectFile));
      fw.write(")");
    }
    else fw.write("\n;; no create jar file");
    
    
    if (_createJarFlags != 0) {
      fw.write("\n(create-jar-flags " + _createJarFlags + ")");
    }
    else fw.write("\n;; no create jar flags");

    
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

    
    if (!_bookmarks.isEmpty()) {
      fw.write("\n(bookmarks");
      for(FileRegion bm: _bookmarks) { fw.write("\n" + encodeBookmarkRelative(bm, "  ")); }
      fw.write(")"); 
    }
    else fw.write("\n;; no bookmarks");

    fw.close();
  }
  

  
  
  
  private DocFile docFileFromGetter(DocumentInfoGetter g) throws IOException {    
      return new DocFile(g.getFile().getCanonicalPath(), g.getSelection(), g.getScroll(), g.isActive(), g.getPackage());
  }
  
  
  
  private String encodeFileRelative(File f, String prefix, File base) throws IOException {
    String path = FileOps.stringMakeRelativeTo(f, base);
    path = replace(path, File.separator, "/");
    return prefix + "(file (name " + convertToLiteral(path) + "))";
  }

  




    
  
  private String encodeFileAbsolute(File f, String prefix) throws IOException {
    String path = f.getCanonicalPath();
    path = replace(path,File.separator, "/");
    return prefix + "(file (name " + convertToLiteral(path) + "))";
  }
  
  
  private String encodeDocFile(DocFile df, String prefix, boolean relative) throws IOException {
    String ret = "";
    String path;
    if (relative) path = FileOps.stringMakeRelativeTo(df, _projectRoot);
    else path = IOUtil.attemptCanonicalFile(df).getPath();

    path = replace(path, File.separator, "/");
    ret += prefix + "(file (name " + convertToLiteral(path) + ")";
    
    Pair<Integer,Integer> p1 = df.getSelection();
    Pair<Integer,Integer> p2 = df.getScroll();
    
    long modDate = df.lastModified();
    
    if (p1 != null || p2 != null )  ret += "\n" + prefix + "      ";

    
    if (p1 != null) ret += "(select " + p1.first() + " " + p1.second() + ")";

    if (p2 != null) ret += "(scroll " + p2.first() + " " + p2.second() + ")";

    if (modDate > 0) {
      String s = MOD_DATE_FORMAT.format(new Date(modDate));
      ret += "(mod-date " + convertToLiteral(s) + ")";
    }
    
    
    
    
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
    String path = FileOps.stringMakeRelativeTo(bp.getFile(), _projectRoot);
    
    path = replace(path,File.separator,"/");
    ret += prefix + "(breakpoint (name " + convertToLiteral(path) + ")";
    
    int lineNumber = bp.getLineNumber();
    ret += "\n" + prefix + "      ";
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

  
  private String encodeBookmarkRelative(FileRegion bm, String prefix) throws IOException {
    String ret = "";
    String path = FileOps.stringMakeRelativeTo(bm.getFile(), _projectRoot);
    
    path = replace(path,File.separator,"/");
    ret += prefix + "(bookmark (name " + convertToLiteral(path) + ")";
    
    int startOffset = bm.getStartOffset();
    int endOffset = bm.getEndOffset();
    ret += "\n" + prefix + "      ";
    ret += "(start " + startOffset + ")";
    ret += "(end " + endOffset + ")";
    ret += ")"; 
    
    return ret;
  }
  
  public String getDrJavaVersion(){
    return _version;
  }
  
  public void setDrJavaVersion(String version){
    _version = version;
  }
  
  
  public String getCustomManifest(){
    return _manifest;
  }
  
  
  public void setCustomManifest(String manifest){
    _manifest = manifest;
  }
}
