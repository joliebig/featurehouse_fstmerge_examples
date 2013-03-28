
package edu.rice.cs.drjava.model;

import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.WeakHashMap;

import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.ProgressMonitor;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.DrJavaRoot;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;
import edu.rice.cs.drjava.model.cache.DCacheAdapter;
import edu.rice.cs.drjava.model.cache.DDReconstructor;
import edu.rice.cs.drjava.model.cache.DocumentCache ;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.debug.DebugBreakpointData;
import edu.rice.cs.drjava.model.debug.DebugException ;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.javadoc.JavadocModel;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.definitions.CompoundUndoManager;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.definitions.DefinitionsEditorKit;
import edu.rice.cs.drjava.model.definitions.DocumentUIListener ;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.definitions.indent.Indenter;
import edu.rice.cs.drjava.model.definitions.reducedmodel.HighlightStatus;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelControl;
import edu.rice.cs.drjava.model.definitions.reducedmodel.ReducedModelState;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.drjava.model.print.DrJavaBook;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel ;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.project.DocFile ;
import edu.rice.cs.drjava.project.DocumentInfoGetter;
import edu.rice.cs.drjava.project.MalformedProjectFileException;
import edu.rice.cs.drjava.project.ProjectFileIR;
import edu.rice.cs.drjava.project.ProjectFileParserFacade;
import edu.rice.cs.drjava.project.ProjectProfile;
import edu.rice.cs.drjava.ui.DrJavaErrorHandler;

