

package edu.rice.cs.drjava.model.javadoc;

import java.io.File;
import java.io.IOException;
import edu.rice.cs.drjava.model.DJError;
import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FileSaveSelector;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;
import edu.rice.cs.util.DirectorySelector;


public class NoJavadocAvailable implements JavadocModel {
  
  private final JavadocEventNotifier _notifier = new JavadocEventNotifier();
  private final CompilerErrorModel _javadocErrorModel;
  
  public NoJavadocAvailable(GlobalModel model) {
    DJError e = new DJError("The javadoc feature is not available.", false);
    _javadocErrorModel = new CompilerErrorModel(new DJError[]{e}, model);
  }
  
  public boolean isAvailable() { return false; }
  
  
  
  public void addListener(JavadocListener listener) { _notifier.addListener(listener); }
  
  
  public void removeListener(JavadocListener listener) { _notifier.removeListener(listener); }
  
  
  public void removeAllListeners() { _notifier.removeAllListeners(); }
  
  
  
  public CompilerErrorModel getJavadocErrorModel() { return _javadocErrorModel; }
  
  
  public void resetJavadocErrors() {  }
  
  
  public File suggestJavadocDestination(OpenDefinitionsDocument doc) { return null; }
  
  
  public void javadocAll(DirectorySelector select, FileSaveSelector saver) throws IOException {
    _notifier.javadocStarted();
    _notifier.javadocEnded(false, null, true);
  }
  
  
  public void javadocDocument(OpenDefinitionsDocument doc, FileSaveSelector saver) throws IOException {
    _notifier.javadocStarted();
    _notifier.javadocEnded(false, null, true);
  }
  
}
