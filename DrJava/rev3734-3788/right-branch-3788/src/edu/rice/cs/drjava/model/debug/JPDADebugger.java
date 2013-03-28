

package edu.rice.cs.drjava.model.debug;

import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import java.util.Enumeration;
import java.util.Vector;


import edu.rice.cs.util.StringOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.drjava.model.DefaultGlobalModel;
import edu.rice.cs.drjava.model.repl.DefaultInteractionsModel;
import edu.rice.cs.drjava.model.GlobalModelListener;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.DummyGlobalModelListener;
import edu.rice.cs.util.Log;

import com.sun.jdi.*;
import com.sun.jdi.connect.*;
import com.sun.jdi.request.*;
import com.sun.jdi.event.*;
import javax.swing.SwingUtilities;


public class JPDADebugger implements Debugger, DebugModelCallback {
  private static final boolean printMessages = false;
  protected final PrintStream printStream = System.out;
  private static final int OBJECT_COLLECTED_TRIES = 5;

  
  private DefaultGlobalModel _model;

  
  private volatile VirtualMachine _vm;

  
  private EventRequestManager _eventManager;

  
  private Vector<Breakpoint> _breakpoints;

  
  private Vector<DebugWatchData> _watches;

  
  private PendingRequestManager _pendingRequestManager;

  
  final DebugEventNotifier _notifier = new DebugEventNotifier();

  
  private ThreadReference _runningThread;

  
  private RandomAccessStack _suspendedThreads;

  
  private ObjectReference _interpreterJVM;

  private GlobalModelListener _watchListener;

  
  private Throwable _eventHandlerError;

  
  private final Log _log;

  
  public JPDADebugger(DefaultGlobalModel model) {
    _model = model;
    _vm = null;
    _eventManager = null;
    _breakpoints = new Vector<Breakpoint>();
    _watches = new Vector<DebugWatchData>();
    _suspendedThreads = new RandomAccessStack();
    _pendingRequestManager = new PendingRequestManager(this);
    _runningThread = null;
    _interpreterJVM = null;
    _eventHandlerError = null;
    _log = new Log("DebuggerLog", false);

    
    
    _watchListener = new DummyGlobalModelListener() {
      public void interactionEnded() {
        try {
          _updateWatches();
        }
        catch(DebugException de) {
          _log("couldn't update watches", de);
        }
      }
    };
  }

  
  public void addListener(DebugListener listener) {
    _notifier.addListener(listener);
  }

  
  public void removeListener(DebugListener listener) {
    _notifier.removeListener(listener);
  }

  protected VirtualMachine getVM() { return _vm; }

  
  protected void _log(String message) {
    _log.logTime(message);
  }

  
  protected void _log(String message, Throwable t) {
    _log.logTime(message, t);
  }

  
  public boolean isAvailable() { return true; }

  
  public boolean isReady() { return _vm != null; }
  