import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.tuple.Pair;
import edu.rice.cs.plt.io.IOUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.collect.CollectUtil;
import edu.rice.cs.plt.lambda.LambdaUtil;
import edu.rice.cs.plt.lambda.Predicate;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.NullFile;
import edu.rice.cs.util.AbsRelFile;
import edu.rice.cs.util.OperationCanceledException;
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
import edu.rice.cs.util.swing.AsyncCompletionArgs ;
import edu.rice.cs.util.swing.AsyncTask;
import edu.rice.cs.util.swing.IAsyncProgress;
import edu.rice.cs.util.swing.DocumentIterator;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.util.text.AbstractDocumentInterface;
import edu.rice.cs.util.text.ConsoleDocument;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class AbstractGlobalModel implements SingleDisplayModel, OptionConstants, DocumentIterator {
  
  public static final Log _log = new Log("GlobalModel.txt", false);
  
  
  protected DocumentCache _cache;  
  
  static final String DOCUMENT_OUT_OF_SYNC_MSG =
    "Current document is out of sync with the Interactions Pane and should be recompiled!\n";
  
  static final String CLASSPATH_OUT_OF_SYNC_MSG =
    "Interactions Pane is out of sync with the current classpath and should be reset!\n";
  
  
  
  
  public void addAuxiliaryFile(OpenDefinitionsDocument doc) { _state.addAuxFile(doc.getRawFile()); }
  
  
  public void removeAuxiliaryFile(OpenDefinitionsDocument doc) { _state.remAuxFile(doc.getRawFile()); }
  
  
  public final GlobalEventNotifier _notifier = new GlobalEventNotifier();
  
  
  
  
  protected final DefinitionsEditorKit _editorKit = new DefinitionsEditorKit(_notifier);
  
  
  private final AbstractMap<File, OpenDefinitionsDocument> _documentsRepos = 
    new LinkedHashMap<File, OpenDefinitionsDocument>();
  
  
  
  
  protected final ConsoleDocument _consoleDoc;
  
  
  protected final InteractionsDJDocument _consoleDocAdapter;
  
  
  public static final int WRITE_DELAY = 50;
  
  
  protected volatile PageFormat _pageFormat = new PageFormat();
  
  
  private volatile OpenDefinitionsDocument _activeDocument;
  
  
  private volatile File _activeDirectory;
  
  
  private volatile boolean classPathChanged = false;
  
  
  protected volatile IDocumentNavigator<OpenDefinitionsDocument> _documentNavigator =
    new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().makeListNavigator();
  
  
  public GlobalEventNotifier getNotifier() { return _notifier; }
  
  
  protected final ConcreteRegionManager<Breakpoint> _breakpointManager;
  
  
  public RegionManager<Breakpoint> getBreakpointManager() { return _breakpointManager; }
  
  
  protected final ConcreteRegionManager<MovingDocumentRegion> _bookmarkManager;
  
  
  public RegionManager<MovingDocumentRegion> getBookmarkManager() { return _bookmarkManager; }
  
  
  protected final LinkedList<RegionManager<MovingDocumentRegion>> _findResultsManagers;
  
  
  public List<RegionManager<MovingDocumentRegion>> getFindResultsManagers() {
    return new LinkedList<RegionManager<MovingDocumentRegion>>(_findResultsManagers);
  }
  
  
  public RegionManager<MovingDocumentRegion> createFindResultsManager() {
    ConcreteRegionManager<MovingDocumentRegion> rm = new ConcreteRegionManager<MovingDocumentRegion>();
    _findResultsManagers.add(rm);

    return rm;
  }
  
  
  public void removeFindResultsManager(RegionManager<MovingDocumentRegion> rm) {
    _findResultsManagers.remove(rm);
  }
  
  
  protected final BrowserHistoryManager _browserHistoryManager;
  
  
  public BrowserHistoryManager getBrowserHistoryManager() { return _browserHistoryManager; }
  
  


  






  
  
  
  
  public AbstractGlobalModel() {
    _cache = new DocumentCache();
    
    _consoleDocAdapter = new InteractionsDJDocument();
    _consoleDoc = new ConsoleDocument(_consoleDocAdapter);
    
    _bookmarkManager = new ConcreteRegionManager<MovingDocumentRegion>();
    _findResultsManagers = new LinkedList<RegionManager<MovingDocumentRegion>>();
    _browserHistoryManager = new BrowserHistoryManager();
    
    _breakpointManager = new ConcreteRegionManager<Breakpoint>();
    
    









    
    _registerOptionListeners();
    
    setFileGroupingState(makeFlatFileGroupingState());
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.projectRunnableChanged(); } });
    _init();
  }
  
  private void _init() {
    
    
    final NodeDataVisitor<OpenDefinitionsDocument, Boolean>  _gainVisitor = 
      new NodeDataVisitor<OpenDefinitionsDocument, Boolean>() {
      public Boolean itemCase(OpenDefinitionsDocument doc, Object... p) {
        _setActiveDoc(doc);  

        

        final File oldDir = _activeDirectory;  
        final File dir = doc.getParentDirectory();  
        if (dir != null && ! dir.equals(oldDir)) {
          
          _activeDirectory = dir;
          _notifier.currentDirectoryChanged(_activeDirectory);
        }
        return Boolean.valueOf(true);
      }
      public Boolean fileCase(File f, Object... p) {
        if (! f.isAbsolute()) { 
          File root = _state.getProjectFile().getParentFile().getAbsoluteFile();
          f = new File(root, f.getPath());
        }
        _activeDirectory = f;  
        _notifier.currentDirectoryChanged(f);
        return Boolean.valueOf(true);
      }
      public Boolean stringCase(String s, Object... p) { return Boolean.valueOf(false); }
    };
    
    
    _documentNavigator.addNavigationListener(new INavigationListener<OpenDefinitionsDocument>() {
      public void gainedSelection(NodeData<? extends OpenDefinitionsDocument> dat, boolean modelInitiated) {
        dat.execute(_gainVisitor, modelInitiated); }
      public void lostSelection(NodeData<? extends OpenDefinitionsDocument> dat, boolean modelInitiated) {
       }
    });
    
    
    _documentNavigator.addFocusListener(new FocusListener() {
      public void focusGained(FocusEvent e) {


        Utilities.invokeLater(new Runnable() { public void run() { _notifier.focusOnDefinitionsPane(); } });
      }
      public void focusLost(FocusEvent e) { }
    });
    
    _ensureNotEmpty();
    setActiveFirstDocument();
    
    
    OptionListener<Integer> clipboardHistorySizeListener = new OptionListener<Integer>() {
      public void optionChanged(OptionEvent<Integer> oce) {
        ClipboardHistoryModel.singleton().resize(oce.value);
      }
    };
    DrJava.getConfig().addOptionListener(CLIPBOARD_HISTORY_SIZE, clipboardHistorySizeListener);
    ClipboardHistoryModel.singleton().resize(DrJava.getConfig().getSetting(CLIPBOARD_HISTORY_SIZE).intValue());
    
    
    OptionListener<Integer> browserHistoryMaxSizeListener = new OptionListener<Integer>() {
      public void optionChanged(OptionEvent<Integer> oce) {
        AbstractGlobalModel.this.getBrowserHistoryManager().setMaximumSize(oce.value);
      }
    };
    DrJava.getConfig().addOptionListener(BROWSER_HISTORY_MAX_SIZE, browserHistoryMaxSizeListener);
    getBrowserHistoryManager().setMaximumSize(DrJava.getConfig().getSetting(BROWSER_HISTORY_MAX_SIZE).intValue());
  }
  
  
  
  
  protected volatile FileGroupingState _state;
  
  
  public void setFileGroupingState(FileGroupingState state) {
    _state = state;
    _notifier.projectRunnableChanged();
    _notifier.projectBuildDirChanged();
    _notifier.projectWorkDirChanged();

  }
  
  protected FileGroupingState
    makeProjectFileGroupingState(File pr, String main, File bd, File wd, File project, File[] srcFiles, File[] auxFiles, 
                                 File[] excludedFiles, Iterable<AbsRelFile> cp, File cjf, int cjflags, boolean refresh, String manifest) {
    
    return new ProjectFileGroupingState(pr, main, bd, wd, project, srcFiles, auxFiles, excludedFiles, cp, cjf, cjflags,
                                        refresh, manifest);
  }
  
  
  public boolean isClassPathChanged() { return classPathChanged; }
  
  
  public void setClassPathChanged(boolean changed) {
    classPathChanged = changed;
  }
  
  
  public void setProjectChanged(boolean changed) {
    _state.setProjectChanged(changed);

  }
  
  
  public boolean isProjectChanged() { return _state.isProjectChanged(); }
  
  
  public boolean isProjectActive() { return _state.isProjectActive(); }
  
  
  public File getProjectFile() { return _state.getProjectFile(); }
  
  
  public File[] getProjectFiles() { return _state.getProjectFiles(); }
  
  
  public boolean inProject(File f) { return _state.inProject(f); }
  
  
  public boolean inProjectPath(OpenDefinitionsDocument doc) { return _state.inProjectPath(doc); }
  
  
  public void setMainClass(String f) {
    _state.setMainClass(f);
    _notifier.projectRunnableChanged();
    setProjectChanged(true);
  }
  
  
  public String getMainClass() { return _state.getMainClass(); }
  
  
  public File getMainClassContainingFile(){
    String path = getMainClass();
    
    if(path == null){
      return null;
    }
    
    if(path.toLowerCase().endsWith(".java")){
      return new File(getProjectFile().getParent(), path);
    }
    
    
    
    
    
    
    
    path = path.replace('.', File.separatorChar);
    File tempFile = new File(getProjectRoot(), path+".java");
    while(path.length() > 0){
      if(tempFile.exists()){
        return tempFile;
      }
      
      if(path.indexOf(File.separatorChar) == -1)
        break;
      
      path = path.substring(0, path.lastIndexOf(File.separatorChar));
      tempFile = new File(getProjectRoot(), path + ".java");
    }
    
    return null;
  }
  
  
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
  
  
  public boolean getAutoRefreshStatus() { return _state.getAutoRefreshStatus(); }
  
  
  public void setAutoRefreshStatus(boolean status) { _state.setAutoRefreshStatus(status); }
  
  
  public File getMasterWorkingDirectory() {
    File file;
    try {
      
      file = FileOps.getValidDirectory(DrJava.getConfig().getSetting(LAST_DIRECTORY));
    }
    catch (RuntimeException e) {
      
      DrJava.getConfig().setSetting(LAST_DIRECTORY, FileOps.NULL_FILE);
      file = FileOps.getValidDirectory(new File(System.getProperty("user.home", ".")));
    }
    
    DrJava.getConfig().setSetting(LAST_DIRECTORY, file);
    return file;
  }
  
  
  public File getWorkingDirectory() { return _state.getWorkingDirectory(); }
  
  
  public void setWorkingDirectory(File f) {
    _state.setWorkingDirectory(f);
    _notifier.projectWorkDirChanged();
    setProjectChanged(true);
    
    DrJava.getConfig().setSetting(LAST_INTERACTIONS_DIRECTORY, _state.getWorkingDirectory());
  }
  
  public void cleanBuildDirectory()  { _state.cleanBuildDirectory(); }
  
  public List<File> getClassFiles() { return _state.getClassFiles(); }
  
  
  protected static String getPackageName(String classname) {
    int index = classname.lastIndexOf(".");
    if (index != -1) return classname.substring(0, index);
    else return "";
  }
  
  class ProjectFileGroupingState implements FileGroupingState {
    
    volatile File _projRoot;
    volatile String _mainClass;
    volatile File _buildDir;
    volatile File _workDir;
    volatile File _projectFile;
    final File[] _projectFiles;
    volatile ArrayList<File> _auxFiles;            
    private volatile ArrayList<File> _exclFiles;   
    volatile Iterable<AbsRelFile> _projExtraClassPath;
    private boolean _isProjectChanged = false;
    volatile File _createJarFile;
    volatile int _createJarFlags;
    volatile boolean _autoRefreshStatus;
    
    volatile String _manifest = null;
    
    HashSet<String> _projFilePaths = new HashSet<String>();
    
    
    ProjectFileGroupingState(File project) {
      this(project.getParentFile(), null, null, null, project, new File[0], new File[0], new File[0], 
           IterUtil.<AbsRelFile>empty(), null, 0, false, null);
    }
    
    ProjectFileGroupingState(File pr, String main, File bd, File wd, File project, File[] srcFiles, File[] auxFiles, 
                             File[] excludedFiles, Iterable<AbsRelFile> cp, File cjf, int cjflags, boolean refreshStatus, String customManifest) {
      _projRoot = pr;
      _mainClass = main;
      _buildDir = bd;
      _workDir = wd;
      _projectFile = project;
      _projectFiles = srcFiles;
      _auxFiles = new ArrayList<File>(auxFiles.length);
      for(File f: auxFiles) { _auxFiles.add(f); }
      _exclFiles = new ArrayList<File>(excludedFiles.length);
      for(File f: excludedFiles) { _exclFiles.add(f); }
      _projExtraClassPath = cp;
      
      if (_projectFiles != null) {
        try { for (File file : _projectFiles) { _projFilePaths.add(file.getCanonicalPath()); } }
        catch(IOException e) {  }
      }
      
      _createJarFile = cjf;
      _createJarFlags = cjflags;
      _autoRefreshStatus = refreshStatus;
      _manifest = customManifest;
    }
    
    public boolean isProjectActive() { return true; }
    
    
    public boolean inProjectPath(OpenDefinitionsDocument doc) {
      if (doc.isUntitled()) return false;
      
      
      File f;
      try { f = doc.getFile(); }
      catch(FileMovedException fme) { f = fme.getFile(); }
      return inProjectPath(f);
    }
    
    
    public boolean inProjectPath(File f) { return IOUtil.isMember(f, getProjectRoot()); }
    
    
    public File getProjectFile() { return _projectFile; }
    
    public boolean inProject(File f) {
      String path;
      
      if (isUntitled(f) || ! inProjectPath(f)) return false;
      try {
        path = f.getCanonicalPath();
        return _projFilePaths.contains(path);
      }
      catch(IOException ioe) { return false; }
    }
    
    public File[] getProjectFiles() { return _projectFiles; }
    
    public File getProjectRoot() {
      if (_projRoot == null || _projRoot.equals( FileOps.NULL_FILE)) return _projectFile.getParentFile();

      return _projRoot;
    }
    
    public File getBuildDirectory() { return _buildDir; }
    
    public File getWorkingDirectory() {
      try {
        if (_workDir == null || _workDir == FileOps.NULL_FILE) {
          File parentDir = _projectFile.getParentFile();
          if (parentDir != null) {
            return parentDir.getCanonicalFile(); 
          }
          else return new File(System.getProperty("user.dir"));
        }
        return _workDir.getCanonicalFile();
      }
      catch(IOException e) {  }
      return _workDir.getAbsoluteFile();
    }
    
    
    public void setProjectFile(File f) { _projectFile = f; }
    
    public void setProjectRoot(File f) {
      _projRoot = f;

    }
    
    
    public void addAuxFile(File f) {
      synchronized(_auxFiles) {
        if (_auxFiles.add(f)) setProjectChanged(true);

      }
    }
    
    
    public void remAuxFile(File file) {
      synchronized(_auxFiles) { 
        if (_auxFiles.remove(file)) setProjectChanged(true);
      }
    }
    
    public void addExcludedFile(File f) {
      if(f == null) return;
      if (isAlreadyOpen(f)) return; 
      synchronized(_exclFiles) {
        if (_exclFiles.add(f)) setProjectChanged(true);

      }
    }
    
    public void removeExcludedFile(File f) {
      synchronized(_exclFiles) {
        for(int i = 0; i < _exclFiles.size(); i++) {
          try {
            if(_exclFiles.get(i).getCanonicalPath().equals(f.getCanonicalPath())) {
              _exclFiles.remove(i);
              setProjectChanged(true);
            }
          }
          catch(IOException e) {}
        }
      }
    }
    
    public File[] getExclFiles() { return _exclFiles.toArray(new File[_exclFiles.size()]); }
    
    public void setExcludedFiles(File[] fs) {
      if(fs == null) return;
      synchronized(_exclFiles) {
        _exclFiles.clear();
        for(File f: fs) { addExcludedFile(f); }
        setProjectChanged(true);
      }
    }
    
    public void setBuildDirectory(File f) { _buildDir = f; }
    
    public void setWorkingDirectory(File f) { _workDir = f; }
    
    public String getMainClass() { return _mainClass; }
    
    public void setMainClass(String f) { _mainClass = f; }
    
    public void setCreateJarFile(File f) { _createJarFile = f; }
    
    public File getCreateJarFile() { return _createJarFile; }
    
    public void setCreateJarFlags(int f) { _createJarFlags = f; }
    
    public int getCreateJarFlags() { return _createJarFlags; }
    
    public boolean isProjectChanged() { return _isProjectChanged; }
    
    public void setProjectChanged(boolean changed) { _isProjectChanged = changed; }
    
    public boolean isAuxiliaryFile(File f) {
      String path;
      
      if (isUntitled(f)) return false;  
      
      try { path = f.getCanonicalPath();}
      catch(IOException ioe) { return false; }
      
      synchronized(_auxFiles) {
        for (File file : _auxFiles) {
          try { if (file.getCanonicalPath().equals(path)) return true; }
          catch(IOException ioe) {  }
        }
        return false;
      }
    }
    
    public boolean isExcludedFile(File f) {
      String path;
      if (isUntitled(f)) return false;  
      
      try { path = f.getCanonicalPath();}
      catch(IOException ioe) { return false; }
      
      synchronized(_exclFiles) {
        for (File file : _exclFiles) {
          try { if (file.getCanonicalPath().equals(path)) return true; }
          catch(IOException ioe) {  }
        }
        return false;
      }
    }
    
    public boolean getAutoRefreshStatus() { return _autoRefreshStatus; }
    public void setAutoRefreshStatus(boolean status) { _autoRefreshStatus = status; }
    
    
    
    public void cleanBuildDirectory() {
      File dir = this.getBuildDirectory ();
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
        if (file.isDirectory ()) {
          File[] children = file.listFiles(_filter);
          for (File child : children) {
            helper(child, accumulator);
            accumulator.add(file);
          }
        }
        else if ( file.getName().endsWith(".class")){
          accumulator.add(file);
        }
      }
    };    
    
    private AsyncTask<List<File>,List<File>> _deleteFilesTask = new AsyncTask<List<File>,List<File>>("Delete Files") {
      public List<File> runAsync(List<File> filesToDelete, IAsyncProgress monitor) throws Exception {
        List<File> undeletableFiles = new ArrayList<File>();
        
        monitor.setMinimum (0);
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
      File dir = this.getBuildDirectory ();
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
        
        if (fs != null) { 
          for (File kid: fs) { getClassFilesHelper(kid, acc); }
        }
        
      } else if (f.getName().endsWith(".class")) acc.add(f);
    }    
    
    
    
    public Iterable<AbsRelFile> getExtraClassPath() { return _projExtraClassPath; }
    public void setExtraClassPath(Iterable<AbsRelFile> cp) { 
      _projExtraClassPath = cp; 
      setClassPathChanged(true);
    }
  
    
    public String getCustomManifest() { return _manifest; }
    public void setCustomManifest(String manifest) { _manifest = manifest; }
  }
  
  protected FileGroupingState makeFlatFileGroupingState() { return new FlatFileGroupingState(); }
  
  class FlatFileGroupingState implements FileGroupingState {
    public File getBuildDirectory() { return FileOps.NULL_FILE; }
    public File getProjectRoot() { return getWorkingDirectory(); }
    public File getWorkingDirectory() {
      Iterable<File> roots = getSourceRootSet();
      if (!IterUtil.isEmpty(roots)) { return IterUtil.first(roots); }
      else {
        
        File file = FileOps.NULL_FILE;
        if (DrJava.getConfig().getSetting(STICKY_INTERACTIONS_DIRECTORY)) {
          try {
            
            file = FileOps.getValidDirectory(DrJava.getConfig().getSetting(LAST_INTERACTIONS_DIRECTORY));
          }
          catch (RuntimeException e) { file = FileOps.NULL_FILE; }
        }
        if (file == FileOps.NULL_FILE) {
          
          file = FileOps.getValidDirectory(new File(System.getProperty("user.home", ".")));
        }
        
        DrJava.getConfig().setSetting(LAST_INTERACTIONS_DIRECTORY, file);
        return file;
      }
    }
    public boolean isProjectActive() { return false; }
    public boolean inProjectPath(OpenDefinitionsDocument doc) { return false; }
    public boolean inProjectPath(File f) { return false; }
    public File getProjectFile() { return FileOps.NULL_FILE; }
    public void setBuildDirectory(File f) { }
    public void setProjectFile(File f) { }
    public void setProjectRoot(File f) { }
    public void addAuxFile(File f) { }
    public void remAuxFile(File f) { }
    public void setWorkingDirectory(File f) { }
    public File[] getProjectFiles() { return new File[0]; }
    public boolean inProject(File f) { return false; }
    public String getMainClass() { return null; }
    public void setMainClass(String f) { }
    public void setCreateJarFile(File f) { }
    public File getCreateJarFile() { return FileOps.NULL_FILE; }
    public void setCreateJarFlags(int f) { }
    public int getCreateJarFlags() { return 0; }
    public Iterable<AbsRelFile> getExtraClassPath() { return IterUtil.empty(); }
    public void setExtraClassPath(Iterable<AbsRelFile> cp) { }
    public boolean isProjectChanged() { return false; }
    public void setProjectChanged(boolean changed) {   }
    public boolean isAuxiliaryFile(File f) { return false; }
    public boolean isExcludedFile(File f) { return false; }
    public File[] getExclFiles() { return null; }
    public void addExcludedFile(File f) {}
    public void removeExcludedFile(File f) {}
    public void setExcludedFiles(File[] fs) {}
    public boolean getAutoRefreshStatus() {return false;}
    public void setAutoRefreshStatus(boolean b) {}
    
    public void cleanBuildDirectory() { }
    
    public List<File> getClassFiles() { return new LinkedList<File>(); }
    
    public String getCustomManifest(){ return null; }
    public void setCustomManifest(String manifest) {}
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
  
  
  public int getNumCompErrors() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
  }
  
  
  public void setNumCompErrors(int num) { 
    throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
  };
  
  
  public JUnitModel getJUnitModel() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support unit testing");
  }
  
  
  public JavadocModel getJavadocModel() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support javadoc");
  }
  
  public IDocumentNavigator<OpenDefinitionsDocument> getDocumentNavigator() { return _documentNavigator; }
  
  public void setDocumentNavigator(IDocumentNavigator<OpenDefinitionsDocument> newnav) { _documentNavigator = newnav; }
  
  
  public void toggleBookmark(int pos1, int pos2) { _toggleBookmark(pos1, pos2); }
  
  
  public void _toggleBookmark(int pos1, int pos2) {

    assert EventQueue.isDispatchThread();
    
    final OpenDefinitionsDocument doc = getActiveDocument();
    
    int startSel = Math.min(pos1, pos2);
    int endSel = Math.max(pos1, pos2);

    RegionManager<MovingDocumentRegion> bm = _bookmarkManager;
    if (startSel == endSel) {  
      endSel = doc._getLineEndPos(startSel);
      startSel = doc._getLineStartPos(startSel);
    }
    
    Collection<MovingDocumentRegion> conflictingRegions = bm.getRegionsOverlapping(doc, startSel, endSel);
    
    if (conflictingRegions.size() > 0) {
      for (MovingDocumentRegion cr: conflictingRegions) bm.removeRegion(cr);
    }
    else {
      MovingDocumentRegion newR = 
        new MovingDocumentRegion(doc, startSel, endSel, doc._getLineStartPos(startSel), doc._getLineEndPos(endSel));
      bm.addRegion(newR);
    }
  }
  
  
  public OpenDefinitionsDocument newFile(File parentDir) {

    final ConcreteOpenDefDoc doc = _createOpenDefinitionsDocument(new NullFile());
    doc.setParentDirectory(parentDir);
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
    
    final StringBuilder buf = new StringBuilder();
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
    buf.append ("* many \"testSomething\" methods in this class as you wish, and each\n");
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
    if (! closeUntitled) addToBrowserHistory();
    
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

    
    addDocToClassPath(odd);  
    setClassPathChanged(true);
    return odd;
  }
  
  
  public OpenDefinitionsDocument[] openFiles(FileOpenSelector com)
    throws IOException, OperationCanceledException, AlreadyOpenException {
    
    
    boolean closeUntitled = _hasOneEmptyDocument();
    if (! closeUntitled) addToBrowserHistory();
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
        OpenDefinitionsDocument d = _rawOpenFile(IOUtil.attemptCanonicalFile(f));
        
        retDocs.add(d);
        filesOpened.add(d);
        if(_state.isExcludedFile(f))
          _state.removeExcludedFile(f);
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
    
    if (filesNotFound.size() > 0)
      _notifier.filesNotFound( filesNotFound.toArray( new File[filesNotFound.size()] ) );
    
    if (! alreadyOpenDocuments.isEmpty()) {
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
    debug.logStart();
    
    final File[] sfiles =  getFilesInFolder(dir, rec); 
    if(sfiles == null) return;
    openFiles(new FileOpenSelector() { public File[] getFiles() { return sfiles; } });
    
    if (sfiles.length > 0 && _state.inProjectPath(dir)) setProjectChanged(true);
   
    debug.logEnd();
  }
  
  
  
  public File[] getFilesInFolder(File dir, boolean rec) throws IOException, OperationCanceledException, 
    AlreadyOpenException {
    
    if (dir == null || !dir.isDirectory()) return null; 
    
    Iterable<File> filesIterable;
    
    String extension = DrJavaRoot.LANGUAGE_LEVEL_EXTENSIONS[DrJava.getConfig().getSetting(LANGUAGE_LEVEL)];
    
    Predicate<File> match = LambdaUtil.and(IOUtil.IS_FILE, IOUtil.extensionFilePredicate(extension));
    if (rec) { filesIterable = IOUtil.listFilesRecursively(dir, match); }
    else { filesIterable = IOUtil.attemptListFilesAsIterable(dir, match); }
    List<File> files = CollectUtil.makeList(filesIterable);
    
    if (isProjectActive()) {
      Collections.sort(files, new Comparator<File>() {
        public int compare(File o1,File o2) {
          return - o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
        }
      });
    }
    else {
      Collections.sort(files, new Comparator<File>() {
        public int compare(File o1,File o2) {
          return - o1.getName().compareTo(o2.getName());
        }
      });
    }
    int ct = files.size();
    
    return files.toArray(new File[ct]);
  }
  
  
  public File[] getNewFilesInProject() {
    
    ArrayList<File> files = new ArrayList<File>();
    File projRoot = _state.getProjectRoot();
    if(projRoot == null)
      return null;
    File[] allFiles;
    try {
      allFiles = getFilesInFolder(projRoot, true);
    } catch(IOException e) { return null; }
    catch(OperationCanceledException e) { return null; }
    catch(AlreadyOpenException e) { return null; }
    
    for(File f : allFiles) {
      if(!isAlreadyOpen(f) && !_state.isExcludedFile(f)) {
        files.add(f);
      }
    }
    
    return files.toArray(new File[files.size()]);
  }
  
  
  public void openNewFilesInProject() {
    File[] newFiles = getNewFilesInProject();
    if (newFiles == null) return;
    try { _openFiles(newFiles); }
    catch(Exception e) {}
  }
  
  
  
  public void saveAllFiles(FileSaveSelector com) throws IOException {

    saveAllFilesHelper(com);
    refreshActiveDocument(); 
  }
  
  
  protected void saveAllFilesHelper(FileSaveSelector com) throws IOException {
    boolean first = true;
    boolean isProjActive = isProjectActive();
    
    List<OpenDefinitionsDocument> docsToWrite = getOpenDefinitionsDocuments();
    while(docsToWrite.size() > 0) {
      ArrayList<OpenDefinitionsDocument> readOnlyDocs = new ArrayList<OpenDefinitionsDocument>();
      for (final OpenDefinitionsDocument doc: docsToWrite) {  
        
        if (doc.isUntitled() && (isProjActive || ! doc.isModifiedSinceSave())) continue;
        try {
          final File docFile = doc.getFile();
          if (docFile == null || !docFile.exists() || docFile.canWrite()) {
            
            aboutToSaveFromSaveAll(doc);
            doc.saveFile(com);
          }
          else if (first) {
            
            readOnlyDocs.add(doc);
          }
        }
        catch(FileMovedException fme) {
          
          aboutToSaveFromSaveAll(doc);
          doc.saveFile(com);
        }
      }
      docsToWrite.clear();
      if (readOnlyDocs.size() > 0) {
        ArrayList<File> files = new ArrayList<File>();
        for(OpenDefinitionsDocument odd: readOnlyDocs) {
          try { 
            File roFile = odd.getFile();
            files.add(roFile);
          }
          catch(FileMovedException fme) {  }
        }
        File[] res = _notifier.filesReadOnly(files.toArray(new File[files.size()]));
        HashSet<File> rewriteFiles = new HashSet<File>(java.util.Arrays.asList(res));
        for(OpenDefinitionsDocument odd: readOnlyDocs) {
          File roFile = odd.getFile();
          if (rewriteFiles.contains(roFile)) {
            docsToWrite.add(odd);
            FileOps.makeWritable(roFile);
          }
        }
      }
      first = false;
    }
  }
  
  
  public void createNewProject(File projFile) { setFileGroupingState(new ProjectFileGroupingState(projFile)); }
  
  
  public void configNewProject() throws IOException {
    
    assert EventQueue.isDispatchThread();
    

    File projFile = getProjectFile();
    
    ProjectProfile builder = new ProjectProfile(projFile);
    
    
    File projectRoot = builder.getProjectRoot();
    

    

    
    for (OpenDefinitionsDocument doc: getOpenDefinitionsDocuments()) {
      
      File f = doc.getFile();
      
      if (!doc.isUntitled()) {
        if (IOUtil.isMember(f, projectRoot)) {
          DocFile file = new DocFile(f);
          file.setPackage(doc.getPackageName());  
          builder.addSourceFile(file);
        }
        else if ( doc.isAuxiliaryFile()) {
          DocFile file = new DocFile(f);
          file.setPackage(doc.getPackageName());  
          builder.addAuxiliaryFile(new DocFile(f));
        }
      }
    }
    
    
    builder.write();
    
    _loadProject(builder);
  }
  
  
  public ProjectProfile _makeProjectProfile(File file, HashMap<OpenDefinitionsDocument, DocumentInfoGetter> info) 
    throws IOException {    
    ProjectProfile builder = new ProjectProfile(file);
    
    
    File pr = getProjectRoot();
    if (pr != null) builder.setProjectRoot(pr);
    
    
    for (OpenDefinitionsDocument doc: getOpenDefinitionsDocuments()) {
      if (doc.inProjectPath()) {
        DocumentInfoGetter g = info.get(doc);
        builder.addSourceFile(g);
      }
      else if (doc.isAuxiliaryFile()) {
        DocumentInfoGetter g = info.get(doc);
        builder.addAuxiliaryFile(g);
      }
    }
    
    
    if (_documentNavigator instanceof JTreeSortNavigator<?>) {
      String[] paths = ((JTreeSortNavigator<?>)_documentNavigator).getCollapsedPaths();
      for (String s : paths) { builder.addCollapsedPath(s); }
    }
    
    Iterable<AbsRelFile> exCp = getExtraClassPath();
    if (exCp != null) {
      for (AbsRelFile f : exCp) { builder.addClassPathFile(f); }
    }

    
    
    File bd = getBuildDirectory();
    if (bd != FileOps.NULL_FILE) builder.setBuildDirectory(bd);
    
    
    File wd = getWorkingDirectory();  
    if (wd != FileOps.NULL_FILE) builder.setWorkingDirectory(wd);
    
    
    String mainClass = getMainClass();
    if (mainClass != null) builder.setMainClass(mainClass);
    
    
    File createJarFile = getCreateJarFile();
    if (createJarFile != null) builder.setCreateJarFile(createJarFile);
    
    int createJarFlags = getCreateJarFlags();
    if (createJarFlags != 0) builder.setCreateJarFlags (createJarFlags);
    
    
    ArrayList<DebugBreakpointData> l = new ArrayList<DebugBreakpointData>();  
    for (OpenDefinitionsDocument odd: _breakpointManager.getDocuments()) {
      for(Breakpoint bp: _breakpointManager.getRegions(odd)) { l.add(bp); }
    }
    builder.setBreakpoints(l);
    try { builder.setWatches(getDebugger().getWatches()); }
    catch(DebugException de) {  }
    
    
    builder.setBookmarks(_bookmarkManager.getFileRegions());
    
    builder.setAutoRefreshStatus(_state.getAutoRefreshStatus());
    
    
    for(File f: _state.getExclFiles()) { builder.addExcludedFile(f); }
    
    
    builder.setCustomManifest(_state.getCustomManifest());
    
    return builder;
  }
  
  
  public void saveProject(File file, HashMap<OpenDefinitionsDocument, DocumentInfoGetter> info) throws IOException {
    
    if (file.exists() && !file.canWrite()) {
      File[] res = _notifier.filesReadOnly(new File[] {file});
      for(File roFile: res) {
        FileOps.makeWritable(roFile);
      }
      if (res.length == 0) { return;  }
    }
    
    ProjectProfile builder = _makeProjectProfile(file, info);
    
    builder.write();
    




    
    setFileGroupingState(makeProjectFileGroupingState(builder.getProjectRoot(), builder.getMainClass(), 
                                                      builder.getBuildDirectory(), builder.getWorkingDirectory(), file,
                                                      builder.getSourceFiles(), builder.getAuxiliaryFiles(), 
                                                      builder.getExcludedFiles(),
                                                      builder.getClassPaths(), builder.getCreateJarFile(), 
                                                      builder.getCreateJarFlags(), builder.getAutoRefreshStatus(), builder.getCustomManifest()));
  }
  
  
  public void exportOldProject(File file, HashMap<OpenDefinitionsDocument,DocumentInfoGetter> info) throws IOException {
    ProjectProfile builder = _makeProjectProfile(file, info);
    
    
    builder.writeOld();
    




    
    setFileGroupingState(makeProjectFileGroupingState(builder.getProjectRoot(), builder.getMainClass (), 
                                                      builder.getBuildDirectory(), builder.getWorkingDirectory(), file,
                                                      builder.getSourceFiles(), builder.getAuxiliaryFiles(),
                                                      builder.getExcludedFiles(),
                                                      builder.getClassPaths(), builder.getCreateJarFile(), 
                                                      builder.getCreateJarFlags(), builder.getAutoRefreshStatus(), builder.getCustomManifest()));
  }
  
  public void reloadProject(File file, HashMap<OpenDefinitionsDocument, DocumentInfoGetter> info) throws IOException {
    boolean projChanged = isProjectChanged();
    ProjectProfile builder = _makeProjectProfile(file, info);
    _loadProject(builder);
    setProjectChanged(projChanged);
  }
  
  
  public void openProject(File projectFile) throws IOException, MalformedProjectFileException {
    _loadProject(ProjectFileParserFacade.ONLY.parse(projectFile));
  }
  
  
  private void _loadProject(final ProjectFileIR ir) throws IOException {
    
    assert EventQueue.isDispatchThread();
    
    final DocFile[] srcFiles = ir.getSourceFiles();
    final DocFile[] auxFiles = ir.getAuxiliaryFiles();
    final DocFile[] excludedFiles = ir.getExcludedFiles();
    final File projectFile = ir.getProjectFile();
    File pr = ir.getProjectRoot();
    
    try { pr = pr.getCanonicalFile(); }
    catch(IOException ioe) {  }
    
    final File projectRoot = pr;
    final File buildDir = ir.getBuildDirectory ();
    final File workDir = ir.getWorkingDirectory();
    final String mainClass = ir.getMainClass();
    final Iterable<AbsRelFile> projectClassPaths = ir.getClassPaths();
    final File createJarFile  = ir.getCreateJarFile ();
    int createJarFlags = ir.getCreateJarFlags();
    final boolean autoRefresh = ir.getAutoRefreshStatus();
    final String manifest = ir.getCustomManifest();
    
    

    if (! _browserHistoryManager.getRegions().isEmpty()) _browserHistoryManager.clearBrowserRegions();
    if (! _breakpointManager.getDocuments().isEmpty()) _breakpointManager.clearRegions();
    if (! _bookmarkManager.getDocuments().isEmpty()) _bookmarkManager.clearRegions();
    
    final String projfilepath = projectRoot.getCanonicalPath();
    
    
    

    
    
    
    
    
    List<Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>> l =
      new LinkedList<Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>>();
    
    INavigatorItemFilter<OpenDefinitionsDocument> navItem1 = new INavigatorItemFilter<OpenDefinitionsDocument>() {
      public boolean accept(OpenDefinitionsDocument d) { return d.inProjectPath(); }
    };
    
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getSourceBinTitle(), navItem1));
    
    INavigatorItemFilter<OpenDefinitionsDocument> navItem2 = new INavigatorItemFilter<OpenDefinitionsDocument>() {
      public boolean accept(OpenDefinitionsDocument d) { return d.isAuxiliaryFile(); }
    };
    
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getAuxiliaryBinTitle(), navItem2));
    
    INavigatorItemFilter<OpenDefinitionsDocument> navItem3 = new INavigatorItemFilter<OpenDefinitionsDocument>() {
      public boolean accept(OpenDefinitionsDocument d) {
        return !(d.inProject() || d.isAuxiliaryFile()) || d.isUntitled();
      }
    };
                                                                          
    l.add(new Pair<String, INavigatorItemFilter<OpenDefinitionsDocument>>(getExternalBinTitle(), navItem3));
                                                                    
    IDocumentNavigator<OpenDefinitionsDocument> newNav =
      new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().
      makeTreeNavigator(projfilepath, getDocumentNavigator(), l);
    
    setDocumentNavigator(newNav);
    
    setFileGroupingState(makeProjectFileGroupingState(projectRoot, mainClass, buildDir, workDir, projectFile, srcFiles,
                                                      auxFiles, excludedFiles, projectClassPaths, createJarFile, 
                                                      createJarFlags, autoRefresh, manifest));
    
    resetInteractions(getWorkingDirectory());  
    
    ArrayList<DocFile> projFiles = new ArrayList<DocFile>();
    DocFile active = null;
    
    
    ArrayList<DocFile> modifiedFiles = new ArrayList<DocFile>();
    for (DocFile f: srcFiles) {
      if (f.lastModified() > f.getSavedModDate()) {
        modifiedFiles.add(f);
        f.setSavedModDate (f.lastModified());
      }
      
      if (f.isActive()) { active = f; }
      projFiles.add(f);
    }
    for (DocFile f: auxFiles) {
      if (f.lastModified() > f.getSavedModDate()) {
        modifiedFiles.add(f);
        f.setSavedModDate (f.lastModified());
      }
      if (f.isActive()) { active = f; }
      projFiles.add(f);
    }
    

    
    final List<OpenDefinitionsDocument> projDocs = getProjectDocuments();  
    
    
    
    
    if (! projDocs.isEmpty())
    for (OpenDefinitionsDocument d: projDocs) {
      try {
        final String path = fixPathForNavigator(d.getFile().getCanonicalPath());
        _documentNavigator.refreshDocument(d, path);  
      }
      catch(IOException e) {  }
    }
    

    
    final DocFile[] filesToOpen = projFiles.toArray(new DocFile[projFiles.size()]);
    _notifier.openProject(projectFile, new FileOpenSelector() {
      public File[] getFiles() { return filesToOpen; }
    });
    
    
    
    for (DebugBreakpointData dbd: ir.getBreakpoints()) {
      try {
        File f = dbd.getFile();
        if (! modifiedFiles.contains(f)) {
          int lnr = dbd.getLineNumber();
          OpenDefinitionsDocument odd = getDocumentForFile(f);
          getDebugger().toggleBreakpoint(odd, odd._getOffset(lnr), dbd.isEnabled());
        }
      }
      catch(DebugException de) {  }
    }
    
    
    if (active != null) setActiveDocument(getDocumentForFile(active));
    
    
    try { getDebugger().removeAllWatches(); }
    catch(DebugException de) {  }
    for (DebugWatchData dwd: ir.getWatches()) {
      try { getDebugger().addWatch( dwd.getName()); }
      catch(DebugException de) {  }
    }
    
    
    for (FileRegion bm: ir.getBookmarks()) {
      File f = bm.getFile();
      if (! modifiedFiles.contains(f)) {
        OpenDefinitionsDocument odd = getDocumentForFile(f);
        int start = bm.getStartOffset();
        int end = bm.getEndOffset();
        if (getOpenDefinitionsDocuments().contains(odd) && 
            _bookmarkManager.getRegionsOverlapping(odd, start, end).size() == 0) { 
          try { 
            int lineStart = odd._getLineStartPos(start);
            int lineEnd = odd._getLineEndPos(end);
            _bookmarkManager.addRegion(new MovingDocumentRegion(odd, start, end, lineStart, lineEnd)); 
          }
          catch(Exception e) { DrJavaErrorHandler.record(e); }  
        }
        
      }
    }
    
    if (_documentNavigator instanceof JTreeSortNavigator<?>) 
      ((JTreeSortNavigator<?>)_documentNavigator).collapsePaths(ir.getCollapsedPaths()); 
    
    if (_state.getAutoRefreshStatus()) openNewFilesInProject(); 
  }  
  
  
  public void autoRefreshProject() { openNewFilesInProject(); }
  
  
  public void closeProject(boolean suppressReset) {
    setDocumentNavigator(new AWTContainerNavigatorFactory<OpenDefinitionsDocument>().
                           makeListNavigator(getDocumentNavigator()));
    setFileGroupingState(makeFlatFileGroupingState());

    if (! suppressReset) resetInteractions(getWorkingDirectory());
    _notifier.projectClosed();
    setActiveDocument(getDocumentNavigator().getDocuments().get(0));
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
    boolean res = closeFiles(docs);
    if (res) {

      resetInteractions(getWorkingDirectory());
    }
    return res;
  }
  
  
  public boolean closeFiles(List<OpenDefinitionsDocument> docs) {
    if (docs.size() == 0) return true;
    
    _log.log("closeFiles(" + docs + ") called");
    
    for (OpenDefinitionsDocument doc : docs) { 
      if (! doc.canAbandonFile()) return false; }
    
    
    if (docs.size() == getDocumentCount()) newFile();
    
    
    _ensureNotActive(docs);
    
    
    for (OpenDefinitionsDocument doc : docs) { closeFileWithoutPrompt(doc); }  
    return true;
  }
  
  
  protected boolean closeFileHelper(OpenDefinitionsDocument doc) {
    
    boolean canClose = doc.canAbandonFile();
    if (canClose) return closeFileWithoutPrompt(doc);
    return false;
  }
  
  
  public boolean closeFileWithoutPrompt(final OpenDefinitionsDocument doc) {
    
    
    _log.log("closeFileWithoutPrompt(" + doc + ") called; getRawFile() = " + doc.getRawFile());
    _log.log("_documentsRepos = " + _documentsRepos);
    boolean found;
    synchronized(_documentsRepos) { found = (_documentsRepos.remove(doc.getRawFile()) != null); }
    
    if (! found) {
      _log.log("Cannot close " + doc + "; not found!");
      return false;
    }
    
    
    _breakpointManager.removeRegions(doc);
    _bookmarkManager.removeRegions(doc);
    
    
    @SuppressWarnings("unchecked")
    RegionManager<MovingDocumentRegion>[] managers = _findResultsManagers.toArray(new RegionManager[0]);
    for (RegionManager<MovingDocumentRegion> rm: managers) rm.removeRegions(doc);
    doc.clearBrowserRegions();
    
    
    if (doc.isAuxiliaryFile()) { removeAuxiliaryFile(doc); }
    
    _documentNavigator.removeDocument(doc);
    _notifier.fileClosed(doc); 
    doc.close();
    return true;
  }
  
  
  public boolean closeAllFilesOnQuit() {
    
    List<OpenDefinitionsDocument> docs = getOpenDefinitionsDocuments();
    
    for (OpenDefinitionsDocument doc : docs) {
      if (! doc.canAbandonFile()) { return false; }
    }
    
    
    
    
    newFile();
    
    
    
    _ensureNotActive(docs);
    
    
    for (OpenDefinitionsDocument doc : docs) { closeFileWithoutPrompt(doc); }  
    
    return true;
  }
  
  
  public void quit() { quit(false); }
  
  
  public void forceQuit() { quit(true); }
  
  
  private void quit(boolean force) {

    try {
      if (! force && ! closeAllFilesOnQuit()) {
        refreshActiveDocument();  
        return;
      }
      
      
      shutdown(force);
    }
    catch(Throwable t) { shutdown(true);  }
  }
  
  
  private void shutdown(boolean force) {
    if (force) Runtime.getRuntime().halt(0);
    
    dispose();  
    
    if (DrJava.getConfig().getSetting(OptionConstants.DRJAVA_USE_FORCE_QUIT)) {
      Runtime.getRuntime().halt(0);  
    }
    
    Thread monitor = new Thread(new Runnable() { 
      public void run() {
        try { Thread.sleep(2000); }
        catch(InterruptedException e) {  }
        Runtime.getRuntime().halt(0);  
      }
    });
    monitor.setDaemon(true);
    monitor.start();
    System.exit(0);
  }
  
  
  public void dispose() {
    synchronized(_documentsRepos) { 
      closeAllFiles();
      _documentsRepos.clear();
    }
    Utilities.invokeLater(new Runnable() {
      public void run() { _documentNavigator.clear(); }  
    });
    
    EventQueue.invokeLater(new Runnable() { public void run() { _notifier.removeAllListeners(); } });
  }
  
  
  public void disposeExternalResources() {  }
  
  
  public OpenDefinitionsDocument getDocumentForFile(File file) throws IOException {
    if ((file instanceof NullFile) ||
        (file instanceof FileOps.NullFile)) return null;
 
    
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
  
  
  public OpenDefinitionsDocument getNextDocument(OpenDefinitionsDocument d) {
    OpenDefinitionsDocument nextdoc = null; 

    OpenDefinitionsDocument doc = getODDForDocument(d);
    nextdoc = _documentNavigator.getNext(doc);
    if (nextdoc == doc) nextdoc = _documentNavigator.getFirst();  
    OpenDefinitionsDocument res = getNextDocHelper(nextdoc);

    return res;


  }
  
  private OpenDefinitionsDocument getNextDocHelper(OpenDefinitionsDocument nextdoc) {
    if ( nextdoc.isUntitled() || nextdoc.verifyExists()) return nextdoc;
    
    
    
    return getNextDocument(nextdoc);
  }
  
  
  public OpenDefinitionsDocument getPrevDocument(OpenDefinitionsDocument d) {
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
      for (OpenDefinitionsDocument doc: _documentsRepos.values()) { docs.add(doc); }
      return docs;
    }
  }

  
  public List<OpenDefinitionsDocument> getLLOpenDefinitionsDocuments() {
    synchronized(_documentsRepos) {
      ArrayList<OpenDefinitionsDocument> docs = new ArrayList<OpenDefinitionsDocument>(_documentsRepos.size());
      for (OpenDefinitionsDocument doc: _documentsRepos.values()) {
        File f = doc.getRawFile();
        if (f.getName().endsWith(".dj0") ||
            f.getName().endsWith(".dj1") ||
            f.getName().endsWith(".dj2")) docs.add(doc);
      }
      return docs;
    }
  }
  
  
  public List<OpenDefinitionsDocument> getSortedOpenDefinitionsDocuments() { return getOpenDefinitionsDocuments(); }

  
  public boolean hasOutOfSyncDocuments() { return getOutOfSyncDocuments().size() > 0; }
  
  public boolean hasOutOfSyncDocuments(List<OpenDefinitionsDocument> lod) { return getOutOfSyncDocuments().size() > 0; }
  
  
  public List<OpenDefinitionsDocument> getOutOfSyncDocuments() { return getOutOfSyncDocuments(getOpenDefinitionsDocuments()); }
  
  public List<OpenDefinitionsDocument> getOutOfSyncDocuments(List<OpenDefinitionsDocument> lod) {
    List<OpenDefinitionsDocument> outOfSync = new ArrayList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument doc: lod) {
      if (doc.isSourceFile() &&
          (! isProjectActive() || doc.inProjectPath() || doc.isAuxiliaryFile()) &&
          (! doc.checkIfClassFileInSync())) {
        
        
        
        
        
        
        try {
          boolean b = doc.containsClassOrInterfaceOrEnum();
          System.out.println("Checking contents of "+doc+": "+b);
          if (b) outOfSync.add(doc);
        }
        catch(BadLocationException e) {
          outOfSync.add(doc);
        }
      }
    }
    return outOfSync;
  }
  
  
  void setDefinitionsIndent(int indent) {
    for (OpenDefinitionsDocument doc: getOpenDefinitionsDocuments()) { doc.setIndent(indent); }
  }
  
  
  public void resetInteractions(File wd) {  }
  
  
  public void resetInteractions(File wd, boolean forceReset) {  }
  
  
  public void resetConsole() {
    _consoleDoc.reset("");
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.consoleReset(); } });
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


    
    



    
    DrJava.getConfig().addOptionListener(DYNAMICJAVA_ACCESS_CONTROL, new OptionListener<String>() {
      public void optionChanged(OptionEvent<String> oce) {
        boolean enforceAllAccess = DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL)
          .equals(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL_CHOICES.get(2)); 
        getInteractionsModel().setEnforceAllAccess(enforceAllAccess);
        
        boolean enforcePrivateAccess = !DrJava.getConfig().getSetting(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL)
          .equals(OptionConstants.DYNAMICJAVA_ACCESS_CONTROL_CHOICES.get(0)); 
        getInteractionsModel().setEnforcePrivateAccess(enforcePrivateAccess);
      }
    });

    DrJava.getConfig().addOptionListener(DYNAMICJAVA_REQUIRE_SEMICOLON, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        getInteractionsModel().setRequireSemicolon(oce.value);
      }
    });

    DrJava.getConfig().addOptionListener(DYNAMICJAVA_REQUIRE_VARIABLE_TYPE, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        getInteractionsModel().setRequireVariableType(oce.value);
      }
    });
  }
  
  
  protected void _docAppend(final ConsoleDocument doc, final String s, final String style) {
    Utilities.invokeLater(new Runnable() {
      public void run() { doc.insertBeforeLastPrompt(s, style); }
    });
  }
  
  
  public void systemOutPrint(final String s) { _docAppend(_consoleDoc, s, ConsoleDocument.SYSTEM_OUT_STYLE); }
  
  
  public void systemErrPrint(final String s) { _docAppend(_consoleDoc, s, ConsoleDocument.SYSTEM_ERR_STYLE); }
  
  
  public void systemInEcho(final String s) { _docAppend(_consoleDoc, s, ConsoleDocument.SYSTEM_IN_STYLE); }
  
  
  public void printDebugMessage(String s) {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support debugging");
  }
  
  
  public Iterable<File> getInteractionsClassPath() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support interactions");
  }
  
  
  public Iterable<AbsRelFile> getExtraClassPath() { return _state.getExtraClassPath(); }
  
  
  public void setExtraClassPath(Iterable<AbsRelFile> cp) {
    _state.setExtraClassPath(cp);
    setClassPathChanged(true);
    
  }
  
  
  public File[] getExclFiles() { return _state.getExclFiles(); }
  
  
  public void setExcludedFiles(File[] fs) { _state.setExcludedFiles(fs); }
  
  
  public Iterable<File> getSourceRootSet() {
    Set<File> roots = new LinkedHashSet<File>();
    
    for (OpenDefinitionsDocument doc: getOpenDefinitionsDocuments()) {
      try {
        if (! doc.isUntitled()) {
          File root = doc.getSourceRoot();
          if (root != null) roots.add(root); 
        }
      }
      catch (InvalidPackageException e) {

        
      }
    }
    return roots;
  }
  






  
  
  public Debugger getDebugger() {
    
    return NoDebuggerAvailable.ONLY;
  }
  
  
  public int getDebugPort() throws IOException {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support debugging");
  }
  
  
  public boolean hasModifiedDocuments() { return hasModifiedDocuments(getOpenDefinitionsDocuments()); }
  
  
  public boolean hasModifiedDocuments(List<OpenDefinitionsDocument> lod) {
    for (OpenDefinitionsDocument doc: lod) {
      if (doc.isModifiedSinceSave()) return true;  
    }
    return false;
  }
  
  
  public boolean hasUntitledDocuments() {
    for (OpenDefinitionsDocument doc: getOpenDefinitionsDocuments()) {
      if (doc.isUntitled()) return true;  
    }
    return false;
  }
  
  
  public File getSourceFile(String fileName) {
    Iterable<File> sourceRoots = getSourceRootSet();
    for (File s: sourceRoots) {
      File f = _getSourceFileFromPath(fileName, s);
      if (f != null) return f;
    }
    Vector<File> sourcepath = DrJava.getConfig().getSetting(OptionConstants.DEBUG_SOURCEPATH);
    return findFileInPaths(fileName, sourcepath);
  }
  
  
  public File findFileInPaths(String fileName, Iterable<File> paths) {
    for (File p: paths) {
      File f = _getSourceFileFromPath(fileName, p);
      if (f != null) return f;
    }
    return FileOps.NULL_FILE;
  }
  
  
  private File _getSourceFileFromPath(String fileName, File path) {
    String root = path.getAbsolutePath();
    File f = new File(root + System.getProperty("file.separator") + fileName);
    return f.exists() ? f : FileOps.NULL_FILE;
  }

  
  public void addToBrowserHistory() {
    addToBrowserHistory(false);
  }

  
  public void addToBrowserHistory(boolean before) {
    assert EventQueue.isDispatchThread();

    _notifier.updateCurrentLocationInDoc();

    final OpenDefinitionsDocument doc = getActiveDocument();

    
    Position startPos = null;
    Position endPos = null;
    try {
      int pos = doc.getCaretPosition();
      startPos = doc.createPosition(pos);
      endPos = startPos; 
    }
    
    catch (BadLocationException ble) { throw new UnexpectedException(ble); }

    BrowserDocumentRegion r = new BrowserDocumentRegion(doc, startPos, endPos);
    if (before) {
      _browserHistoryManager.addBrowserRegionBefore(r, _notifier);
    }
    else {
      _browserHistoryManager.addBrowserRegion(r, _notifier);
    }

  }
  
  
  public Iterable<File> getClassPath() {
    throw new UnsupportedOperationException("AbstractGlobalModel does not support class paths");
  }
  
  public static boolean isUntitled(final File f) { return f == null || (f instanceof NullFile); }
  
  
  
  
  class ConcreteOpenDefDoc implements OpenDefinitionsDocument {
    
    public void addBrowserRegion(BrowserDocumentRegion r) { _browserRegions.add(r); }
    
    public void removeBrowserRegion(BrowserDocumentRegion r) { _browserRegions.remove(r); }
    

    
    
    private volatile String _image;
    private volatile File _file;
    private volatile long _timestamp;
    
    
    private volatile File _parentDir;
    
    
    private volatile File _classFile;
    
    
    private volatile boolean _classFileInSync = false;
    
    
    protected volatile String _packageName = "";
    
    
    protected volatile String _lexiName = "";
    
    private volatile DCacheAdapter _cacheAdapter;
    
    
    protected final Set<BrowserDocumentRegion> _browserRegions;
    
    private volatile int _initVScroll;
    private volatile int _initHScroll;
    private volatile int _initSelStart;
    private volatile int _initSelEnd;
    
    private volatile DrJavaBook _book;
    
    
    ConcreteOpenDefDoc(File f) { this(f, f.getParentFile(), f.lastModified()); }
    
    
    ConcreteOpenDefDoc(NullFile f) { this(f, null, 0L); }
    
    
    private ConcreteOpenDefDoc(File f, File dir, long stamp) {
      
      _file = f;
      _parentDir = dir;
      _classFile = FileOps.NULL_FILE;
      _timestamp = stamp;
      _image = null;

      if (_file instanceof NullFile)
        _lexiName = ((NullFile) _file).getLexiName();  
      else 
        _lexiName = _file.getPath().replace(File.separatorChar, ' ');
      
      try {
        DDReconstructor ddr = makeReconstructor();

        _cacheAdapter = _cache.register(this, ddr);
      } catch(IllegalStateException e) { throw new UnexpectedException(e); }
      
      
      _browserRegions = new HashSet<BrowserDocumentRegion>();
    }
    
    
    
    
    public File getRawFile() { return _file; }
    
    
    public File getFile() throws FileMovedException {
      File f = _file;  
      if (AbstractGlobalModel.isUntitled(f)) return null;  
      if (f.exists()) return f;
      else throw new FileMovedException(f, "This document's file has been moved or deleted.");
    }
    
    
    public synchronized void setFile(final File file) {
      _file = file;
      if (! AbstractGlobalModel.isUntitled(file)) _timestamp = file.lastModified();
      else _timestamp = 0L;
    }
    
    
    public long getTimestamp() { return _timestamp; }
    
    public void setClassFileInSync(boolean inSync) { _classFileInSync = inSync; }
    
    public boolean getClassFileInSync() { return _classFileInSync; }
    
    public void setCachedClassFile(File classFile) { _classFile = classFile; }
    
    public File getCachedClassFile() { return _classFile; }
    
    
    public synchronized void resetModification() {
      getDocument().resetModification();
      File f = _file; 
      if (! AbstractGlobalModel.isUntitled(f)) _timestamp = f.lastModified();
    }
    
    
    public File getParentDirectory() { return _parentDir; }
    
    
    public synchronized void setParentDirectory(File pd) {
      if (! AbstractGlobalModel.isUntitled(_file))
        throw new IllegalArgumentException("The parent directory can only be set for untitled documents");
      _parentDir = pd;  
    }
    
    public int getInitialVerticalScroll()   { return _initVScroll; }
    public int getInitialHorizontalScroll() { return _initHScroll; }
    public int getInitialSelectionStart()   { return _initSelStart; }
    public int getInitialSelectionEnd()     { return _initSelEnd; }
    
    void setInitialVScroll(int i)  { _initVScroll = i; }
    void setInitialHScroll(int i)  { _initHScroll = i; }
    void setInitialSelStart(int i) { _initSelStart = i; }
    void setInitialSelEnd(int i)   { _initSelEnd = i; }
    
    
    public DefinitionsDocument getDocument() {
      

      try { return _cacheAdapter.getDocument(); }
      catch(IOException ioe) { 

        try {
          _notifier.documentNotFound(this, _file);
          final String path = fixPathForNavigator(getFile().getCanonicalFile().getCanonicalPath());
          _documentNavigator.refreshDocument(ConcreteOpenDefDoc.this, path); 

          return _cacheAdapter.getDocument();
        }
        catch(Throwable t) { throw new UnexpectedException(t); }
      }
    }
    


    
    
    public String getFirstTopLevelClassName() throws ClassNameNotFoundException {
      return getDocument().getFirstTopLevelClassName();
    }
    
    
    public String getMainClassName() throws ClassNameNotFoundException {
      return getDocument().getMainClassName();
    }
    
    
    public String getFileName() {
      if (_file == null) return "(Untitled)";

      return _file.getName(); 
    }
    
    
    public String getName() {
      String fileName = getFileName();
      if (isModifiedSinceSave()) fileName = fileName + "*";
      else fileName = fileName + "  ";  
      return fileName;
    }
    
    
    public String getCanonicalPath() {
      if (isUntitled()) { return "(Untitled)"; }
      else { return IOUtil.attemptCanonicalFile(getRawFile()).getPath(); }
    }
    
    
    public String getCompletePath() {
      String path = getCanonicalPath();
      
      if (isModifiedSinceSave()) path = path + " *";
      return path;
    }
    
    
    public File getSourceRoot() throws InvalidPackageException { 
      if (isUntitled())
        throw new InvalidPackageException(-1, "Can not get source root for unsaved file. Please save.");
      
      try {
        String[] packages = _packageName.split("\\.");
        if (packages.length == 1 && packages[0].equals("")) {
          packages = new String[0]; 
        }
        File dir = getFile().getParentFile();
        for (String p : IterUtil.reverse(IterUtil.asIterable(packages))) {
          if (dir == null || !dir.getName().equals(p)) {
            String m = "File is in the wrong directory or is declared part of the wrong package.  " +
              "Directory name " + ((dir == null) ? "(root)" : "'" + dir.getName() + "'") +
              " does not match package name '" + p + "'.";
            throw new InvalidPackageException(-1, m);
          }
          dir = dir.getParentFile();
        }
        if (dir == null) {
          
          
          throw new InvalidPackageException(-1, "File is in a directory tree with a null root");
        }
        return dir;
      }
      catch (FileMovedException fme) {
        throw new
          InvalidPackageException(-1, "File has been moved or deleted from its previous location. Please save.");
      }
    }
    
    
    public String getPackageName() { return _packageName; }
    
      
    public void setPackage(String name)   { _packageName = name; }
    
    
    public String getPackageNameFromDocument() { return getDocument().getPackageName(); }
    
    
    
    public void updateModifiedSinceSave() { getDocument().updateModifiedSinceSave(); }
    
    
    public String getLexiName() { return _lexiName; }
    
    
    public Pageable getPageable() throws IllegalStateException { return _book; }
    
    
    public void cleanUpPrintJob() { _book = null; }
    
    
    
    
    
    public boolean inProjectPath() { return _state.inProjectPath(this); }
    
    
    public boolean inNewProjectPath(File projRoot) {
      try { return ! isUntitled() && IOUtil.isMember(getFile(), projRoot); }
      catch(FileMovedException e) { return false; }
    }
    
    
    public boolean inProject() { return ! isUntitled() && _state.inProject(_file); }
    
    
    public boolean isEmpty() { return getLength() == 0; }
    
    
    public boolean isAuxiliaryFile() { return ! isUntitled() && _state.isAuxiliaryFile(_file); }
    
    
    public boolean isSourceFile() {
      if (isUntitled()) return false;  
      String name = _file.getName();
      for (String ext: CompilerModel.EXTENSIONS) { if (name.endsWith(ext)) return true; }
      return false;
    }
    
    
    public boolean isUntitled() { return AbstractGlobalModel.isUntitled(_file); }
    
    public boolean isUntitledAndEmpty() { return isUntitled() && getLength() == 0; }  
    
    
    public boolean fileExists() { 
      File f = _file; 
      return  ! AbstractGlobalModel.isUntitled(f) && f.exists(); 
    }
    
    
    
    
    public boolean verifyExists() {

      if (fileExists()) return true;
      
      try {
        _notifier.documentNotFound(this, _file);

        if (isUntitled()) return false;
        String path = fixPathForNavigator(getFile().getCanonicalPath());
        _documentNavigator.refreshDocument(this, path);
        return true;
      }
      catch(FileMovedException e) { return false; }
      catch(IOException e) { return false; }

    }
    
    
    protected DDReconstructor makeReconstructor() {
      return new DDReconstructor() {
        
        
        private volatile int _loc = 0;
        
        
        private volatile DocumentListener[] _list = { };
        private volatile List<FinalizationListener<DefinitionsDocument>> _finalListeners =
          new LinkedList<FinalizationListener<DefinitionsDocument>>();
        
        
        private volatile WeakHashMap< DefinitionsDocument.WrappedPosition, Integer> _positions =
          new WeakHashMap<DefinitionsDocument.WrappedPosition, Integer>();
        
        
        public String getText() {
          String image = _image;
          if (image != null) return image;
          
          
          
          
          
          try { image = FileOps.readFileAsSwingText(_file); }
          catch(IOException e) {  image = ""; }  

          _image = image;
          return _image;
        }
        
        public DefinitionsDocument make() throws IOException, BadLocationException, FileMovedException {
          

          DefinitionsDocument newDefDoc = new DefinitionsDocument(_notifier);
          newDefDoc.setOpenDefDoc(ConcreteOpenDefDoc.this);
          
          
          String image = getText();  
          assert image != null;  
          
          _editorKit.read(new StringReader(image), newDefDoc, 0);
          
          newDefDoc.putProperty(DefaultEditorKit.EndOfLineStringProperty, StringOps.EOL);
          _log.log("Reading from image for " + _file + " containing " + _image.length() + " chars");    
          
          _loc = Math.min(_loc, image.length()); 
          _loc = Math.max(_loc, 0); 
          newDefDoc.setCurrentLocation(_loc);
          for (DocumentListener d : _list) {
            if (d instanceof DocumentUIListener) newDefDoc.addDocumentListener(d);
          }
          for (FinalizationListener<DefinitionsDocument> l: _finalListeners) {
            newDefDoc.addFinalizationListener(l);
          }
          
          
          newDefDoc.setWrappedPositionOffsets(_positions);
          
          newDefDoc.resetModification();  
          
          
          assert ! newDefDoc.isModifiedSinceSave();


          _packageName = newDefDoc.getPackageName();

          return newDefDoc;
        }
        
        
        
        public void saveDocInfo(DefinitionsDocument doc) {



          
          String text = doc.getText();
          if (text.length() > 0) {
            _image = text;  

          }
          _loc = doc.getCurrentLocation();
          _list = doc.getDocumentListeners();
          _finalListeners = doc.getFinalizationListeners ();
          
          
          _positions.clear();
          _positions = doc.getWrappedPositionOffsets();
        }
        
        public void addDocumentListener(DocumentListener dl) {
          ArrayList<DocumentListener> tmp = new ArrayList<DocumentListener>();
          for (DocumentListener l: _list) { if (dl != l) tmp.add(l); }
          tmp.add(dl);
          _list = tmp.toArray (new DocumentListener[tmp.size()]);
        }
        public String toString() { return ConcreteOpenDefDoc.this.toString(); }
      };
    }
    
    
    public boolean saveFile(FileSaveSelector com) throws IOException {

      
      if (! isModifiedSinceSave()) return true;
      if (isUntitled()) return saveFileAs(com);
      
      
      

      
      
      _packageName = getDocument().getPackageName();
      FileSaveSelector realCommand = com;
      try {
        final File file = getFile();

        if (! isUntitled()) {

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
      assert EventQueue.isDispatchThread();

      File oldFile = getRawFile();
      
      _packageName = getDocument().getPackageName();
      try {
        final OpenDefinitionsDocument openDoc = this;
        final File file = com.getFile().getCanonicalFile();
        _log.log("saveFileAs called on " + file);
        OpenDefinitionsDocument otherDoc = _getOpenDocument(file);
        
        
        boolean openInOtherDoc = ((otherDoc != null) && (openDoc != otherDoc));
        

        
        
        if (openInOtherDoc) {
          boolean shouldOverwrite = com.warnFileOpen(file);
          if (! shouldOverwrite) return true; 
        }
        
        if (! file.exists() || com.verifyOverwrite()) {  
          

          
          
          if (! file.getCanonicalFile().getName().equals(file.getName())) file.renameTo(file);
          
          
          
          if (file.getAbsolutePath().indexOf("#") != -1) _notifier.filePathContainsPound();
          
          
          if (file.exists() && ! file.canWrite()) {
            File[] res = _notifier.filesReadOnly(new File[] {file});
            for(File roFile: res) {
              FileOps.makeWritable(roFile);
            }
            if (res.length == 0) { return false;  }
          }
          
          

          FileOps.saveFile(new FileOps.DefaultFileSaver(file) {
            
            public void saveTo(OutputStream os) throws IOException {
              DefinitionsDocument dd = getDocument();
              try {
                _editorKit.write(os, dd, 0, dd.getLength());

              }
              catch (BadLocationException docFailed) { throw new UnexpectedException(docFailed); }
            }
          });
          resetModification();
          if (! oldFile.equals(file)) {
            
            removeFromDebugger();
            _breakpointManager.removeRegions(this);
            _bookmarkManager.removeRegions(this);
            for (RegionManager<MovingDocumentRegion> rm: getFindResultsManagers()) rm.removeRegions(this);
            clearBrowserRegions();
          }
          synchronized(_documentsRepos) {
            File f = getRawFile();

            

            _documentsRepos.remove(f);
            _documentsRepos.put(file, this);
          }
          setFile(file);
          
          






          setCachedClassFile(FileOps.NULL_FILE);
          checkIfClassFileInSync();
          

          _notifier.fileSaved(openDoc);
          
          
          addDocToClassPath(this);
          
          
          _documentNavigator.refreshDocument(this, fixPathForNavigator(file.getCanonicalPath()));
          
          
          setProjectChanged(true);          
        }
        return true;
      }
      catch (OperationCanceledException oce) {
        
        
        return false;
      }
    }
    
    
    public void preparePrintJob() throws BadLocationException, FileMovedException {
      String fileName = "(Untitled)";
      File sourceFile = getFile();  
      if (! AbstractGlobalModel.isUntitled(sourceFile)) fileName = sourceFile.getAbsolutePath();
      
      _book = new DrJavaBook(getDocument().getText(), fileName, _pageFormat);
    }
    
    
    public void print() throws PrinterException, BadLocationException, FileMovedException {
      preparePrintJob();
      PrinterJob printJob = PrinterJob.getPrinterJob();
      printJob.setPageable(_book);
      if (printJob.printDialog()) printJob.print();
      cleanUpPrintJob();
    }
    
    
    public void startCompile() throws IOException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support compilation");
    }
    
    
    public void runMain(String className) throws IOException, ClassNameNotFoundException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support running");
    }
    
    
    public void runApplet(String className) throws IOException, ClassNameNotFoundException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support running");
    }
    
    
    public void startJUnit() throws IOException, ClassNotFoundException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support unit testing");
    }
    
    
    public void generateJavadoc(FileSaveSelector saver) throws IOException {
      throw new UnsupportedOperationException("AbstractGlobalModel does not support javadoc");
    }
    
    
    public boolean isReady() { return _cacheAdapter != null && _cacheAdapter.isReady(); }
    
    
    public boolean isModifiedSinceSave() {
      
      if (isReady()) return getDocument().isModifiedSinceSave();
      else return false;
    }
    
    public void documentSaved() { _cacheAdapter.documentSaved(); }
    
    public void documentModified() { 
      _cacheAdapter.documentModified();
      _classFileInSync = false;
    }
    
    public void documentReset() { _cacheAdapter.documentReset(); }
    
    
    public boolean modifiedOnDisk() {
      boolean ret = false;
      final File f = _file;  
      if (! AbstractGlobalModel.isUntitled(f)) ret = (f.lastModified() > _timestamp);
      return ret;
    }
    
    
    public boolean checkIfClassFileInSync() {
      _log.log("checkIfClassFileInSync() called for " + this);
      if (isEmpty()) return true;
      
      
      
      if (isModifiedSinceSave()) {
        setClassFileInSync(false);
        _log.log("checkIfClassFileInSync = false because isModifiedSinceSave()");
        return false;
      }
      
      
      File classFile = getCachedClassFile();
      _log.log("In checkIfClassFileInSync cacched value of classFile = " + classFile);
      if (classFile == FileOps.NULL_FILE) {
        
        classFile = _locateClassFile();
        _log.log(this + ": in checkIfClassFileInSync _locateClassFile() = " + classFile);
        setCachedClassFile(classFile);
        if ((classFile == FileOps.NULL_FILE) || (! classFile.exists())) {
          
          _log.log(this + ": Could not find class file");
          setClassFileInSync(false);
          return false;
        }
      }
      
      
      
      File sourceFile;
      try { sourceFile = getFile(); }
      catch (FileMovedException fme) {
        setClassFileInSync(false);
        _log.log(this + ": File moved");
        return false;
      }
      if (sourceFile != null) { 
        _log.log(sourceFile + " has timestamp " + sourceFile.lastModified());
        _log.log(classFile + " has timestamp " + classFile.lastModified());
      }
      if (sourceFile == null || sourceFile.lastModified() > classFile.lastModified()) {  
        setClassFileInSync(false);
        _log.log(this + ": date stamps indicate modification");
        return false;
      }
      else {
        setClassFileInSync(true);
        return true;
      }
    }
    
    
    private File _locateClassFile() {
      
      
      if (isUntitled()) return FileOps.NULL_FILE;
      
      String className;
      try { className = getDocument().getQualifiedClassName(); }
      catch (ClassNameNotFoundException cnnfe) {
        _log.log("_locateClassFile() failed for " + this + " because getQualifedClassName returned ClassNotFound");
        return FileOps.NULL_FILE;   
      }

      String ps = System.getProperty("file.separator");
      
      className = StringOps.replace(className, ".", ps);
      String fileName = className + ".class";
      

      
      
      ArrayList<File> roots = new ArrayList<File>();
      

      
      if (getBuildDirectory() != FileOps.NULL_FILE) roots.add(getBuildDirectory());
      
      
      try {
        File root = getSourceRoot();

        roots.add(root); 
      }
      catch (InvalidPackageException ipe) {
        try {

          File root = getFile().getParentFile();
          if (root != FileOps.NULL_FILE) {
            roots.add(root);

          }
        }
        catch(NullPointerException e) { throw new UnexpectedException(e); }
        catch(FileMovedException fme) {
          
          _log.log("File for " + this + "has moved; adding parent directory to list of roots");
          File root = fme.getFile().getParentFile();
          if (root != FileOps.NULL_FILE) roots.add(root);
        }
      }
      
      File classFile = findFileInPaths(fileName, roots);
      if (classFile != FileOps.NULL_FILE) {

        return classFile;
      }
      

      
      classFile = findFileInPaths(fileName, ReflectUtil.SYSTEM_CLASS_PATH);
      
      if (classFile != FileOps.NULL_FILE) return classFile;
      
      
      Vector<File> cpSetting = DrJava.getConfig().getSetting(EXTRA_CLASSPATH);
      return findFileInPaths(fileName, cpSetting);
    }
    
    
    public boolean revertIfModifiedOnDisk() throws IOException {
      final OpenDefinitionsDocument doc = this;
      if (modifiedOnDisk()) {
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
      
      final OpenDefinitionsDocument doc = this;
      
      if (doc.isUntitled()) throw new UnexpectedException("Cannot revert an Untitled file!");
      
      
      removeFromDebugger();
      _breakpointManager.removeRegions(this);
      _bookmarkManager.removeRegions(this);
      for (RegionManager<MovingDocumentRegion> rm: getFindResultsManagers()) rm.removeRegions(this);
      doc.clearBrowserRegions();
      
      FileReader reader = null;
      try {
        
        File file = doc.getFile();
        reader = new FileReader(file);
        doc.clear();
        
        _editorKit.read(reader, doc, 0);
        
        resetModification();
        doc.checkIfClassFileInSync();
        setCurrentLocation(0);
        _notifier.fileReverted(doc);
      }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
      finally { if (reader != null) reader.close();  }
    }
    
    
    public boolean canAbandonFile() {

      if (isUntitledAndEmpty()) return true;
      File f = _file;
      if (isModifiedSinceSave() || (! AbstractGlobalModel.isUntitled(f) && ! f.exists() && _cacheAdapter.isReady()))
        return _notifier.canAbandonFile(this);
      else return true;
    }
    
    
    public boolean quitFile() {
      assert EventQueue.isDispatchThread();
      File f = _file;
      if (isModifiedSinceSave() || (f != null && ! f.exists() && _cacheAdapter.isReady())) 
        return _notifier.quitFile(this);
      return true;
    }
    
    
    public int gotoLine(int line) {

      final int offset = getOffsetOfLine(line - 1);
      setCurrentLocation(offset);
      return offset;
    }
    
    protected int _caretPosition = 0;
    
    
    public void setCurrentLocation(int location) { 

      _caretPosition = location; 
      getDocument().setCurrentLocation(location); 
    }
    
    
    public int getCurrentLocation() { return getDocument().getCurrentLocation(); }
    

    
    
    public int getCaretPosition() { return _caretPosition; }
    
    
    public int balanceBackward() { return getDocument().balanceBackward(); }
    
    
    public int balanceForward() { return getDocument().balanceForward(); }
    
    
    public RegionManager<Breakpoint> getBreakpointManager() { return _breakpointManager; }
    
    
    public RegionManager<MovingDocumentRegion> getBookmarkManager() { return _bookmarkManager; }
    
    
    public void clearBrowserRegions() { 
      BrowserDocumentRegion[] regions = _browserRegions.toArray(new BrowserDocumentRegion[0]);
      for (BrowserDocumentRegion r: regions) _browserHistoryManager.remove(r);
      _browserRegions.clear();
    }
    
    
    public void removeFromDebugger() {  }
    
    public String toString() { return getFileName(); }
    
    
    public int compareTo(OpenDefinitionsDocument o) { 
      int diff = hashCode() - o.hashCode();
      if (diff != 0) return diff;
      return _lexiName.compareTo(o.getLexiName()); 
    }
    
    
    public void addDocumentListener(DocumentListener listener) {
      if (_cacheAdapter.isReady()) getDocument().addDocumentListener(listener);
      else _cacheAdapter.addDocumentListener(listener);
    }
    

    
    public void addUndoableEditListener(UndoableEditListener listener) {

      getDocument().addUndoableEditListener(listener);
    }
    
    public void removeUndoableEditListener(UndoableEditListener listener) {

      getDocument().removeUndoableEditListener(listener);
    }
    
    public UndoableEditListener[] getUndoableEditListeners() {
      return getDocument().getUndoableEditListeners();
    }
    
    public Position createUnwrappedPosition(int offs) throws BadLocationException {
      return getDocument().createUnwrappedPosition(offs); 
    }
    
    public Position createPosition(int offs) throws BadLocationException {
      return getDocument().createPosition(offs);
    }
    
    public Element getDefaultRootElement() { return getDocument().getDefaultRootElement(); }
    
  
  public Position getStartPosition() { 
    throw new UnsupportedOperationException("ConcreteOpenDefDoc does not support getStartPosition()"); 
  }
  public Position getEndPosition() { 
    throw new UnsupportedOperationException("ConcreteOpenDefDoc does not support getEndPosition()"); 
  }
    
    public int getLength() { return _cacheAdapter.getLength(); }
    
    public Object getProperty(Object key) { return getDocument().getProperty(key); }
    
    public Element[] getRootElements() { return getDocument().getRootElements(); }
    

    






    

    
    
    public String getText() { return _cacheAdapter.getText(); }
    
    
    public String getText(int offset, int length) throws BadLocationException {
      return _cacheAdapter.getText(offset, length);
    }
    
    public void getText(int offset, int length, Segment txt) throws BadLocationException {
      getDocument().getText(offset, length, txt);
    }
    
    public void insertString(int offset, String str, AttributeSet a) throws BadLocationException {
      getDocument().insertString(offset, str, a);
    }
    
    public void append(String str, AttributeSet set) { getDocument().append(str, set); }
    
    public void append(String str, Style style) { getDocument().append(str, style); }
    
    public void append(String str) { getDocument().append(str); }
    
    public void putProperty(Object key, Object value) { getDocument().putProperty(key, value); }
    
    public void remove(int offs, int len) throws BadLocationException { getDocument().remove(offs, len); }
    
    public void removeDocumentListener(DocumentListener listener) { getDocument().removeDocumentListener(listener); }
    
    public void render(Runnable r) { getDocument().render(r); }
    
    
    
    
    public boolean undoManagerCanUndo() { return _cacheAdapter.isReady() && getUndoManager().canUndo(); }
    
    public boolean undoManagerCanRedo() { return _cacheAdapter.isReady() && getUndoManager().canRedo(); }
    
    
    public CompoundUndoManager getUndoManager() { return getDocument().getUndoManager(); }
    
        
    public int _getLineStartPos(int pos) { 
      DefinitionsDocument doc = getDocument();
      return doc._getLineStartPos(pos); 
    }
    
    
    public int _getLineEndPos(int pos) { 
      DefinitionsDocument doc = getDocument();
      return doc._getLineEndPos(pos); 
    }
    
    public int commentLines(int selStart, int selEnd) { return getDocument().commentLines(selStart, selEnd); }
    
    public int uncommentLines(int selStart, int selEnd) {
      return getDocument().uncommentLines(selStart, selEnd);
    }
    
    public void indentLines(int selStart, int selEnd) { 
      DefinitionsDocument doc = getDocument();
      doc.indentLines(selStart, selEnd); 
    }
    
    public void indentLines(int selStart, int selEnd, Indenter.IndentReason reason, ProgressMonitor pm)
      throws OperationCanceledException {
      DefinitionsDocument doc = getDocument();
      doc.indentLines(selStart, selEnd, reason, pm); 
    }
    
    public int getCurrentLine() { return getDocument().getCurrentLine(); }
    
    public int getCurrentCol() { return getDocument().getCurrentCol(); }
    
    public int getIntelligentBeginLinePos(int currPos) throws BadLocationException {
      return getDocument().getIntelligentBeginLinePos(currPos);
    }
    
        
    public int _getOffset(int lineNum) { return getDocument()._getOffset(lineNum); }
    
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
    
    public DocumentListener[] getDocumentListeners() { return getDocument().getDocumentListeners(); }
    
    
    

    

    

    

    
    public String getEnclosingClassName(int pos, boolean fullyQualified) throws BadLocationException, 
      ClassNameNotFoundException {
      return getDocument().getEnclosingClassName(pos, fullyQualified);
    }
    
    
    public int findPrevEnclosingBrace(int pos, char opening, char closing) throws BadLocationException {
      return getDocument().findPrevEnclosingBrace(pos, opening, closing);
    }
    
       
    public int findNextEnclosingBrace(int pos, char opening, char closing) throws BadLocationException {
      return getDocument().findNextEnclosingBrace(pos, opening, closing);
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
    
    
    public int _getLineFirstCharPos(int pos) throws BadLocationException {
      return getDocument()._getLineFirstCharPos(pos);
    }
    
    
    public int findCharOnLine(int pos, char findChar) {
      return getDocument().findCharOnLine(pos, findChar);
    }
    
    
    public int _getIndentOfCurrStmt(int pos) throws BadLocationException {
      return getDocument()._getIndentOfCurrStmt(pos);
    }
    
    
    public int _getIndentOfCurrStmt(int pos, char[] delims) throws BadLocationException {
      return getDocument()._getIndentOfCurrStmt(pos, delims);
    }
    
    
    public int _getIndentOfCurrStmt(int pos, char[] delims, char[] whitespace) throws BadLocationException {
      return getDocument()._getIndentOfCurrStmt(pos, delims, whitespace);
    }
    



    



    
    public int findPrevDelimiter(int pos, char[] delims) throws BadLocationException {
      return getDocument().findPrevDelimiter(pos, delims);
    }
    
    public int findPrevDelimiter(int pos, char[] delims, boolean skipParenPhrases) throws BadLocationException {
      return getDocument().findPrevDelimiter(pos, delims, skipParenPhrases);
    }
    

    

    

    
    public void move(int dist) { getDocument().move(dist); }
    
    public ArrayList<HighlightStatus> getHighlightStatus(int start, int end) {
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
    
    public void clear() { getDocument().clear(); }
    
    
    
    
    public ReducedModelControl getReduced() { return getDocument().getReduced(); }
    
    
    public int getNumberOfLines() { return getLineOfOffset(getLength()); }
    
    
    public boolean isShadowed(int pos) { return getDocument().isShadowed(pos); }
    
    
    public int getLineOfOffset(int offset) { return getDefaultRootElement().getElementIndex(offset); }
    
    
    public int getOffsetOfLine(int line) {
      final int count = getDefaultRootElement().getElementCount();
      if (line >= count) { line = count - 1; }
      return getDefaultRootElement().getElement(line).getStartOffset();
    }
    



    




    
    public boolean containsClassOrInterfaceOrEnum() throws BadLocationException {
      return getDocument().containsClassOrInterfaceOrEnum();
    }
  } 
  
  private static class TrivialFSS implements FileSaveSelector {
    private File _file;
    private TrivialFSS(File file) { _file = file; }
    public File getFile() throws OperationCanceledException { return _file; }
    public boolean warnFileOpen(File f) { return true; }
    public boolean verifyOverwrite() { return true; }
    public boolean shouldSaveAfterFileMoved(OpenDefinitionsDocument doc, File oldFile) { return true; }
  }
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(NullFile f) { return new ConcreteOpenDefDoc(f); }
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(File f) throws IOException {
    if (! f.exists()) throw new FileNotFoundException("file " + f + " cannot be found");
    return new ConcreteOpenDefDoc(f);
  }
  
  
  protected OpenDefinitionsDocument _getOpenDocument(File file) {
    synchronized(_documentsRepos) { return _documentsRepos.get(file); }
  }
  
  
  public List<OpenDefinitionsDocument> getNonProjectDocuments() {
    List<OpenDefinitionsDocument> allDocs = getOpenDefinitionsDocuments();
    List<OpenDefinitionsDocument> selectedDocs = new LinkedList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument d : allDocs) {
      if (! d.inProjectPath() && ! d.isAuxiliaryFile ()) selectedDocs.add(d);
    }
    return selectedDocs;
  }
  
  
  public List<OpenDefinitionsDocument> getAuxiliaryDocuments() {
    List<OpenDefinitionsDocument> allDocs = getOpenDefinitionsDocuments();
    List<OpenDefinitionsDocument> selectedDocs = new LinkedList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument d : allDocs)
      if (d.isAuxiliaryFile()) selectedDocs.add(d);
    return selectedDocs;
  }
  
  
  public List<OpenDefinitionsDocument> getProjectDocuments() {
    List<OpenDefinitionsDocument> allDocs = getOpenDefinitionsDocuments();
    List<OpenDefinitionsDocument> projectDocs = new LinkedList<OpenDefinitionsDocument>();
    for (OpenDefinitionsDocument d: allDocs)
      if (d.inProjectPath() || d.isAuxiliaryFile()) projectDocs.add(d);
    return projectDocs;
  }
  
  public String fixPathForNavigator(String path) throws IOException {
    String parent = path.substring(0, path.lastIndexOf(File.separator ));
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
      String pkg = df.getPackage();
      doc.setPackage(pkg);  
      doc.setInitialVScroll(scroll.first());
      doc.setInitialHScroll( scroll.second());
      doc.setInitialSelStart(sel.first());
      doc.setInitialSelEnd(sel.second());
    }
    else {

      doc.setPackage(doc.getPackageNameFromDocument()); 
    }
    return doc;
  }
  
  
  protected static <T> T pop(ArrayList<T> stack) { return stack.remove(stack.size() - 1); }
  
  
  protected void addDocToNavigator(final OpenDefinitionsDocument doc) {
    try {
      if (doc.isUntitled()) _documentNavigator.addDocument(doc);
      else {
        String path = doc.getFile().getCanonicalPath();
        _documentNavigator.addDocument(doc, fixPathForNavigator(path));
      }
    }
    catch(IOException e) { _documentNavigator.addDocument(doc); }
    synchronized(_documentsRepos) { _documentsRepos.put(doc.getRawFile(), doc); }
  }
  
  
  protected void addDocToClassPath(OpenDefinitionsDocument doc) { }
  
  
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
      if (! inProject(f) && inProjectPath(d)) setProjectChanged(true);
    } 
    catch(FileMovedException fme) {
      
    }
    
    _notifier.fileOpened(d);
  }
  






  

  
  
  public OpenDefinitionsDocument getActiveDocument() { return  _activeDocument; }
  
  
  public void setActiveDocument(final OpenDefinitionsDocument doc) {
    
    

    


    
    try {
      Utilities.invokeAndWait(new Runnable() {  
        public void run() {

      _documentNavigator.setNextChangeModelInitiated(true);
      _documentNavigator.selectDocument(doc);
        }
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
    return getDocumentCount() == 1 && _activeDocument.isUntitled() &&
      ! _activeDocument.isModifiedSinceSave();
  }
  
  
  private void _ensureNotEmpty() {
    if (getDocumentCount() == 0) newFile(getMasterWorkingDirectory());
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
  
  
  public void setActiveFirstDocument() { setActiveDocument(getOpenDefinitionsDocuments().get(0)); }
  
  private void _setActiveDoc(INavigatorItem idoc) {


    _activeDocument = (OpenDefinitionsDocument) idoc;
    installActiveDocument();    
  }
  
  
  public void installActiveDocument() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.activeDocumentChanged(_activeDocument); } });
  }
  
  
  public void refreshActiveDocument() { 
    _documentNavigator.selectDocument(_activeDocument);
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.activeDocumentRefreshed(_activeDocument); } });  
  }

  
  public void ensureJVMStarterFinished() { }
  
  public void setCustomManifest(String manifest){ 
    _state.setProjectChanged(true);
    _state.setCustomManifest(manifest); 
  }
  public String getCustomManifest(){ return _state.getCustomManifest(); }
}

