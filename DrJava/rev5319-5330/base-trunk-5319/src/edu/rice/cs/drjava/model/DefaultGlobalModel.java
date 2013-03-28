

package edu.rice.cs.drjava.model;


import java.awt.EventQueue;

import java.io.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.TreeMap;

import edu.rice.cs.drjava.DrJava;

import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.drjava.model.compiler.DummyCompilerListener;
import edu.rice.cs.drjava.config.BooleanOption;
import edu.rice.cs.drjava.model.definitions.ClassNameNotFoundException;
import edu.rice.cs.drjava.model.definitions.InvalidPackageException;
import edu.rice.cs.drjava.model.debug.Breakpoint;
import edu.rice.cs.drjava.model.debug.Debugger;
import edu.rice.cs.drjava.model.debug.DebugException;
import edu.rice.cs.drjava.model.debug.NoDebuggerAvailable;
import edu.rice.cs.drjava.model.debug.DebugListener;
import edu.rice.cs.drjava.model.debug.DebugWatchData;
import edu.rice.cs.drjava.model.debug.DebugThreadData;
import edu.rice.cs.drjava.model.javadoc.JavadocModel;
import edu.rice.cs.drjava.model.javadoc.NoJavadocAvailable;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.repl.DummyInteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsDocument;
import edu.rice.cs.drjava.model.repl.InteractionsDJDocument;
import edu.rice.cs.drjava.model.repl.InteractionsListener;
import edu.rice.cs.drjava.model.repl.InteractionsScriptModel;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.drjava.model.compiler.CompilerListener;
import edu.rice.cs.drjava.model.compiler.CompilerModel;
import edu.rice.cs.drjava.model.compiler.DefaultCompilerModel;
import edu.rice.cs.drjava.model.compiler.CompilerInterface;
import edu.rice.cs.drjava.model.junit.DefaultJUnitModel;
import edu.rice.cs.drjava.model.junit.JUnitModel;
import edu.rice.cs.util.text.ConsoleDocument;

import edu.rice.cs.plt.reflect.JavaVersion;
import edu.rice.cs.plt.reflect.ReflectUtil;
import edu.rice.cs.plt.iter.IterUtil;
import edu.rice.cs.plt.io.IOUtil;

import edu.rice.cs.util.FileOpenSelector;
import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.NullFile;
import edu.rice.cs.util.AbsRelFile;
import edu.rice.cs.util.OperationCanceledException;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


public class DefaultGlobalModel extends AbstractGlobalModel {
  
  
  
  
  
  
  
  
  protected final InteractionsDJDocument _interactionsDocument;
  
  
  final MainJVM _jvm; 
  
  private final Thread _jvmStarter; 
  
  
  protected final DefaultInteractionsModel _interactionsModel;
  
  
  protected InteractionsListener _interactionsListener = new InteractionsListener() {
    public void interactionStarted() { }
    
    public void interactionEnded() { }
    
    public void interactionErrorOccurred(int offset, int length) { }
    
    public void interpreterResetting() { }
    
    public void interpreterReady(File wd) {
      File buildDir = _state.getBuildDirectory();
      if (buildDir != null) {
        
        _jvm.addBuildDirectoryClassPath(IOUtil.attemptAbsoluteFile(buildDir));
      }
    }
    
    public void interpreterResetFailed(Throwable t) { }
    
    public void interpreterExited(int status) { }
    
    public void interpreterChanged(boolean inProgress) { }
    
    public void interactionIncomplete() { }
  };
  
  private CompilerListener _clearInteractionsListener = new DummyCompilerListener() {
    public void compileEnded(File workDir, List<? extends File> excludedFiles) {
      
      if ( (_compilerModel.getNumErrors() == 0 || _compilerModel.getCompilerErrorModel().hasOnlyWarnings())
            && ! _junitModel.isTestInProgress() && _resetAfterCompile) {

        resetInteractions(workDir);  
      }
    }
    public void activeCompilerChanged() {
      File workDir = _interactionsModel.getWorkingDirectory();
      resetInteractions(workDir, true);  
    }
  };
  
  
  
  
  private final CompilerModel _compilerModel;
  
  
  private volatile boolean _resetAfterCompile = true;
  
  
  private volatile int _numCompErrors = 0;
  
  
  
  
  private final DefaultJUnitModel _junitModel;
  
  
  
  
  protected volatile JavadocModel _javadocModel;
  
  
  
  
  private volatile Debugger _debugger;
  
  
  
