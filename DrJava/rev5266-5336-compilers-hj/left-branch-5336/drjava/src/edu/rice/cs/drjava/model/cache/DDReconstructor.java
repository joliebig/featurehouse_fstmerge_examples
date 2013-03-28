

package edu.rice.cs.drjava.model.cache;

import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import java.io.IOException;
import javax.swing.text.BadLocationException;
import javax.swing.event.DocumentListener;
import edu.rice.cs.drjava.model.FileMovedException;


public interface DDReconstructor {
  
  
  public DefinitionsDocument make() throws IOException, BadLocationException, FileMovedException;
  
  
  public void saveDocInfo(DefinitionsDocument doc);
  
  
  public void addDocumentListener(DocumentListener dl);
  
  
  public String getText();
}
