

package edu.rice.cs.drjava.project;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.sexp.*;
import edu.rice.cs.drjava.model.FileRegion;
import edu.rice.cs.drjava.model.DummyDocumentRegion;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;


public class ProjectFileParser extends ProjectFileParserFacade {
  
  public static final ProjectFileParser ONLY = new ProjectFileParser();
  
  private String _parent;
  private String _srcFileBase;
  
  BreakpointListVisitor breakpointListVisitor = new BreakpointListVisitor();
  BookmarkListVisitor bookmarkListVisitor = new BookmarkListVisitor();
  
  private ProjectFileParser() { _xmlProjectFile = false; }
  
  
  public ProjectFileIR parse(File projFile) throws IOException, FileNotFoundException, MalformedProjectFileException {
    
    _projectFile = projFile;
    _parent = projFile.getParent();  
    _srcFileBase = _parent; 

    
    List<SEList> forest = null;
    try { forest = SExpParser.parse(projFile); }
    catch(SExpParseException e) { throw new MalformedProjectFileException("Parse Error: " + e.getMessage()); }
    
    ProjectFileIR pfir = new ProjectProfile(projFile);
    
    
    pfir.setDrJavaVersion("unknown");

    try { for (SEList exp : forest) evaluateExpression(exp, pfir, new DocFileListVisitor(_parent)); }
    catch(PrivateProjectException e) { throw new MalformedProjectFileException("Parse Error: " + e.getMessage()); }
    

    
    return pfir;
  }
  
  
  private void evaluateExpression(SEList e, ProjectFileIR pfir, DocFileListVisitor flv) throws IOException {
    if (e == Empty.ONLY) return;
    Cons exp = (Cons) e; 
      
    String name = exp.accept(NameVisitor.ONLY);
    if ((name.compareToIgnoreCase("source") == 0) || (name.compareToIgnoreCase("source-files") == 0)) {
      List<DocFile> dfList = exp.getRest().accept(new DocFileListVisitor(_srcFileBase));
      pfir.setSourceFiles(dfList);
    }
    else if (name.compareToIgnoreCase("proj-root") == 0) {  
      List<DocFile> fList = exp.getRest().accept(flv);
      if (fList.size() > 1) throw new PrivateProjectException("Cannot have multiple source roots");
      else if (fList.size() == 0) throw new PrivateProjectException("Cannot have no source roots");
      pfir.setProjectRoot(fList.get(0));
    }
    else if (name.compareToIgnoreCase("proj-root-and-base") == 0) { 
      List<DocFile> fList = exp.getRest().accept(flv);
      if (fList.size() > 1) throw new PrivateProjectException("Cannot have multiple source roots");
      else if (fList.size() == 0) throw new PrivateProjectException("Cannot have no source roots");
      File root = fList.get(0);
      if (! root.exists()) throw new IOException("Project root " + root + " no longer exists");
      pfir.setProjectRoot(root);
      _srcFileBase = root.getCanonicalPath();
    }else if (name.compareToIgnoreCase("proj-manifest") == 0) {
      List<String> sList = exp.getRest().accept(PathListVisitor.ONLY);
      if(sList.size() > 1) throw new PrivateProjectException("Cannot have multiple manifests");
      if(sList.size() > 0)
        pfir.setCustomManifest(sList.get(0));
    }
    else if (name.compareToIgnoreCase("auxiliary") == 0) {
      List<DocFile> dfList = exp.getRest().accept(flv);
      pfir.setAuxiliaryFiles(dfList);
    }
    else if (name.compareToIgnoreCase("collapsed") == 0) {
      List<String> sList = exp.getRest().accept(PathListVisitor.ONLY);
      pfir.setCollapsedPaths(sList);
    }
    else if (name.compareToIgnoreCase("build-dir") == 0) {
      List<DocFile> fList = exp.getRest().accept(flv);

      if (fList.size() > 1) throw new PrivateProjectException("Cannot have multiple build directories");
      else if (fList.size() == 0) pfir.setBuildDirectory(FileOps.NULL_FILE);
      else pfir.setBuildDirectory(fList.get(0));
    }
    else if (name.compareToIgnoreCase("work-dir") == 0) {
      List<DocFile> fList = exp.getRest().accept(flv);
      if (fList.size() > 1) throw new PrivateProjectException("Cannot have multiple working directories");

      else pfir.setWorkingDirectory(fList.get(0));
    }
    else if (name.compareToIgnoreCase("classpaths") == 0) {
      List<DocFile> fList = exp.getRest().accept(flv);
      pfir.setClassPaths(fList);
    }
    else if (name.compareToIgnoreCase("main-class") == 0) {
      try{
        List<DocFile> fList = exp.getRest().accept(flv);
        if(fList.size() == 1){
          String main = fList.get(0).getAbsolutePath();
          
          pfir.setMainClass(main);
          
          return;
        }
      }catch(Exception exc){}
      
      String mainClass = exp.getRest().accept(NameVisitor.ONLY);
      pfir.setMainClass(mainClass);
    }
    else if (name.compareToIgnoreCase("create-jar-file") == 0) {
      List<DocFile> fList = exp.getRest().accept(flv);
      if (fList.size() > 1) throw new PrivateProjectException("Cannot have more than one \"create jar\" file");
      else if (fList.size() == 0) pfir.setCreateJarFile(null);
      else pfir.setCreateJarFile(fList.get(0));
    }
    else if (name.compareToIgnoreCase("create-jar-flags") == 0) {
      Integer i = exp.getRest().accept(NumberVisitor.ONLY);
      pfir.setCreateJarFlags(i);
    }
    else if (name.compareToIgnoreCase("breakpoints") == 0) {
       List<DebugBreakpointData> bpList = exp.getRest().accept(breakpointListVisitor);
       pfir.setBreakpoints(bpList);
    }
    else if (name.compareToIgnoreCase("watches") == 0) {
      List<DebugWatchData> sList = exp.getRest().accept(WatchListVisitor.ONLY);
      pfir.setWatches(sList);
    }
    else if (name.compareToIgnoreCase("bookmarks") == 0) {
       List<FileRegion> bmList = exp.getRest().accept(bookmarkListVisitor);
       pfir.setBookmarks(bmList);
    }
  } 
  
  
  DocFile parseFile(SExp s, String pathRoot) {
    String name = s.accept(NameVisitor.ONLY);
    if (name.compareToIgnoreCase("file") != 0)
      throw new PrivateProjectException("Expected a file tag, found: " + name);
    if (! (s instanceof Cons))
      throw new PrivateProjectException("Expected a labeled node, found a label: " + name);
    SEList c = ((Cons)s).getRest(); 
    
    DocFilePropertyVisitor v = new DocFilePropertyVisitor(pathRoot);
    return c.accept(v);
  }
  