  public DefaultGlobalModel() {
    Iterable<? extends JDKToolsLibrary> tools = findLibraries();
    List<CompilerInterface> compilers = new LinkedList<CompilerInterface>();
    _debugger = null;
    _javadocModel = null;
    for (JDKToolsLibrary t : tools) {
      
      if (t.compiler().isAvailable() && t.version().supports(JavaVersion.JAVA_5)) {
          compilers.add(t.compiler());
      }
      if (_debugger == null && t.debugger().isAvailable()) { _debugger = t.debugger(); }
      if (_javadocModel == null && t.javadoc().isAvailable()) { _javadocModel = t.javadoc(); }
    }
    if (_debugger == null) { _debugger = NoDebuggerAvailable.ONLY; }
    if (_javadocModel == null) { _javadocModel = new NoJavadocAvailable(this); }
    
    File workDir = Utilities.TEST_MODE ? new File(System.getProperty("user.home")) : getWorkingDirectory();
    _jvm = new MainJVM(workDir);

    _compilerModel = new DefaultCompilerModel(this, compilers);
    _junitModel = new DefaultJUnitModel(_jvm, _compilerModel, this);
    _interactionsDocument = new InteractionsDJDocument(_notifier);
    
    _interactionsModel = new DefaultInteractionsModel(this, _jvm, _interactionsDocument, workDir);
    _interactionsModel.addListener(_interactionsListener);
    _jvm.setInteractionsModel(_interactionsModel);
    _jvm.setJUnitModel(_junitModel);
    
    _setupDebugger();
    
    
    _interactionsModel.addListener(_notifier);
    _compilerModel.addListener(_notifier);
    _junitModel.addListener(_notifier);
    _javadocModel.addListener(_notifier);
    
    
    
    
    _compilerModel.addListener(_clearInteractionsListener);
    
    _jvmStarter = new Thread("Start interpreter JVM") {
      public void run() { _jvm.startInterpreterJVM(); }
    };
    _jvmStarter.start();
    


  }

  
  
  private static JavaVersion.FullVersion coarsenVersion(JavaVersion.FullVersion tVersion) {
    BooleanOption displayAllOption = edu.rice.cs.drjava.config.OptionConstants.DISPLAY_ALL_COMPILER_VERSIONS;
    if (!DrJava.getConfig().getSetting(displayAllOption).booleanValue()) {
      tVersion = tVersion.onlyMajorVersionAndVendor();
    }
    return tVersion;
  }
  
  private Iterable<JDKToolsLibrary> findLibraries() {
    
    
    
    
    
    
    Map<JavaVersion.FullVersion, JDKToolsLibrary> results = new TreeMap<JavaVersion.FullVersion, JDKToolsLibrary>();
    
    File configTools = DrJava.getConfig().getSetting(JAVAC_LOCATION);
    if (configTools != FileOps.NULL_FILE) {
      JDKToolsLibrary fromConfig = JarJDKToolsLibrary.makeFromFile(configTools, this);
      if (fromConfig.isValid()) { 
        JarJDKToolsLibrary.msg("From config: "+fromConfig);
        results.put(coarsenVersion(fromConfig.version()), fromConfig);
      }
      else { JarJDKToolsLibrary.msg("From config: invalid "+fromConfig); }
    }
    else { JarJDKToolsLibrary.msg("From config: not set"); }
    
    Iterable<JDKToolsLibrary> allFromRuntime = JDKToolsLibrary.makeFromRuntime(this);

    for(JDKToolsLibrary fromRuntime: allFromRuntime) {
      JavaVersion.FullVersion runtimeVersion = fromRuntime.version();
      if (fromRuntime.isValid()) {
        if (!results.containsKey(coarsenVersion(runtimeVersion))) {
          JarJDKToolsLibrary.msg("From runtime: "+fromRuntime);
          results.put(coarsenVersion(runtimeVersion), fromRuntime);
        }
        else { JarJDKToolsLibrary.msg("From runtime: duplicate "+fromRuntime); }
      }
      else { JarJDKToolsLibrary.msg("From runtime: invalid "+fromRuntime); }
    }
    
    Iterable<JarJDKToolsLibrary> fromSearch = JarJDKToolsLibrary.search(this);
    for (JDKToolsLibrary t : fromSearch) {
      JavaVersion.FullVersion tVersion = t.version();
      if (!results.containsKey(coarsenVersion(tVersion))) {
        JarJDKToolsLibrary.msg("From search: "+t);
        results.put(coarsenVersion(tVersion), t);
      }
      else { JarJDKToolsLibrary.msg("From search: duplicate "+t); }
    }
    
    return IterUtil.reverse(results.values());
  }
  

  
  
