

package edu.rice.cs.drjava.model.cache;

import java.io.IOException;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import edu.rice.cs.drjava.model.FileMovedException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;


public interface DCacheAdapter {
  
  
  public DefinitionsDocument getDocument() throws IOException, FileMovedException;
  
  public int getLength();
  
  
  public String getText();
  
  
  public String getText(int offset, int length) throws BadLocationException;
  
  
  public boolean isReady();
  
  
  public void close();
  
  
  public void addDocumentListener(DocumentListener l);
  
  
  public void documentSaved();
  
  
  public void documentModified();
  
  
  public void documentReset();
}