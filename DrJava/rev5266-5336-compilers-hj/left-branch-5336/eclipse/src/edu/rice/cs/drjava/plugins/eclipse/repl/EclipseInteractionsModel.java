

package edu.rice.cs.drjava.plugins.eclipse.repl;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.LinkedList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import edu.rice.cs.drjava.plugins.eclipse.EclipsePlugin;
import edu.rice.cs.drjava.plugins.eclipse.util.text.SWTDocumentAdapter;
import edu.rice.cs.drjava.model.repl.*;
import edu.rice.cs.drjava.model.repl.newjvm.MainJVM;
import edu.rice.cs.util.*;
import edu.rice.cs.util.text.ConsoleDocument;

import static edu.rice.cs.plt.debug.DebugUtil.debug;
import static edu.rice.cs.plt.debug.DebugUtil.error;


public class EclipseInteractionsModel extends RMIInteractionsModel {
 
 
 
 
 protected static final int HISTORY_SIZE = 1000;
 
 
 protected static final int WRITE_DELAY = 50;

 public static final File WORKING_DIR = new File(System.getProperty("user.home", ""));
 
 
 private static final boolean DEBUG = false;
 
 
 protected final LinkedList<InteractionsListener> _listeners;
 
 
 protected boolean _warnedToReset;
 
 
 private static MainJVM newMainJVM() {
   try { return new MainJVM(WORKING_DIR); }
   catch(RemoteException e) {
     error.log(e);
     throw new UnexpectedException(e);
   }
 }
 
 
 public EclipseInteractionsModel(SWTDocumentAdapter adapter)  {
   this(newMainJVM(), adapter);
 }
 
 
 public EclipseInteractionsModel(MainJVM control, SWTDocumentAdapter adapter) {
   super(control, adapter, WORKING_DIR, HISTORY_SIZE, WRITE_DELAY);
   _listeners = new LinkedList<InteractionsListener>();
   _warnedToReset = false;
   if (DEBUG) _debugSystemOutAndErr();
   
   _jvm.setInteractionsModel(this);
   EclipsePlugin plugin = EclipsePlugin.getDefault();
   if (plugin != null) {
     String classpath = plugin.getPluginClasspath();
     _jvm.setStartupClassPath(classpath);
   }
   _jvm.startInterpreterJVM();
   _addChangeListener();
 }
 
 
 public void dispose() { _jvm.killInterpreter(null); }
 
 
 public Iterable<File> getClassPath() { return _jvm.getClassPath(); }
 
 
 public void addInteractionsListener(InteractionsListener l) { _listeners.addLast(l); }
 
 
 public void removeInteractionsListener(InteractionsListener l) { _listeners.remove(l); }
 
 
 public void removeAllInteractionsListeners() { _listeners.clear(); }
 
 
 protected void _interpreterResetFailed(Throwable t) {
   _document.insertBeforeLastPrompt("Reset Failed!" + StringOps.NEWLINE, InteractionsDocument.ERROR_STYLE);
 }
 
 
 public void interpreterReady(File wd) {
   debug.logStart();
   _resetInteractionsClasspath();
   super.interpreterReady(wd);
   debug.logEnd();
 }
 
 
 protected void _resetInterpreter(File wd) {
   super._resetInterpreter(wd);
   _warnedToReset = false;
 }
 
 
 public void _notifyInteractionStarted() {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interactionStarted();
   }
 }
 
 
 protected void _notifyInteractionEnded() {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interactionEnded();
   }
 }
 
 
 protected void _notifySyntaxErrorOccurred(final int offset, final int length) {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interactionErrorOccurred(offset, length);
   }
 }
 
 
 protected void _notifyInterpreterResetting() {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interpreterResetting();
   }
 }
 
 
 public void _notifyInterpreterReady(File wd) {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interpreterReady(wd);
   }
 }
 
 
 protected void _notifyInterpreterExited(final int status) {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interpreterExited(status);
   }
 }
 
 
 protected void _notifyInterpreterResetFailed(Throwable t) {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interpreterResetFailed(t);
   }
 }
 
 
 protected void _notifyInterpreterChanged(final boolean inProgress) {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interpreterChanged(inProgress);
   }
 }
 
 
 protected void _notifyInteractionIncomplete() {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).interactionIncomplete();
   }
 }
 
 
 protected void _notifySlaveJVMUsed() {
   for (int i=0; i < _listeners.size(); i++) {
     _listeners.get(i).slaveJVMUsed();
   }
 }
 
 
 protected void _resetInteractionsClasspath() {
   try {
     
     IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
     
     
     IJavaModel jModel = JavaCore.create(root);
     
     
     IJavaProject jProjects[] = jModel.getJavaProjects();
     
     
     for(int i = 0; i < jProjects.length; i++) {
       IJavaProject jProj = jProjects[i];
       _addProjectToClasspath(jProj, jModel, root);
     }
   }
   catch (CoreException ce) {
     
     
     throw new UnexpectedException(ce);
   }
 }
 
 private void _addProjectToClasspath(IJavaProject jProj) throws CoreException {
   
   IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
   
   
   IJavaModel jModel = JavaCore.create(root);
   
   _addProjectToClasspath(jProj, jModel, root);
 }
 
 private void _addProjectToClasspath(IJavaProject jProj, IJavaModel jModel, IWorkspaceRoot root)
   throws CoreException {
   
   IProject proj = jProj.getProject();
   URI projRoot = proj.getDescription().getLocationURI();
   
   
   
   
   
   IClasspathEntry entries[] = jProj.getResolvedClasspath(true);
   
   
   for(int j = 0; j < entries.length; j++) {
     IClasspathEntry entry = entries[j];
     
     
     int kind = entry.getEntryKind();
     
     
     IPath path;
     switch (kind) {
       case IClasspathEntry.CPE_LIBRARY:
         
         path = entry.getPath();
         
         addToClassPath(path.toOSString());
         break;
       case IClasspathEntry.CPE_SOURCE:
         
         
         path = entry.getOutputLocation();
         if (path == null) {
           path = jProj.getOutputLocation();
           
         }
         
         
         
         
         if (projRoot != null && (!projRoot.isAbsolute() || projRoot.getScheme().equals("file"))) {
           
           
           
           
           path = path.removeFirstSegments(1);
           path = new Path(projRoot.getPath()).append(path);
         }
         else {
           
           
           path = root.getLocation().append(path);
         }
         
         
         
         addBuildDirectoryClassPath(path.toOSString());
         break;
       case IClasspathEntry.CPE_PROJECT:
         
         
         
         
         break;
       default:
         
         throw new RuntimeException("Unsupported classpath entry type.");
     }
   }
 }
 
 
 protected void _warnUserToReset() {
   if (!_warnedToReset && _jvm.slaveJVMUsed()) {
     String warning =
       "Warning: Interactions are out of sync with the current class files.\n" +
       "You should reset interactions from the toolbar menu.\n";
     _document.insertBeforeLastPrompt(warning,
                                      InteractionsDocument.ERROR_STYLE);
     _warnedToReset = true;
   }
 }
 
 
 protected void _addChangeListener() {
   
   
   
   
   JavaCore.addElementChangedListener(new IElementChangedListener() {
     public void elementChanged(ElementChangedEvent e) {
       IJavaElementDelta delta = e.getDelta();
       _visitDelta(delta, 0);
     }
   });
 }
 public URL toURL(String path) {
   try { return new File(path).toURI().toURL(); } 
   catch (MalformedURLException e) {
     _document.insertBeforeLastPrompt("Malformed URL " + path +"\n", InteractionsDocument.ERROR_STYLE);
   }
   throw new RuntimeException("Trying to add an invalid file:" + path);
 }
 
 public void addBuildDirectoryClassPath(String path) {    
   
   
   
   super.addBuildDirectoryClassPath(new File(path));
   
 }
 public void addProjectFilesClassPath(String path) {    
   
   
   super.addProjectFilesClassPath(new File(path));
 }
 
 public void addToClassPath(String path) {
   
   
   
   super.addProjectClassPath(new File(path));
 }
 
 
 protected void _visitDelta(IJavaElementDelta delta, int depth) {
   int kind = delta.getKind();

   IJavaElement element = delta.getElement();
   






   



   
   
   
   
   if (_isCompilationUnit(element) && (kind == IJavaElementDelta.CHANGED)) {
     
     _warnUserToReset();
   }
   
   
   
   if ((delta.getFlags() & IJavaElementDelta.F_CHILDREN) != 0) {
     IJavaElementDelta[] children = delta.getAffectedChildren();
     
     for (int i=0; i < children.length; i++) {
       _visitDelta(children[i], depth + 1);
     }
   }
   
   
   else if (kind == IJavaElementDelta.ADDED) {
     if (element instanceof IJavaProject) {
       try {
         _addProjectToClasspath((IJavaProject)element);
       }
       catch(CoreException e) {
         throw new UnexpectedException(e);
       }
     }
   }
   
   
   
   
 }
 
 
 
 
 
 
 
 
 
 protected boolean _isCompilationUnit(IJavaElement element) {
   boolean isCompilationUnit = element instanceof ICompilationUnit;
   boolean isWorkingCopy = isCompilationUnit &&
     ((ICompilationUnit)element).isWorkingCopy();
   
   return isCompilationUnit && !isWorkingCopy;
 }
 
 
 private void _debugSystemOutAndErr() {
   try {
     File outF = new File(System.getProperty("user.home") +
                          System.getProperty("file.separator") + "out.txt");
     FileWriter wo = new FileWriter(outF);
     final PrintWriter outWriter = new PrintWriter(wo);
     File errF = new File(System.getProperty("user.home") +
                          System.getProperty("file.separator") + "err.txt");
     FileWriter we = new FileWriter(errF);
     final PrintWriter errWriter = new PrintWriter(we);
     System.setOut(new PrintStream(new edu.rice.cs.util.OutputStreamRedirector() {
       public void print(String s) {
         outWriter.print(s);
         outWriter.flush();
       }
     }));
     System.setErr(new PrintStream(new edu.rice.cs.util.OutputStreamRedirector() {
       public void print(String s) {
         errWriter.print(s);
         errWriter.flush();
       }
     }));
   }
   catch (IOException ioe) {}
 }
 
 
 public void setPrivateAccessible(boolean allow) {
   _jvm.setPrivateAccessible(allow);
 }
 
 
 public void setOptionArgs(String optionArgString) {
   _jvm.setOptionArgs(optionArgString);
 }
 
 
 public ConsoleDocument getConsoleDocument() {
   return new ConsoleDocument(new InteractionsDJDocument());
 }
}