  public void setBuildDirectory(File f) {
    _state.setBuildDirectory(f);
    if (f != FileOps.NULL_FILE) {
      
      _jvm.addBuildDirectoryClassPath(IOUtil.attemptAbsoluteFile(f));
    }
    
    _notifier.projectBuildDirChanged();
    setProjectChanged(true);
    setClassPathChanged(true);
  }
  
  
  
  
  public DefaultInteractionsModel getInteractionsModel() { return _interactionsModel; }
  
  
  public InteractionsDJDocument getSwingInteractionsDocument() { return _interactionsDocument; }
  
  public InteractionsDocument getInteractionsDocument() { return _interactionsModel.getDocument(); }
  
  
  public CompilerModel getCompilerModel() { return _compilerModel; }
  
  
  public JUnitModel getJUnitModel() { return _junitModel; }
  
  
  public JavadocModel getJavadocModel() { return _javadocModel; }
  
  public int getNumCompErrors() { return _numCompErrors; }
  public void setNumCompErrors(int num) { _numCompErrors = num; }
  
  
  public void dispose() {
    ensureJVMStarterFinished();
    _jvm.dispose();
    _notifier.removeAllListeners();  
  }

  
  public void ensureJVMStarterFinished() {
    try { _jvmStarter.join(); } 
    catch (InterruptedException e) { throw new UnexpectedException(e); }
  }
  
  
  public void disposeExternalResources() { _jvm.stopInterpreterJVM(); }
  
  public void resetInteractions(File wd) { resetInteractions(wd, false); }
  
  
  public void resetInteractions(File wd, boolean forceReset) {
    assert _interactionsModel._pane != null;
    
    debug.logStart();
    File workDir = _interactionsModel.getWorkingDirectory();
    if (wd == null) { wd = workDir; }
    forceReset |= isClassPathChanged();
    forceReset |= !wd.equals(workDir);
    
    DrJava.getConfig().setSetting(LAST_INTERACTIONS_DIRECTORY, wd);
    getDebugger().setAutomaticTraceEnabled(false);
    _interactionsModel.resetInterpreter(wd, forceReset);
    debug.logEnd();
  }
  
  
  public void interpretCurrentInteraction() { _interactionsModel.interpretCurrentInteraction(); }
  
  
  public void loadHistory(final FileOpenSelector selector) { 
    Utilities.invokeLater(new Runnable() { 
      public void run() { 
        try {_interactionsModel.loadHistory(selector); } 
        catch(IOException e) { throw new UnexpectedException(e); }
      }
    });
  }
  
  
  public InteractionsScriptModel loadHistoryAsScript(FileOpenSelector selector)
    throws IOException, OperationCanceledException {
    return _interactionsModel.loadHistoryAsScript(selector);
  }
  
  
  public void clearHistory() { _interactionsModel.getDocument().clearHistory(); }
  
  
  public void saveHistory(FileSaveSelector selector) throws IOException {
    _interactionsModel.getDocument().saveHistory(selector);
  }

  
  public void saveConsoleCopy(ConsoleDocument doc, FileSaveSelector selector) throws IOException {
    doc.saveCopy(selector);
  }
  
  
  public void saveHistory(FileSaveSelector selector, String editedVersion) throws IOException {
    _interactionsModel.getDocument().saveHistory(selector, editedVersion);
  }
  
  
  public String getHistoryAsStringWithSemicolons() {
    return _interactionsModel.getDocument().getHistoryAsStringWithSemicolons();
  }
  
  
  public String getHistoryAsString() {
    return _interactionsModel.getDocument().getHistoryAsString();
  }
  
  
  public void printDebugMessage(String s) {
    _interactionsModel.getDocument().
      insertBeforeLastPrompt(s + "\n", InteractionsDocument.DEBUGGER_STYLE);
  }
  
  
  public Iterable<File> getInteractionsClassPath() {
    return _jvm.getClassPath().unwrap(IterUtil.<File>empty());
  }
  
  
  void setResetAfterCompile(boolean shouldReset) { _resetAfterCompile = shouldReset; }
  
  
  public Debugger getDebugger() { return _debugger; }
  
  
  public int getDebugPort() throws IOException { return _interactionsModel.getDebugPort(); }
  
  
  
  
  class ConcreteOpenDefDoc extends AbstractGlobalModel.ConcreteOpenDefDoc {    
    