  private String parseFileName(SExp s) {
    if (s instanceof Cons) {
      SEList l = ((Cons)s).getRest();
      if (l == Empty.ONLY)
        throw new PrivateProjectException("expected filename, but nothing found");
      else {
        String name = l.accept(NameVisitor.ONLY);
        name = edu.rice.cs.util.StringOps.replace(name,"\\","/");
        return name;
      }
    }
    else throw new PrivateProjectException("expected name tag, found string");
  }
  
  private int parseInt(SExp s) {
    if (s instanceof Cons) {
      SEList l = ((Cons)s).getRest();
      if (l == Empty.ONLY)
        throw new PrivateProjectException("expected integer, but nothing found");
      else {
        int i = l.accept(NumberVisitor.ONLY);
        return i;
      }
    }
    else throw new PrivateProjectException("expected name tag, found string");
  }
  
  private Pair<Integer,Integer> parseIntPair(SExp s) {
    
    if (!(s instanceof Cons)) {
      throw new PrivateProjectException("expected name tag, found string");
    }
    
    
    final List<Integer> intList = new ArrayList<Integer>();
    SEList l = ((Cons)s).getRest();
    List<Integer> li = l.accept(new SExpVisitor<List<Integer>>() {
      public List<Integer> forEmpty(Empty e) { return intList; }
  
      public List<Integer> forCons(Cons c) {
        c.getFirst().accept(this);
        return c.getRest().accept(this);
      }
  
      public List<Integer> forBoolAtom(BoolAtom b) {
        throw new PrivateProjectException("unexpected boolean found, int expected");
      }
      
      public List<Integer> forNumberAtom(NumberAtom n) {
        intList.add(Integer.valueOf(n.intValue()));
        return intList;
      }
      
      public List<Integer> forTextAtom(TextAtom t) {
        throw new PrivateProjectException("unexpected string found where number expected: " + t.getText());
      }
      
    });
    
    if (li.size() == 2) return new Pair<Integer,Integer>(li.get(0), li.get(1));
    else throw new PrivateProjectException("expected a list of 2 ints for select, found list of size " + li.size());
  }

    
    private String parseStringNode(SExp n) {
      if (n instanceof Cons) 
        return ((Cons)n).getRest().accept(NameVisitor.ONLY);
      else throw new PrivateProjectException("List expected, but found text instead");  
    }
  
  
  
  
  private static class DocFileListVisitor implements SEListVisitor<List<DocFile>> {
    
