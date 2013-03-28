

package edu.rice.cs.drjava.model.compiler;

import java.io.File;
import java.util.List;

import edu.rice.cs.drjava.model.EventNotifier;

import static edu.rice.cs.plt.debug.DebugUtil.debug;


class CompilerEventNotifier extends EventNotifier<CompilerListener> implements CompilerListener {
  
  
  public void compileStarted() {

    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.compileStarted(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void compileEnded(File workDir, List<? extends File> excludedFiles) {
    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.compileEnded(workDir, excludedFiles); } }
    finally { _lock.endRead(); }
  }

  
  public void compileAborted(Exception e) {
    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.compileAborted(e); } }
    finally { _lock.endRead(); }
  }
  
  
  public void saveBeforeCompile() {
    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.saveBeforeCompile(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void saveUntitled() {
    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.saveUntitled(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void activeCompilerChanged() {

    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.activeCompilerChanged(); } }
    finally { _lock.endRead(); }
  }
}