  public boolean inDebugMode() { return isReady(); }
  
  
  protected synchronized void _ensureReady() throws DebugException {
    if (!isReady()) throw new IllegalStateException("Debugger is not active.");
    
    if (_eventHandlerError != null) {
      Throwable t = _eventHandlerError;
      _eventHandlerError = null;
      throw new DebugException("Error in Debugger Event Handler: " + t);
    }
  }

  
  synchronized void eventHandlerError(Throwable t) {
    _log("Error in EventHandlerThread: " + t);
    _eventHandlerError = t;
  }

  
  public synchronized void startup() throws DebugException {
    if (! isReady()) {
      _eventHandlerError = null;
      
      for (OpenDefinitionsDocument doc: _model.getOpenDefinitionsDocuments()) {
        doc.checkIfClassFileInSync();
      }

      _attachToVM();

      
      ThreadDeathRequest tdr = _eventManager.createThreadDeathRequest();
      tdr.setSuspendPolicy(EventRequest.SUSPEND_EVENT_THREAD);
      tdr.enable();

      
      EventHandlerThread eventHandler = new EventHandlerThread(this, _vm);
      eventHandler.start();
      _model.addListener(_watchListener);
      
      
      Vector<Breakpoint> oldBreakpoints = new Vector<Breakpoint>(_breakpoints);
      removeAllBreakpoints();
      for (int i = 0; i < oldBreakpoints.size(); i++) {
        Breakpoint bp = oldBreakpoints.get(i);        
        setBreakpoint(new Breakpoint(bp.getDocument(), bp.getOffset(), bp.getLineNumber(), bp.isEnabled(), this));
      }
    }

    else
      
      throw new IllegalStateException("Debugger has already been started.");
  }

  
  private void _attachToVM() throws DebugException {
    
    _model.waitForInterpreter();

    
    AttachingConnector connector = _getAttachingConnector();

    
    Map<String, Connector.Argument> args = connector.defaultArguments();  
    Connector.Argument port = args.get("port");
    Connector.Argument host = args.get("hostname");
    try {
      int debugPort = _model.getDebugPort();
      port.setValue("" + debugPort);
      host.setValue("127.0.0.1"); 
      _vm = connector.attach(args);
      _eventManager = _vm.eventRequestManager();
    }
    catch (IOException ioe) {
      throw new DebugException("Could not connect to VM: " + ioe);
    }
    catch (IllegalConnectorArgumentsException icae) {
      throw new DebugException("Could not connect to VM: " + icae);
    }

    _interpreterJVM = _getInterpreterJVMRef();
  }

  
  protected AttachingConnector _getAttachingConnector()
    throws DebugException {
    VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
    List<AttachingConnector> connectors = vmm.attachingConnectors();  
    AttachingConnector connector = null;
    for (AttachingConnector conn: connectors) {
      if (conn.name().equals("com.sun.jdi.SocketAttach"))  connector = conn;
    }
    if (connector == null) throw new DebugException("Could not find an AttachingConnector!");
    return connector;
  }

  
  protected ObjectReference _getInterpreterJVMRef()
    throws DebugException {
    String className = "edu.rice.cs.drjava.model.repl.newjvm.InterpreterJVM";
    List<ReferenceType> referenceTypes = _vm.classesByName(className);  
    if (referenceTypes.size() > 0) {
      ReferenceType rt = referenceTypes.get(0);
      Field field = rt.fieldByName("ONLY");
      if (field == null) throw new DebugException("Unable to get ONLY field");
      return (ObjectReference) rt.getValue(field);
    }
    else throw new DebugException("Could not get a reference to interpreterJVM");
  }

  
  public synchronized void shutdown() {
    if (isReady()) {
      Runnable command = new Runnable() { public void run() { _model.removeListener(_watchListener); } };
      
      
      SwingUtilities.invokeLater(command);
      
      _removeAllDebugInterpreters();
      
      try { _vm.dispose(); }
      catch (VMDisconnectedException vmde) {
        
      }
      finally {
        _model.getInteractionsModel().setToDefaultInterpreter();
        _vm = null;
        _suspendedThreads = new RandomAccessStack();
        _eventManager = null;
        _runningThread = null;
        try {
          _updateWatches();
        }
        catch (DebugException de) {
          
          _log("Could not remove breakpoints/watches or update watches: " + de);
        }
      }
    }
  }


  
  synchronized EventRequestManager getEventRequestManager() { return _eventManager; }

  
  synchronized PendingRequestManager getPendingRequestManager() { return _pendingRequestManager; }

  
  synchronized boolean setCurrentThread(ThreadReference thread) {
    if (!thread.isSuspended()) {
      throw new IllegalArgumentException("Thread must be suspended to set " +
                                         "as current.  Given: " + thread);
    }

    try {
      if ((_suspendedThreads.isEmpty() || 
           !_suspendedThreads.contains(thread.uniqueID())) && (thread.frameCount() > 0)) {
        _suspendedThreads.push(thread);
        return true;
      }
      else return false;
    }
    catch (IncompatibleThreadStateException itse) {
      
      
      throw new UnexpectedException(itse);
    }
  }

  
  public synchronized void setCurrentThread(DebugThreadData threadData) throws DebugException {
    _ensureReady();

    if (threadData == null) {
      throw new IllegalArgumentException("Cannot set current thread to null.");
    }

    ThreadReference threadRef = _getThreadFromDebugThreadData(threadData);

    
    

    
    





    
    
    if (_suspendedThreads.contains(threadRef.uniqueID())) {
      _suspendedThreads.remove(threadRef.uniqueID());
    }
    if (!threadRef.isSuspended()) {
      throw new IllegalArgumentException("Given thread must be suspended.");


















      
    }

    _suspendedThreads.push(threadRef);

    try {
      if (threadRef.frameCount() <= 0) {
        printMessage(threadRef.name() +
                     " could not be suspended since it has no stackframes.");
        resume();
        return;
      }
    }
    catch (IncompatibleThreadStateException e) {
      throw new DebugException("Could not suspend thread: " + e);
    }

    
    _switchToInterpreterForThreadReference(threadRef);
    _switchToSuspendedThread();
    printMessage("The current thread has changed.");
  }

  
  synchronized ThreadReference getCurrentThread() {
    
    return _suspendedThreads.peek();
  }

  
  synchronized ThreadReference getThreadAt(int i) {
    return _suspendedThreads.peekAt(i);
  }

  
  synchronized ThreadReference getCurrentRunningThread() {
    return _runningThread;
  }

  
  public synchronized boolean hasSuspendedThreads() throws DebugException {
    _ensureReady();
    return _suspendedThreads.size() > 0;
  }


  
  public synchronized boolean isCurrentThreadSuspended() throws DebugException {
    _ensureReady();
    return hasSuspendedThreads() && !hasRunningThread();
  }

  
  public synchronized boolean hasRunningThread() throws DebugException {
    _ensureReady();
    return _runningThread != null;
  }

  
  synchronized Vector<ReferenceType> getReferenceTypes(String className) {
    return getReferenceTypes(className, DebugAction.ANY_LINE);
  }

  
  synchronized Vector<ReferenceType> getReferenceTypes(String className,
                                                       int lineNumber) {
    
    List<ReferenceType> classes;
    try {
      classes = _vm.classesByName(className);  
    }
    catch (VMDisconnectedException vmde) {
      
      return new Vector<ReferenceType>();
    }

    
    Vector<ReferenceType> refTypes = new Vector<ReferenceType>();
    ReferenceType ref;
    for (int i=0; i < classes.size(); i++) {
      ref = classes.get(i);

      if (lineNumber != DebugAction.ANY_LINE) {
        List<Location> lines = new LinkedList<Location>();
        try {
          lines = ref.locationsOfLine(lineNumber); 
        }
        catch (AbsentInformationException aie) {
          
        }
        catch (ClassNotPreparedException cnpe) {
          
          continue;
        }
        
        if (lines.size() == 0) {
          
          
          List<ReferenceType> innerRefs = ref.nestedTypes();  
          ref = null;
          for (int j = 0; j < innerRefs.size(); j++) {
            try {
              ReferenceType currRef = innerRefs.get(j);
              lines = currRef.locationsOfLine(lineNumber);  
              if (lines.size() > 0) {
                ref = currRef;
                break;
              }
            }
            catch (AbsentInformationException aie) {
              
            }
            catch (ClassNotPreparedException cnpe) {
              
            }
          }
        }
      }
      if ((ref != null) && ref.isPrepared()) {
        refTypes.add(ref);
      }
    }
    return refTypes;
  }

  
  protected ThreadReference _getThreadFromDebugThreadData(DebugThreadData d)
    throws NoSuchElementException
  {
    List<ThreadReference> threads = _vm.allThreads(); 
    Iterator<ThreadReference> iterator = threads.iterator();
    while (iterator.hasNext()) {
      ThreadReference threadRef = iterator.next();
      if (threadRef.uniqueID() == d.getUniqueID()) {
        return threadRef;
      }
    }
    
    throw new NoSuchElementException("Thread " + d.getName() +
                                     " not found in virtual machine!");
  }

  

  

  
  protected synchronized void _resumeFromStep()
    throws DebugException
  {
    _resumeHelper(true);
  }

  
  public synchronized void resume() throws DebugException {
    _ensureReady();
    _resumeHelper(false);
  }

  
  protected synchronized void _resumeHelper(boolean fromStep)
    throws DebugException
  {
    try {
      ThreadReference thread = _suspendedThreads.pop();

      if (printMessages) {
        printStream.println("In resumeThread()");
      }
      _resumeThread(thread, fromStep);
    }
    catch (NoSuchElementException e) {
      throw new DebugException("No thread to resume.");
    }
  }

  
  public synchronized void resume(DebugThreadData threadData)
    throws DebugException
  {
    _ensureReady();
    ThreadReference thread = _suspendedThreads.remove(threadData.getUniqueID());
    _resumeThread(thread, false);
  }

  
  private void _resumeThread(ThreadReference thread, boolean fromStep)
    throws DebugException
  {
    if (thread == null) {
      throw new IllegalArgumentException("Cannot resume a null thread");
    }

    int suspendCount = thread.suspendCount();
    if (printMessages) {
      printStream.println("Getting suspendCount = " + suspendCount);
    }

    _runningThread = thread;
    if (!fromStep) {
      
      _copyVariablesFromInterpreter();
      _updateWatches();
    }
    try {
      _removeCurrentDebugInterpreter(fromStep);
      currThreadResumed();
    }
    catch(DebugException e) {  
      throw new UnexpectedException(e);
    }

    
    for (int i=suspendCount; i>0; i--) {
      thread.resume();
    }

    

    
    if (!fromStep && !_suspendedThreads.isEmpty()) {
      _switchToSuspendedThread();
    }
  }

  
  public synchronized void step(int flag) throws DebugException {
    _ensureReady();
    _stepHelper(flag, true);
  }

  
  private synchronized void _stepHelper(int flag, boolean shouldNotify)
    throws DebugException
  {
    if (_suspendedThreads.size() <= 0 || _runningThread != null) {
      throw new IllegalStateException("Cannot step if the current thread is not suspended.");
    }

    if (printMessages) {
      printStream.println("About to peek...");
    }

    ThreadReference thread = _suspendedThreads.peek();
    if (printMessages) {
      printStream.println("Stepping " + thread.toString());
    }

    
    
    
    _runningThread = thread;
    _copyVariablesFromInterpreter();

    if (printMessages) {
      printStream.println("Deleting pending requests...");
    }

    
    
    List<StepRequest> steps = _eventManager.stepRequests();  
    for (int i = 0; i < steps.size(); i++) {
      StepRequest step = steps.get(i);
      if (step.thread().equals(thread)) {
        _eventManager.deleteEventRequest(step);
        break;
      }
    }

    if (printMessages) printStream.println("Issued step request");
    
    new Step(this, StepRequest.STEP_LINE, flag);
    if (shouldNotify) notifyStepRequested();
    if (printMessages) printStream.println("About to resume");
    _resumeFromStep();
  }


  
  public synchronized void addWatch(String field) throws DebugException {
    

    final DebugWatchData w = new DebugWatchData(field);
    _watches.add(w);
    _updateWatches();
    
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.watchSet(w); } });
  }

  
  public synchronized void removeWatch(String field) throws DebugException {
    

    for (int i=0; i < _watches.size(); i++) {
      final DebugWatchData watch = _watches.get(i);
      if (watch.getName().equals(field)) {
        _watches.remove(i);
        Utilities.invokeLater(new Runnable() { public void run() { _notifier.watchRemoved(watch); } });
      }
    }
  }

  
  public synchronized void removeWatch(int index) throws DebugException {
    

    if (index < _watches.size()) {
      final DebugWatchData watch = _watches.get(index);
      _watches.remove(index);
      Utilities.invokeLater(new Runnable() { public void run() { _notifier.watchRemoved(watch); } });
    }
  }

  
  public synchronized void removeAllWatches() throws DebugException {
    

    while (_watches.size() > 0) {
      removeWatch( _watches.get(0).getName());
    }
  }

  
  public synchronized void toggleBreakpoint(OpenDefinitionsDocument doc, int offset, int lineNum, boolean enabled) 
    throws DebugException {
    
    Breakpoint breakpoint = doc.getBreakpointAt(offset);
    
    if (breakpoint == null)  setBreakpoint(new Breakpoint (doc, offset, lineNum, enabled, this));
    else removeBreakpoint(breakpoint);
  }

  
  public synchronized void setBreakpoint(final Breakpoint breakpoint) throws DebugException {
    breakpoint.getDocument().checkIfClassFileInSync();

    _breakpoints.add(breakpoint);
    breakpoint.getDocument().addBreakpoint(breakpoint);

    Utilities.invokeLater(new Runnable() { public void run() { _notifier.breakpointSet(breakpoint); } });
  }

 
  public synchronized void removeBreakpoint(final Breakpoint breakpoint) throws DebugException {
    _breakpoints.remove(breakpoint);

    Vector<BreakpointRequest> requests = breakpoint.getRequests();
    if (requests.size() > 0 && _eventManager != null) {
      
      try {
        for (int i=0; i < requests.size(); i++) {
          _eventManager.deleteEventRequest(requests.get(i));
        }
      }
      catch (VMMismatchException vme) {
        
        
        _log("VMMismatch when removing breakpoint.", vme);
      }
      catch (VMDisconnectedException vmde) {
        
        
        _log("VMDisconnected when removing breakpoint.", vmde);
      }
    }

    
    _pendingRequestManager.removePendingRequest(breakpoint);
    breakpoint.getDocument().removeBreakpoint(breakpoint);

    Utilities.invokeLater(new Runnable() { public void run() { _notifier.breakpointRemoved(breakpoint); } });
  }

  
  public synchronized void removeAllBreakpoints() throws DebugException {
    while (_breakpoints.size() > 0) {
      removeBreakpoint( _breakpoints.get(0));
    }
  }

  
  synchronized void reachedBreakpoint(BreakpointRequest request) {

    Object property = request.getProperty("debugAction");
    if ( (property != null) && (property instanceof Breakpoint) ) {
      final Breakpoint breakpoint = (Breakpoint) property;
      printMessage("Breakpoint hit in class " + breakpoint.getClassName() + "  [line " +
                   breakpoint.getLineNumber() + "]");

      Utilities.invokeLater(new Runnable() { public void run() { _notifier.breakpointReached(breakpoint); } });
    }
    else {
      
      _log("Reached a breakpoint without a debugAction property: " + request);
    }
  }

  
  public synchronized Vector<Breakpoint> getBreakpoints() throws DebugException {
    Vector<Breakpoint> sortedBreakpoints = new Vector<Breakpoint>();
    List<OpenDefinitionsDocument> docs = _model.getOpenDefinitionsDocuments();
    for (int i = 0; i < docs.size(); i++) {
      Vector<Breakpoint> docBreakpoints =
        docs.get(i).getBreakpoints();
      for (int j = 0; j < docBreakpoints.size(); j++) {
        sortedBreakpoints.add(docBreakpoints.get(j));
      }
    }
    return sortedBreakpoints;
  }

  
  public synchronized void printBreakpoints() throws DebugException {
    Enumeration<Breakpoint> breakpoints = getBreakpoints().elements();
    if (breakpoints.hasMoreElements()) {
      printMessage("Breakpoints: ");
      while (breakpoints.hasMoreElements()) {
        Breakpoint breakpoint = breakpoints.nextElement();
        printMessage("  " + breakpoint.getClassName() +
                                 "  [line " + breakpoint.getLineNumber() + "]");
      }
    }
    else {
      printMessage("No breakpoints set.");
    }
  }

  
  public synchronized Vector<DebugWatchData> getWatches() throws DebugException {
    
    return _watches;
  }

  
  public synchronized Vector<DebugThreadData> getCurrentThreadData()
    throws DebugException
  {
    _ensureReady();
    List<ThreadReference> listThreads; 
    try {
      listThreads = _vm.allThreads();  
    }
    catch (VMDisconnectedException vmde) {
      
      return new Vector<DebugThreadData>();
    }

    
    Iterator<ThreadReference> iter = listThreads.iterator(); 
    Vector<DebugThreadData> threads = new Vector<DebugThreadData>();
    while (iter.hasNext()) {
      try {
        threads.add(new DebugThreadData(iter.next()));
      }
      catch (ObjectCollectedException e) {
        
      }
    }
    return threads;
  }

  
  public synchronized Vector<DebugStackData> getCurrentStackFrameData()
    throws DebugException
  {
    if (!isReady()) return new Vector<DebugStackData>();

    if (_runningThread != null || _suspendedThreads.size() <= 0) {
      throw new DebugException("No suspended thread to obtain stack frames.");
    }

    try {
      ThreadReference thread = _suspendedThreads.peek();
      Iterator<StackFrame> iter = thread.frames().iterator();  
      Vector<DebugStackData> frames = new Vector<DebugStackData>();
      while (iter.hasNext()) {
        frames.add(new DebugStackData(iter.next()));
      }
      return frames;
    }
    catch (IncompatibleThreadStateException itse) {
      _log("Unable to obtain stack frame.", itse);
      return new Vector<DebugStackData>();
    }
    catch (VMDisconnectedException vmde) {
      _log("VMDisconnected when getting the current stack frame data.", vmde);
      return new Vector<DebugStackData>();
    }
  }

  
  synchronized void scrollToSource(LocatableEvent e) {
    Location location = e.location();

    
    EventRequest request = e.request();
    Object docProp = request.getProperty("document");
    if ((docProp != null) && (docProp instanceof OpenDefinitionsDocument)) {
      openAndScroll((OpenDefinitionsDocument) docProp, location, true);
    }
    else  scrollToSource(location);
  }

  
  synchronized void scrollToSource(Location location) {
    scrollToSource(location, true);
  }

  
  synchronized void scrollToSource(Location location, boolean shouldHighlight) {
    OpenDefinitionsDocument doc = null;

    
    ReferenceType rt = location.declaringType();
    String fileName;
    try { fileName = getPackageDir(rt.name()) + rt.sourceName(); }
    catch (AbsentInformationException aie) {
      
      
      String className = rt.name();
      String ps = System.getProperty("file.separator");
      
      className = StringOps.replace(className, ".", ps);

      
      int indexOfDollar = className.indexOf('$');
      if (indexOfDollar > -1) {
        className = className.substring(0, indexOfDollar);
      }

      fileName = className + ".java";
    }

    
    File f = _model.getSourceFile(fileName);












    if (f != null) {
      
      try { doc = _model.getDocumentForFile(f); }
      catch (IOException ioe) {
        
      }
    }

    openAndScroll(doc, location, shouldHighlight);
  }

  
  public synchronized void scrollToSource(DebugStackData stackData)
    throws DebugException
  {
    _ensureReady();
    if (_runningThread != null) {
      throw new DebugException("Cannot scroll to source unless thread is suspended.");
    }

    ThreadReference threadRef = _suspendedThreads.peek();
    Iterator<StackFrame> i;

    try {
      if (threadRef.frameCount() <= 0 ) {
        printMessage("Could not scroll to source. The current thread had no stack frames.");
        return;
      }
      i = threadRef.frames().iterator(); 
    }
    catch (IncompatibleThreadStateException e) {
      throw new DebugException("Unable to find stack frames: " + e);
    }

    while (i.hasNext()) {
      StackFrame frame = i.next();

      if (frame.location().lineNumber() == stackData.getLine() &&
          stackData.getMethod().equals(frame.location().declaringType().name() + "." +
                                       frame.location().method().name()))
      {
        scrollToSource(frame.location(), false);
      }
    }
  }

  
  public synchronized void scrollToSource(Breakpoint bp) {
    openAndScroll(bp.getDocument(), bp.getLineNumber(), bp.getClassName(), false);
  }

  
  public synchronized Breakpoint getBreakpoint(int line, String className) {
    for (int i = 0; i < _breakpoints.size(); i++) {
      Breakpoint bp = _breakpoints.get(i);
      if ((bp.getLineNumber() == line) && (bp.getClassName().equals(className))) {
        return bp;
      }
    }
    
    return null;
  }

  
  synchronized void openAndScroll(OpenDefinitionsDocument doc, Location location, boolean shouldHighlight) {
    openAndScroll(doc, location.lineNumber(), location.declaringType().name(), shouldHighlight);
  }

  
  synchronized void openAndScroll(final OpenDefinitionsDocument doc, final int line, String className, final boolean shouldHighlight) {
    
    if (doc != null) { 
      doc.checkIfClassFileInSync();
      

      Utilities.invokeLater(new Runnable() { public void run() { _notifier.threadLocationUpdated(doc, line, shouldHighlight); } });
    }
    else printMessage("  (Source for " + className + " not found.)");
  }

  
  String getPackageDir(String className) {
    
    int lastDotIndex = className.lastIndexOf(".");
    if (lastDotIndex == -1) {
      
      return "";
    }
    else {
      String packageName = className.substring(0, lastDotIndex);
      
      String ps = System.getProperty("file.separator");
      packageName = StringOps.replace(packageName, ".", ps);
      return packageName + ps;
    }
  }

  
  synchronized void printMessage(String message) {
    _model.printDebugMessage(message);
  }

  
  private boolean hasAnonymous(ReferenceType rt) {
    String className = rt.name();
    StringTokenizer st = new StringTokenizer(className, "$");
    while (st.hasMoreElements()) {
      String currToken = st.nextToken();
      try {
        Integer anonymousNum = Integer.valueOf(currToken);
        return true;
      }
      catch(NumberFormatException nfe) {
        
      }
    }
    return false;
  }

  private boolean _getWatchFromInterpreter(DebugWatchData currWatch) {
    String currName = currWatch.getName();
    
    String value = _model.getInteractionsModel().getVariableToString(currName);
    if (value != null) {
      String type = _model.getInteractionsModel().getVariableClassName(currName);
      currWatch.setValue(value);
      currWatch.setType(type);
      return true;
    }
    else {
      return false;
    }
  }

  
  private synchronized void _hideWatches() {
    for (int i = 0; i < _watches.size(); i++) {
      DebugWatchData currWatch = _watches.get(i);
      currWatch.hideValueAndType();
    }
  }

  
  private synchronized void _updateWatches() throws DebugException {
    if (!isReady()) { return; }
      
    if (_suspendedThreads.size() <= 0) {
      
      for (int i = 0; i < _watches.size(); i++) {
        DebugWatchData currWatch = _watches.get(i);
        if (!_getWatchFromInterpreter(currWatch)) {
          currWatch.hideValueAndType();
        }
      }
      return;





    }

    try {
      StackFrame currFrame;
      List<StackFrame> frames;
      ThreadReference thread = _suspendedThreads.peek();
      if (thread.frameCount() <= 0 ) {
        printMessage("Could not update watch values. The current thread " +
                     "had no stack frames.");
        return;
      }
      frames = thread.frames(); 
      currFrame = frames.get(0);
      Location location = currFrame.location();

      ReferenceType rt = location.declaringType();
      ObjectReference obj = currFrame.thisObject();
      

      
      String rtName = rt.name();
      int numDollars = 0;
      int dollarIndex = rtName.indexOf("$", 0);
      while (dollarIndex != -1) {
        numDollars++;
        dollarIndex = rtName.indexOf("$", dollarIndex+1);
      }

      for (int i = 0; i < _watches.size(); i++) {
        DebugWatchData currWatch = _watches.get(i);
        String currName = currWatch.getName();
        if (_getWatchFromInterpreter(currWatch)) {
          continue;
        }





























































        
        
        ReferenceType outerRt = rt;
        ObjectReference outer = obj;  
        Field field = outerRt.fieldByName(currName);

        if (obj != null) {
          

          
          
          
          int outerIndex = numDollars - 1;
          if (hasAnonymous(outerRt)) {
            
            
            List<Field> fields = outerRt.allFields();  
            Iterator<Field> iter = fields.iterator();
            while (iter.hasNext()) {
              Field f = iter.next();
              String name = f.name();
              if (name.startsWith("this$")) {
                int lastIndex = name.lastIndexOf("$");
                outerIndex = Integer.valueOf(name.substring(lastIndex+1, name.length())).intValue();
                break;
              }
            }
          }
          Field outerThis = outerRt.fieldByName("this$" + outerIndex);
          if (field == null) {
            
            
            field = outerRt.fieldByName("val$" + currName);
          }

          while ((field == null) && (outerThis != null)) {
            outer = (ObjectReference) outer.getValue(outerThis);
            if (outer == null) {
              
              
              
              break;
            }
            outerRt = outer.referenceType();
            field = outerRt.fieldByName(currName);

            if (field == null) {
              
              
              field = outerRt.fieldByName("val$" + currName);

              if (field == null) {
                
                outerIndex--;
                outerThis = outerRt.fieldByName("this$" + outerIndex);
              }
            }
          }
        }
        else {
          

          
          
          
          
          String rtClassName = outerRt.name();
          int index = rtClassName.lastIndexOf("$");
          while ((field == null) && (index != -1)) {
            rtClassName = rtClassName.substring(0, index);
            List<ReferenceType> l = _vm.classesByName(rtClassName); 
            if (l.isEmpty()) {
              
              
              break;
            }
            outerRt = l.get(0);
            field = outerRt.fieldByName(currName);

            if (field == null) {
              
              index = rtClassName.lastIndexOf("$");
            }
          }
        }

        
        
        
        if ((field != null) &&
            (field.isStatic() || (outer != null))) {
          Value v = (field.isStatic()) ?
            outerRt.getValue(field) :
            outer.getValue(field);
          currWatch.setValue(_getValue(v));
          try {
            currWatch.setType(field.type().name());
          }
          catch (ClassNotLoadedException cnle) {
            List<ReferenceType> classes = _vm.classesByName(field.typeName());  
            if (!classes.isEmpty()) {
              currWatch.setType(classes.get(0).name());
            }
            else {
              currWatch.setTypeNotLoaded();
            }
          }
        }
        else {
          currWatch.setNoValue();
          currWatch.setNoType();
        }

      }
    }
    catch (IncompatibleThreadStateException itse) {
      _log("Exception updating watches.", itse);
    }
    catch (InvalidStackFrameException isfe) {
      _log("Exception updating watches.", isfe);
    }
    catch (VMDisconnectedException vmde) {
      _log("Exception updating watches.", vmde);
      shutdown();
    }
  }

  
  private String _getValue(Value value) throws DebugException {
    
    
    if (value == null) {
      return "null";
    }

    if (!(value instanceof ObjectReference)) {
      return value.toString();
    }
    ObjectReference object = (ObjectReference) value;
    ReferenceType rt = object.referenceType();
    ThreadReference thread = _suspendedThreads.peek();
    List<Method> toStrings = rt.methodsByName("toString");  
    if (toStrings.size() == 0) {
      
      return value.toString();
    }
    
    Method method = toStrings.get(0);
    try {
      Value stringValue = 
        object.invokeMethod(thread, method, new LinkedList<Value>(), ObjectReference.INVOKE_SINGLE_THREADED);
      if (stringValue == null)  return "null";
      return stringValue.toString();
    }
    catch (InvalidTypeException ite) {
      
      throw new UnexpectedException(ite);
    }
    catch (ClassNotLoadedException cnle) {
      
      throw new UnexpectedException(cnle);
    }
    catch (IncompatibleThreadStateException itse) {
      throw new DebugException("Cannot determine value from thread: " + itse);
    }
    catch (InvocationException ie) {
      throw new DebugException("Could not invoke toString: " + ie);
    }
  }

  
  private Method _getDefineVariableMethod(ReferenceType interpreterRef, Value val) throws DebugException {
    List<Method> methods;
    String signature_beginning = "(Ljava/lang/String;";
    String signature_end = ")V";
    String signature_mid;
    String signature;

    if ((val == null) || ( val instanceof ObjectReference )) {
      signature_mid = "Ljava/lang/Object;Ljava/lang/Class;";
    }
    else if ( val instanceof BooleanValue ) {
      signature_mid = "Z";
    }
    else if ( val instanceof ByteValue ) {
      signature_mid = "B";
    }
    else if ( val instanceof CharValue ) {
      signature_mid = "C";
    }
    else if ( val instanceof DoubleValue ) {
      signature_mid = "D";
    }
    else if ( val instanceof FloatValue ) {
      signature_mid = "F";
    }
    else if ( val instanceof IntegerValue ) {
      signature_mid = "I";
    }
    else if ( val instanceof LongValue ) {
      signature_mid = "J";
    }
    else if ( val instanceof ShortValue ) {
      signature_mid = "S";
    }
    else{
      throw new IllegalArgumentException("Tried to define a variable which is\n" +
                                         "not an Object or a primitive type:\n" +
                                         val);
    }

    signature = signature_beginning + signature_mid + signature_end;
    methods = interpreterRef.methodsByName("defineVariable", signature);  
    if (methods.size() <= 0) {
      throw new DebugException("Could not find defineVariable method.");
    }

    
    Method tempMethod = methods.get(0);
    for (int i = 1; i < methods.size() && tempMethod.isAbstract(); i++) {
      tempMethod = methods.get(i);
    }
    if (tempMethod.isAbstract()) {
      throw new DebugException("Could not find concrete defineVariable method.");
    }

    return tempMethod;
  }

  
  private ObjectReference _getDebugInterpreter() throws InvalidTypeException, ClassNotLoadedException,
    IncompatibleThreadStateException, InvocationException, DebugException {
    
    ThreadReference threadRef = _suspendedThreads.peek();
    String interpreterName = _getUniqueThreadName(threadRef);
    return _getDebugInterpreter(interpreterName, threadRef);
  }

  
  private ObjectReference _getDebugInterpreter(String interpreterName, ThreadReference threadRef) throws 
    InvalidTypeException, ClassNotLoadedException, IncompatibleThreadStateException, InvocationException, 
    DebugException {
    
    if (!threadRef.isSuspended()) {
      throw new IllegalStateException("threadRef must be suspended to get a debug interpreter.");
    }

    
    Method m = _getMethod(_interpreterJVM.referenceType(), "getJavaInterpreter");

    
    
    
    

    int tries = 0;
    StringReference sr = null;
    while (tries < OBJECT_COLLECTED_TRIES) {
      try{
        LinkedList<StringReference> args = new LinkedList<StringReference>(); 
        sr = _vm.mirrorOf(interpreterName);
        sr.disableCollection();
        args.add(sr); 
        if ( printMessages ) {
          printStream.println("Invoking " + m.toString() + " on " + args.toString());
          printStream.println("Thread is " + threadRef.toString() + " <suspended = " + threadRef.isSuspended() + ">");
        }

        ObjectReference tmpInterpreter = 
          (ObjectReference) _interpreterJVM.invokeMethod(threadRef, m, args, ObjectReference.INVOKE_SINGLE_THREADED);

        if ( printMessages ) printStream.println("Returning...");
        return tmpInterpreter;
      }
      catch (ObjectCollectedException e) { tries++; }
      finally { sr.enableCollection(); }
    }
    throw new DebugException("The debugInterpreter: " + interpreterName + " could not be obtained from interpreterJVM");
  }

  

  
  private void _dumpVariablesIntoInterpreterAndSwitch() throws DebugException, AbsentInformationException {
    if (printMessages) {
      printStream.println("dumpVariablesIntoInterpreterAndSwitch");
    }
    try {
      ThreadReference suspendedThreadRef = _suspendedThreads.peek();
      StackFrame frame = suspendedThreadRef.frame(0);
      Location l = frame.location();
      ReferenceType rt = l.declaringType();
      String className = rt.name();

      
      String interpreterName = _getUniqueThreadName(suspendedThreadRef);
      _model.getInteractionsModel().addDebugInterpreter(interpreterName, className);
      ObjectReference debugInterpreter = _getDebugInterpreter();
      if (printMessages) {
        printStream.println("frame = suspendedThreadRef.frame(0);");
      }
      frame = suspendedThreadRef.frame(0);

      List<LocalVariable> vars = frame.visibleVariables();  
      Iterator<LocalVariable> varsIterator = vars.iterator();

      if (printMessages) {
        printStream.println("got visibleVariables");
      }

      
      while(varsIterator.hasNext()) {
        LocalVariable localVar = varsIterator.next();
        if (printMessages) {
          printStream.println("local variable: " + localVar);
        }
        
        frame = suspendedThreadRef.frame(0);
        Value val = frame.getValue(localVar);
        Type type;
        if (val != null) {
          type = val.type();
        }
        else {
          try {
            type = localVar.type();
          }
          catch(ClassNotLoadedException e) {
            List<ReferenceType> classes = _vm.classesByName(localVar.typeName());  
            if (!classes.isEmpty()) {
              type = classes.get(0);
            }
            else {
              type = null;
            }
          }
        }
        _defineVariable(suspendedThreadRef, debugInterpreter,
                        localVar.name(), val, type);
      }

      
      frame = suspendedThreadRef.frame(0);

      
      Value thisVal = frame.thisObject();
      if (thisVal != null) {
        _defineVariable(suspendedThreadRef, debugInterpreter,
                        "this", thisVal, thisVal.type());
        
      }

      
      String prompt = _getPromptString(suspendedThreadRef);
      if (printMessages) {
        printStream.println("setting active interpreter");
      }
      _model.getInteractionsModel().setActiveInterpreter(interpreterName,
                                                         prompt);
      if (printMessages) {
        printStream.println("got active interpreter");
      }
    }
    catch(InvalidTypeException exc) {
      throw new DebugException(exc.toString());
    }
    catch(IncompatibleThreadStateException e2) {
      throw new DebugException(e2.toString());
    }
    catch(ClassNotLoadedException e3) {
      throw new DebugException(e3.toString());
    }
    catch(InvocationException e4) {
      throw new DebugException(e4.toString());
    }
  }

  
  private String _getPromptString(ThreadReference threadRef) {
    return "[" + threadRef.name() + "] > ";
  }

  
  private void _defineVariable(ThreadReference suspendedThreadRef,
                               ObjectReference debugInterpreter,
                               String name, Value val, Type type)
    throws InvalidTypeException, IncompatibleThreadStateException,
      ClassNotLoadedException, InvocationException, DebugException
  {
    ReferenceType rtDebugInterpreter = debugInterpreter.referenceType();
    Method method2Call = _getDefineVariableMethod(rtDebugInterpreter,  val);

    
    
    
    

    int tries = 0;
    StringReference sr = null;
    while (tries < OBJECT_COLLECTED_TRIES) {
      try {
        
        List<Value> args = new LinkedList<Value>();  
        
        sr = _vm.mirrorOf(name);
        sr.disableCollection();
        args.add(sr);
        args.add(val);
        if (type == null) args.add(null);
        else if (type instanceof ReferenceType) {
          args.add(((ReferenceType)type).classObject());
        }

        
        debugInterpreter.invokeMethod(suspendedThreadRef, method2Call, args, ObjectReference.INVOKE_SINGLE_THREADED);
        return;
      }
      catch (ObjectCollectedException oce) {
        tries++;
      }
      finally {
        sr.enableCollection();
      }
    }
    throw new DebugException("The variable: " + name +
                             " could not be defined in the debug interpreter");
  }

  
  synchronized void currThreadSuspended() {
    try {
      try {
      
      
        _dumpVariablesIntoInterpreterAndSwitch();
        _switchToSuspendedThread();
      }
      catch(AbsentInformationException aie) {
        
        
        printMessage("No debug information available for this class.\nMake sure to compile classes to be debugged with the -g flag.");
        _hideWatches();
        
        
        _switchToSuspendedThread(false);
      }
    }
    catch(DebugException de) {
      throw new UnexpectedException(de);
    }
  }

  
  private void _switchToSuspendedThread() throws DebugException {
    _switchToSuspendedThread(true);
  }

  
  private void _switchToSuspendedThread(boolean updateWatches) throws DebugException {
    if (printMessages) {
      printStream.println("_switchToSuspendedThread()");
    }
    _runningThread = null;
    if (updateWatches) _updateWatches();
    final ThreadReference currThread = _suspendedThreads.peek();
    _notifier.currThreadSuspended();
    
    
    
    _notifier.currThreadSet(new DebugThreadData(currThread));

    try {
      if (currThread.frameCount() > 0) {
        scrollToSource(currThread.frame(0).location());
      }
    }
    catch (IncompatibleThreadStateException itse) {
      throw new UnexpectedException(itse);
    }
  }

  
  private String _getUniqueThreadName(ThreadReference thread) {
    return Long.toString(thread.uniqueID());
  }

  
  private Method _getGetVariableMethod(ReferenceType rtInterpreter) {
    return _getMethod(rtInterpreter, "getVariable");
  }

  
  private Method _getMethod(ReferenceType rt, String name) {
    List<Method> methods = rt.methodsByName(name);  
    Iterator<Method> methodsIterator = methods.iterator();

    
    while( methodsIterator.hasNext() ) {
      Method m = methodsIterator.next();
      if ( !m.isAbstract() ) {
        return m;
      }
    }

    throw new NoSuchElementException("No non-abstract method called " + name + " found in " + rt.name());
  }

  
  private Value _convertToActualType(ThreadReference threadRef, LocalVariable localVar,
                                     Value v)
    throws InvalidTypeException, ClassNotLoadedException,
    IncompatibleThreadStateException, InvocationException
  {
    String typeSignature;
    try {
      typeSignature = localVar.type().signature();
    }
    catch (ClassNotLoadedException cnle) {
      return v;
    }
    Method m;
    ObjectReference ref = (ObjectReference)v;
    ReferenceType rt = ref.referenceType();

    if ( typeSignature.equals("Z") ) {
      m = _getMethod(rt, "booleanValue");
    }
    else if ( typeSignature.equals("B") ) {
      m = _getMethod(rt, "byteValue");
    }
    else if ( typeSignature.equals("C") ) {
      m = _getMethod(rt, "charValue");
    }
    else if ( typeSignature.equals("S") ) {
      m = _getMethod(rt, "shortValue");
    }
    else if ( typeSignature.equals("I") ) {
      m = _getMethod(rt, "intValue");
    }
    else if ( typeSignature.equals("J") ) {
      m = _getMethod(rt, "longValue");
    }
    else if ( typeSignature.equals("F") ) {
      m = _getMethod(rt, "floatValue");
    }
    else if ( typeSignature.equals("D") ) {
      m = _getMethod(rt, "doubleValue");
    }
    else{
      return v;
    }

    return ref.invokeMethod(threadRef, m, new LinkedList<Value>(), ObjectReference.INVOKE_SINGLE_THREADED);
  }




































  private Value _getValueOfLocalVariable(LocalVariable var, ThreadReference thread)
    throws InvalidTypeException, ClassNotLoadedException,
      IncompatibleThreadStateException, InvocationException, DebugException
  {
    ObjectReference interpreter = _getDebugInterpreter(_getUniqueThreadName(thread), thread);
    ReferenceType rtInterpreter = interpreter.referenceType();
    Method method2Call = _getGetVariableMethod(rtInterpreter);

    
    
    
    

    int tries = 0;
    StringReference sr = null;
    String varName = var.name();
    while (tries < OBJECT_COLLECTED_TRIES) {
      try {
        List<Value> args = new LinkedList<Value>(); 
        sr = _vm.mirrorOf(varName);
        sr.disableCollection();
        args.add(sr);
        Value v = interpreter.invokeMethod(thread, method2Call, args, ObjectReference.INVOKE_SINGLE_THREADED);
        if (v != null)  v = _convertToActualType(thread, var, v);

        return v;
      }
      catch (ObjectCollectedException oce) { tries++; }
      finally { sr.enableCollection(); }
    }
    throw new DebugException("The value of variable: " + varName +
                             " could not be obtained from the debug interpreter");

  }

  
  private void _copyBack(ThreadReference threadRef) throws IncompatibleThreadStateException, AbsentInformationException,
      InvocationException, DebugException {
    if (printMessages) printStream.println("Getting debug interpreter");
    if (printMessages) printStream.println("Getting variables");
    StackFrame frame = threadRef.frame(0);
    List<LocalVariable> vars = frame.visibleVariables();  
    Iterator<LocalVariable> varsIterator = vars.iterator();

    
    while(varsIterator.hasNext()) {
      if (printMessages) printStream.println("Iterating through vars");
      LocalVariable localVar = varsIterator.next();

      try {
        Value v = _getValueOfLocalVariable(localVar, threadRef);
        frame = threadRef.frame(0);
        frame.setValue(localVar, v);
      }
      catch (ClassNotLoadedException cnle) {
        printMessage("Could not update the value of '" + localVar.name() + "' (class not loaded)");
      }
      catch (InvalidTypeException ite) {
        printMessage("Could not update the value of '" + localVar.name() + "' (invalid type exception)");
      }
    }
  }

  protected void _copyVariablesFromInterpreter() throws DebugException {
    try {
      
      
      if (printMessages) printStream.println("In _copyBack()");
      _copyBack(_runningThread);
      if (printMessages) printStream.println("Out of _copyBack()");
    }
    catch(AbsentInformationException e2) {
      
      
      
    }
    catch(IncompatibleThreadStateException e) {
      throw new DebugException(e.toString());
    }
    catch(InvocationException e4) {
      throw new DebugException(e4.toString());
    }
  }

  
  private void _removeAllDebugInterpreters() {
    DefaultInteractionsModel interactionsModel =
      _model.getInteractionsModel();
    String oldInterpreterName;
    if (_runningThread != null) {
      oldInterpreterName = _getUniqueThreadName(_runningThread);
      interactionsModel.removeInterpreter(oldInterpreterName);
    }
    while (!_suspendedThreads.isEmpty()) {
      ThreadReference threadRef = _suspendedThreads.pop();
      oldInterpreterName = _getUniqueThreadName(threadRef);
      interactionsModel.removeInterpreter(oldInterpreterName);
    }
  }

  
  private void _removeCurrentDebugInterpreter(boolean fromStep) {
    DefaultInteractionsModel interactionsModel =
      _model.getInteractionsModel();
    
    if (fromStep || _suspendedThreads.isEmpty()) {
      interactionsModel.setToDefaultInterpreter();
    }
    else {
      ThreadReference threadRef = _suspendedThreads.peek();
      _switchToInterpreterForThreadReference(threadRef);
    }
    String oldInterpreterName = _getUniqueThreadName(_runningThread);
    interactionsModel.removeInterpreter(oldInterpreterName);
  }

  
  synchronized void currThreadResumed() throws DebugException {
    if (printMessages) { printStream.println("In currThreadResumed()"); }
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.currThreadResumed(); } });
  }

  
  private void _switchToInterpreterForThreadReference(ThreadReference threadRef) {
    String threadName = _getUniqueThreadName(threadRef);
    String prompt = _getPromptString(threadRef);
    _model.getInteractionsModel().setActiveInterpreter(threadName, prompt);
  }

  synchronized void threadStarted() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.threadStarted(); } });
  }

  
  synchronized void currThreadDied() throws DebugException {
    printMessage("The current thread has finished.");
    _runningThread = null;

    _updateWatches();

    if (_suspendedThreads.size() > 0) {
      ThreadReference thread = _suspendedThreads.peek();
      _switchToInterpreterForThreadReference(thread);

      try {
        if (thread.frameCount() <= 0) {
          printMessage("Could not scroll to source for " + thread.name() + ". It has no stackframes.");
        }
        else scrollToSource(thread.frame(0).location());
      }
      catch(IncompatibleThreadStateException e) { throw new UnexpectedException(e); }

      
      
      _switchToSuspendedThread();
    }
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.currThreadDied(); } });
  }

  synchronized void nonCurrThreadDied() { 
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.nonCurrThreadDied(); } }); 
  }

  
  synchronized void notifyDebuggerShutdown() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.debuggerShutdown(); } });
  }

  
  synchronized void notifyDebuggerStarted() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.debuggerStarted(); } });
  }

  
  synchronized void notifyStepRequested() {
    Utilities.invokeLater(new Runnable() { public void run() { _notifier.stepRequested(); } });
  }






  
  protected static class RandomAccessStack {
    private Vector<ThreadReference> _data = new Vector<ThreadReference>();

    public synchronized void push(ThreadReference t) {
      _data.add(0, t);
    }

    public synchronized ThreadReference peek() throws NoSuchElementException {
      try {
        return _data.get(0);
      }
      catch(ArrayIndexOutOfBoundsException e) {
        throw new NoSuchElementException("Cannot peek at the top of an empty RandomAccessStack!");
      }
    }

    public synchronized ThreadReference peekAt(int i) throws NoSuchElementException {
      try {
        return _data.get(i);
      }
      catch(ArrayIndexOutOfBoundsException e) {
        throw new NoSuchElementException("Cannot peek at element " + i + " of this stack!");
      }
    }

    public synchronized ThreadReference remove(long id) throws NoSuchElementException{
      for(int i = 0; i < _data.size(); i++) {
        if ( _data.get(i).uniqueID() == id ) {
          ThreadReference t = _data.get(i);
          _data.remove(i);
          return t;
        }
      }

      throw new NoSuchElementException("Thread " + id + " not found in debugger suspended threads stack!");
    }

    public synchronized ThreadReference pop() throws NoSuchElementException{
      try {
        ThreadReference t = _data.get(0);
        _data.remove(0);
        return t;
      }
      catch (ArrayIndexOutOfBoundsException e) {
        throw new NoSuchElementException("Cannot pop from an empty RandomAccessStack!");
      }
    }

    public synchronized boolean contains(long id) {
      for(int i = 0; i < _data.size(); i++) {
        if ( _data.get(i).uniqueID() == id ) {
          return true;
        }
      }

      return false;
    }

    public int size() {
      return _data.size();
    }

    public boolean isEmpty() {
      return size() == 0;
    }
  }

  
}