    private String _base;
    DocFileListVisitor(String base) { _base = base; }
    public List<DocFile> forEmpty(Empty e) { return new ArrayList<DocFile>(); }
    public List<DocFile> forCons(Cons c) {
      List<DocFile> list = c.getRest().accept(this);
      DocFile tmp = ProjectFileParser.ONLY.parseFile(c.getFirst(), _base);
      list.add(0, tmp); 
      return list;
    }
  };
  
  
  private static class DocFilePropertyVisitor implements SEListVisitor<DocFile> {
    private String fname = "";
    private Pair<Integer,Integer> select = new Pair<Integer,Integer>(Integer.valueOf(0), Integer.valueOf(0));
    private Pair<Integer,Integer> scroll = new Pair<Integer,Integer>(Integer.valueOf(0), Integer.valueOf(0));
    private boolean active = false;
    private String pack = "";
    private Date modDate = null;
    
    private String pathRoot;
    public DocFilePropertyVisitor(String pr) { pathRoot = pr; }
    
    public DocFile forCons(Cons c) {
      String name = c.getFirst().accept(NameVisitor.ONLY); 
      if (name.compareToIgnoreCase("name") == 0) { fname = ProjectFileParser.ONLY.parseFileName(c.getFirst()); }
      else if (name.compareToIgnoreCase("select") == 0) { select = ProjectFileParser.ONLY.parseIntPair(c.getFirst()); }
      else if (name.compareToIgnoreCase("scroll") == 0) { scroll = ProjectFileParser.ONLY.parseIntPair(c.getFirst()); }
      else if (name.compareToIgnoreCase("active") == 0) { active = true; }
      else if (name.compareToIgnoreCase("package") == 0) { 
        pack = ProjectFileParser.ONLY.parseStringNode(c.getFirst()); 
      }
      else if (name.compareToIgnoreCase("mod-date") == 0) {
        String tmp = ProjectFileParser.ONLY.parseStringNode(c.getFirst());
        try {
          
          modDate = ProjectProfile.MOD_DATE_FORMAT.parse(tmp); }
        catch (java.text.ParseException e1) {
          
          try {
            
            modDate = new SimpleDateFormat(ProjectProfile.MOD_DATE_FORMAT_STRING).parse(tmp);
          } catch (java.text.ParseException e2) {
            
            throw new PrivateProjectException("Bad mod-date: " + e2.getMessage());
          }
        }
      }
        
      return c.getRest().accept(this);
    }
    
    public DocFile forEmpty(Empty c) {
      if (pathRoot == null || new File(fname).isAbsolute()) {
        return new DocFile(fname, select, scroll, active, pack);
      }
      else {
        DocFile f = new DocFile(pathRoot, fname, select, scroll, active, pack);
        if (modDate != null) f.setSavedModDate(modDate.getTime());
        return f;
      }
    }
  }
  
  
  private static class PathListVisitor implements SEListVisitor<List<String>> {
    public static final PathListVisitor ONLY = new PathListVisitor();
    private PathListVisitor() { }
    
