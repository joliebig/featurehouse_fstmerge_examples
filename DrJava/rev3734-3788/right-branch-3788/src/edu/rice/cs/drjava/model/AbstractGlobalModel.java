

package edu.rice.cs.drjava.model;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.ProgressMonitor;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaRoot;
import edu.rice.cs.drjava.config.FileOption;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.cache.DCacheAdapter;
import edu.rice.cs.drjava.model.cache.DDReconstructor;
import edu.rice.cs.drjava.model.cache.DocumentCache;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugException;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.definitions.CompoundUndoManager;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.definitions.DocumentUIListener;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.definitions.reducedmodel.HighlightStatus;
import edu.rice.cs.drjava.model.definitions.reducedmodel.IndentInfo;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.drjava.model.print.DrJavaBook;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.project.DocFile;
import edu.rice.cs.drjava.project.DocumentInfoGetter;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.drjava.project.ProjectFileIR;
import edu.rice.cs.drjava.project.ProjectFileParser;
import edu.rice.cs.drjava.project.ProjectProfile;
import edu.rice.cs.util.ClassPathVector;
import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.OrderedHashSet;
import edu.rice.cs.util.Pair;
import edu.rice.cs.util.SRunnable;
import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.docnavigation.AWTContainerNavigatorFactory;
import edu.rice.cs.util.docnavigation.IDocumentNavigator;
import edu.rice.cs.util.docnavigation.INavigationListener;
import edu.rice.cs.util.docnavigation.INavigatorItem;
import edu.rice.cs.util.docnavigation.INavigatorItemFilter;
import edu.rice.cs.util.docnavigation.JTreeSortNavigator;
import edu.rice.cs.util.docnavigation.NodeData;
import edu.rice.cs.util.docnavigation.NodeDataVisitor;
import edu.rice.cs.util.swing.AsyncCompletionArgs;
import edu.rice.cs.util.swing.AsyncTask;
import edu.rice.cs.util.swing.IAsyncProgress;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;


public class AbstractGlobalModel implements SingleDisplayModel, OptionConstants, DocumentIterator {
  
  
  protected DocumentCache _cache;  
  
  static final String DOCUMENT_OUT_OF_SYNC_MSG =
    "Current document is out of sync with the Interactions Pane and should be recompiled!\n";
  
  static final String CLASSPATH_OUT_OF_SYNC_MSG =
    "Interactions Pane is out of sync with the current classpath and should be reset!\n";
  
  
  
  
  protected LinkedList<File> _auxiliaryFiles = new LinkedList<File>();
  
  
  public void addAuxiliaryFile(OpenDefinitionsDocument doc) {
    if (! doc.inProject()) {
      File f;
      
      try { f = doc.getFile(); } 
      catch(FileMovedException fme) { f = fme.getFile(); }
      
      synchronized(_auxiliaryFiles) { _auxiliaryFiles.add(f); }
      setProjectChanged(true);
    }
  }
  
  
  public void removeAuxiliaryFile(OpenDefinitionsDocument doc) {
    File file;
    try                           { file = doc.getFile(); } 
    catch(FileMovedException fme) { file = fme.getFile(); }
    
    String path = "";
    try { path = file.getCanonicalPath(); }
    catch(IOException e) { throw new UnexpectedException(e); }
    
    synchronized(_auxiliaryFiles) {
      ListIterator<File> it = _auxiliaryFiles.listIterator();
      while (it.hasNext()) {
        try { 
          if (it.next().getCanonicalPath().equals(path)) {
            it.remove();
            setProjectChanged(true);
            break;
          }
        } 
        catch(IOException e) {  }
      }
    }
  }
  
  
  final GlobalEventNotifier _notifier = new GlobalEventNotifier();
  
  
  
  
  protected final DefinitionsEditorKit _editorKit = new DefinitionsEditorKit(_notifier);
  
  
  protected final OrderedHashSet<OpenDefinitionsDocument> _documentsRepos =
    new OrderedHashSet<OpenDefinitionsDocument>();
  
  
  
  
  protected final ConsoleDocument _consoleDoc;
  
  
  protected final InteractionsDJDocument _consoleDocAdapter;
  
  
  protected boolean _isClosingAllDocs;
  
  
  private final Object _systemWriterLock = new Object();
  
  
  public static final int WRITE_DELAY = 5;
  
  
  protected PageFormat _pageFormat = new PageFormat();
  
  
  private OpenDefinitionsDocument _activeDocument;
  
  
  private File _activeDirectory;
   
  
  protected IDocumentNavigator<OpenDefinitionsDocument> _documentNavigator = 
      new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().makeListNavigator(); 
  
  
  
  
  
  public AbstractGlobalModel() {
    _cache = new DocumentCache();
    
    _consoleDocAdapter = new InteractionsDJDocument();
    _consoleDoc = new ConsoleDocument(_consoleDocAdapter);
    
    _registerOptionListeners();
        
    setFileGroupingState(makeFlatFileGroupingState());
    _notifier.projectRunnableChanged();
    _init();
  }
  