    ConcreteOpenDefDoc(File f) { super(f); }
    
    
    ConcreteOpenDefDoc(NullFile f) { super(f); }
    
    
    public void startCompile() throws IOException { 
      assert EventQueue.isDispatchThread();
      _compilerModel.compile(ConcreteOpenDefDoc.this); 
    }
    
    private volatile InteractionsListener _runMain;
    
    
    protected void _runInInteractions(final String command, String qualifiedClassName) throws ClassNameNotFoundException, 
      IOException {
      
      assert EventQueue.isDispatchThread();

      _notifier.prepareForRun(ConcreteOpenDefDoc.this);
      
      String tempClassName = null;
      
      if(qualifiedClassName == null)
        tempClassName = getDocument().getQualifiedClassName();
      else
        tempClassName = qualifiedClassName;
      
      
      final String className = tempClassName;
      final InteractionsDocument iDoc = _interactionsModel.getDocument();
      if (! checkIfClassFileInSync()) {
        iDoc.insertBeforeLastPrompt(DOCUMENT_OUT_OF_SYNC_MSG, InteractionsDocument.ERROR_STYLE);
        return;
      }
      
      final boolean wasDebuggerEnabled = getDebugger().isReady();
      
      _runMain = new DummyInteractionsListener() {
        public void interpreterReady(File wd) {
          
          
          
          
          
          
          _interactionsModel.removeListener(_runMain);  
          
          
          
          javax.swing.SwingUtilities.invokeLater(new Runnable() {   
            public void run() {
              
              if (wasDebuggerEnabled && (! getDebugger().isReady())) {

                try { getDebugger().startUp(); } catch(DebugException de) {  }
              }
              
              iDoc.clearCurrentInput();
              iDoc.append(java.text.MessageFormat.format(command, className), null);
              
              
              new Thread("Running document") {
                public void run() { _interactionsModel.interpretCurrentInteraction(); }
              }.start();
            }
          });
        }
      };
      
      File oldWorkDir = _interactionsModel.getWorkingDirectory();
      _interactionsModel.addListener(_runMain);
      
      File workDir;
      workDir = getWorkingDirectory();
      
      
      resetInteractions(workDir, !workDir.equals(oldWorkDir));
    }
    
    
    public void runMain(String qualifiedClassName) throws ClassNameNotFoundException, IOException {
      _runInInteractions("java {0}", qualifiedClassName);
    }
    
    
    public void runApplet(String qualifiedClassName) throws ClassNameNotFoundException, IOException {
      _runInInteractions("applet {0}", qualifiedClassName);
    }
    
    
    public void runSmart(String qualifiedClassName) throws ClassNameNotFoundException, IOException {
      _runInInteractions("run {0}", qualifiedClassName);
    }
    
    
    public void startJUnit() throws ClassNotFoundException, IOException { _junitModel.junit(this); }
    
    
    public void generateJavadoc(FileSaveSelector saver) throws IOException {
      
      _javadocModel.javadocDocument(this, saver);
    }
    
    
    public void removeFromDebugger() { getBreakpointManager().removeRegions(this); }
    
    
    






  } 
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(NullFile f) { return new ConcreteOpenDefDoc(f); }
  
  
  protected ConcreteOpenDefDoc _createOpenDefinitionsDocument(File f) throws IOException { 
    if (! f.exists()) throw new FileNotFoundException("file " + f + " cannot be found");
    return new ConcreteOpenDefDoc(f); 
  }
  
  
  protected void addDocToClassPath(OpenDefinitionsDocument doc) {
    try {
      File sourceRoot = doc.getSourceRoot();
      if (doc.isAuxiliaryFile()) { _interactionsModel.addProjectFilesClassPath(sourceRoot); }
      else { _interactionsModel.addExternalFilesClassPath(sourceRoot); }
      setClassPathChanged(true);
    }
    catch (InvalidPackageException e) {
      
    }
  }
  