    public List<String> forEmpty(Empty e) { return new ArrayList<String>(); }
    public List<String> forCons(Cons c) {
      List<String> list = c.getRest().accept(this);
      SExp first = c.getFirst();
      String name = first.accept(NameVisitor.ONLY); 
      if (name.compareToIgnoreCase("path") == 0) {
        String tmp = ProjectFileParser.ONLY.parseStringNode(c.getFirst());
        list.add(0,tmp); 
      }
      return list;
    }
  };
  
  
  private static class NameVisitor implements SExpVisitor<String> {
    public static final NameVisitor ONLY = new NameVisitor();
    private NameVisitor() { }
    
    public String forEmpty(Empty e) {
      throw new PrivateProjectException("Found an empty node, expected a labeled node");
    }
    public String forCons(Cons c) { return c.getFirst().accept(this); }
    public String forBoolAtom(BoolAtom b) {
      throw new PrivateProjectException("Found a boolean, expected a label");
    }
    public String forNumberAtom(NumberAtom n) {
      throw new PrivateProjectException("Found a number, expected a label");
    }
    public String forTextAtom(TextAtom t) { return t.getText(); }
  };
  
  
  private static class NumberVisitor implements SExpVisitor<Integer> {
    public static final NumberVisitor ONLY = new NumberVisitor();
    private NumberVisitor() { }
    
    public Integer forEmpty(Empty e) {
      throw new PrivateProjectException("Found an empty node, expected an integer");
    }
    public Integer forCons(Cons c) { return c.getFirst().accept(this); }
    public Integer forBoolAtom(BoolAtom b) {
      throw new PrivateProjectException("Found a boolean, expected an integer");
    }
    public Integer forNumberAtom(NumberAtom n) { return n.intValue(); }
    public Integer forTextAtom(TextAtom t) {
      throw new PrivateProjectException("Found a string '"+t+"', expected an integer");
    }
  };

  
  private static class WatchListVisitor implements SEListVisitor<List<DebugWatchData>> {
    public static final WatchListVisitor ONLY = new WatchListVisitor();
    private WatchListVisitor() { }
    
    public List<DebugWatchData> forEmpty(Empty e) { return new ArrayList<DebugWatchData>(); }
    public List<DebugWatchData> forCons(Cons c) {
      List<DebugWatchData> list = c.getRest().accept(this);
      SExp first = c.getFirst();
      String name = first.accept(NameVisitor.ONLY); 
      if (name.compareToIgnoreCase("watch") == 0) {
        String tmp = ProjectFileParser.ONLY.parseStringNode(c.getFirst());
        list.add(0,new DebugWatchData(tmp)); 
      }
      return list;
    }
  };
  
  
  
  
  private class BreakpointListVisitor implements SEListVisitor<List<DebugBreakpointData>> {
    public List<DebugBreakpointData> forEmpty(Empty e) { return new ArrayList<DebugBreakpointData>(); }
    public List<DebugBreakpointData> forCons(Cons c) {
      List<DebugBreakpointData> list = c.getRest().accept(this);
      DebugBreakpointData tmp = ProjectFileParser.ONLY.parseBreakpoint(c.getFirst(), _srcFileBase);
      list.add(0, tmp); 
      return list;
    }
  };
    
  
  DebugBreakpointData parseBreakpoint(SExp s, String pathRoot) {
    String name = s.accept(NameVisitor.ONLY);
    if (name.compareToIgnoreCase("breakpoint") != 0)
      throw new PrivateProjectException("Expected a breakpoint tag, found: " + name);
    if (! (s instanceof Cons))
      throw new PrivateProjectException("Expected a labeled node, found a label: " + name);
    SEList c = ((Cons)s).getRest(); 
    
    BreakpointPropertyVisitor v = new BreakpointPropertyVisitor(pathRoot);
    return c.accept(v);
  }
  
  
  
  private static class BreakpointPropertyVisitor implements SEListVisitor<DebugBreakpointData> {
    private String fname = null;

