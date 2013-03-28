

package edu.rice.cs.drjava.model.javadoc;

import java.io.File;
import edu.rice.cs.drjava.model.EventNotifier;
import edu.rice.cs.drjava.model.compiler.CompilerListener;


class JavadocEventNotifier extends EventNotifier<JavadocListener>
  implements JavadocListener {
  
  
  public void javadocStarted() {
    _lock.startRead();
    try { for (JavadocListener jl: _listeners) { jl.javadocStarted(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void javadocEnded(boolean success, File destDir, boolean allDocs) {
    _lock.startRead();
    try { for (JavadocListener jl: _listeners) { jl.javadocEnded(success, destDir, allDocs); } }
    finally { _lock.endRead();}
  }
  
  
  public void saveBeforeJavadoc() {
    _lock.startRead();
    try { for (JavadocListener jl: _listeners) { jl.saveBeforeJavadoc(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void compileBeforeJavadoc(final CompilerListener afterCompile) {
    _lock.startRead();
    try { for (JavadocListener jl: _listeners) { jl.compileBeforeJavadoc(afterCompile); } }
    finally { _lock.endRead(); }
  }
}