  private void _setupDebugger() {
    _jvm.setDebugModel(_debugger.callback());
    
    
    getBreakpointManager().addListener(new RegionManagerListener<Breakpoint>() {
      public void regionAdded(final Breakpoint bp) { setProjectChanged(true); }
      public void regionChanged(final Breakpoint bp) { setProjectChanged(true); }
      public void regionRemoved(final Breakpoint bp) { 
        try { getDebugger().removeBreakpoint(bp); } 
        catch(DebugException de) {
          
          





        }
        setProjectChanged(true);
      }
    });
    getBookmarkManager().addListener(new RegionManagerListener<MovingDocumentRegion>() {
      public void regionAdded(MovingDocumentRegion r) { setProjectChanged(true); }
      public void regionChanged(MovingDocumentRegion r) { setProjectChanged(true); }
      public void regionRemoved(MovingDocumentRegion r) { setProjectChanged(true); }
    });
    
    _debugger.addListener(new DebugListener() {
      public void watchSet(final DebugWatchData w) { setProjectChanged(true); }
      public void watchRemoved(final DebugWatchData w) { setProjectChanged(true); }    
      
      public void regionAdded(final Breakpoint bp) { }
      public void regionChanged(final Breakpoint bp) { }
      public void regionRemoved(final Breakpoint bp) { }
      public void debuggerStarted() { }
      public void debuggerShutdown() { }
      public void threadLocationUpdated(OpenDefinitionsDocument doc, int lineNumber, boolean shouldHighlight) { }
      public void breakpointReached(final Breakpoint bp) { }
      public void stepRequested() { }
      public void currThreadSuspended() { }
      public void currThreadResumed() { }
      public void threadStarted() { }
      public void currThreadDied() { }
      public void nonCurrThreadDied() {  }
      public void currThreadSet(DebugThreadData thread) { }
    });
  }
  
  
  public Iterable<File> getClassPath() {
    Iterable<File> result = IterUtil.empty();
    
    if (isProjectActive()) {
      File buildDir = getBuildDirectory();
      if (buildDir != null) { result = IterUtil.compose(result, buildDir); }
      
      
      File projRoot = getProjectRoot();
      if (projRoot != null) { result = IterUtil.compose(result, projRoot); }
      
      Iterable<AbsRelFile> projectExtras = getExtraClassPath();
      if (projectExtras != null) { result = IterUtil.compose(result, projectExtras); }
    }
    else { result = IterUtil.compose(result, getSourceRootSet()); }
    
    Vector<File> globalExtras = DrJava.getConfig().getSetting(EXTRA_CLASSPATH);
    if (globalExtras != null) { result = IterUtil.compose(result, globalExtras); }
    
    
    result = IterUtil.compose(result, ReflectUtil.SYSTEM_CLASS_PATH);
    
    return result;
  }
  
  
  public void resetInteractionsClassPath() {

    Iterable<AbsRelFile> projectExtras = getExtraClassPath();
    
    if (projectExtras != null)  for (File cpE : projectExtras) { _interactionsModel.addProjectClassPath(cpE); }
    
    Vector<File> cp = DrJava.getConfig().getSetting(EXTRA_CLASSPATH);
    if (cp != null) {
      for (File f : cp) { _interactionsModel.addExtraClassPath(f); }
    }
    
    for (OpenDefinitionsDocument odd: getAuxiliaryDocuments()) {
      
      try { _interactionsModel.addProjectFilesClassPath(odd.getSourceRoot()); }
      catch(InvalidPackageException e) {   }
    }
    
    for (OpenDefinitionsDocument odd: getNonProjectDocuments()) {
      
      try {
        File sourceRoot = odd.getSourceRoot();
        if (sourceRoot != null) _interactionsModel.addExternalFilesClassPath(sourceRoot); 
      }
      catch(InvalidPackageException e) {  }
    }
    
    
    
    _interactionsModel.addProjectFilesClassPath(getProjectRoot());  
    setClassPathChanged(false);  
  } 
}