    private Integer lineNumber = null;
    private boolean isEnabled = false;
    
    private String pathRoot;
    public BreakpointPropertyVisitor(String pr) { pathRoot = pr; }
    
    public DebugBreakpointData forCons(Cons c) {
      String name = c.getFirst().accept(NameVisitor.ONLY); 
      if (name.compareToIgnoreCase("name") == 0) { fname = ProjectFileParser.ONLY.parseFileName(c.getFirst()); }
      

      else if (name.compareToIgnoreCase("line") == 0) { lineNumber = ProjectFileParser.ONLY.parseInt(c.getFirst()); }
      else if (name.compareToIgnoreCase("enabled") == 0) { isEnabled = true; }
        
      return c.getRest().accept(this);
    }
    
    public DebugBreakpointData forEmpty(Empty c) {
      if ((fname == null) || (lineNumber == null)) {
        throw new PrivateProjectException("Breakpoint information incomplete, need name and line tags");
      }
      if (pathRoot == null || new File(fname).isAbsolute()) {
        final File f = new File(fname);
        return new DebugBreakpointData() {
          public File getFile() { return f; }
          public int getLineNumber() { return lineNumber; }
          public boolean isEnabled() { return isEnabled; }
        };
      }
      else {
        final File f = new File(pathRoot, fname);
        return new DebugBreakpointData() {
          public File getFile() { return f; }
          public int getLineNumber() { return lineNumber; }
          public boolean isEnabled() { return isEnabled; }
        };
      }
    }
  }
  
  
  
  
  private class BookmarkListVisitor implements SEListVisitor<List<FileRegion>> {
    public List<FileRegion> forEmpty(Empty e) { return new ArrayList<FileRegion>(); }
    public List<FileRegion> forCons(Cons c) {
      List<FileRegion> list = c.getRest().accept(this);
      FileRegion tmp = ProjectFileParser.ONLY.parseBookmark(c.getFirst(), _srcFileBase);
      list.add(0, tmp); 
      return list;
    }
  };
    
  
  FileRegion parseBookmark(SExp s, String pathRoot) {
    String name = s.accept(NameVisitor.ONLY);
    if (name.compareToIgnoreCase("bookmark") != 0)
      throw new PrivateProjectException("Expected a bookmark tag, found: " + name);
    if (! (s instanceof Cons))
      throw new PrivateProjectException("Expected a labeled node, found a label: " + name);
    SEList c = ((Cons)s).getRest(); 
    
    BookmarkPropertyVisitor v = new BookmarkPropertyVisitor(pathRoot);
    return c.accept(v);
  }
  
  
  
  private static class BookmarkPropertyVisitor implements SEListVisitor<FileRegion> {
    private String fname = null;
    private Integer startOffset = null;
    private Integer endOffset = null;
    
    private String pathRoot;
    public BookmarkPropertyVisitor(String pr) { pathRoot = pr; }
    
    public FileRegion forCons(Cons c) {
      String name = c.getFirst().accept(NameVisitor.ONLY); 
      if (name.compareToIgnoreCase("name") == 0) { fname = ProjectFileParser.ONLY.parseFileName(c.getFirst()); }
      else if (name.compareToIgnoreCase("start") == 0) { startOffset = ProjectFileParser.ONLY.parseInt(c.getFirst()); }
      else if (name.compareToIgnoreCase("end") == 0) { endOffset = ProjectFileParser.ONLY.parseInt(c.getFirst()); }
        
      return c.getRest().accept(this);
    }
    
    public FileRegion forEmpty(Empty c) {
      if ((fname == null) || (startOffset == null) || (endOffset == null)) {
        throw new PrivateProjectException("Bookmark information incomplete, need name, start offset and end offset");
      }
      File f;
      if (pathRoot == null || new File(fname).isAbsolute()) f = new File(fname);
      else f = new File(pathRoot, fname);
      return new DummyDocumentRegion(f, startOffset, endOffset);
    }
  }
  
  private static class PrivateProjectException extends RuntimeException {
    public PrivateProjectException(String message) { super(message); }
  }
}
