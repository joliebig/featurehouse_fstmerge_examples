

package edu.rice.cs.drjava.project;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.w3c.dom.Node;

import edu.rice.cs.util.AbsRelFile;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.drjava.model.DummyDocumentRegion;
import edu.rice.cs.drjava.model.FileRegion;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.util.XMLConfig;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.util.StringOps;

import static edu.rice.cs.util.XMLConfig.XMLConfigException;

import edu.rice.cs.plt.text.TextUtil;


public class XMLProjectFileParser extends ProjectFileParserFacade {
  
  public static final XMLProjectFileParser ONLY = new XMLProjectFileParser();
  private XMLProjectFileParser() { _xmlProjectFile = true; }
  
  protected String _parent;
  protected String _srcFileBase;
  protected XMLConfig _xc;
  
  static edu.rice.cs.util.Log LOG = new edu.rice.cs.util.Log("xmlparser.log", false);
    
  
  public ProjectFileIR parse(File projFile) throws IOException, FileNotFoundException, MalformedProjectFileException {  
    _projectFile = projFile;
    _parent = projFile.getParent();
    _srcFileBase = _parent;
    ProjectFileIR pfir = new ProjectProfile(projFile);
    
    try {
      XMLConfig xcParent = new XMLConfig(projFile);

      
      String version = xcParent.get("drjava.version", "unknown");
      LOG.log("version = '"+version+"'");
      
      pfir.setDrJavaVersion(version);
      
      
      _xc = new XMLConfig(xcParent, xcParent.getNodes("drjava/project").get(0));
      LOG.log(_xc.toString());
      String s;
      
      
      try {
        s = _xc.get(".root");
        LOG.log("root = '"+s+"'");
        File root = new File(_parent, s);
        LOG.log("_parent = " + _parent);
        pfir.setProjectRoot(root);
        _srcFileBase = root.getCanonicalPath();
        LOG.log("_srcFileBase from reading the prject root = " + _srcFileBase);
      }
      catch(XMLConfigException e) { throw new MalformedProjectFileException("XML Parse Error: "+e.getMessage()+"\n"+StringOps.getStackTrace(e)); }
      
      
      try {
        s = _xc.get("createjar.file");
        LOG.log("createjar.file = '"+s+"'");
        File jarFile = new File(_parent, s);
        pfir.setCreateJarFile(jarFile);
      }
      catch(XMLConfigException e) {  }
      try {
        s = _xc.get("createjar.flags");
        LOG.log("createjar.flags = '"+s+"'");
        int flags = Integer.valueOf(s);
        pfir.setCreateJarFlags(flags);
      }
      catch(XMLConfigException e) {  }
      
      try{
        s = _xc.get(".manifest");
        LOG.log("manifest = '"+s+"'");
        pfir.setCustomManifest(TextUtil.xmlUnescape(s));
      }catch(XMLConfigException e) {  }
      
      
      try {
        s = _xc.get(".build");
        LOG.log("build = '"+s+"'");
        File buildDir = (!new File(s).isAbsolute())?new File(_parent, s):new File(s);
        pfir.setBuildDirectory(buildDir);
      }
      catch(XMLConfigException e) {  }

      
      try {
        s = _xc.get(".work");
        LOG.log("work = '"+s+"'");
        File workDir = (!new File(s).isAbsolute())?new File(_parent, s):new File(s);
        pfir.setWorkingDirectory(workDir);
      }
      catch(XMLConfigException e) { throw new MalformedProjectFileException("XML Parse Error: "+e.getMessage()+"\n"+StringOps.getStackTrace(e)); }

      
      try {
        s = _xc.get(".main");
        LOG.log("main = '"+s+"'");
        
        pfir.setMainClass(s);
      }
      catch(XMLConfigException e) {  }
      
      try {
        s = _xc.get(".autorefresh");
        boolean b = Boolean.valueOf(s);
        pfir.setAutoRefreshStatus(b);
      } 
      catch(XMLConfigException e) { }
      
      try { 
        
        
        pfir.setSourceFiles(readSourceFiles("source", _srcFileBase));
        pfir.setAuxiliaryFiles(readSourceFiles("included", ""));      
        
        
        pfir.setExcludedFiles(readSourceFiles("excluded", ""));
        
      
        
        pfir.setCollapsedPaths(readCollapsed());
      
        
        pfir.setClassPaths(readFiles("classpath", _srcFileBase));
      
        
        pfir.setBreakpoints(readBreakpoints());
      
        
        pfir.setWatches(readWatches());

        
        pfir.setBookmarks(readBookmarks());
      }
      catch(XMLConfigException e) { throw new MalformedProjectFileException("XML Parse Error: "+e.getMessage()+"\n"+StringOps.getStackTrace(e)); }
    }
    catch(XMLConfigException e) {
      throw new MalformedProjectFileException("Malformed XML project file."+e.getMessage()+"\n"+StringOps.getStackTrace(e));
    }
    catch(NumberFormatException e) {
      throw new MalformedProjectFileException("Malformed XML project file; a value that should have been an integer was not.\n"+StringOps.getStackTrace(e));
    }
    catch(IllegalArgumentException e) {
      throw new MalformedProjectFileException("Malformed XML project file; a value had the wrong type.\n"+StringOps.getStackTrace(e));
    }
    catch(IndexOutOfBoundsException e) {
      throw new MalformedProjectFileException("Malformed XML project file; a required value was missing.\n"+StringOps.getStackTrace(e));
    }    
    LOG.log(pfir.toString());
    return pfir;
  }
  