  private void _init() {
    
    
    final NodeDataVisitor<OpenDefinitionsDocument, Boolean> _gainVisitor = new NodeDataVisitor<OpenDefinitionsDocument, Boolean>() {
      public Boolean itemCase(OpenDefinitionsDocument doc) {
        OpenDefinitionsDocument oldDoc = AbstractGlobalModel.this.getActiveDocument();
        _setActiveDoc(doc);  
        

        File oldDir = _activeDirectory;  
        File dir = doc.getParentDirectory();  
        if (dir != null && ! dir.equals(oldDir)) { 
        
          _activeDirectory = dir;
          _notifier.currentDirectoryChanged(_activeDirectory);
        }
        return Boolean.valueOf(true); 
      }
      public Boolean fileCase(File f) {
        if (! f.isAbsolute()) { 
          File root = _state.getProjectFile().getParentFile().getAbsoluteFile();
          f = new File(root, f.getPath());
        }
        _activeDirectory = f;  
        _notifier.currentDirectoryChanged(f);
        return Boolean.valueOf(true);
      }
      public Boolean stringCase(String s) { return Boolean.valueOf(false); }
    };
    
    _documentNavigator.addNavigationListener(new INavigationListener<OpenDefinitionsDocument>() {
      public void gainedSelection(NodeData<? extends OpenDefinitionsDocument> dat) { dat.execute(_gainVisitor); }
      public void lostSelection(NodeData<? extends OpenDefinitionsDocument> dat) {
      
      }
    });
    
    _documentNavigator.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) { 

        if (_documentNavigator.getCurrent() != null) 
          _notifier.focusOnDefinitionsPane(); 
      }
      public void focusLost(FocusEvent e) { }
    });
    
    _isClosingAllDocs = false;
    _ensureNotEmpty();
    setActiveFirstDocument();
  }
  
  
  protected File getSourceRoot(String packageName, File sourceFile) throws InvalidPackageException {

    if (packageName.equals("")) {

      return sourceFile.getParentFile();
    }
    
    ArrayList<String> packageStack = new ArrayList<String>();
    int dotIndex = packageName.indexOf('.');
    int curPartBegins = 0;
    
    while (dotIndex != -1) {
      packageStack.add(packageName.substring(curPartBegins, dotIndex));
      curPartBegins = dotIndex + 1;
      dotIndex = packageName.indexOf('.', dotIndex + 1);
    }
    
    
    packageStack.add(packageName.substring(curPartBegins));
    
    
    
    try {
      File parentDir = sourceFile.getCanonicalFile();
      while (! packageStack.isEmpty()) {
        String part = pop(packageStack);
        parentDir = parentDir.getParentFile();
        if (parentDir == null) throw new UnexpectedException("parent dir is null!");
        
        
        if (! part.equals(parentDir.getName())) {
          String msg = "The source file " + sourceFile.getAbsolutePath() +
            " is in the wrong directory or in the wrong package. " +
            "The directory name " + parentDir.getName() +
            " does not match the package component " + part + ".";
          
          throw new InvalidPackageException(-1, msg);
        }
      }
      
      
      
      parentDir = parentDir.getParentFile();
      if (parentDir == null) {

        throw new RuntimeException("parent dir of first component is null!");
      }
      

      return parentDir;
    }
    catch (IOException ioe) {
      String msg = "Could not locate directory of the source file: " + ioe;
      throw new InvalidPackageException(-1, msg);
    }
  }
  
  
  protected FileGroupingState _state;
  
  public void compileAll() throws IOException { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
  }
  
  
  public void setFileGroupingState(FileGroupingState state) {
    _state = state;
    _notifier.projectRunnableChanged();
    _notifier.projectBuildDirChanged();
    _notifier.projectWorkDirChanged();
    _notifier.projectModified();
  }
  
  protected FileGroupingState 
    makeProjectFileGroupingState(File pr, File main, File bd, File wd, File project, File[] files, ClassPathVector cp, File cjf, int cjflags) {
    return new ProjectFileGroupingState(pr, main, bd, wd, project, files, cp, cjf, cjflags);
  }
  
  
  public void setProjectChanged(boolean changed) {

    _state.setProjectChanged(changed);
    _notifier.projectModified();
  }
  
  
  public boolean isProjectChanged() { return _state.isProjectChanged(); }
  
  
  public boolean isProjectActive() { return _state.isProjectActive(); }
  
  
  public File getProjectFile() { return _state.getProjectFile(); }
  
  
  public File[] getProjectFiles() { return _state.getProjectFiles(); }
  
  
  public boolean inProject(File f) { return _state.inProject(f); }
  
  
  public boolean isInProjectPath(OpenDefinitionsDocument doc) { return _state.isInProjectPath(doc); }
  
  
  public void setMainClass(File f) {
    _state.setMainClass(f);
    _notifier.projectRunnableChanged();
    setProjectChanged(true);
  }
  
  
  public File getMainClass() { return _state.getMainClass(); }
  
  
  public void setCreateJarFile(File f) {
    _state.setCreateJarFile(f);
    setProjectChanged(true);
  }
  
    
  public File getCreateJarFile() { return _state.getCreateJarFile(); }
  
  
  public void setCreateJarFlags(int f) {
    _state.setCreateJarFlags(f);
    setProjectChanged(true);
  }
  
    
  public int getCreateJarFlags() { return _state.getCreateJarFlags(); }
  
  
  public void junitAll() { 
    throw new UnsupportedOperationException("AbstractGlobalDocument does not support unit testing");
  }
  
   
  public File getProjectRoot() { return _state.getProjectRoot(); } 
    
  
  public void setProjectRoot(File f) {
    _state.setProjectRoot(f);

    setProjectChanged(true);
  }
  
  
  public void setProjectFile(File f) { _state.setProjectFile(f); }
  
  
  public File getBuildDirectory() { return _state.getBuildDirectory(); }
  
  
  public void setBuildDirectory(File f) {
    _state.setBuildDirectory(f);
    _notifier.projectBuildDirChanged();
    setProjectChanged(true);
  }
  
  
  public File getMasterWorkingDirectory() { 
    File workDir = DrJava.getConfig().getSetting(OptionConstants.WORKING_DIRECTORY);
    if (workDir != null && workDir != FileOption.NULL_FILE) return workDir;
    return new File(System.getProperty("user.dir"));
  }
    
  
  public File getWorkingDirectory() { 


    return _state.getWorkingDirectory(); 
  }
  
  
  public void setWorkingDirectory(File f) {
    _state.setWorkingDirectory(f);
    _notifier.projectWorkDirChanged();
    setProjectChanged(true);
  }
  
  public void cleanBuildDirectory()  {
    _state.cleanBuildDirectory();
  }
  
  public List<File> getClassFiles() { return _state.getClassFiles(); }
  
  
  protected static String getPackageName(String classname) {
    int index = classname.lastIndexOf(".");
    if (index != -1) return classname.substring(0, index);
    else return "";
  }
  
  
  class ProjectFileGroupingState implements FileGroupingState {
    
    File _projRoot;
    File _mainFile;
    File _builtDir;
    File _workDir;
    File _projectFile;
    final File[] projectFiles;
    ClassPathVector _projExtraClassPath;
    private boolean _isProjectChanged = false;
    File _createJarFile;
    int _createJarFlags;
    
    
    
    HashSet<String> _projFilePaths = new HashSet<String>();
    
    
    ProjectFileGroupingState(File project) {
      this(project.getParentFile(), null, null, null, project, new File[0], new ClassPathVector(), null, 0);
    }
    
    ProjectFileGroupingState(File pr, File main, File bd, File wd, File project, File[] files, ClassPathVector cp, File cjf, int cjflags) {
      _projRoot = pr;

      _mainFile = main;
      _builtDir = bd;
      _workDir = wd;
      _projectFile = project;
      projectFiles = files;
      _projExtraClassPath = cp;
      
      if (projectFiles != null) try {  for (File file : projectFiles) { _projFilePaths.add(file.getCanonicalPath()); } }
      catch(IOException e) {  }
      
      _createJarFile = cjf;
      _createJarFlags = cjflags;
    }
    
    public boolean isProjectActive() { return true; }
    
    
    public boolean isInProjectPath(OpenDefinitionsDocument doc) {
      if (doc.isUntitled()) return false;
      
      
      
      
      File f;
      try { f = doc.getFile(); } 
      catch(FileMovedException fme) { f = fme.getFile(); }
      return isInProjectPath(f);
    }
    
    
    public boolean isInProjectPath(File f) { return FileOps.isInFileTree(f, getProjectRoot()); }
    
    
    public File getProjectFile() { return _projectFile; }
    
    public boolean inProject(File f) {
      String path;
      
      if (f == null || ! isInProjectPath(f)) return false;
      try { 
        path = f.getCanonicalPath();
        return _projFilePaths.contains(path);
      }
      catch(IOException ioe) { return false; }
    }
    
    public File[] getProjectFiles() { return projectFiles; }
    
    public File getProjectRoot() { 
      if (_projRoot == null || _projRoot.equals(FileOption.NULL_FILE)) return _projectFile.getParentFile();

      return _projRoot;
    }
    
    public File getBuildDirectory() { return _builtDir; }
    
    public File getWorkingDirectory() { 
      try {
        if (_workDir == null || _workDir == FileOption.NULL_FILE) 
          return _projectFile.getParentFile().getCanonicalFile(); 
        return _workDir.getCanonicalFile();
      }
      catch(IOException e) {  }
      return _workDir.getAbsoluteFile(); 
    }
    
    
    public void setProjectFile(File f) { _projectFile = f; }
    
    public void setProjectRoot(File f) { 
      _projRoot = f; 

    }
    
    public void setBuildDirectory(File f) { _builtDir = f; }
    
    public void setWorkingDirectory(File f) { _workDir = f; }
    
    public File getMainClass() { return _mainFile; }
    
    public void setMainClass(File f) { _mainFile = f; }
    
    public void setCreateJarFile(File f) { _createJarFile = f; }
  
    public File getCreateJarFile() { return _createJarFile; }
    
    public void setCreateJarFlags(int f) { _createJarFlags = f; }
  
    public int getCreateJarFlags() { return _createJarFlags; }
    
    public boolean isProjectChanged() { return _isProjectChanged; }
    
    public void setProjectChanged(boolean changed) { _isProjectChanged = changed; }
    
    public boolean isAuxiliaryFile(File f) {
      String path;
      
      if (f == null) return false;
      
      try { path = f.getCanonicalPath();}
      catch(IOException ioe) { return false; }
      
      synchronized(_auxiliaryFiles) {
        for (File file : _auxiliaryFiles) {
          try { if (file.getCanonicalPath().equals(path)) return true; }
          catch(IOException ioe) {  }
        }
        return false;
      }
    }






















    
    
    public void cleanBuildDirectory() {
      File dir = this.getBuildDirectory();
      _notifier.executeAsyncTask(_findFilesToCleanTask, dir, false, true);
    }
    
    private AsyncTask<File,List<File>> _findFilesToCleanTask = new AsyncTask<File,List<File>>("Find Files to Clean") {
      private FilenameFilter _filter = new FilenameFilter() {
        public boolean accept(File parent, String name) {
          return new File(parent, name).isDirectory() || name.endsWith(".class");
        }
      };
      
      public List<File> runAsync(File buildDir, IAsyncProgress monitor) throws Exception {
        List<File> accumulator = new LinkedList<File>();
        helper(buildDir, accumulator); 
        return accumulator;
      }
      public void complete(AsyncCompletionArgs<List<File>> args) {
        _notifier.executeAsyncTask(_deleteFilesTask, args.getResult(), true, true);
      }
      public String getDiscriptionMessage() {
        return "Finding files to delete...";
      }
      private void helper(File file, List<File> accumulator) {
        if (file.isDirectory()) {
          File[] children = file.listFiles(_filter);
          for (File child : children) {
            helper(child, accumulator);
            accumulator.add(file);
          }
        }
        else if (file.getName().endsWith(".class")){
          accumulator.add(file);
        }
      }
    };    
    
    private AsyncTask<List<File>,List<File>> _deleteFilesTask = new AsyncTask<List<File>,List<File>>("Delete Files") {
      public List<File> runAsync(List<File> filesToDelete, IAsyncProgress monitor) throws Exception {
        List<File> undeletableFiles = new ArrayList<File>();
        
        monitor.setMinimum(0);
        monitor.setMaximum(filesToDelete.size());
        int progress = 1;
        for(File file : filesToDelete) {
          if (monitor.isCanceled()) {
            break;
          }
          monitor.setNote(file.getName());
          boolean could = file.delete();
          if (!could) undeletableFiles.add(file);
          monitor.setProgress(progress++);
        }

        return undeletableFiles;
      }
      public void complete(AsyncCompletionArgs<List<File>> args) {
        
      }
      public String getDiscriptionMessage() {
        return "Deleting files...";
      }
    };
    
    public List<File> getClassFiles() {
      File dir = this.getBuildDirectory();
      LinkedList<File> acc = new LinkedList<File>();
      getClassFilesHelper(dir, acc);
      if (! dir.exists()) dir.mkdirs();
      return acc;
    }
    
    private void getClassFilesHelper(File f, LinkedList<File> acc) {
      if (f.isDirectory()) {
        
        File fs[] = f.listFiles(new FilenameFilter() {
          public boolean accept(File parent, String name) {
            return new File(parent, name).isDirectory() || name.endsWith(".class");
          }
        });
        
        for (File kid: fs) { getClassFilesHelper(kid, acc); }
        
      } else if (f.getName().endsWith(".class")) acc.add(f);
    }    
    
    
    

    
    public void compileAll() throws IOException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
    }
    
    
    
    public void junitAll() {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support JUnit testing");
    }

    public void jarAll() {
      throw new UnsupportedOperationException("AbstractGlobaModel does not support jarring");
    }
    
    public ClassPathVector getExtraClassPath() { return _projExtraClassPath; }
    
    public void setExtraClassPath(ClassPathVector cp) { _projExtraClassPath = cp; }
  }
  
  protected FileGroupingState makeFlatFileGroupingState() { return new FlatFileGroupingState(); }
  
  class FlatFileGroupingState implements FileGroupingState {
    public File getBuildDirectory() { return null; }
    public File getProjectRoot() { return getWorkingDirectory(); }
    public File getWorkingDirectory() { 
      try { 
        File[] roots = getSourceRootSet();

        if (roots.length == 0) return getMasterWorkingDirectory();
        return roots[0].getCanonicalFile(); 
      }
      catch(IOException e) {  }
      return new File(System.getProperty("user.dir"));  
    }
    public boolean isProjectActive() { return false; }
    public boolean isInProjectPath(OpenDefinitionsDocument doc) { return false; }
    public boolean isInProjectPath(File f) { return false; }
    public File getProjectFile() { return null; }
    public void setBuildDirectory(File f) { }
    public void setProjectFile(File f) { }
    public void setProjectRoot(File f) { }
    public void setWorkingDirectory(File f) { }
    public File[] getProjectFiles() { return null; }
    public boolean inProject(File f) { return false; }
    public File getMainClass() { return null; }
    public void setMainClass(File f) { }
    public void setCreateJarFile(File f) { }
    public File getCreateJarFile() { return null; }
    public void setCreateJarFlags(int f) { }
    public int getCreateJarFlags() { return 0; }
    public ClassPathVector getExtraClassPath() { return new ClassPathVector(); }
    public void setExtraClassPath(ClassPathVector cp) { }
    public boolean isProjectChanged() { return false; }
    public void setProjectChanged(boolean changed) {   }
    public boolean isAuxiliaryFile(File f) { return false; }
    
    
    public void compileAll() throws IOException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not suport compilation");
    }
    
    
    public void junitAll() { 
      throw new UnsupportedOperationException("AbstractGlobalModel does not support unit tests");
    }
    public void cleanBuildDirectory() { }
    
    public List<File> getClassFiles() { return new LinkedList<File>(); }
    
    
    public void jarAll() { 
      throw new UnsupportedOperationException("AbstractGlobalModel does not support jarring");
    }
  }
  
  
  public String getSourceBinTitle() { return "[ Source Files ]"; }
  
  
  public String getExternalBinTitle() { return "[ External Files ]"; }
  
  
  public String getAuxiliaryBinTitle() { return "[ Included External Files ]"; }
  
  
  
  
  public void addListener(GlobalModelListener listener) { _notifier.addListener(listener); }
  
  
  public void removeListener(GlobalModelListener listener) { _notifier.removeListener(listener); }
  
  
  
  public DefinitionsEditorKit getEditorKit() { return _editorKit; }
  
  
  public DefaultInteractionsModel getInteractionsModel() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interaction");
  }
  
  
  public InteractionsDJDocument getSwingInteractionsDocument() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interaction");
  }
  
  
  public InteractionsDocument getInteractionsDocument() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interaction");
  }
  
  public ConsoleDocument getConsoleDocument() { return _consoleDoc; }
  
  public InteractionsDJDocument getSwingConsoleDocument() { return _consoleDocAdapter; }
  
  public PageFormat getPageFormat() { return _pageFormat; }
  
  public void setPageFormat(PageFormat format) { _pageFormat = format; }
  
  
  public CompilerModel getCompilerModel() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
  }
  
  
  public JUnitModel getJUnitModel() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support unit testing");
  }
  
  
  public JavadocModel getJavadocModel() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support javadoc");
  }
  
  public IDocumentNavigator<OpenDefinitionsDocument> getDocumentNavigator() { return _documentNavigator; }
  
  public void setDocumentNavigator(IDocumentNavigator<OpenDefinitionsDocument> newnav) { _documentNavigator = newnav; }
  
  
  public OpenDefinitionsDocument newFile(File parentDir) {
    final ConcreteOpenDefDoc doc = _createOpenDefinitionsDocument();
    doc.setParentDirectory(parentDir);
    doc.setFile(null);
    addDocToNavigator(doc);
    _notifier.newFileCreated(doc);
    return doc;
  }

  
  
  public OpenDefinitionsDocument newFile() {
    File dir = _activeDirectory;
    if (dir == null) dir = getMasterWorkingDirectory();
    OpenDefinitionsDocument doc = newFile(dir);
    setActiveDocument(doc);
    return doc;
  }
                                               
  
  public OpenDefinitionsDocument newTestCase(String name, boolean makeSetUp, boolean makeTearDown) {
    boolean elementary = (DrJava.getConfig().getSetting(LANGUAGE_LEVEL) == 1);
    
    StringBuffer buf = new StringBuffer();
    if (! elementary) buf.append("import junit.framework.TestCase;\n\n");
    buf.append("/**\n");
    buf.append("* A JUnit test case class.\n");
    buf.append("* Every method starting with the word \"test\" will be called when running\n");
    buf.append("* the test with JUnit.\n");
    buf.append("*/\n");
    if (! elementary) buf.append("public ");
    buf.append("class ");
    buf.append(name);
    buf.append(" extends TestCase {\n\n");
    if (makeSetUp) {
      buf.append("/**\n");
      buf.append("* This method is called before each test method, to perform any common\n");
      buf.append("* setup if necessary.\n");
      buf.append("*/\n");
      if (! elementary) buf.append("public ");
      buf.append("void setUp() throws Exception {\n}\n\n");
    }
    if (makeTearDown) {
      buf.append("/**\n");
      buf.append("* This method is called after each test method, to perform any common\n");
      buf.append("* clean-up if necessary.\n");
      buf.append("*/\n");
      if (! elementary) buf.append("public ");
      buf.append("void tearDown() throws Exception {\n}\n\n");
    }
    buf.append("/**\n");
    buf.append("* A test method.\n");
    buf.append("* (Replace \"X\" with a name describing the test.  You may write as\n");
    buf.append("* many \"testSomething\" methods in this class as you wish, and each\n");
    buf.append("* one will be called when running JUnit over this class.)\n");
    buf.append("*/\n");
    if (! elementary) buf.append("public ");
    buf.append("void testX() {\n}\n\n");
    buf.append("}\n");
    String test = buf.toString();
    
    OpenDefinitionsDocument openDoc = newFile();
    try {
      openDoc.insertString(0, test, null);
      openDoc.indentLines(0, test.length());
    }
    catch (BadLocationException ble) {
      throw new UnexpectedException(ble);
    }
    return openDoc;
  }
  
  
  public DocumentCache getDocumentCache() { return _cache; }
  
  
  
  
  public OpenDefinitionsDocument openFile(FileOpenSelector com) throws 
    IOException, OperationCanceledException, AlreadyOpenException {
    
    boolean closeUntitled = _hasOneEmptyDocument();
    OpenDefinitionsDocument oldDoc = _activeDocument; 
    OpenDefinitionsDocument openedDoc = openFileHelper(com);
    if (closeUntitled) closeFileHelper(oldDoc);

    setActiveDocument(openedDoc);
    setProjectChanged(true);

    return openedDoc;
  }
  
  protected OpenDefinitionsDocument openFileHelper(FileOpenSelector com) throws IOException, 
    OperationCanceledException, AlreadyOpenException {
    
    
    final File file = (com.getFiles())[0].getCanonicalFile();  
    OpenDefinitionsDocument odd = _openFile(file);

    
    try {
      File classPath = odd.getSourceRoot();
      addDocToClassPath(odd);
    }
    catch (InvalidPackageException e) {
      
    }
    
    return odd;
  }
  
  
  public OpenDefinitionsDocument[] openFiles(FileOpenSelector com)
    throws IOException, OperationCanceledException, AlreadyOpenException {
    
    
    boolean closeUntitled = _hasOneEmptyDocument();
    OpenDefinitionsDocument oldDoc = _activeDocument;

    OpenDefinitionsDocument[] openedDocs = openFilesHelper(com);
    if (openedDocs.length > 0) {
      if (closeUntitled) closeFileHelper(oldDoc);
      setActiveDocument(openedDocs[0]);
    }
    return openedDocs;
  }
  
  protected OpenDefinitionsDocument[] openFilesHelper(FileOpenSelector com)
    throws IOException, OperationCanceledException, AlreadyOpenException {
    
    final File[] files = com.getFiles();
    if (files == null) { throw new IOException("No Files returned from FileSelector"); }
    OpenDefinitionsDocument[] docs = _openFiles(files);
    return docs;
  }
  
  
  
  
  
  
  
  private OpenDefinitionsDocument[] _openFiles(File[] files) 
    throws IOException, OperationCanceledException, AlreadyOpenException {
    
    ArrayList<OpenDefinitionsDocument> alreadyOpenDocuments = new ArrayList<OpenDefinitionsDocument>();
    ArrayList<OpenDefinitionsDocument> retDocs = new ArrayList<OpenDefinitionsDocument>();
    
    
    
    LinkedList<File> filesNotFound = new LinkedList<File>();
    LinkedList<OpenDefinitionsDocument> filesOpened = new LinkedList<OpenDefinitionsDocument>();
    for (final File f: files) {
      if (f == null) throw new IOException("File name returned from FileSelector is null");
      try {
        OpenDefinitionsDocument d = _rawOpenFile(FileOps.getCanonicalFile(f));
        
        retDocs.add(d);
        filesOpened.add(d);
      }
      catch (AlreadyOpenException aoe) {
        OpenDefinitionsDocument d = aoe.getOpenDocument();
        retDocs.add(d);
        alreadyOpenDocuments.add(d);
      }
      catch(FileNotFoundException e) { filesNotFound.add(f); }
    }
    
    for (final OpenDefinitionsDocument d: filesOpened) {
      _completeOpenFile(d); 
    }
    
    for (File f: filesNotFound) { _notifier.fileNotFound(f); }
    
    if (!alreadyOpenDocuments.isEmpty()) {
      for(OpenDefinitionsDocument d : alreadyOpenDocuments) {
        _notifier.handleAlreadyOpenDocument(d);
        _notifier.fileOpened(d);
      }
    }                                   
    
    if (retDocs != null) {
      return retDocs.toArray(new OpenDefinitionsDocument[0]);
    }
    else {
      
      throw new OperationCanceledException();
    }
  }
  
  
  
  
  
  public void openFolder(File dir, boolean rec) throws IOException, OperationCanceledException, AlreadyOpenException {
    if (dir == null) return; 
  
    ArrayList<File> files;
    if (dir.isDirectory()) {
      files = FileOps.getFilesInDir(dir, rec, new FileFilter() {
        public boolean accept(File f) { 
          return f.isDirectory() ||
            f.isFile() && 
            f.getName().endsWith(DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS[DrJava.getConfig().getSetting(LANGUAGE_LEVEL)]);
        }
      });
      
      if (isProjectActive())
        Collections.sort(files, new Comparator<File>() {
        public int compare(File o1,File o2) {
          return - o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
        }
      });
      else
        Collections.sort(files, new Comparator<File>() {
        public int compare(File o1,File o2) {
          return - o1.getName().compareTo(o2.getName());
        }
      });
      
      int ct = files.size();
      
      final File[] sfiles = files.toArray(new File[ct]);
      
      openFiles(new FileOpenSelector() { public File[] getFiles() { return sfiles; } });
      
      if (ct > 0 && _state.isInProjectPath(dir)) setProjectChanged(true);
    }
  }
  
  
  
   public void saveAllFiles(FileSaveSelector com) throws IOException {
     OpenDefinitionsDocument curdoc = getActiveDocument();
     saveAllFilesHelper(com);
     setActiveDocument(curdoc); 
   }
  
  
  protected void saveAllFilesHelper(FileSaveSelector com) throws IOException {
    
    boolean isProjActive = isProjectActive();
    
    OpenDefinitionsDocument[] docs;
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    for (final OpenDefinitionsDocument doc: docs) {
      if (doc.isUntitled() && isProjActive) continue;  
      aboutToSaveFromSaveAll(doc);
      doc.saveFile(com);
    }
  }
  
  
  
  public void createNewProject(File projFile) { setFileGroupingState(new ProjectFileGroupingState(projFile)); }
    
  
  public void configNewProject() throws IOException {
    

    File projFile = getProjectFile();
    
    ProjectProfile builder = new ProjectProfile(projFile);
    
    
    ArrayList<File> srcFileList = new ArrayList<File>();
    LinkedList<File> auxFileList = new LinkedList<File>();
    ArrayList<File> extFileList = new ArrayList<File>();

    OpenDefinitionsDocument[] docs;
    
    File projectRoot = builder.getProjectRoot();
    

    
    ClassPathVector exCp = new ClassPathVector();
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
   
    for (OpenDefinitionsDocument doc: docs) {
      
      File f = doc.getFile();
      
      if (doc.isUntitled()) extFileList.add(f);
      else if (FileOps.isInFileTree(f, projectRoot)) {
        builder.addSourceFile(new DocFile(f));
        srcFileList.add(f);
      }
      else if (doc.isAuxiliaryFile()) {
        builder.addAuxiliaryFile(new DocFile(f));
        auxFileList.add(f);
      }
      else  extFileList.add(f);
    }
    
    File[] srcFiles = srcFileList.toArray(new File[srcFileList.size()]);
    File[] extFiles = extFileList.toArray(new File[extFileList.size()]);
    
    
    builder.write();
    
    _loadProject(builder);
  }
    
  
  public void saveProject(File file, Hashtable<OpenDefinitionsDocument, DocumentInfoGetter> info) 
    throws IOException {
    
    ProjectProfile builder = new ProjectProfile(file);
    
    
    File pr = getProjectRoot();
    if (pr != null) builder.setProjectRoot(pr);
    
    
    ArrayList<File> srcFileList = new ArrayList<File>();
    LinkedList<File> auxFileList = new LinkedList<File>();
    
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    for (OpenDefinitionsDocument doc: docs) {
      if (doc.isInProjectPath()) {
        DocumentInfoGetter g = info.get(doc);
        builder.addSourceFile(g);
        srcFileList.add(g.getFile());
      }
      else if (doc.isAuxiliaryFile()) {
        DocumentInfoGetter g = info.get(doc);
        builder.addAuxiliaryFile(g);
        auxFileList.add(g.getFile());
      }
    } 
      
    
    if (_documentNavigator instanceof JTreeSortNavigator) {
      String[] paths = ((JTreeSortNavigator<?>)_documentNavigator).getCollapsedPaths();
      for (String s : paths) { builder.addCollapsedPath(s); }
    }
    
    ClassPathVector exCp = getExtraClassPath();
    if (exCp != null) {
      Vector<File> exCpF = exCp.asFileVector();
      for (File f : exCpF) {
        builder.addClassPathFile(f);
        
      }
    } 

    
    
    File bd = getBuildDirectory();
    if (bd != null) builder.setBuildDirectory(bd);
    
    
    File wd = getWorkingDirectory();  
    if (wd != null && bd != FileOption.NULL_FILE) builder.setWorkingDirectory(wd);
    
    
    File mainClass = getMainClass();
    if (mainClass != null) builder.setMainClass(mainClass);
    
    
    File createJarFile = getCreateJarFile();
    if (createJarFile != null) builder.setCreateJarFile(createJarFile);
    
    int createJarFlags = getCreateJarFlags();
    if (createJarFlags != 0) builder.setCreateJarFlags(createJarFlags);
    
    
    try {
      ArrayList<DebugBreakpointData> l = new ArrayList<DebugBreakpointData>();
      for(Breakpoint bp: getDebugger().getBreakpoints()) { l.add(bp); }
      builder.setBreakpoints(l);
    }
    catch(DebugException de) {  }
    try {
      builder.setWatches(getDebugger().getWatches());
    }
    catch(DebugException de) {  }
    
    
    builder.write();
    
    
    File[] srcFiles = srcFileList.toArray(new File[srcFileList.size()]);
    
    synchronized(_auxiliaryFiles) { _auxiliaryFiles = auxFileList;  }
    
    setFileGroupingState(makeProjectFileGroupingState(pr, mainClass, bd, wd, file, srcFiles, exCp, createJarFile, 
                                                      createJarFlags));
  }
  
  
  public File[] openProject(File projectFile) throws IOException, MalformedProjectFileException {
    return _loadProject(ProjectFileParser.ONLY.parse(projectFile));
  }
  
  
  private File[] _loadProject(ProjectFileIR ir) throws IOException {
    
    final DocFile[] srcFiles = ir.getSourceFiles();
    final DocFile[] auxFiles = ir.getAuxiliaryFiles();
    final File projectFile = ir.getProjectFile();
    final File projectRoot = ir.getProjectRoot();
    final File buildDir = ir.getBuildDirectory();
    final File workDir = ir.getWorkingDirectory();
    final File mainClass = ir.getMainClass();
    final File[] projectClassPaths = ir.getClassPaths();
    final File createJarFile  = ir.getCreateJarFile();
    int createJarFlags = ir.getCreateJarFlags();
    
    
    try { getDebugger().removeAllBreakpoints(); }
    catch(DebugException de) {  }
    for (DebugBreakpointData dbd: ir.getBreakpoints()) {
      try { 
        getDebugger().toggleBreakpoint(getDocumentForFile(dbd.getFile()), dbd.getOffset(), dbd.getLineNumber(), 
                                           dbd.isEnabled()); 
      }
      catch(DebugException de) {  }
    }
    
    
    try { getDebugger().removeAllWatches(); }
    catch(DebugException de) {  }
    for (DebugWatchData dwd: ir.getWatches()) {
      try { getDebugger().addWatch(dwd.getName()); }
      catch(DebugException de) {  }
    }
    
    final String projfilepath = projectRoot.getCanonicalPath();
    
    


    

    
    
    
    
    
    List<Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>> l = 
        new LinkedList<Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>>();
    
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getSourceBinTitle(), 
        new INavigatorItemFilter<OpenDefinitionsDocument>() {
          public boolean accept(OpenDefinitionsDocument d) { return d.isInProjectPath(); }
        }));
    
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getAuxiliaryBinTitle(), 
        new INavigatorItemFilter<OpenDefinitionsDocument>() {
          public boolean accept(OpenDefinitionsDocument d) { return d.isAuxiliaryFile(); }
        }));
    
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getExternalBinTitle(), 
        new INavigatorItemFilter<OpenDefinitionsDocument>() {
          public boolean accept(OpenDefinitionsDocument d) {
            return !(d.inProject() || d.isAuxiliaryFile()) || d.isUntitled();
          }
        }));
    
    IDocumentNavigator<OpenDefinitionsDocument> newNav = 
      new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().makeTreeNavigator(projfilepath, getDocumentNavigator(), l);
    
    setDocumentNavigator(newNav);
    
    synchronized(_auxiliaryFiles) {
      _auxiliaryFiles.clear();
      for (File file: auxFiles) { _auxiliaryFiles.add(file); }
    }
    
    ClassPathVector extraClassPaths = new ClassPathVector();
    for (File f : projectClassPaths) { extraClassPaths.add(f); }
    

    
    setFileGroupingState(makeProjectFileGroupingState(projectRoot, mainClass, buildDir, workDir, projectFile, srcFiles,
                                                      extraClassPaths, createJarFile, createJarFlags));
    
    resetInteractions(getWorkingDirectory());  
    
    ArrayList<File> projFiles = new ArrayList<File>();
    File active = null;
    for (DocFile f: srcFiles) {
      File file = f;
      if (f.lastModified() > f.getSavedModDate()) file = new File(f.getPath());
      if (f.isActive() && active == null) active = file;
      else projFiles.add(file);
    }
    for (DocFile f: auxFiles) {
      File file = f;
      if (f.lastModified() > f.getSavedModDate()) file = new File(f.getPath());
      if (f.isActive() && active == null) active = file;
      else projFiles.add(file);
    }
    
    if (active != null) projFiles.add(active); 
    

    
    final List<OpenDefinitionsDocument> projDocs = getProjectDocuments();  
    
    
    
    
    if (! projDocs.isEmpty()) 
      Utilities.invokeAndWait(new SRunnable() {
      public void run() {
        for (OpenDefinitionsDocument d: projDocs) {
          try {
            final String path = fixPathForNavigator(d.getFile().getCanonicalPath());
            _documentNavigator.refreshDocument(d, path);  
          }
          catch(IOException e) {  }
        }
      }
    });
    

    
    final File[] filesToOpen = projFiles.toArray(new File[projFiles.size()]);
    _notifier.projectOpened(projectFile, new FileOpenSelector() {
      public File[] getFiles() { return filesToOpen; }
    });
    
    if (_documentNavigator instanceof JTreeSortNavigator) {
      ((JTreeSortNavigator<?>)_documentNavigator).collapsePaths(ir.getCollapsedPaths());
    }
   
    return srcFiles; 
  }
  
  
  public void closeProject(boolean suppressReset) {
    setDocumentNavigator(new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().
                           makeListNavigator(getDocumentNavigator()));
    setFileGroupingState(makeFlatFileGroupingState());

    
    
   
    if (! suppressReset) resetInteractions(getWorkingDirectory());
    _notifier.projectClosed();
  }
  
  
  public void aboutToSaveFromSaveAll(OpenDefinitionsDocument doc) {
    if (doc.isUntitled()) setActiveDocument(doc);
  }
  
  
   public boolean closeFile(OpenDefinitionsDocument doc) {
     List<OpenDefinitionsDocument> list = new LinkedList<OpenDefinitionsDocument>();
     list.add(doc);
     return closeFiles(list);
   }
  
  
   public boolean closeAllFiles() {
     List<OpenDefinitionsDocument> docs = getOpenDefinitionsDocuments();
     return closeFiles(docs);
   }
  
  
  public boolean closeFiles(List<OpenDefinitionsDocument> docList) {
    if (docList.size() == 0) return true;
    
    
    for (OpenDefinitionsDocument doc : docList) { if (! doc.canAbandonFile()) return false; }
    
    
    
    if (docList.size() == getOpenDefinitionsDocumentsSize()) newFile();
    
    
    
    _ensureNotActive(docList);
        
    
    for (OpenDefinitionsDocument doc : docList) { closeFileWithoutPrompt(doc); }  
    return true;
  }
  
  
  protected boolean closeFileHelper(OpenDefinitionsDocument doc) {
    
    boolean canClose = doc.canAbandonFile();
    if (canClose) return closeFileWithoutPrompt(doc);
    return false;
  }
  
  
  protected void closeFileOnQuitHelper(OpenDefinitionsDocument doc) {
    
    doc.quitFile();
    closeFileWithoutPrompt(doc);
  }
  
  
  public boolean closeFileWithoutPrompt(final OpenDefinitionsDocument doc) {
    
    
    boolean found;
    synchronized(_documentsRepos) { found = _documentsRepos.remove(doc); }
    
    if (! found) return false;
        
    
    Debugger dbg = getDebugger();
    if (dbg.isAvailable()) {
      Vector<Breakpoint> bps = new Vector<Breakpoint>(doc.getBreakpoints());
      for (int i = 0; i < bps.size(); i++) {
        Breakpoint bp = bps.get(i);
        try { dbg.removeBreakpoint(bp); }
        catch(DebugException de) {  }
      }
    }
    
    Utilities.invokeLater(new SRunnable() { 
      public void run() { _documentNavigator.removeDocument(doc); }   
    });
    _notifier.fileClosed(doc);
    doc.close();
    return true;
  }
  
  
  public void closeAllFilesOnQuit() {
    
    OpenDefinitionsDocument[] docs;
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    
    for (OpenDefinitionsDocument doc : docs) {
      closeFileOnQuitHelper(doc);  
    }
  }
    
  
  public void quit() {
    try {
      closeAllFilesOnQuit();

      dispose();  
    }
    catch(Throwable t) {  }
    finally { System.exit(0); }
  }

  
  public void dispose() {
    
    _notifier.removeAllListeners();

    synchronized(_documentsRepos) { _documentsRepos.clear(); }

    Utilities.invokeAndWait(new SRunnable() { 
      public void run() { _documentNavigator.clear(); }  
    });
  }

  
  

  public OpenDefinitionsDocument getDocumentForFile(File file) throws IOException {
    
    OpenDefinitionsDocument doc = _getOpenDocument(file);
    if (doc == null) {
      
      final File f = file;
      FileOpenSelector selector = 
        new FileOpenSelector() { public File[] getFiles() { return new File[] {f}; } };
      try { doc = openFile(selector);}
      catch (AlreadyOpenException e) { doc = e.getOpenDocument(); }
      catch (OperationCanceledException e) { throw new UnexpectedException(e);  }
    }
    return doc;
  }

  
  public boolean isAlreadyOpen(File file) { return (_getOpenDocument(file) != null); }

  
  public OpenDefinitionsDocument getODDForDocument(AbstractDocumentInterface doc) {
    
    if (doc instanceof OpenDefinitionsDocument) return (OpenDefinitionsDocument) doc;
    if  (doc instanceof DefinitionsDocument) return ((DefinitionsDocument) doc).getOpenDefDoc();
    throw new IllegalStateException("Could not get the OpenDefinitionsDocument for Document: " + doc);
  }

  
  public DocumentIterator getDocumentIterator() { return this; }

  
  public OpenDefinitionsDocument getNextDocument(AbstractDocumentInterface d) {
    OpenDefinitionsDocument nextdoc = null; 

      OpenDefinitionsDocument doc = getODDForDocument(d);
      nextdoc = _documentNavigator.getNext(doc);
      if (nextdoc == doc) nextdoc = _documentNavigator.getFirst();  
      OpenDefinitionsDocument res = getNextDocHelper(nextdoc);

      return res;


  }
  
  private OpenDefinitionsDocument getNextDocHelper(OpenDefinitionsDocument nextdoc) {
    if (nextdoc.isUntitled() || nextdoc.verifyExists()) return nextdoc;
    
    
    
    return getNextDocument(nextdoc);
  }

  
  public OpenDefinitionsDocument getPrevDocument(AbstractDocumentInterface d) {
    OpenDefinitionsDocument prevdoc = null;  

      OpenDefinitionsDocument doc = getODDForDocument(d);
      prevdoc = _documentNavigator.getPrevious(doc);
      if (prevdoc == doc) prevdoc = _documentNavigator.getLast(); 
      return getPrevDocHelper(prevdoc);


  }
  
  private OpenDefinitionsDocument getPrevDocHelper(OpenDefinitionsDocument prevdoc) { 
    if (prevdoc.isUntitled() || prevdoc.verifyExists()) return prevdoc;
    
    
    
    return getPrevDocument(prevdoc);
  }

  public int getDocumentCount() { return _documentsRepos.size(); }
  
  
  public List<OpenDefinitionsDocument> getOpenDefinitionsDocuments() {
    synchronized(_documentsRepos) {
      ArrayList<OpenDefinitionsDocument> docs = new ArrayList<OpenDefinitionsDocument>(_documentsRepos.size());
      for (OpenDefinitionsDocument doc: _documentsRepos) { docs.add(doc); }
      return docs;
    }
  }
  
  
  public int getOpenDefinitionsDocumentsSize() { synchronized(_documentsRepos) { return _documentsRepos.size(); } }
  
  
  public boolean hasOutOfSyncDocuments() {
    synchronized(_documentsRepos) {      
      for (OpenDefinitionsDocument doc: _documentsRepos) { if (! doc.checkIfClassFileInSync()) return true; }
      return false;
    }
  }
  



  



  
  
  
  
  void setDefinitionsIndent(int indent) {
    
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
      
    for (OpenDefinitionsDocument doc: docs) { doc.setIndent(indent); }
  }

  
  public void resetInteractions(File wd) {  }

  
  public void resetConsole() {
    _consoleDoc.reset("");
    _notifier.consoleReset();
  }

  
  public void interpretCurrentInteraction() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public void loadHistory(FileOpenSelector selector) throws IOException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector) throws
    IOException, OperationCanceledException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public void clearHistory() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public void saveHistory(FileSaveSelector selector) throws IOException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public String getHistoryAsStringWithSemicolons() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  public String getHistoryAsString() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }

  
  private void _registerOptionListeners() {




    DrJava.getConfig().addOptionListener(BACKUP_FILES,
                                         new BackUpFileOptionListener());
    Boolean makeBackups = DrJava.getConfig().getSetting(BACKUP_FILES);
    FileOps.DefaultFileSaver.setBackupsEnabled(makeBackups.booleanValue());







  }

  
  protected void _docAppend(ConsoleDocument doc, String s, String style) {
    synchronized(_systemWriterLock) {
      try {
        doc.insertBeforeLastPrompt(s, style);
        
        
        _systemWriterLock.wait(WRITE_DELAY);
      }
      catch (InterruptedException e) {
        
      }
    }
  }


  
  public void systemOutPrint(String s) {
    _docAppend(_consoleDoc, s, ConsoleDocument.SYSTEM_OUT_STYLE);
  }

  
  public void systemErrPrint(String s) { _docAppend(_consoleDoc, s, ConsoleDocument.SYSTEM_ERR_STYLE); }

  
  public void printDebugMessage(String s) {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support debugging");
  }


  
  public void waitForInterpreter() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }


  
  public ClassPathVector getClassPath() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support classPaths");
  }
  
  
  public ClassPathVector getExtraClassPath() { return _state.getExtraClassPath(); }
  
  
  public void setExtraClassPath(ClassPathVector cp) {
    _state.setExtraClassPath(cp);
    
  }

  
  public File[] getSourceRootSet() {
    HashSet<File> roots = new HashSet<File>();
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs =  _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }

    for (OpenDefinitionsDocument doc: docs) {
      try {
        File root = doc.getSourceRoot();
        if (root != null) roots.add(root); 
      }
      catch (InvalidPackageException e) { 

       
      }
    }
    return roots.toArray(new File[roots.size()]);
  }
  
  
  public String getDisplayFilename(OpenDefinitionsDocument doc) {

    String fileName = doc.getFilename();

    
    if (fileName.endsWith(".java")) {
      int extIndex = fileName.lastIndexOf(".java");
      if (extIndex > 0) fileName = fileName.substring(0, extIndex);
    }
    
    
    if (doc.isModifiedSinceSave()) fileName = fileName + "*";
    
    return fileName;
  }

  
  public String getDisplayFullPath(int index) {
    OpenDefinitionsDocument doc = getOpenDefinitionsDocuments().get(index);
    if (doc == null) throw new RuntimeException( "Document not found with index " + index);
    return GlobalModelNaming.getDisplayFullPath(doc);
  }
   






  
  public Debugger getDebugger() {
    
    return NoDebuggerAvailable.ONLY;
  }

  
  public int getDebugPort() throws IOException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support debugging");
  }

  
  public boolean hasModifiedDocuments() {
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    for (OpenDefinitionsDocument doc: docs) { 
      if (doc.isModifiedSinceSave()) return true;  
    }
    return false;
  }
  
  
  public boolean hasUntitledDocuments() {
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    for (OpenDefinitionsDocument doc: docs) { 
      if (doc.isUntitled()) return true;  
    }
    return false;
  }

  
  public File getSourceFile(String fileName) {
    File[] sourceRoots = getSourceRootSet();
    for (File s: sourceRoots) {
      File f = _getSourceFileFromPath(fileName, s);
      if (f != null) return f;
    }
    Vector<File> sourcepath = DrJava.getConfig().getSetting(OptionConstants.DEBUG_SOURCEPATH);
    return getSourceFileFromPaths(fileName, sourcepath);
  }

  
  public File getSourceFileFromPaths(String fileName, List<File> paths) {
    for (File p: paths) {
      File f = _getSourceFileFromPath(fileName, p);
      if (f != null) return f;
    }
    return null;
  }

  
  private File _getSourceFileFromPath(String fileName, File path) {
    String root = path.getAbsolutePath();
    File f = new File(root + System.getProperty("file.separator") + fileName);
    return f.exists() ? f : null;
  }

  
  public void jarAll() { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support jarring documents");
  }
  
  private static int ID_COUNTER = 0; 
  

  
  class ConcreteOpenDefDoc implements OpenDefinitionsDocument, AbstractDocumentInterface {
    
    private int _id;
    private DrJavaBook _book;
    protected Vector<Breakpoint> _breakpoints;
    

    
    private File _file;
    private long _timestamp;
    
    
    private File _parentDir;  

    protected String _packageName = null;
    
    private int _initVScroll;
    private int _initHScroll;
    private int _initSelStart;
    private int _initSelEnd;
    
    private DCacheAdapter _cacheAdapter;

    
    ConcreteOpenDefDoc(File f) throws IOException {
      if (! f.exists()) throw new FileNotFoundException("file " + f + " cannot be found");
      
      _file = f;
      _parentDir = f.getParentFile();  
      _timestamp = f.lastModified();
      init();
    }
    
    
    ConcreteOpenDefDoc() {
      _file = null;
      _parentDir = null;
      init();
    }
    
    public void init() {
      _id = ID_COUNTER++;
      
      try {

        DDReconstructor ddr = makeReconstructor();

        _cacheAdapter = _cache.register(this, ddr);
      } catch(IllegalStateException e) { throw new UnexpectedException(e); }

      _breakpoints = new Vector<Breakpoint>();
    }
    
    
    public int id() { return _id; }
    
    
    public void setParentDirectory(File pd) {
      if (_file != null) 
        throw new IllegalArgumentException("The parent directory can only be set for untitled documents");
      _parentDir = pd;  
    }
    
    
    public File getParentDirectory() { return _parentDir; }
    
    
    public boolean isInProjectPath() { return _state.isInProjectPath(this); }
    
    
    public boolean isInNewProjectPath(File projRoot) { 
      try { return ! isUntitled() && FileOps.isInFileTree(getFile(), projRoot); }
      catch(FileMovedException e) { return false; }
    }
  
    
    public boolean inProject() { return ! isUntitled() && _state.inProject(_file); }
    
    
    public boolean isAuxiliaryFile() { return ! isUntitled() && _state.isAuxiliaryFile(_file); }
    
    
    protected DDReconstructor makeReconstructor() {
      return new DDReconstructor() {
        
        
        private int _loc = 0;
        
        
        private DocumentListener[] _list = { };
        private List<FinalizationListener<DefinitionsDocument>> _finalListeners =
          new LinkedList<FinalizationListener<DefinitionsDocument>>();
        
        
        private WeakHashMap<DefinitionsDocument.WrappedPosition, Integer> _positions =
          new WeakHashMap<DefinitionsDocument.WrappedPosition, Integer>();
        
        public DefinitionsDocument make() throws IOException, BadLocationException, FileMovedException {
          DefinitionsDocument tempDoc;
          tempDoc = new DefinitionsDocument(_notifier);
          tempDoc.setOpenDefDoc(ConcreteOpenDefDoc.this);
                 
          if (_file != null) {
            FileReader reader = new FileReader(_file);
            _editorKit.read(reader, tempDoc, 0);
            reader.close(); 
          }
          _loc = Math.min(_loc, tempDoc.getLength()); 
          _loc = Math.max(_loc, 0); 
          tempDoc.setCurrentLocation(_loc);
          for (DocumentListener d : _list) {
            if (d instanceof DocumentUIListener) tempDoc.addDocumentListener(d);
          }
          for (FinalizationListener<DefinitionsDocument> l: _finalListeners) {
            tempDoc.addFinalizationListener(l);
          }

          
          tempDoc.setWrappedPositionOffsets(_positions);
          
          tempDoc.resetModification();  

          
          assert ! tempDoc.isModifiedSinceSave();
          try { _packageName = tempDoc.getPackageName(); } 
          catch(InvalidPackageException e) { _packageName = null; }
          return tempDoc;
        }
        
        public void saveDocInfo(DefinitionsDocument doc) {



          _loc = doc.getCurrentLocation();
          _list = doc.getDocumentListeners();
          _finalListeners = doc.getFinalizationListeners();
          
          
          _positions.clear();
          _positions = doc.getWrappedPositionOffsets();
        }
        
        public void addDocumentListener(DocumentListener dl) {
          ArrayList<DocumentListener> tmp = new ArrayList<DocumentListener>();
          for (DocumentListener l: _list) { if (dl != l) tmp.add(l); }
          tmp.add(dl);
          _list = tmp.toArray(new DocumentListener[tmp.size()]);
        }
        public String toString() { return ConcreteOpenDefDoc.this.toString(); }
      };
    }
    
    public int getInitialVerticalScroll()   { return _initVScroll; }
    public int getInitialHorizontalScroll() { return _initHScroll; }
    public int getInitialSelectionStart()   { return _initSelStart; }
    public int getInitialSelectionEnd()     { return _initSelEnd; }
    
    void setPackage(String pack)   { _packageName = pack; }
    void setInitialVScroll(int i)  { _initVScroll = i; }
    void setInitialHScroll(int i)  { _initHScroll = i; }
    void setInitialSelStart(int i) { _initSelStart = i; }
    void setInitialSelEnd(int i)   { _initSelEnd = i; }
      
    
    public void updateModifiedSinceSave() { getDocument().updateModifiedSinceSave(); }

    
    protected DefinitionsDocument getDocument() {


      try { return _cacheAdapter.getDocument(); } 
      catch(IOException ioe) { 

        try {
          _notifier.documentNotFound(this, _file);
          final String path = fixPathForNavigator(getFile().getCanonicalFile().getCanonicalPath());
          Utilities.invokeAndWait(new SRunnable() {
            public void run() { _documentNavigator.refreshDocument(ConcreteOpenDefDoc.this, path); }
          });
          return _cacheAdapter.getDocument(); 
        }
        catch(Throwable t) { throw new UnexpectedException(t); }
      }
    }

    
    public String getFirstTopLevelClassName() throws ClassNameNotFoundException {
      return getDocument().getFirstTopLevelClassName();
    }

    
    public boolean isUntitled() { return _file == null; }

    
    public File getFile() throws FileMovedException {
        if (_file == null) return null;
        if (_file.exists()) return _file;
        else throw new FileMovedException(_file, "This document's file has been moved or deleted.");
    }
    
    
    public boolean fileExists() { return _file != null && _file.exists(); }
    
    
    
    public File file() { return _file; }
    
    
    public boolean verifyExists() {

      if (fileExists()) return true;
      
      try {
        _notifier.documentNotFound(this, _file);
        File f = getFile();
        if (f == null) return false;
        String path = fixPathForNavigator(getFile().getCanonicalPath());
        _documentNavigator.refreshDocument(this, path);
        return true;
      } 
      catch(Throwable t) { return false; }

    }

    
    public String getFilename() {
      if (_file == null) return "(Untitled)";
      return _file.getName();
    }

    
    public String getName() {
      String fileName = getFilename();
      if (isModifiedSinceSave()) fileName = fileName + "*";
      else fileName = fileName + "  ";  
      return fileName;
    }

    
    public boolean saveFile(FileSaveSelector com) throws IOException {


      if (isUntitled()) return saveFileAs(com);
      
      if (! isModifiedSinceSave()) return true;
      
      
      FileSaveSelector realCommand = com;
      try {
        final File file = getFile();

        if (file != null) {
          realCommand = new TrivialFSS(file);

        }
      }
      catch (FileMovedException fme) {
        
        if (com.shouldSaveAfterFileMoved(this, fme.getFile())) realCommand = com;
        else return false;
        
      }

      return saveFileAs(realCommand);
    }

    
    public boolean saveFileAs(FileSaveSelector com) throws IOException {
      try {
        final OpenDefinitionsDocument openDoc = this;

        final File file = com.getFile();

        OpenDefinitionsDocument otherDoc = _getOpenDocument(file);

        
        boolean openInOtherDoc = ((otherDoc != null) && (openDoc != otherDoc));
        
        
        if (openInOtherDoc) {
          boolean shouldOverwrite = com.warnFileOpen(file);
          if (! shouldOverwrite) return true; 
        }
        
        if (! file.exists() || com.verifyOverwrite()) {  
          


          
          if (! file.getCanonicalFile().getName().equals(file.getName())) file.renameTo(file);
          
          
          
          if (file.getAbsolutePath().indexOf("#") != -1) _notifier.filePathContainsPound();
          
          

          FileOps.saveFile(new FileOps.DefaultFileSaver(file) {
            public void saveTo(OutputStream os) throws IOException {
              DefinitionsDocument doc = getDocument();
              try { 
                doc.acquireReadLock();  
                _editorKit.write(os, doc, 0, doc.getLength());
                doc.releaseReadLock();

              } 
              catch (BadLocationException docFailed) { throw new UnexpectedException(docFailed); }
            }
          });
          
          resetModification();
          setFile(file);
          
          try {
            
            
            _packageName = getDocument().getPackageName();
          } 
          catch(InvalidPackageException e) { _packageName = null; }
          getDocument().setCachedClassFile(null);
          checkIfClassFileInSync();
          

          _notifier.fileSaved(openDoc);
          
          
          addDocToClassPath(this);
          
          
          _documentNavigator.refreshDocument(this, fixPathForNavigator(file.getCanonicalPath()));
        }
        return true;
      }
      catch (OperationCanceledException oce) {
        
        
        return false;
      }
    }
  
    
    public void resetModification() {
      getDocument().resetModification();
      if (_file != null) _timestamp = _file.lastModified();
    }
    
    
    public void setFile(File file) {
      _file = file;

      
      if (_file != null) _timestamp = _file.lastModified();
    }
    
    
    public long getTimestamp() { return _timestamp; }
    
    
    public void preparePrintJob() throws BadLocationException, FileMovedException {
      String fileName = "(Untitled)";
      File sourceFile = getFile();
      if (sourceFile != null)  fileName = sourceFile.getAbsolutePath();

      _book = new DrJavaBook(getDocument().getText(), fileName, _pageFormat);
    }

    
    public void print() throws PrinterException, BadLocationException, FileMovedException {
      preparePrintJob();
      PrinterJob printJob = PrinterJob.getPrinterJob();
      printJob.setPageable(_book);
      if (printJob.printDialog()) printJob.print();
      cleanUpPrintJob();
    }

    
    public Pageable getPageable() throws IllegalStateException { return _book; }
    
    
    public void cleanUpPrintJob() { _book = null; }

    
    public void startCompile() throws IOException { 
      throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
    }

    
    public void runMain() throws IOException, ClassNameNotFoundException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support running");
    }

    
    public void startJUnit() throws IOException, ClassNotFoundException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support unit testing");
    }

    
    public void generateJavadoc(FileSaveSelector saver) throws IOException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support javadoc");
    }
    
    
    public boolean isModifiedSinceSave() {
      
      if (_cacheAdapter != null && _cacheAdapter.isReady()) return getDocument().isModifiedSinceSave();
      else return false;
    }
    
    public void documentSaved() { _cacheAdapter.documentSaved(getFilename()); }
    
    public void documentModified() { _cacheAdapter.documentModified(); }
    
    public void documentReset() { _cacheAdapter.documentReset(); }
    
    
    public boolean isModifiedOnDisk() {
      boolean ret = false;
      try {
        getDocument().aquireReadLock();
        if (_file != null) ret = (_file.lastModified() > _timestamp);
      }
      finally { getDocument().releaseReadLock(); }
      return ret;
    }
    
    
    public boolean checkIfClassFileInSync() {
      
      if (isModifiedSinceSave()) {
        getDocument().setClassFileInSync(false);
        return false;
      }
      
      if (isUntitled()) return true;

      
      File classFile = getDocument().getCachedClassFile();
      if (classFile == null) {
        
        classFile = _locateClassFile();
        getDocument().setCachedClassFile(classFile);
        if (classFile == null) {
          
          getDocument().setClassFileInSync(false);
          return false;
        }
      }

      
      
      File sourceFile;
      try { sourceFile = getFile(); }
      catch (FileMovedException fme) {
        getDocument().setClassFileInSync(false);
        return false;
      }
      if ((sourceFile == null) || (sourceFile.lastModified() > classFile.lastModified())) {
        getDocument().setClassFileInSync(false);
        return false;
      }
      else {
        getDocument().setClassFileInSync(true);
        return true;
      }
    }

    
    private File _locateClassFile() {
      if (isUntitled()) return null;
      
      String className;
      try { className = getDocument().getQualifiedClassName(); }
      catch (ClassNameNotFoundException cnnfe) { return null;   }
      
      String ps = System.getProperty("file.separator");
      
      className = StringOps.replace(className, ".", ps);
      String fileName = className + ".class";
      
      
      ArrayList<File> roots = new ArrayList<File>();
      
      if (getBuildDirectory() != null) roots.add(getBuildDirectory());
      
      
      try { roots.add(getSourceRoot()); }
      catch (InvalidPackageException ipe) {
        try {
          File root = getFile().getParentFile();
          if (root != null) roots.add(root);
        }
        catch(NullPointerException e) { throw new UnexpectedException(e); }
        catch(FileMovedException fme) {
          
          File root = fme.getFile().getParentFile();
          if (root != null) roots.add(root);
        }
      }
      
      File classFile = getSourceFileFromPaths(fileName, roots);
      if (classFile != null) return classFile;
      
      
      String cp = System.getProperty("java.class.path");
      String pathSeparator = System.getProperty("path.separator");
      Vector<File> cpVector = new Vector<File>();
      int i = 0;
      while (i < cp.length()) {
        int nextSeparator = cp.indexOf(pathSeparator, i);
        if (nextSeparator == -1) {
          cpVector.add(new File(cp.substring(i, cp.length())));
          break;
        }
        cpVector.add(new File(cp.substring(i, nextSeparator)));
        i = nextSeparator + 1;
      }
      classFile = getSourceFileFromPaths(fileName, cpVector);
      
      if (classFile != null) return classFile;
      
      
      return getSourceFileFromPaths(fileName, DrJava.getConfig().getSetting(EXTRA_CLASSPATH));
    }

    
    public boolean revertIfModifiedOnDisk() throws IOException{
      final OpenDefinitionsDocument doc = this;
      if (isModifiedOnDisk()) {
        boolean shouldRevert = _notifier.shouldRevertFile(doc);
        if (shouldRevert) doc.revertFile();
        return shouldRevert;
      }
      return false;
    }
    
    
    public void close() {
      removeFromDebugger();
      _cacheAdapter.close();
    }

    public void revertFile() throws IOException {

      
      removeFromDebugger();

      final OpenDefinitionsDocument doc = this;

      try {
        File file = doc.getFile();
        if (file == null) throw new UnexpectedException("Cannot revert an Untitled file!");
        

        FileReader reader = new FileReader(file);
        doc.clear();

        _editorKit.read(reader, doc, 0);
        reader.close(); 

        resetModification();
        doc.checkIfClassFileInSync();
        setCurrentLocation(0);
        _notifier.fileReverted(doc);
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
    }

    
    public boolean canAbandonFile() {
      if (isModifiedSinceSave() || (_file != null && !_file.exists() && _cacheAdapter.isReady()))
        return _notifier.canAbandonFile(this);
      else return true;
    }
    
    
    public void quitFile() {
      if (isModifiedSinceSave() || (_file != null && !_file.exists() && _cacheAdapter.isReady()))
        _notifier.quitFile(this);
    }
    
    
    public int gotoLine(int line) {
      getDocument().gotoLine(line);
      return getDocument().getCurrentLocation();
    }

    
    public void setCurrentLocation(int location) { getDocument().setCurrentLocation(location); }

    
    public int getCurrentLocation() { return getDocument().getCurrentLocation(); }

    
    public int balanceBackward() { return getDocument().balanceBackward(); }

    
    public int balanceForward() { return getDocument().balanceForward(); }

    
    public Breakpoint getBreakpointAt(int offset) {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support debugger");
    }

    
    public void addBreakpoint( Breakpoint breakpoint) {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support debugger");
    }
    
    
    public void removeBreakpoint(Breakpoint breakpoint) {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support debugger");
    }
    
    
    public Vector<Breakpoint> getBreakpoints() {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support debugger");
    }
    
    
    public void clearBreakpoints() { 
      throw new UnsupportedOperationException("AbstractGlobalModel does not support debugger");
    }
    
   
    public void removeFromDebugger() {  }    
    
    
    public File getSourceRoot() throws InvalidPackageException {

      if (_packageName == null) _packageName = getPackageName();

      return _getSourceRoot(_packageName);
    }
    
    
    public String getPackageName() throws InvalidPackageException {
      if (isUntitled()) _packageName = "";
      else if (_packageName == null) _packageName = getDocument().getPackageName();
      return _packageName;
    }
    
    
    File _getSourceRoot(String packageName) throws InvalidPackageException {
      File sourceFile;
      try { 
        sourceFile = getFile();
        if (sourceFile == null) 
          throw new InvalidPackageException(-1, "Can not get source root for unsaved file. Please save.");
      }
      catch (FileMovedException fme) {
        throw new 
          InvalidPackageException(-1, "File has been moved or deleted from its previous location. Please save.");
      }
      
      if (packageName.equals("")) { return sourceFile.getParentFile(); }
      
      ArrayList<String> packageStack = new ArrayList<String>();
      int dotIndex = packageName.indexOf('.');
      int curPartBegins = 0;
      
      while (dotIndex != -1) {
        packageStack.add(packageName.substring(curPartBegins, dotIndex));
        curPartBegins = dotIndex + 1;
        dotIndex = packageName.indexOf('.', dotIndex + 1);
      }
      
      
      packageStack.add(packageName.substring(curPartBegins));
      
      
      try {
        File parentDir = sourceFile.getCanonicalFile();
        while (! packageStack.isEmpty()) {
          String part = pop(packageStack);
          parentDir = parentDir.getParentFile();

          if (parentDir == null) throw new RuntimeException("parent dir is null!");

          
          if (! part.equals(parentDir.getName())) {
            String msg = "The source file " + sourceFile.getAbsolutePath() +
              " is in the wrong directory or in the wrong package. " +
              "The directory name " + parentDir.getName() +
              " does not match the package component " + part + ".";

            throw new InvalidPackageException(-1, msg);
          }
        }

        
        
        parentDir = parentDir.getParentFile();
        if (parentDir == null) {
          throw new RuntimeException("parent dir of first component is null?!");
        }

        return parentDir;
      }
      catch (IOException ioe) {
        String msg = "Could not locate directory of the source file: " + ioe;
        throw new InvalidPackageException(-1, msg);
      }
    }
    
    public String toString() { return getFilename(); }
    
    
    public int compareTo(OpenDefinitionsDocument o) { return _id - o.id(); }
    
    
    public void addDocumentListener(DocumentListener listener) {
      if (_cacheAdapter.isReady()) getDocument().addDocumentListener(listener);
      else _cacheAdapter.getReconstructor().addDocumentListener(listener);
    }
    
    List<UndoableEditListener> _undoableEditListeners = new LinkedList<UndoableEditListener>();
    
    public void addUndoableEditListener(UndoableEditListener listener) {
      _undoableEditListeners.add(listener);
      getDocument().addUndoableEditListener(listener);
    }
    
    public void removeUndoableEditListener(UndoableEditListener listener) {
      _undoableEditListeners.remove(listener);
      getDocument().removeUndoableEditListener(listener);
    }
    
    public UndoableEditListener[] getUndoableEditListeners() {
      return getDocument().getUndoableEditListeners();
    }
    
    public Position createPosition(int offs) throws BadLocationException {
      return getDocument().createPosition(offs);
    }
    
    public Element getDefaultRootElement() { return getDocument().getDefaultRootElement(); }
    
    public Position getEndPosition() { return getDocument().getEndPosition(); }
    
    public int getLength() { return getDocument().getLength(); }
    
    public Object getProperty(Object key) { return getDocument().getProperty(key); }
    
    public Element[] getRootElements() { return getDocument().getRootElements(); }
    
    public Position getStartPosition() { return getDocument().getStartPosition(); }
    
    public String getText(int offset, int length) throws BadLocationException {
      return getDocument().getText(offset, length);
    }
    
    public void getText(int offset, int length, Segment txt) throws BadLocationException {
      getDocument().getText(offset, length, txt);
    }
    
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
      getDocument().insertString(offset, str, a);
    }
    
    public void append(String str, AttributeSet set) { getDocument().append(str, set); }
    
    public void append(String str, Style style) { getDocument().append(str, style); }
    
    public void putProperty(Object key, Object value) { getDocument().putProperty(key, value); }
    
    public void remove(int offs, int len) throws BadLocationException { getDocument().remove(offs, len); }
    
    public void removeDocumentListener(DocumentListener listener) {
      getDocument().removeDocumentListener(listener);
    }
    
    public void render(Runnable r) { getDocument().render(r); }
    
    
    
    
    public boolean undoManagerCanUndo() { return _cacheAdapter.isReady() && getUndoManager().canUndo(); }
    
    public boolean undoManagerCanRedo() { return _cacheAdapter.isReady() && getUndoManager().canRedo(); }
    
    
    public CompoundUndoManager getUndoManager() { return getDocument().getUndoManager(); }
    
    public int getLineStartPos(int pos) { return getDocument().getLineStartPos(pos); }
    
    public int getLineEndPos(int pos) { return getDocument().getLineEndPos(pos); }
    
    public int commentLines(int selStart, int selEnd) { return getDocument().commentLines(selStart, selEnd); }
    
    public int uncommentLines(int selStart, int selEnd) {
      return getDocument().uncommentLines(selStart, selEnd);
    }
    
    public void indentLines(int selStart, int selEnd) { getDocument().indentLines(selStart, selEnd); }
    
    public int getCurrentCol() { return getDocument().getCurrentCol(); }
    
    public boolean getClassFileInSync() { return getDocument().getClassFileInSync(); }
    
    public int getIntelligentBeginLinePos(int currPos) throws BadLocationException {
      return getDocument().getIntelligentBeginLinePos(currPos);
    }
    
    public int getOffset(int lineNum) { return getDocument().getOffset(lineNum); }
    
    public String getQualifiedClassName() throws ClassNameNotFoundException {
      return getDocument().getQualifiedClassName();
    }
    
    public String getQualifiedClassName(int pos) throws ClassNameNotFoundException {
      return getDocument().getQualifiedClassName(pos);
    }
    
    public ReducedModelState getStateAtCurrent() { return getDocument().getStateAtCurrent(); }
    
    public void resetUndoManager() {
      
      if (_cacheAdapter.isReady()) getDocument().resetUndoManager();
    }
      
    public File getCachedClassFile() { return getDocument().getCachedClassFile(); }
    
    public DocumentListener[] getDocumentListeners() { return getDocument().getDocumentListeners(); }
    
    
    
    public void setTab(String tab, int pos) { getDocument().setTab(tab,pos); }
    
    public int getWhiteSpace() { return getDocument().getWhiteSpace(); }
    
    public boolean posInParenPhrase(int pos) { return getDocument().posInParenPhrase(pos); }
    
    public boolean posInParenPhrase() { return getDocument().posInParenPhrase(); }
    
    public int findPrevNonWSCharPos(int pos) throws BadLocationException {
      return getDocument().findPrevNonWSCharPos(pos);
    }
    
    public int getFirstNonWSCharPos(int pos) throws BadLocationException {
      return getDocument().getFirstNonWSCharPos(pos);
    }
    
    public int getFirstNonWSCharPos(int pos, boolean acceptComments) throws BadLocationException {
      return getDocument().getFirstNonWSCharPos(pos, acceptComments);
    }
    
    public int getFirstNonWSCharPos (int pos, char[] whitespace, boolean acceptComments) 
      throws BadLocationException {
      return getDocument().getFirstNonWSCharPos(pos, whitespace, acceptComments);
    }
    
    public int getLineFirstCharPos(int pos) throws BadLocationException {
      return getDocument().getLineFirstCharPos(pos);
    }
    
    public int findCharOnLine(int pos, char findChar) { 
      return getDocument().findCharOnLine(pos, findChar);
    }
    
    public String getIndentOfCurrStmt(int pos) throws BadLocationException {
      return getDocument().getIndentOfCurrStmt(pos);
    }
    
    public String getIndentOfCurrStmt(int pos, char[] delims) throws BadLocationException {
      return getDocument().getIndentOfCurrStmt(pos, delims);
    }
    
    public String getIndentOfCurrStmt(int pos, char[] delims, char[] whitespace) throws BadLocationException {
      return getDocument().getIndentOfCurrStmt(pos, delims, whitespace);
    }
    
    public void indentLines(int selStart, int selEnd, int reason, ProgressMonitor pm) throws OperationCanceledException {
      getDocument().indentLines(selStart, selEnd, reason, pm);
    }     
    
    public int findPrevCharPos(int pos, char[] whitespace) throws BadLocationException {
      return getDocument().findPrevCharPos(pos, whitespace);
    }
    
    public boolean findCharInStmtBeforePos(char findChar, int position) {
      return getDocument().findCharInStmtBeforePos(findChar, position);
    }
    
    public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException {
      return getDocument().findPrevDelimiter(pos, delims);
    }
    
    public int findPrevDelimiter(int pos, char[] delims, boolean skipParenPhrases) throws BadLocationException {
      return getDocument().findPrevDelimiter(pos, delims, skipParenPhrases);
    }
    
    public void resetReducedModelLocation() { getDocument().resetReducedModelLocation(); }
    
    public ReducedModelState stateAtRelLocation(int dist) { return getDocument().stateAtRelLocation(dist); }
    
    public IndentInfo getIndentInformation() { return getDocument().getIndentInformation(); }
    
    public void move(int dist) { getDocument().move(dist); }
    
    public Vector<HighlightStatus> getHighlightStatus(int start, int end) {
      return getDocument().getHighlightStatus(start, end);
    }
    
    public void setIndent(int indent) { getDocument().setIndent(indent); }
    
    public int getIndent() { return getDocument().getIndent(); }
    
    
    
    
    public void addFinalizationListener(FinalizationListener<DefinitionsDocument> fl) {
      getDocument().addFinalizationListener(fl);
    }
    
    public List<FinalizationListener<DefinitionsDocument>> getFinalizationListeners() {
      return getDocument().getFinalizationListeners();
    }
    
    
    public Font getFont(AttributeSet attr) { return getDocument().getFont(attr); }
    
    public Color getBackground(AttributeSet attr) { return getDocument().getBackground(attr); }
    
    public Color getForeground(AttributeSet attr) { return getDocument().getForeground(attr); }
    
    public Element getCharacterElement(int pos) { return getDocument().getCharacterElement(pos); }
    
    public Element getParagraphElement(int pos) { return getDocument().getParagraphElement(pos); }
    
    public Style getLogicalStyle(int p) { return getDocument().getLogicalStyle(p); }
    
    public void setLogicalStyle(int pos, Style s) { getDocument().setLogicalStyle(pos, s); }
    
    public void setCharacterAttributes(int offset, int length, AttributeSet s, boolean replace) {
      getDocument().setCharacterAttributes(offset, length, s, replace); 
    }
    
    public void setParagraphAttributes(int offset, int length, AttributeSet s, boolean replace) {
      getDocument().setParagraphAttributes(offset, length, s, replace); 
    }
    
    public Style getStyle(String nm) { return getDocument().getStyle(nm); }
    
    public void removeStyle(String nm) { getDocument().removeStyle(nm); }
    
    public Style addStyle(String nm, Style parent) { return getDocument().addStyle(nm, parent); }
    
    public String getText() {
      DefinitionsDocument doc = getDocument();
      doc.acquireReadLock();
      try { return doc.getText(0, doc.getLength()); }
      catch(BadLocationException e) { throw new UnexpectedException(e); }
      finally { releaseReadLock(); }
    }
    
    public void clear() {
      DefinitionsDocument doc = getDocument();
      doc.acquireWriteLock();
      try { doc.remove(0, doc.getLength()); }
      catch(BadLocationException e) { throw new UnexpectedException(e); }
      finally { releaseWriteLock(); }
    }
    
    
    
    
    
    public void acquireReadLock() { getDocument().readLock(); }
    
    
    public void releaseReadLock() { getDocument().readUnlock(); }
    
    
    public void acquireWriteLock() { getDocument().acquireWriteLock(); }
    
    
    public void releaseWriteLock() { getDocument().releaseWriteLock(); }
    
    
    public int getNumberOfLines() { return getDefaultRootElement().getElementIndex(getEndPosition().getOffset()-1); }
  } 

  private static class TrivialFSS implements FileSaveSelector {
    private File _file;
    private TrivialFSS(File file) { _file = file; }
    public File getFile() throws OperationCanceledException { return _file; }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() { return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
  }
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument() { return new ConcreteOpenDefDoc(); }
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(File f) throws IOException { return new ConcreteOpenDefDoc(f); }
  
  
  protected OpenDefinitionsDocument _getOpenDocument(File file) {
    
    OpenDefinitionsDocument[] docs;
    
    synchronized(_documentsRepos) { docs = _documentsRepos.toArray(new OpenDefinitionsDocument[0]); }
    for (OpenDefinitionsDocument doc: docs) {
      try {
        File thisFile = null;
        try { thisFile = doc.getFile(); }
        catch (FileMovedException fme) { thisFile = fme.getFile(); } 
        finally {
          
          if (thisFile != null) {
            try {
              
              if (thisFile.getCanonicalFile().equals(file.getCanonicalFile())) return doc;
            }
            catch (IOException ioe) {
              
              if (thisFile.equals(file)) return doc;
            }
          }
        }
      }
      catch (IllegalStateException ise) {  }
    }
    return null;
  }

  public List<OpenDefinitionsDocument> getNonProjectDocuments() {
    List<OpenDefinitionsDocument> allDocs = getOpenDefinitionsDocuments();
    List<OpenDefinitionsDocument> projectDocs = new LinkedList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument tempDoc : allDocs) {
      if (!tempDoc.isInProjectPath()) projectDocs.add(tempDoc);
    }
    return projectDocs;
  }
  
  
  public List<OpenDefinitionsDocument> getProjectDocuments() {
    List<OpenDefinitionsDocument> allDocs = getOpenDefinitionsDocuments();
    List<OpenDefinitionsDocument> projectDocs = new LinkedList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument tempDoc : allDocs)
      if (tempDoc.isInProjectPath() || tempDoc.isAuxiliaryFile()) projectDocs.add(tempDoc);
    return projectDocs;
  }
  
  public String fixPathForNavigator(String path) throws IOException {
    String parent = path.substring(0, path.lastIndexOf(File.separator));
    String topLevelPath;
    String rootPath = getProjectRoot().getCanonicalPath();
    
    if (! parent.equals(rootPath) && ! parent.startsWith(rootPath + File.separator))
      
      return "";
    else 
      return parent.substring(rootPath.length());
  }
  
  
  private OpenDefinitionsDocument _rawOpenFile(File file) throws IOException, AlreadyOpenException{
    OpenDefinitionsDocument openDoc = _getOpenDocument(file);
    if (openDoc != null) throw new AlreadyOpenException(openDoc);
    final ConcreteOpenDefDoc doc = _createOpenDefinitionsDocument(file);
    if (file instanceof DocFile) {
      DocFile df = (DocFile)file;
      Pair<Integer,Integer> scroll = df.getScroll();
      Pair<Integer,Integer> sel = df.getSelection();
      doc.setPackage(df.getPackage());
      doc.setInitialVScroll(scroll.getFirst());
      doc.setInitialHScroll(scroll.getSecond());
      doc.setInitialSelStart(sel.getFirst());
      doc.setInitialSelEnd(sel.getSecond());
    }
    return doc;
  }
  
  
  protected static <T> T pop(ArrayList<T> stack) { return stack.remove(stack.size() - 1); }
  
  
  protected void addDocToNavigator(final OpenDefinitionsDocument doc) {
    Utilities.invokeLater(new SRunnable() {
      public void run() {
        try {
          if (doc.isUntitled()) _documentNavigator.addDocument(doc);
          else {
            String path = doc.getFile().getCanonicalPath();
            _documentNavigator.addDocument(doc, fixPathForNavigator(path)); 
          }
        }
        catch(IOException e) { _documentNavigator.addDocument(doc); }
      }});
      synchronized(_documentsRepos) { _documentsRepos.add(doc); }
  }
  
  
  protected void addDocToClassPath(OpenDefinitionsDocument doc) {  }
  
  
  public OpenDefinitionsDocument _openFile(File file) throws IOException, AlreadyOpenException {
    
    OpenDefinitionsDocument doc = _rawOpenFile(file);
    _completeOpenFile(doc);
    return doc;
  }
  
  private void _completeOpenFile(OpenDefinitionsDocument d) {
      addDocToNavigator(d);
      addDocToClassPath(d);
      
      try {
        File f = d.getFile();
        if (!inProject(f) && isInProjectPath(d)) {
          setProjectChanged(true);
        }
      } catch(FileMovedException fme) {
        
      }
      
      _notifier.fileOpened(d);
  }
  
  private static class BackUpFileOptionListener implements OptionListener<Boolean> {
    public void optionChanged (OptionEvent<Boolean> oe) {
      Boolean value = oe.value;
      FileOps.DefaultFileSaver.setBackupsEnabled(value.booleanValue());
    }
  }



  
  public OpenDefinitionsDocument getActiveDocument() {return  _activeDocument; }
  
  
  
  public void setActiveDocument(final OpenDefinitionsDocument doc) {
    
    


    
    try {
      Utilities.invokeAndWait(new SRunnable() {  
        public void run() { _documentNavigator.setActiveDoc(doc); }
      });
    }
    catch(Exception e) { throw new UnexpectedException(e); } 
  }
  
  public Container getDocCollectionWidget() { return _documentNavigator.asContainer(); }
  
  
  public void setActiveNextDocument() {
    OpenDefinitionsDocument key = _activeDocument;
    OpenDefinitionsDocument nextKey = _documentNavigator.getNext(key);
    if (key != nextKey) setActiveDocument(nextKey);
    else setActiveDocument(_documentNavigator.getFirst());
    
  }

  
  public void setActivePreviousDocument() {
    OpenDefinitionsDocument key = _activeDocument;
    OpenDefinitionsDocument prevKey = _documentNavigator.getPrevious(key);
    if (key != prevKey) setActiveDocument(prevKey);
    else setActiveDocument(_documentNavigator.getLast());
      
  }

  

  
  private boolean _hasOneEmptyDocument() {
    return getOpenDefinitionsDocumentsSize() == 1 && _activeDocument.isUntitled() &&
            ! _activeDocument.isModifiedSinceSave();
  }

  
  private void _ensureNotEmpty() {
    if ((!_isClosingAllDocs) && (getOpenDefinitionsDocumentsSize() == 0)) newFile(getMasterWorkingDirectory());
  }
  
  
  private void _ensureNotActive(List<OpenDefinitionsDocument> docs) {
    if (docs.contains(getActiveDocument())) {
      
      IDocumentNavigator<OpenDefinitionsDocument> nav = getDocumentNavigator();
      
      OpenDefinitionsDocument item = docs.get(docs.size()-1);
      OpenDefinitionsDocument nextActive = nav.getNext(item);
      if (!nextActive.equals(item)) {
        setActiveDocument(nextActive); 
        return;
      }
      
      item = docs.get(0);
      nextActive = nav.getPrevious(item);
      if (!nextActive.equals(item)) { 
        setActiveDocument(nextActive);
        return;
      }
      
      throw new RuntimeException("No document to set active before closing");
    }
  }
  
  
  public void setActiveFirstDocument() {
    List<OpenDefinitionsDocument> docs = getOpenDefinitionsDocuments();

    
    setActiveDocument(docs.get(0));
  }
  
  private void _setActiveDoc(INavigatorItem idoc) {
    synchronized (this) { _activeDocument = (OpenDefinitionsDocument) idoc; }
    refreshActiveDocument();
  }
  
  
  public void refreshActiveDocument() {
    try {
      _activeDocument.checkIfClassFileInSync();
      
      _notifier.activeDocumentChanged(_activeDocument);
    } catch(DocumentClosedException dce) {  }
  }
}
