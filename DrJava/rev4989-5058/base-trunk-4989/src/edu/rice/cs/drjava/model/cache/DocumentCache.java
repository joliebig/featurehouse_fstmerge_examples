

package edu.rice.cs.drjava.model.cache;

import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.util.*;
import java.io.IOException;

import edu.rice.cs.drjava.model.definitions.DefinitionsDocument;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.drjava.model.FileMovedException;

import edu.rice.cs.util.Log;
import edu.rice.cs.util.UnexpectedException;
import edu.rice.cs.util.swing.Utilities;
import edu.rice.cs.plt.iter.IterUtil;



public class DocumentCache {
  
  
  private static final Log _log = new Log("DocumentCache.txt", false);
  
  private static final int INIT_CACHE_SIZE = 32;
  
  
  private int CACHE_SIZE;
  
  private LinkedHashSet<DocManager> _residentQueue;
  
  private Object _cacheLock = new Object();
  
  
  public DocumentCache(int size) {

    CACHE_SIZE = size;
    _residentQueue = new LinkedHashSet<DocManager>();
  }
  
  
  public DocumentCache() { this(INIT_CACHE_SIZE); }
  
  
  public DCacheAdapter register(OpenDefinitionsDocument odd, DDReconstructor rec) {
    DocManager mgr = new DocManager(rec, odd.isUntitled());
    notifyRegistrationListeners(odd, mgr);  

    return mgr;
  }
  
  
  public void setCacheSize(int size) {
    if (size <= 0) throw new IllegalArgumentException("Cannot set the cache size to zero or less.");
    synchronized(_cacheLock) {    
      CACHE_SIZE = size;
      int diff = _residentQueue.size() - CACHE_SIZE;
      if (diff > 0) {
        Iterable<DocManager> toRemove = IterUtil.snapshot(IterUtil.truncate(_residentQueue, diff));
        for (DocManager dm : toRemove) { _residentQueue.remove(dm); dm.kickOut(); }
      }
    }
  }
  
  public int getCacheSize() { return CACHE_SIZE; }
  public int getNumInCache() { return _residentQueue.size(); }
  
  public String toString() { return _residentQueue.toString(); }
  
  
  
  
  private static final int IN_QUEUE = 0;     
  private static final int UNTITLED = 1;     
  private static final int NOT_IN_QUEUE = 2; 
  private static final int UNMANAGED = 3;    
  
  
  
  private class DocManager implements DCacheAdapter {
    
    private final DDReconstructor _rec;
    private volatile int _stat; 
    private volatile DefinitionsDocument _doc;
    
    
    public DocManager(DDReconstructor rec, boolean isUntitled) {

      _rec = rec;
      if (isUntitled) _stat = UNTITLED; 
      else _stat = NOT_IN_QUEUE;
      _doc = null;

    }
    
    
    public void addDocumentListener(DocumentListener l) { _rec.addDocumentListener(l); }
    
    
    private DefinitionsDocument makeDocument() {
      try { 
        _doc = _rec.make();
        assert _doc != null;
      }
      catch(Exception e) { throw new UnexpectedException(e); }


      if (_stat == NOT_IN_QUEUE) add();       
      return _doc;
    }
    
    
    public DefinitionsDocument getDocument() throws IOException, FileMovedException {

      

      final DefinitionsDocument doc = _doc;  
      if (doc != null) return doc;  
      synchronized(_cacheLock) { 
        if (_doc != null) return _doc;  
        return makeDocument();
      }
    }
    
    
    public int getLength() {
      final DefinitionsDocument doc = _doc;  
      if (doc == null ) return _rec.getText().length();
      return doc.getLength();
    }
    
    
    
    public String getText() {
      final DefinitionsDocument doc = _doc;  
      if (doc == null ) return _rec.getText();  

      return doc.getText();
    }
    
    
    public String getText(int offset, int len) throws BadLocationException { 
      final DefinitionsDocument doc = _doc; 
      if (doc == null) {
        try { return _rec.getText().substring(offset, offset + len); }
        catch(IndexOutOfBoundsException e) { throw new BadLocationException(e.getMessage(), offset); }  
      }

      return doc.getText(offset, len); 
    }
    
    
    public boolean isReady() {  return _doc != null; }  
    
    
    public void close() {

      synchronized(_cacheLock) {
        _residentQueue.remove(this);
        closingKickOut();
      }
    }
    
    public void documentModified() {
      synchronized(_cacheLock) { 
        _residentQueue.remove(this); 
        _stat = UNMANAGED;
      }
    }
    
    public void documentReset() {
      synchronized(_cacheLock) { 
        if (_stat == UNMANAGED) add(); 
      }
    }
    
    
    public void documentSaved() {


      synchronized(_cacheLock) {  
        if (isUnmanagedOrUntitled()) {
          add();  
        }
      }
    }
    
    
    private void add() {


      if (! _residentQueue.contains(this)) {
        _residentQueue.add(this);
        _stat = IN_QUEUE;
      }
      if (_residentQueue.size() > CACHE_SIZE) IterUtil.first(_residentQueue).remove();
    }
    
    
    private void remove() { 
      _residentQueue.remove(this);
      kickOut();
    }
    
    
    private boolean isUnmanagedOrUntitled() { return (_stat & 0x1) != 0; }  
    
    
    void kickOut() { kickOut(false); }
    
    
    void closingKickOut() { kickOut(true); }
    
    
    private void kickOut(boolean isClosing) {

      if (! isClosing) {
        

        _rec.saveDocInfo(_doc);
      }
      if (_doc != null) {
        _doc.close(); 
        _doc = null;
      }
      _stat = NOT_IN_QUEUE;
    }
    
    public String toString() { return "DocManager for " + _rec.toString() + "[stat = " + _stat + "]"; } 
  }
  
  
  
  
  public interface RegistrationListener {
    public void registered(OpenDefinitionsDocument odd, DCacheAdapter man);
  }
  
  private LinkedList<RegistrationListener> _regListeners =   new LinkedList<RegistrationListener>();
  
  public void addRegistrationListener(RegistrationListener list) { synchronized(_regListeners) { _regListeners.add(list); } }
  public void removeRegistrationListener(RegistrationListener list) { synchronized(_regListeners) { _regListeners.remove(list); } }
  public void clearRegistrationListeners() { _regListeners.clear(); }
  
  private void notifyRegistrationListeners(final OpenDefinitionsDocument odd, final DocManager man) {
    synchronized(_regListeners) {
      if (_regListeners.isEmpty()) return; 
      Utilities.invokeAndWait(new Runnable() {
        public void run() { for (RegistrationListener list : _regListeners) { list.registered(odd, man); } }
      });
    }
  }
}