  protected List<DocFile> readSourceFiles(String path, String rootPath) throws MalformedProjectFileException {
    LOG.log("readSourceFiles(path='"+path+"', rootPath='"+rootPath+"')");
    List<DocFile> docFList = new ArrayList<DocFile>();
    List<Node> defs = _xc.getNodes(path+"/file");
    LOG.log("\tdefs.size() = "+defs.size());
    for(Node n: defs) { LOG.log("\t"+n.getNodeValue()); }

    for(Node n: defs) {
      LOG.log("\t"+n.toString());
      
      
      String name = _xc.get(".name",n);
      LOG.log("\t\tname = '"+name+"'");
      
      int selectFrom = _xc.getInt("select.from",n);
      int selectTo = _xc.getInt("select.to",n);
      LOG.log("\t\tselect = '"+selectFrom+" to "+selectTo+"'");
      
      int scrollCol = _xc.getInt("scroll.column",n);
      int scrollRow = _xc.getInt("scroll.row",n);
      LOG.log("\t\tscroll = '"+scrollCol+" , "+scrollRow+"'");
      
      String timestamp = _xc.get(".timestamp",n);
      LOG.log("\t\ttimestamp = '"+timestamp+"'");
      Date modDate;
      try {
        
        modDate = ProjectProfile.MOD_DATE_FORMAT.parse(timestamp); }
      catch (java.text.ParseException e1) {
        
        try {
          
          modDate = new SimpleDateFormat(ProjectProfile.MOD_DATE_FORMAT_STRING).parse(timestamp);
        }
        catch (java.text.ParseException e2) {
          
          throw new MalformedProjectFileException("Source file node contains badly formatted timestamp.");
        }
      }
      
      String pkg = _xc.get(".package",n);
      LOG.log("\t\tpackage = '"+pkg+"'");
      
      boolean active;
      try {
        active = _xc.getBool(".active",n);
        LOG.log("\t\tactive = '"+active+"'");
      }
      catch(XMLConfigException e) { active = false;  }
      
      
      Boolean absName = (new File(name)).isAbsolute();   
      
      DocFile docF = new DocFile(((rootPath.length()>0 && !absName)?new File(rootPath,name):new File(name)).getAbsoluteFile(),
                                 new Pair<Integer,Integer>(selectFrom,selectTo),
                                 new Pair<Integer,Integer>(scrollCol,scrollCol),
                                 active,
                                 pkg);
      docF.setSavedModDate(modDate.getTime());
      docFList.add(docF);
    }
    return docFList;
  }

  protected List<AbsRelFile> readFiles(String path) {
    return readFiles(path, "");
  }
  
  protected List<AbsRelFile> readFiles(String path, String rootPath) {
    List<AbsRelFile> fList = new ArrayList<AbsRelFile>();
    List<Node> defs = _xc.getNodes(path+"/file");
    for(Node n: defs) {
      
      String name = _xc.get(".name",n);
      boolean abs = _xc.getBool(".absolute",n,true); 

      
      abs |= (new File(name)).isAbsolute();   
      
      AbsRelFile f = new AbsRelFile(((rootPath.length()>0 && !abs)?
                                       new File(rootPath,name):
                                       new File(name)).getAbsoluteFile(),abs);
      fList.add(f);
    }
    return fList;
  }
  
  protected List<String> readCollapsed() {
    List<String> pList = new ArrayList<String>();
    List<Node> defs = _xc.getNodes("collapsed/path");
    for(Node n: defs) {
      
      pList.add(_xc.get(".name", n));
    }
    return pList;
  }
  
  protected List<DebugBreakpointData> readBreakpoints() {
    List<DebugBreakpointData> bpList = new ArrayList<DebugBreakpointData>();
    List<Node> defs = _xc.getNodes("breakpoints/breakpoint");
    for(Node n: defs) {
      
      String name = _xc.get(".file", n);
      final int lnr = _xc.getInt(".line", n);
      final boolean enabled = _xc.getBool(".enabled", n);
      DebugBreakpointData dbd;
      if ((_srcFileBase==null) || (new File(name).isAbsolute())) {
        final File f = new File(name);
        dbd = new DebugBreakpointData() {
          public File getFile() { return f; }
          public int getLineNumber() { return lnr; }
          public boolean isEnabled() { return enabled; }
        };
      }
      else {
        final File f = new File(_srcFileBase, name);
        dbd = new DebugBreakpointData() {
          public File getFile() { return f; }
          public int getLineNumber() { return lnr; }
          public boolean isEnabled() { return enabled; }
        };
      }
      bpList.add(dbd);
    }
    return bpList;
  }

  protected List<DebugWatchData> readWatches() {
    List<DebugWatchData> wList = new ArrayList<DebugWatchData>();
    List<Node> defs = _xc.getNodes("watches/watch");
    for(Node n: defs) {
      
      wList.add(new DebugWatchData(_xc.get(".name", n)));
    }
    return wList;
  }
    
  protected List<FileRegion> readBookmarks() {
    List<FileRegion> rList = new ArrayList<FileRegion>();
    List<Node> defs = _xc.getNodes("bookmarks/bookmark");
    for(Node n: defs) {
      
      String name = _xc.get(".file", n);
      final int from = _xc.getInt(".from", n);
      final int to = _xc.getInt(".to", n);
      File f;
      if ((_srcFileBase==null) || (new File(name).isAbsolute())) { f = new File(name); }
      else { f = new File(_srcFileBase, name); }
      rList.add(new DummyDocumentRegion(f, from, to));
    }
    return rList;
  }
}
