

package edu.rice.cs.drjava.model.compiler;

import java.io.File;

import edu.rice.cs.drjava.model.EventNotifier;


class CompilerEventNotifier extends EventNotifier<CompilerListener> implements CompilerListener {

  
  public void compileStarted() {

    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.compileStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void compileEnded(File workDir) {
    _lock.startRead();
    try { for (CompilerListener cl : _listeners) { cl.compileEnded(workDir); } }
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
}
