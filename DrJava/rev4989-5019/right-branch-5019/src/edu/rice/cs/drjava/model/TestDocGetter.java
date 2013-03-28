

package edu.rice.cs.drjava.model;

import java.util.HashMap;
import java.io.File;
import java.io.IOException;
import javax.swing.text.BadLocationException;

import edu.rice.cs.util.FileOps;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;


public class TestDocGetter extends DummyGlobalModel {
  
  
  HashMap<File, OpenDefinitionsDocument> docs;

  
  public TestDocGetter() { this(new File[0], new String[0]); }

  
  public TestDocGetter(File[] files, String[] texts) {
    if (files.length != texts.length) {
      throw new IllegalArgumentException("Argument arrays must match in size.");
    }

    docs = new HashMap<File, OpenDefinitionsDocument>(texts.length * 2);

    GlobalEventNotifier en = new GlobalEventNotifier();
    for (int i = 0; i < texts.length; i++) {
      DefinitionsDocument doc = new DefinitionsDocument(en);
      OpenDefinitionsDocument odoc = new TestOpenDoc(doc);
      odoc.setFile(files[i]);
      try { doc.insertString(0, texts[i], null); }
      catch (BadLocationException e) { throw new UnexpectedException(e); }
      docs.put(files[i], odoc);
    }
  }

  public OpenDefinitionsDocument getDocumentForFile(File file)
    throws IOException {
    
    if (docs.containsKey(file)) return docs.get(file);
    else throw new IllegalStateException("TestDocGetter can't open new files!");
  }

  
  private static class TestOpenDoc extends DummyOpenDefDoc {
    DefinitionsDocument _doc;
    File _file;
    TestOpenDoc(DefinitionsDocument d) {
      _doc = d;
      _defDoc = d;
      _file = FileOps.NULL_FILE;
    }

    
    public DefinitionsDocument getDocument() { return _doc; }

    
    public File getFile() throws FileMovedException  { return _file; }
    
    public void setFile(File f) { _file = f; }
  }
}
